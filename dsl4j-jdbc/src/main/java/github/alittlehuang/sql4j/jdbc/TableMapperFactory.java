package github.alittlehuang.sql4j.jdbc;

public interface TableMapperFactory {

    TableMapper getMapper(Class<?> type);

}
