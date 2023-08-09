package github.alittlehuang.sql4j.jdbc.mapper.model;

import github.alittlehuang.sql4j.jdbc.mapper.ColumnMapper;
import lombok.Data;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Data
public class SimpleColumnMapper implements ColumnMapper {

    private String fieldName;
    private Field field;
    private Method getter;
    private Method setter;
    private Class<?> javaType;
    private boolean insertable;
    private boolean updatable;

    @SneakyThrows
    @Override
    public void setValue(Object entity, Object object) {
        setter.invoke(entity,object);
    }

    @SneakyThrows
    @Override
    public Object getValue(Object entity) {
        return getter.invoke(entity);
    }
}
