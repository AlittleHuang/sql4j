package github.alittlehuang.sql4j.jdbc.support.model;

import github.alittlehuang.sql4j.jdbc.PreparedSql;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimplePreparedSql implements PreparedSql {
    private String sql;
    private List<Object> args;
}
