package github.alittlehuang.sql4j.jdbc.mapper;

import java.util.List;

public interface TableMapper {

    BasicColumnMapper getIdColumnMapper();

    String getTableName();

    ColumnMapper getMapperByAttributeName(String s);

    List<BasicColumnMapper> getBasicColumnMappers();

    Class<?> getJavaType();

}
