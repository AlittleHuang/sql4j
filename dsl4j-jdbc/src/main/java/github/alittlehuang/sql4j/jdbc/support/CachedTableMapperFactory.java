package github.alittlehuang.sql4j.jdbc.support;

import github.alittlehuang.sql4j.jdbc.TableMapper;
import github.alittlehuang.sql4j.jdbc.TableMapperFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class CachedTableMapperFactory implements TableMapperFactory {

    private final Map<Class<?>, TableMapper> cache;

    protected CachedTableMapperFactory(Map<Class<?>, TableMapper> cache) {
        this.cache = cache;
    }

    public CachedTableMapperFactory() {
        this(new ConcurrentHashMap<>());
    }

    @Override
    public TableMapper getMapper(Class<?> type) {
        return cache.computeIfAbsent(type, this::createMapper);
    }

    protected abstract TableMapper createMapper(Class<?> aClass);

}
