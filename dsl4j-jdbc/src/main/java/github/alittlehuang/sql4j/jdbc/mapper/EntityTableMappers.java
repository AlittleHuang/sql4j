package github.alittlehuang.sql4j.jdbc.mapper;

public interface EntityTableMappers {
    <T> EntityTableMapper<T> getMapper(Class<T> clazz);

}
