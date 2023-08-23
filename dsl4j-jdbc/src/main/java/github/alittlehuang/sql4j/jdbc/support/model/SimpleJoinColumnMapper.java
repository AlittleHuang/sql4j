package github.alittlehuang.sql4j.jdbc.support.model;

import github.alittlehuang.sql4j.jdbc.JoinColumnMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SimpleJoinColumnMapper extends SimpleColumnMapper implements JoinColumnMapper {

    private String joinColumnName;
    private String JoinColumnReferencedName;

}
