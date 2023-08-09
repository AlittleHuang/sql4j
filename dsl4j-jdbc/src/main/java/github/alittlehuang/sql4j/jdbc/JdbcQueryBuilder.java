package github.alittlehuang.sql4j.jdbc;


import github.alittlehuang.sql4j.dsl.support.ResultBuilderFactory;
import github.alittlehuang.sql4j.dsl.support.builder.AbstractQueryBuilder;
import github.alittlehuang.sql4j.jdbc.mapper.jpa.JpaTableMapperFactory;
import github.alittlehuang.sql4j.jdbc.sql.*;

public class JdbcQueryBuilder extends AbstractQueryBuilder {


    public JdbcQueryBuilder(ResultBuilderFactory typeQueryFactory) {
        super(typeQueryFactory);
    }

    public JdbcQueryBuilder(PreparedSqlExecutor executor,
                            SqlBuilderFactory sqlBuilderFactory) {
        this(new JdbcQueryTypeQueryFactory(executor, sqlBuilderFactory));
    }


    public JdbcQueryBuilder(SqlExecutor sqlExecutor,
                            SqlBuilderFactory sqlBuilderFactory,
                            JpaTableMapperFactory mappers) {
        this(new SqlExecutorImpl(sqlExecutor, mappers), sqlBuilderFactory);
    }

}
