package github.alittlehuang.sql4j.jdbc.mapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author ALittleHuang
 */
public interface AttributeColumnMapper {
    <T extends Annotation> T getAnnotation(Class<T> annotationClass);

    String getFieldName();

    MappedColumn getColumn();

    String getColumnName();

    Method getGetter();

    MappedJoinColumn getJoinColumn();

    Class<?> getJavaType();

    void setValue(Object entity, Object object);

    Object getValue(Object entity);
}
