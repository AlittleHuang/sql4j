package github.alittlehuang.sql4j.jdbc.mapper.model;

import github.alittlehuang.sql4j.jdbc.mapper.BasicColumnMapper;
import github.alittlehuang.sql4j.jdbc.mapper.ColumnMapper;
import github.alittlehuang.sql4j.jdbc.mapper.TableMapper;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SimpleTableMapper implements TableMapper {

    private Class<?> javaType;

    private String tableName;

    private BasicColumnMapper idColumnMapper;

    private Map<String, ColumnMapper> attributeNameMapper;

    private List<BasicColumnMapper> basicColumnMappers;

    @Override
    public ColumnMapper getMapperByAttributeName(String s) {
        ColumnMapper mapper = attributeNameMapper.get(s);
        if (mapper == null) {
            throw new NullPointerException(s + " not exist");
        }
        return mapper;
    }

}
