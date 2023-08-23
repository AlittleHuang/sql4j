package github.alittlehuang.sql4j.jdbc;

import github.alittlehuang.sql4j.dsl.builder.LockModeType;
import github.alittlehuang.sql4j.dsl.builder.ResultBuilder;
import github.alittlehuang.sql4j.dsl.support.QuerySpecification;
import github.alittlehuang.sql4j.dsl.support.ResultBuilderFactory;
import github.alittlehuang.sql4j.dsl.support.builder.projection.ProjectionResultBuilder;
import github.alittlehuang.sql4j.dsl.support.builder.projection.meta.ProjectionMetaProvider;
import github.alittlehuang.sql4j.dsl.util.Tuple;
import github.alittlehuang.sql4j.dsl.util.TypeCastUtil;
import github.alittlehuang.sql4j.jdbc.support.DefaultResultSetCollector;
import github.alittlehuang.sql4j.jdbc.support.SqlExecutor;
import github.alittlehuang.sql4j.jdbc.support.JpaTableMapperFactory;

import java.util.List;

public class JdbcResultBuilderFactory implements ResultBuilderFactory {

    private final TableMapperFactory mappers;
    private final PreparedSqlBuilder sqlBuilder;
    private final SqlExecutor sqlExecutor;
    private final ResultSetCollector collector;

    public JdbcResultBuilderFactory(TableMapperFactory mappers,
                                    PreparedSqlBuilder sqlBuilder,
                                    SqlExecutor sqlExecutor,
                                    ResultSetCollector collector) {
        this.mappers = mappers;
        this.sqlBuilder = sqlBuilder;
        this.sqlExecutor = sqlExecutor;
        this.collector = collector;
    }

    public JdbcResultBuilderFactory(PreparedSqlBuilder sqlBuilder, SqlExecutor sqlExecutor) {
        this(new JpaTableMapperFactory(), sqlBuilder, sqlExecutor, new DefaultResultSetCollector());
    }

    @Override
    public <T> ResultBuilder<T> getEntityResultBuilder(QuerySpecification spec, Class<T> type) {
        return new EntityResultBuilderImpl<>(spec, type);
    }

    @Override
    public ResultBuilder<Tuple> getTupleResultBuilder(QuerySpecification spec, Class<?> type) {
        return new ObjectResultBuilderImpl(spec, type);
    }

    @Override
    public <T, R> ResultBuilder<R> getProjectionResultBuilder(QuerySpecification spec, Class<T> type, Class<R> projectionType) {
        return new ProjectionResultBuilder<>(
                this, spec, type, projectionType, ProjectionMetaProvider.DEFAULT);
    }

    private abstract class ResultBuilderImpl<T> implements ResultBuilder<T> {

        final QuerySpecification spec;
        final Class<T> type;

        private ResultBuilderImpl(QuerySpecification spec, Class<T> type) {
            this.spec = spec;
            this.type = type;
        }

        @Override
        public int count() {
            PreparedSql sql = sqlBuilder.count(spec, type, mappers);
            return sqlExecutor.query(sql.getSql(), sql.getArgs(), collector::collectCountResult);
        }

        @Override
        public boolean exist(int offset) {
            PreparedSql sql = sqlBuilder.exist(offset, spec, type, mappers);
            return sqlExecutor.query(sql.getSql(), sql.getArgs(), collector::collectExistResult);
        }
    }

    private class EntityResultBuilderImpl<T> extends ResultBuilderImpl<T> {

        private EntityResultBuilderImpl(QuerySpecification spec, Class<T> type) {
            super(spec, type);
        }

        @Override
        public List<T> getList(int offset, int maxResult, LockModeType lockModeType) {
            SelectedPreparedSql sql = sqlBuilder.getEntityList(offset, maxResult, lockModeType, spec, type, mappers);
            return sqlExecutor.query(sql.getSql(), sql.getArgs(),
                    resultSet -> collector.collectEntityResult(resultSet, sql.getSelectedPath(), type, mappers));
        }

    }

    private class ObjectResultBuilderImpl extends ResultBuilderImpl<Tuple> {

        private ObjectResultBuilderImpl(QuerySpecification spec, Class<?> type) {
            super(spec, TypeCastUtil.cast(type));
        }

        @Override
        public List<Tuple> getList(int offset, int maxResult, LockModeType lockModeType) {
            PreparedSql sql = sqlBuilder.getObjectsList(offset, maxResult, lockModeType, spec, type, mappers);
            return sqlExecutor.query(sql.getSql(), sql.getArgs(), collector::collectObjectResult);
        }
    }

}
