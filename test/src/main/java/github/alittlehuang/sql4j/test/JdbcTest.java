package github.alittlehuang.sql4j.test;

import com.mysql.cj.jdbc.MysqlDataSource;
import github.alittlehuang.sql4j.dsl.QueryBuilder;
import github.alittlehuang.sql4j.jdbc.JdbcQueryBuilder;
import github.alittlehuang.sql4j.jdbc.mapper.jpa.JpaTableMapperFactory;
import github.alittlehuang.sql4j.jdbc.mysql.MysqlSqlBuilder;
import github.alittlehuang.sql4j.jdbc.sql.SqlExecutor;
import github.alittlehuang.sql4j.test.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


@Slf4j
public class JdbcTest extends JpaTest {

    @BeforeAll
    public static void init() {
        JpaTest.init();
        MysqlDataSource source = new MysqlDataSource();
        source.setUrl("jdbc:mysql:///sql-dsl");
        source.setUser("root");
        source.setPassword("root");
        JpaTableMapperFactory mappers = new JpaTableMapperFactory();
        QueryBuilder queryBuilder = new JdbcQueryBuilder(SqlExecutor.fromDatasource(source), (criteria, javaType) -> new MysqlSqlBuilder(criteria, javaType, mappers), mappers);
        userQuery = queryBuilder.query(User.class);
    }

    @Test
    @Override
    public void testGroupBy() {
        super.testGroupBy();
    }
}
