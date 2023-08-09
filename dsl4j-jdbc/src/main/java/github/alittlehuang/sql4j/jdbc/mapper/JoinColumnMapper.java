package github.alittlehuang.sql4j.jdbc.mapper;

public interface JoinColumnMapper extends ColumnMapper {

    String getJoinColumnName();

    String getJoinColumnReferencedName();

}
