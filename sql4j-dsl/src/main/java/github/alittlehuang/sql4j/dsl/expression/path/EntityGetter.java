package github.alittlehuang.sql4j.dsl.expression.path;

import github.alittlehuang.sql4j.dsl.expression.PathExpression;

@FunctionalInterface
public interface EntityGetter<T, R extends Persistable> extends ColumnGetter<T, R> {

    default <V extends Persistable> EntityGetter<T, V> to(EntityGetter<R, V> reference) {
        return new EntityGetter<>() {
            @Override
            public V methodReference(T t) {
                throw new UnsupportedOperationException();
            }

            @Override
            public PathExpression expression() {
                return EntityGetter.this.expression().join(reference.expression());
            }
        };
    }

    default <V extends Number & Comparable<?>> NumberGetter<T, V> to(NumberGetter<R, V> reference) {
        return new NumberGetter<>() {
            @Override
            public V methodReference(T t) {
                throw new UnsupportedOperationException();
            }

            @Override
            public PathExpression expression() {
                return EntityGetter.this.expression().join(reference.expression());
            }
        };
    }

    default <V extends Comparable<?>> ComparableGetter<T, V> to(ComparableGetter<R, V> reference) {
        return new ComparableGetter<>() {
            @Override
            public V methodReference(T t) {
                throw new UnsupportedOperationException();
            }

            @Override
            public PathExpression expression() {
                return EntityGetter.this.expression().join(reference.expression());
            }
        };
    }

    default StringGetter<T> to(StringGetter<R> attribute) {
        return new StringGetter<>() {
            @Override
            public String methodReference(T t) {
                throw new UnsupportedOperationException();
            }

            @Override
            public PathExpression expression() {
                return EntityGetter.this.expression().join(attribute.expression());
            }
        };
    }

    default BooleanGetter<T> to(BooleanGetter<R> attribute) {
        return new BooleanGetter<>() {
            @Override
            public Boolean methodReference(T t) {
                throw new UnsupportedOperationException();
            }

            @Override
            public PathExpression expression() {
                return EntityGetter.this.expression().join(attribute.expression());
            }
        };
    }

    default <V> ColumnGetter<T, V> to(ColumnGetter<R, V> reference) {
        return new ColumnGetter<>() {
            @Override
            public V methodReference(T t) {
                throw new UnsupportedOperationException();
            }

            @Override
            public PathExpression expression() {
                return EntityGetter.this.expression().join(reference.expression());
            }
        };
    }

}
