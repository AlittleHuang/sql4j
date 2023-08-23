package github.alittlehuang.sql4j.test;

import com.mysql.cj.jdbc.MysqlDataSource;
import github.alittlehuang.sql4j.dsl.QueryBuilder;
import github.alittlehuang.sql4j.dsl.support.builder.AbstractQueryBuilder;
import github.alittlehuang.sql4j.jdbc.JdbcResultBuilderFactory;
import github.alittlehuang.sql4j.jdbc.support.SqlExecutor;
import github.alittlehuang.sql4j.jdbc.support.mysql.MySqlSqlPreparedBuilder;
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
        SqlExecutor sqlExecutor = SqlExecutor.fromDatasource(source);
        JdbcResultBuilderFactory factory = new JdbcResultBuilderFactory(new MySqlSqlPreparedBuilder(), sqlExecutor);
        QueryBuilder queryBuilder = new AbstractQueryBuilder(factory);
        userQuery = queryBuilder.query(User.class);
    }

    @Test
    @Override
    public void testWhere() {
        super.testWhere();
    }
}
