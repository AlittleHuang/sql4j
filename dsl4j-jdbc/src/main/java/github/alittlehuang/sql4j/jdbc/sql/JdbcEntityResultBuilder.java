package github.alittlehuang.sql4j.jdbc.sql;


import github.alittlehuang.sql4j.dsl.builder.LockModeType;
import github.alittlehuang.sql4j.dsl.builder.ResultBuilder;

import java.util.List;

public class JdbcEntityResultBuilder<T> implements ResultBuilder<T> {

    private final PreparedSqlExecutor executor;
    private final PreparedSqlBuilder builder;
    private final Class<T> entityType;

    public JdbcEntityResultBuilder(PreparedSqlExecutor executor, PreparedSqlBuilder builder, Class<T> entityType) {
        this.executor = executor;
        this.builder = builder;
        this.entityType = entityType;
    }

    @Override
    public int count() {
        return executor.count(builder.count(), entityType);
    }

    @Override
    public List<T> getList(int offset, int maxResult, LockModeType lockModeType) {
        return executor.getEntityList(builder.getEntityList(offset, maxResult, lockModeType), entityType);
    }

    @Override
    public boolean exist(int offset) {
        return executor.exist(builder.exist(offset), entityType);
    }

}
