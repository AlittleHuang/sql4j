package github.alittlehuang.sql4j.jdbc.support;

import github.alittlehuang.sql4j.jdbc.BasicColumnMapper;
import github.alittlehuang.sql4j.jdbc.ColumnMapper;
import github.alittlehuang.sql4j.jdbc.TableMapper;
import github.alittlehuang.sql4j.jdbc.support.model.SimpleBasicColumnMapper;
import github.alittlehuang.sql4j.jdbc.support.model.SimpleColumnMapper;
import github.alittlehuang.sql4j.jdbc.support.model.SimpleJoinColumnMapper;
import github.alittlehuang.sql4j.jdbc.support.model.SimpleTableMapper;
import github.alittlehuang.sql4j.jdbc.util.ReflectUtil;
import jakarta.persistence.*;
import org.jetbrains.annotations.NotNull;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JpaTableMapperFactory extends CachedTableMapperFactory {

    public static final String FIX = "`";

    public JpaTableMapperFactory() {
    }

    @Override
    public TableMapper createMapper(Class<?> type) {
        SimpleTableMapper tableMapper = new SimpleTableMapper();
        tableMapper.setJavaType(type);
        List<ColumnMapper> columnMappers = getColumnMappers(type);
        BasicColumnMapper idColumnMapper = null;
        List<BasicColumnMapper> basicColumnMappers = new ArrayList<>();
        Map<String, ColumnMapper> attributeNameMapper = new HashMap<>();

        for (ColumnMapper columnMapper : columnMappers) {
            if (columnMapper instanceof BasicColumnMapper bcm) {
                Id id = getAnnotation(columnMapper.getField(), columnMapper.getGetter(), Id.class);
                if (id != null) {
                    idColumnMapper = bcm;
                } else if (idColumnMapper == null && "id".equals(columnMapper.getFieldName())) {
                    idColumnMapper = bcm;
                }
            }
            if (columnMapper instanceof BasicColumnMapper basicColumnMapper) {
                basicColumnMappers.add(basicColumnMapper);
            }
            attributeNameMapper.put(columnMapper.getFieldName(), columnMapper);
        }
        tableMapper.setIdColumnMapper(idColumnMapper);
        tableMapper.setTableName(getTableName(type));
        tableMapper.setAttributeNameMapper(attributeNameMapper);
        tableMapper.setBasicColumnMappers(basicColumnMappers);

        return tableMapper;
    }

    private String getTableName(Class<?> javaType) {
        Table table = javaType.getAnnotation(Table.class);
        if (table != null && table.name().length() > 0) {
            return table.name();
        }
        Entity entity = javaType.getAnnotation(Entity.class);
        if (entity != null && entity.name().length() > 0) {
            return entity.name();
        }
        String tableName = javaType.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
        if (tableName.startsWith(FIX) && tableName.endsWith(FIX)) {
            tableName = tableName.substring(1, tableName.length() - 1);
        }
        return tableName;
    }

    private List<ColumnMapper> getColumnMappers(Class<?> javaType) {
        List<ColumnMapper> columnMappers = new ArrayList<>();
        Field[] fields = javaType.getDeclaredFields();
        Map<Field, Method> readerMap = new HashMap<>();
        Map<Field, Method> writeMap = new HashMap<>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(javaType);
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor descriptor : propertyDescriptors) {
                String propertyName = descriptor.getName();
                Field field = ReflectUtil.getDeclaredField(javaType, propertyName);
                if (field == null) {
                    continue;
                }
                readerMap.put(field, descriptor.getReadMethod());
                writeMap.put(field, descriptor.getWriteMethod());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (Field field : fields) {
            if (!readerMap.containsKey(field)) {
                readerMap.put(field, null);
            }
            if (!writeMap.containsKey(field)) {
                writeMap.put(field, null);
            }
        }

        for (Field field : writeMap.keySet()) {

            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers)) {
                continue;
            }

            if (field.getAnnotation(Transient.class) != null) {
                continue;
            }

            Method getter = readerMap.get(field);
            if (getter.getAnnotation(Transient.class) != null) {
                continue;
            }

            SimpleColumnMapper mapper;

            if (isBasicField(field, getter)) {
                SimpleBasicColumnMapper m = new SimpleBasicColumnMapper();
                Column column = getAnnotation(field, getter, Column.class);
                m.setColumnName(getColumnName(field, column));
                m.setInsertable(column == null || column.insertable());
                m.setUpdatable(column == null || column.updatable());
                mapper = m;
            } else {
                SimpleJoinColumnMapper m = new SimpleJoinColumnMapper();
                JoinColumn joinColumn = getAnnotation(field, getter, JoinColumn.class);
                if (joinColumn != null) {
                    m.setJoinColumnName(joinColumn.name());
                    m.setJoinColumnReferencedName(joinColumn.referencedColumnName());
                    m.setInsertable(joinColumn.insertable());
                    m.setUpdatable(joinColumn.updatable());
                }
                mapper = m;
            }
            mapper.setFieldName(field.getName());
            mapper.setGetter(getter);
            mapper.setSetter(writeMap.get(field));
            mapper.setJavaType(field.getType());
            mapper.setField(field);
            columnMappers.add(mapper);
        }
        return columnMappers;
    }

    private static final List<Class<? extends Annotation>> JOIN_ANNOTATIONS = List.of(ManyToOne.class, OneToMany.class, ManyToMany.class, OneToOne.class);


    private boolean isBasicField(Field field, Method getter) {
        for (Class<? extends Annotation> annotationClass : JOIN_ANNOTATIONS) {
            if (getAnnotation(field, getter, annotationClass) != null) {
                return false;
            }
        }
        return true;
    }


    @NotNull
    private static String getColumnName(Field field, Column column) {
        String columnName;
        if (column != null && column.name().length() != 0) {
            columnName = column.name();
        } else {
            columnName = field.getName().replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
        }
        if (columnName.startsWith(FIX) && columnName.endsWith(FIX)) {
            columnName = columnName.substring(1, columnName.length() - 1);
        }
        return columnName;
    }

    private <T extends Annotation> T getAnnotation(Field field, Method getter, Class<T> annotationClass) {
        T column = field.getAnnotation(annotationClass);
        if (column == null) {
            column = getter.getAnnotation(annotationClass);
        }
        return column;
    }

}
