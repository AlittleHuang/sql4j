package github.alittlehuang.sql4j.jdbc.mapper;

import java.util.List;

/**
 * @author ALittleHuang
 */
public interface EntityTableMapper<T> {

    AttributeColumnMapper getIdAttribute();

    String getTableName();

    AttributeColumnMapper getAttribute(String s);

    List<? extends AttributeColumnMapper> getBasicAttributes();

    Class<T> getJavaType();

}
