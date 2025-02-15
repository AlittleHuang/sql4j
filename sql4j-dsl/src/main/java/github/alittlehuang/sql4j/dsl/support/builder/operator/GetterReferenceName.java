package github.alittlehuang.sql4j.dsl.support.builder.operator;

import github.alittlehuang.sql4j.dsl.expression.path.ColumnGetter;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class GetterReferenceName {

    private static final Map<Object, String> cache = new ConcurrentHashMap<>();

    public static String getPropertyName(ColumnGetter<?, ?> getterReference) {
        return cache.computeIfAbsent(getterReference, k -> getterNameToPropertyName(getMethodReferenceName(getterReference)));
    }

    public static String getterNameToPropertyName(String getterName) {
        StringBuilder builder = null;
        if (getterName != null) {
            if (getterName.length() > 3 && getterName.startsWith("get")) {
                builder = new StringBuilder(getterName.substring(3));
            } else if (getterName.length() > 2 && getterName.startsWith("is")) {
                builder = new StringBuilder(getterName.substring(2));
            }
        }
        Objects.requireNonNull(builder, "the function is not getters");
        if (builder.length() == 1) {
            return builder.toString().toLowerCase();
        }
        if (Character.isUpperCase(builder.charAt(1))) {
            return builder.toString();
        }
        builder.setCharAt(0, Character.toLowerCase(builder.charAt(0)));
        return builder.toString();
    }

    public static String getMethodReferenceName(Serializable getterReference) {
        try {
            Class<? extends Serializable> clazz = getterReference.getClass();
            Method method = clazz.getDeclaredMethod("writeReplace");
            method.setAccessible(Boolean.TRUE);
            SerializedLambda serializedLambda = (SerializedLambda) method.invoke(getterReference);
            return serializedLambda.getImplMethodName();
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
