package github.alittlehuang.sql4j.jdbc.mapper.jpa;

import github.alittlehuang.sql4j.jdbc.mapper.AttributeColumnMapper;
import github.alittlehuang.sql4j.jdbc.mapper.MappedColumn;
import github.alittlehuang.sql4j.jdbc.mapper.MappedJoinColumn;
import jakarta.persistence.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;

/**
 * @author ALittleHuang
 */
public class Attribute implements AttributeColumnMapper {

    public static final String FIX = "`";

    private final Field field;
    private final Method getter;
    private final Method setter;
    private final Class<?> entityType;
    private final ManyToOne manyToOne;
    private final OneToMany oneToMany;
    private final JoinColumn joinColumn;
    private final Version version;
    private final Column column;
    private final ManyToMany manyToMany;
    private final OneToOne oneToOne;
    private final String columnName;
    private final Class<?> javaType;
    private final boolean collection;

    public Attribute(Field field, Method getter, Method setter, Class<?> entityType) {
        this.field = field;
        this.getter = getter;
        this.setter = setter;
        this.entityType = entityType;
        this.manyToOne = getAnnotation(ManyToOne.class);
        this.oneToMany = getAnnotation(OneToMany.class);
        this.joinColumn = getAnnotation(JoinColumn.class);
        this.version = getAnnotation(Version.class);
        this.column = getAnnotation(Column.class);
        this.manyToMany = getAnnotation(ManyToMany.class);
        this.oneToOne = getAnnotation(OneToOne.class);
        this.columnName = initColumnName();
        this.collection = Iterable.class.isAssignableFrom(field.getType());
        this.javaType = initJavaType();
    }

    public boolean isBasic() {
        return manyToOne == null
               && oneToMany == null
               && manyToMany == null
               && oneToOne == null
               && !collection;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        if (field != null) {
            T annotation = field.getAnnotation(annotationClass);
            if (annotation != null) {
                return annotation;
            }
        }
        if (getter != null) {
            return getter.getAnnotation(annotationClass);
        }
        return null;
    }

    public void setValue(Object entity, Object value) {
        boolean accessible = field.canAccess(entity);
        try {
            if (setter != null) {
                if (value != null || !setter.getParameterTypes()[0].isPrimitive()) {
                    setter.invoke(entity, value);
                }
            } else {
                if (value != null || !field.getType().isPrimitive()) {
                    if (!accessible) {
                        field.setAccessible(true);
                    }
                    field.set(entity, value);
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        } finally {
            field.setAccessible(accessible);
        }
    }

    public Object getValue(Object entity) {
        boolean accessible = field.canAccess(entity);
        try {
            Object result;
            if (getter != null) {
                result = getter.invoke(entity);
            } else {
                if (!accessible) {
                    field.setAccessible(true);
                }
                result = field.get(entity);
            }
            return result;
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            field.setAccessible(accessible);
        }
        throw new RuntimeException();
    }

    public String initColumnName() {
        Column column = getAnnotation(Column.class);
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

    @Override
    public String getFieldName() {
        return field.getName();
    }

    private Class<?> initJavaType() {
        Class<?> javaType = null;
        Class<?> fieldType = field.getType();
        if (collection) {
            Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType parameterizedType) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                if (actualTypeArguments.length == 1) {
                    Type actualTypeArgument = actualTypeArguments[0];
                    if (actualTypeArgument instanceof Class) {
                        javaType = (Class<?>) actualTypeArgument;
                    }
                }
            }
        } else {
            javaType = fieldType;
        }
        if (javaType == null) {
            throw new RuntimeException("field " + field + " unspecified type in " + entityType);
        }
        return javaType;
    }

    public Class<?> getEntityType() {
        return entityType;
    }

    public Field getField() {
        return field;
    }

    public ManyToOne getManyToOne() {
        return manyToOne;
    }

    public OneToMany getOneToMany() {
        return oneToMany;
    }

    public MappedColumn getColumn() {
        return MappedColumnImpl.wrap(column);
    }

    public Version getVersion() {
        return version;
    }

    public MappedJoinColumn getJoinColumn() {
        return MappedJoinColumnImpl.wrap(joinColumn);
    }

    public ManyToMany getManyToMany() {
        return manyToMany;
    }

    public OneToOne getOneToOne() {
        return oneToOne;
    }

    public boolean isEntityType() {
        return getJavaType().getAnnotation(Entity.class) != null;
    }

    @Override
    public String getColumnName() {
        return columnName;
    }

    public boolean isCollection() {
        return collection;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public Method getSetter() {
        return setter;
    }

    public Method getGetter() {
        return getter;
    }

}
