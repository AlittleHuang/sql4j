package github.alittlehuang.sql4j.jdbc.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface ColumnMapper {

    String getFieldName();

    Method getGetter();

    Method getSetter();

    Field getField();

    Class<?> getJavaType();

    void setValue(Object entity, Object object);

    Object getValue(Object entity);

    boolean isInsertable();

    boolean isUpdatable();


}
