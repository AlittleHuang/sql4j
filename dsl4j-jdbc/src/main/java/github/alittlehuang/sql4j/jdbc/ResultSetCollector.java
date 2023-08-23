package github.alittlehuang.sql4j.jdbc;

import github.alittlehuang.sql4j.dsl.expression.PathExpression;
import github.alittlehuang.sql4j.dsl.util.Tuple;

import java.sql.ResultSet;
import java.util.List;

public interface ResultSetCollector {

    int collectCountResult(ResultSet resultSet);

    <T> List<T> collectEntityResult(ResultSet resultSet, List<PathExpression> selectedPath, Class<T> type, TableMapperFactory mappers);

    boolean collectExistResult(ResultSet resultSet);

    List<Tuple> collectObjectResult(ResultSet resultSet);

}
