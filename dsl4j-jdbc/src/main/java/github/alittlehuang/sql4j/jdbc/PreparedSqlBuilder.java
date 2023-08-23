package github.alittlehuang.sql4j.jdbc;

import github.alittlehuang.sql4j.dsl.builder.LockModeType;
import github.alittlehuang.sql4j.dsl.support.QuerySpecification;

public interface PreparedSqlBuilder {

    SelectedPreparedSql getEntityList(int offset,
                                      int maxResultant,
                                      LockModeType lockModeType,
                                      QuerySpecification spec,
                                      Class<?> type,
                                      TableMapperFactory mappers);

    PreparedSql getObjectsList(int offset,
                               int maxResultant,
                               LockModeType lockModeType,
                               QuerySpecification spec,
                               Class<?> type,
                               TableMapperFactory mappers);

    PreparedSql exist(int offset,
                      QuerySpecification spec,
                      Class<?> type,
                      TableMapperFactory mappers);

    PreparedSql count(QuerySpecification spec,
                      Class<?> type,
                      TableMapperFactory mappers);


}
