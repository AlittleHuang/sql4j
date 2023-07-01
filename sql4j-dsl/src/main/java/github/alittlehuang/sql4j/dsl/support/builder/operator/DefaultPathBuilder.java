package github.alittlehuang.sql4j.dsl.support.builder.operator;

import github.alittlehuang.sql4j.dsl.builder.*;
import github.alittlehuang.sql4j.dsl.expression.Expression;
import github.alittlehuang.sql4j.dsl.expression.PathExpression;
import github.alittlehuang.sql4j.dsl.expression.path.*;

public class DefaultPathBuilder<T, U, BUILDER> implements PathBuilder<T, U, BUILDER> {

    private final DataAction<PathExpression, Expression, BUILDER> dataBuilder;

    public DefaultPathBuilder(DataAction<PathExpression, Expression, BUILDER> dataBuilder) {
        this.dataBuilder = dataBuilder;
    }

    @Override
    public <R extends Persistable> PathBuilder<T, R, BUILDER> to(EntityGetter<U, R> column) {
        String attributeName = GetterReferenceName.getPropertyName(column);
        var builder = dataBuilder.data(path -> path.to(attributeName));
        return new DefaultPathBuilder<>(builder);
    }

    @Override
    public <R extends Number & Comparable<?>> NumberOperator<T, R, BUILDER> to(NumberGetter<U, R> column) {
        String attributeName = GetterReferenceName.getPropertyName(column);
        var builder = dataBuilder.data(path -> path.to(attributeName));
        return new DefaultNumberOperator<>(builder);
    }

    @Override
    public <R extends Comparable<?>> ComparableOperator<T, R, BUILDER> to(ComparableGetter<U, R> column) {
        String attributeName = GetterReferenceName.getPropertyName(column);
        var builder = dataBuilder.data(path -> path.to(attributeName));
        return new DefaultComparableOperator<>(builder);
    }

    @Override
    public <R extends Comparable<?>> PredicateOperator<T, R, BUILDER> to(ColumnGetter<U, R> column) {
        String attributeName = GetterReferenceName.getPropertyName(column);
        var builder = dataBuilder.data(path -> path.to(attributeName));
        return new DefaultPredicateOperator<>(builder);
    }

    @Override
    public StringOperator<T, BUILDER> to(StringGetter<U> column) {
        String attributeName = GetterReferenceName.getPropertyName(column);
        var builder = dataBuilder.data(path -> path.to(attributeName));
        return new DefaultStringOperator<>(builder);
    }

}
