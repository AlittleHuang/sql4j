package github.alittlehuang.sql4j.dsl.support;

import github.alittlehuang.sql4j.dsl.builder.ResultBuilder;
import github.alittlehuang.sql4j.dsl.support.builder.projection.ProjectionResultBuilder;
import github.alittlehuang.sql4j.dsl.support.builder.projection.meta.ProjectionMetaProvider;
import github.alittlehuang.sql4j.dsl.util.Tuple;

public interface ResultBuilderFactory {

    <T> ResultBuilder<T> getEntityResultBuilder(QuerySpecification spec, Class<T> type);

    default <T, R> ResultBuilder<R> getProjectionResultBuilder(QuerySpecification spec,
                                                               Class<T> type,
                                                               Class<R> projectionType) {
        return new ProjectionResultBuilder<>(
                this, spec, type, projectionType, ProjectionMetaProvider.DEFAULT);
    }

    ResultBuilder<Tuple> getTupleResultBuilder(QuerySpecification spec, Class<?> type);

}
