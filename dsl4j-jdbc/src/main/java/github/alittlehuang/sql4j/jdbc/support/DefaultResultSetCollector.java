package github.alittlehuang.sql4j.jdbc.support;

import github.alittlehuang.sql4j.dsl.expression.PathExpression;
import github.alittlehuang.sql4j.dsl.support.builder.operator.DefaultTuple;
import github.alittlehuang.sql4j.dsl.util.Tuple;
import github.alittlehuang.sql4j.jdbc.ColumnMapper;
import github.alittlehuang.sql4j.jdbc.ResultSetCollector;
import github.alittlehuang.sql4j.jdbc.TableMapperFactory;
import github.alittlehuang.sql4j.jdbc.util.JdbcUtil;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

public class DefaultResultSetCollector implements ResultSetCollector {

    @SneakyThrows
    @Override
    public int collectCountResult(ResultSet resultSet) {
        try (resultSet) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        return 0;
    }

    @SneakyThrows
    @Override
    public <T> List<T> collectEntityResult(ResultSet resultSet, List<PathExpression> selectedPath, Class<T> type, TableMapperFactory mappers) {
        try (resultSet) {
            List<T> result = new ArrayList<>();
            int columnsCount = resultSet.getMetaData().getColumnCount();
            while (resultSet.next()) {
                T row = mapRow(resultSet, selectedPath, type, mappers, columnsCount);
                result.add(row);
            }
            return result;
        }
    }

    @NotNull
    private static <T> T mapRow(ResultSet resultSet,
                                List<PathExpression> selectedPath,
                                Class<T> type,
                                TableMapperFactory mappers,
                                int columnsCount) throws Exception {
        T row = type.getConstructor().newInstance();
        int column = 0;
        while (column < columnsCount) {
            PathExpression path = selectedPath.get(column++);
            int size = path.length();
            var mapper = mappers.getMapper(type);
            Object entity = row;
            Object object = resultSet.getObject(column);
            if (object != null) {
                int i = -1;
                ColumnMapper attribute;
                while (++i < size - 1) {
                    attribute = mapper.getMapperByAttributeName(path.get(i));
                    Object next = attribute.getValue(entity);
                    if (next == null) {
                        next = attribute.getJavaType().getConstructor().newInstance();
                        attribute.setValue(entity, next);
                    }
                    entity = next;
                }
                attribute = mapper.getMapperByAttributeName(path.get(i));
                Class<?> javaType = attribute.getJavaType();
                if (JdbcUtil.getWrapedClass(javaType).isInstance(object)) {
                    attribute.setValue(entity, object);
                } else {
                    Object value = JdbcUtil.getValue(resultSet, column, javaType);
                    attribute.setValue(entity, value);
                }
            }
        }
        return row;
    }

    @Override
    @SneakyThrows
    public boolean collectExistResult(ResultSet resultSet) {
        try (resultSet) {
            return resultSet.next();
        }
    }

    @Override
    @SneakyThrows
    public List<Tuple> collectObjectResult(ResultSet resultSet) {
        try (resultSet) {
            List<Tuple> result = new ArrayList<>();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnsCount = metaData.getColumnCount();
            while (resultSet.next()) {
                Object[] row = new Object[columnsCount];
                int i = 0;
                while (i < columnsCount) {
                    row[i++] = resultSet.getObject(i);
                }
                result.add(new DefaultTuple(row));
            }
            return result;
        }
    }
}
