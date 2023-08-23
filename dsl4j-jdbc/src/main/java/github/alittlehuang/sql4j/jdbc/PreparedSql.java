package github.alittlehuang.sql4j.jdbc;

import java.util.List;

public interface PreparedSql {

    String getSql();

    List<Object> getArgs();

}
