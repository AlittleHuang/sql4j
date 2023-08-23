package github.alittlehuang.sql4j.jdbc.support.model;

import github.alittlehuang.sql4j.dsl.expression.PathExpression;
import github.alittlehuang.sql4j.jdbc.SelectedPreparedSql;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleSelectedPreparedSql implements SelectedPreparedSql {
    private String sql;
    private List<Object> args;
    private List<PathExpression> selectedPath;
}
