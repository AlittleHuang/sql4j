package github.alittlehuang.sql4j.jdbc.support.mysql;

import github.alittlehuang.sql4j.dsl.builder.LockModeType;
import github.alittlehuang.sql4j.dsl.support.QuerySpecification;
import github.alittlehuang.sql4j.jdbc.PreparedSql;
import github.alittlehuang.sql4j.jdbc.PreparedSqlBuilder;
import github.alittlehuang.sql4j.jdbc.SelectedPreparedSql;
import github.alittlehuang.sql4j.jdbc.TableMapperFactory;

public class MySqlSqlPreparedBuilder implements PreparedSqlBuilder {

    @Override
    public SelectedPreparedSql getEntityList(int offset,
                                             int maxResultant,
                                             LockModeType lockModeType,
                                             QuerySpecification spec,
                                             Class<?> type,
                                             TableMapperFactory mappers) {
        return new SqlEditor(spec, type, mappers).getEntityList(offset, maxResultant, lockModeType);
    }

    @Override
    public PreparedSql getObjectsList(int offset, int maxResultant, LockModeType lockModeType, QuerySpecification spec, Class<?> type, TableMapperFactory mappers) {
        return new SqlEditor(spec, type, mappers).getObjectsList(offset, maxResultant, lockModeType);
    }

    @Override
    public PreparedSql exist(int offset, QuerySpecification spec, Class<?> type, TableMapperFactory mappers) {
        return new SqlEditor(spec, type, mappers).exist(offset);
    }

    @Override
    public PreparedSql count(QuerySpecification spec, Class<?> type, TableMapperFactory mappers) {
        return new SqlEditor(spec, type, mappers).count();
    }

}
