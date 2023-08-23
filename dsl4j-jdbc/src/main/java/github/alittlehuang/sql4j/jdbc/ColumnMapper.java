package github.alittlehuang.sql4j.jdbc;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface ColumnMapper {

    String getFieldName();

    Method getGetter();

    Method getSetter();

    Field getField();

    Class<?> getJavaType();

    default void setValue(Object entity, Object object) throws InvocationTargetException, IllegalAccessException {
        getSetter().invoke(entity, object);
    }

    default Object getValue(Object entity) throws InvocationTargetException, IllegalAccessException {
        return getGetter().invoke(entity);
    }


}
