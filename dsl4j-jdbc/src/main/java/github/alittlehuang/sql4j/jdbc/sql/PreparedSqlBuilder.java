package github.alittlehuang.sql4j.jdbc.sql;


import github.alittlehuang.sql4j.dsl.builder.LockModeType;

public interface PreparedSqlBuilder {

    SelectedPreparedSql getEntityList(int offset, int maxResultant, LockModeType lockModeType);

    PreparedSql getObjectsList(int offset, int maxResultant, LockModeType lockModeType);

    PreparedSql exist(int offset);

    PreparedSql count();

}
