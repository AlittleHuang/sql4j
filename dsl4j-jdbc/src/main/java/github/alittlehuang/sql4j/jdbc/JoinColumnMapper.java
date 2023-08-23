package github.alittlehuang.sql4j.jdbc;

public interface JoinColumnMapper extends ColumnMapper {

    String getJoinColumnName();

    String getJoinColumnReferencedName();

}
