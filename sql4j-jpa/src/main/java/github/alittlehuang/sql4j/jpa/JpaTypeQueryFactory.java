package github.alittlehuang.sql4j.jpa;

import github.alittlehuang.sql4j.dsl.builder.ResultBuilder;
import github.alittlehuang.sql4j.dsl.support.QuerySpecification;
import github.alittlehuang.sql4j.dsl.support.ResultBuilderFactory;
import github.alittlehuang.sql4j.dsl.support.builder.projection.ProjectionResultBuilder;
import github.alittlehuang.sql4j.dsl.support.builder.projection.meta.ProjectionMetaProvider;
import github.alittlehuang.sql4j.dsl.util.Tuple;
import jakarta.persistence.EntityManager;

public class JpaTypeQueryFactory implements ResultBuilderFactory {

    private final EntityManager entityManager;
    private final ProjectionMetaProvider metaProvider;

    public JpaTypeQueryFactory(EntityManager entityManager) {
        this(entityManager, ProjectionMetaProvider.DEFAULT);
    }

    public JpaTypeQueryFactory(EntityManager entityManager, ProjectionMetaProvider metaProvider) {
        this.entityManager = entityManager;
        this.metaProvider = metaProvider;
    }

    @Override
    public <T> ResultBuilder<T> getEntityResultBuilder(QuerySpecification spec, Class<T> type) {
        return new JpaEntityResultBuilder<>(entityManager, type, spec);
    }

    @Override
    public <T, R> ResultBuilder<R> getProjectionResultBuilder(QuerySpecification spec, Class<T> type, Class<R> projectionType) {
        return new ProjectionResultBuilder<>(this, spec, type, projectionType, metaProvider);
    }

    @Override
    public ResultBuilder<Tuple> getTupleResultBuilder(QuerySpecification spec, Class<?> type) {
        return new JpaObjectsResultBuilder<>(entityManager, type, spec);
    }


}
