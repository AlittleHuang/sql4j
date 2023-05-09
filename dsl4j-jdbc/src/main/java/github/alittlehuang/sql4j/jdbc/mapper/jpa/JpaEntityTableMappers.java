package github.alittlehuang.sql4j.jdbc.mapper.jpa;

import github.alittlehuang.sql4j.dsl.util.TypeCastUtil;
import github.alittlehuang.sql4j.jdbc.mapper.EntityTableMapper;
import github.alittlehuang.sql4j.jdbc.mapper.EntityTableMappers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JpaEntityTableMappers implements EntityTableMappers {

    private static final JpaEntityTableMappers INSTANCE = new JpaEntityTableMappers();

    private final Map<Class<?>, EntityInformation<?>> cache = new ConcurrentHashMap<>();

    JpaEntityTableMappers() {
    }

    @Override
    public <T> EntityTableMapper<T> getMapper(Class<T> clazz) {
        EntityInformation<?> information = cache.computeIfAbsent(clazz, EntityInformation::new);
        return TypeCastUtil.cast(information);
    }

    public static JpaEntityTableMappers getInstance() {
        return INSTANCE;
    }

}
