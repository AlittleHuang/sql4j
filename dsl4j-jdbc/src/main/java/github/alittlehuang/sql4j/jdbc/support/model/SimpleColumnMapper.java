package github.alittlehuang.sql4j.jdbc.support.model;

import github.alittlehuang.sql4j.jdbc.ColumnMapper;
import lombok.Data;

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


}
