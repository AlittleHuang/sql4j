package github.alittlehuang.sql4j.jdbc.sql;


import github.alittlehuang.sql4j.dsl.builder.LockModeType;
import github.alittlehuang.sql4j.dsl.builder.ResultBuilder;
import github.alittlehuang.sql4j.dsl.util.Tuple;

import java.util.List;

public class JdbcObjectsResultBuilder implements ResultBuilder<Tuple> {

    private final PreparedSqlExecutor executor;
    private final PreparedSqlBuilder builder;
    private final Class<?> entityType;

    public JdbcObjectsResultBuilder(PreparedSqlExecutor executor, PreparedSqlBuilder builder, Class<?> entityType) {
        this.executor = executor;
        this.builder = builder;
        this.entityType = entityType;
    }

    @Override
    public List<Tuple> getList(int offset, int maxResult, LockModeType lockModeType) {
        return executor.listResult(builder.getObjectsList(offset, maxResult, lockModeType), entityType);
    }

    @Override
    public int count() {
        return executor.count(builder.count(), entityType);
    }

    @Override
    public boolean exist(int offset) {
        return executor.exist(builder.exist(offset), entityType);
    }

}
