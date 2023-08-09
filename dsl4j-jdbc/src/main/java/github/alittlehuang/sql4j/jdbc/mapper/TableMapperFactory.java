package github.alittlehuang.sql4j.jdbc.mapper;

public interface TableMapperFactory {

    TableMapper getMapper(Class<?> type);

}
