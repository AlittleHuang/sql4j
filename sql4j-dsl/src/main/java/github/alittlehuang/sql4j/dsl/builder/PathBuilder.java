package github.alittlehuang.sql4j.dsl.builder;

import github.alittlehuang.sql4j.dsl.expression.path.*;

public interface PathBuilder<T, U, BUILDER> {

    <R extends Persistable> PathBuilder<T, R, BUILDER> to(EntityGetter<U, R> column);

    <R extends Number & Comparable<?>> NumberOperator<T, R, BUILDER> to(NumberGetter<U, R> column);

    <R extends Comparable<?>> ComparableOperator<T, R, BUILDER> to(ComparableGetter<U, R> column);

    <R extends Comparable<?>> PredicateOperator<T, R, BUILDER> to(ColumnGetter<U, R> attribute);

    StringOperator<T, BUILDER> to(StringGetter<U> column);


}
