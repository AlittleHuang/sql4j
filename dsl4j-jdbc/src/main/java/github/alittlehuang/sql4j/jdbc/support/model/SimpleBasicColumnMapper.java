package github.alittlehuang.sql4j.jdbc.support.model;

import github.alittlehuang.sql4j.jdbc.BasicColumnMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SimpleBasicColumnMapper extends SimpleColumnMapper implements BasicColumnMapper {

    private String columnName;

}
