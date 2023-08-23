package github.alittlehuang.sql4j.dsl.support.builder;

import github.alittlehuang.sql4j.dsl.QueryBuilder;
import github.alittlehuang.sql4j.dsl.builder.Query;
import github.alittlehuang.sql4j.dsl.support.Configure;
import github.alittlehuang.sql4j.dsl.support.ResultBuilderFactory;
import lombok.Setter;

@Setter
public class AbstractQueryBuilder implements QueryBuilder {

    private final ResultBuilderFactory resultBuilderFactory;
    private Configure configure = Configure.DEFAULT;

    public AbstractQueryBuilder(ResultBuilderFactory resultBuilderFactory) {
        this.resultBuilderFactory = resultBuilderFactory;
    }

    @Override
    public <T> Query<T> query(Class<T> type) {
        QuerySupport<T> support = QuerySupport.of(type, resultBuilderFactory, configure);
        return new DefaultQuery<>(support);
    }

}
