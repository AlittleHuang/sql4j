package github.alittlehuang.sql4j.jdbc.sql;

import github.alittlehuang.sql4j.jdbc.util.JdbcUtil;
import lombok.SneakyThrows;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public interface SqlExecutor {

    <T> T execute(ConnectionCallback<T> connectionCallback) throws SQLException;

    static SqlExecutor fromDatasource(DataSource dataSource) {
        return fromConnectionSupplier(dataSource::getConnection);
    }

    static SqlExecutor fromConnectionSupplier(ConnectionProvider supplier) {
        return new SqlExecutor() {
            public <T> T execute(ConnectionCallback<T> connectionCallback) throws SQLException {
                try (Connection connection = supplier.getConnection()) {
                    return connectionCallback.doInConnection(connection);
                }
            }
        };
    }

    @SneakyThrows
    default int update(String sql, Object[] args) {
        SqlLogger.traceSql(sql, args);
        return execute(connection -> {
            PreparedStatement pst = connection.prepareStatement(sql);
            JdbcUtil.setParam(pst, args);
            return pst.executeUpdate();
        });
    }

    @SneakyThrows
    default int[] batchUpdate(String sql, List<Object[]> batchArgs) {
        SqlLogger.traceSql(sql, batchArgs);
        return execute(connection -> {
            PreparedStatement pst = connection.prepareStatement(sql);
            JdbcUtil.setParamBatch(pst, batchArgs);
            return pst.executeBatch();
        });
    }

    @SneakyThrows
    default <T> T query(String sql,
                        Object[] args,
                        ResultSetCallback<T> resultSetCallback) {
        SqlLogger.traceSql(sql, args);
        return execute(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql);
            JdbcUtil.setParam(statement, args);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSetCallback.doInResultSet(resultSet);
            }
        });
    }

    @SneakyThrows
    default <T> T insertAndReturnGeneratedKeys(String sql,
                                               Object[] args,
                                               ResultSetCallback<T> resultSetCallback) {
        SqlLogger.traceSql(sql, args);
        return execute(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            JdbcUtil.setParam(ps, args);
            ps.execute();
            try (ResultSet resultSet = ps.getGeneratedKeys()) {
                return resultSetCallback.doInResultSet(resultSet);
            }
        });
    }

    @SneakyThrows
    default <T> T batchInsertAndReturnGeneratedKeys(String sql,
                                                    List<Object[]> batchArgs,
                                                    ResultSetCallback<T> resultSetCallback) {
        SqlLogger.traceSql(sql, batchArgs);
        return execute(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            JdbcUtil.setParamBatch(ps, batchArgs);
            ps.executeBatch();
            try (ResultSet resultSet = ps.getGeneratedKeys()) {
                return resultSetCallback.doInResultSet(resultSet);
            }
        });
    }

    @FunctionalInterface
    interface ConnectionCallback<T> {
        T doInConnection(Connection connection) throws SQLException;
    }

    @FunctionalInterface
    interface ResultSetCallback<T> {
        T doInResultSet(ResultSet connection) throws SQLException;
    }

}
