package github.alittlehuang.sql4j.jdbc.support.mysql;

import github.alittlehuang.sql4j.dsl.builder.LockModeType;
import github.alittlehuang.sql4j.dsl.expression.*;
import github.alittlehuang.sql4j.dsl.support.QuerySpecification;
import github.alittlehuang.sql4j.dsl.util.Array;
import github.alittlehuang.sql4j.jdbc.*;
import github.alittlehuang.sql4j.jdbc.support.JdbcOperator;
import github.alittlehuang.sql4j.jdbc.support.model.SimplePreparedSql;
import github.alittlehuang.sql4j.jdbc.support.model.SimpleSelectedPreparedSql;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static github.alittlehuang.sql4j.dsl.expression.Operator.AND;

class SqlEditor {


    public static final String NONE_DELIMITER = "";
    public static final String DELIMITER = ",";
    public static final String FOR_SHARE = " for share";
    public static final String FOR_UPDATE = " for update";
    public static final String FOR_UPDATE_NOWAIT = " for update nowait";
    public static final String SELECT = "select ";
    public static final String FROM = "from ";
    public static final String WHERE = " where ";

    protected final StringBuilder sql = new StringBuilder();
    protected final List<Object> args = new ArrayList<>();
    protected final Map<PathExpression, Integer> joins = new LinkedHashMap<>();
    protected final QuerySpecification spec;
    protected final TableMapper tableMapper;
    protected final TableMapperFactory mappers;

    public SqlEditor(QuerySpecification spec, Class<?> type, TableMapperFactory mappers) {
        this.spec = spec;
        this.mappers = mappers;
        this.tableMapper = mappers.getMapper(type);
    }

    protected SelectedPreparedSql getEntityList(int offset, int maxResult, LockModeType lockModeType) {
        ArrayList<PathExpression> selectedPath = new ArrayList<>();
        sql.append(SELECT);
        appendEntityPath(selectedPath);
        appendFetchPath(selectedPath);
        appendQueryConditions(offset, maxResult);
        appendLockModeType(lockModeType);
        return new SimpleSelectedPreparedSql(sql.toString(), args, selectedPath);
    }

    protected void appendEntityPath(List<PathExpression> selectedPath) {
        String join = NONE_DELIMITER;
        for (BasicColumnMapper basicAttribute : tableMapper.getBasicColumnMappers()) {
            sql.append(join);
            PathExpression path = new PathExpression(basicAttribute.getFieldName());
            appendPath(path);
            selectedPath.add(path);
            join = DELIMITER;
        }
    }

    protected void appendFetchPath(List<PathExpression> selectedPath) {
        Array<PathExpression> fetchClause = spec.fetchClause();
        if (fetchClause != null) {
            for (PathExpression fetch : fetchClause) {
                ColumnMapper attribute = getAttribute(fetch);
                TableMapper entityInfo = mappers.getMapper(attribute.getJavaType());
                for (BasicColumnMapper basicAttribute : entityInfo.getBasicColumnMappers()) {
                    sql.append(",");
                    PathExpression path = fetch.to(basicAttribute.getFieldName());
                    appendPath(path);
                    selectedPath.add(path);
                }
            }
        }
    }


    protected PreparedSql getObjectsList(int offset, int maxResult, LockModeType lockModeType) {
        sql.append(SELECT);
        appendSelectedPath();
        appendBlank().append(FROM).append("`")
                .append(tableMapper.getTableName())
                .append("` ");
        appendRootTableAlias();
        int sqlIndex = sql.length();
        appendWhere();
        appendGroupBy();
        appendOrderBy();
        limit(offset, maxResult);
        insertJoin(sqlIndex);
        appendLockModeType(lockModeType);
        return newPreparedSql();
    }

    @NotNull
    private PreparedSql newPreparedSql() {
        return new SimplePreparedSql(sql.toString(), args);
    }

    protected void appendLockModeType(LockModeType lockModeType) {
        if (lockModeType == LockModeType.PESSIMISTIC_READ) {
            sql.append(FOR_SHARE);
        } else if (lockModeType == LockModeType.PESSIMISTIC_WRITE) {
            sql.append(FOR_UPDATE);
        } else if (lockModeType == LockModeType.PESSIMISTIC_FORCE_INCREMENT) {
            sql.append(FOR_UPDATE_NOWAIT);
        }
    }

    protected void appendQueryConditions(int offset, int maxResult) {
        appendBlank()
                .append(FROM + "`")
                .append(tableMapper.getTableName())
                .append("` ");
        appendRootTableAlias();
        int sqlIndex = sql.length();
        appendWhere();
        appendOrderBy();
        limit(offset, maxResult);
        insertJoin(sqlIndex);
    }

    protected PreparedSql exist(int offset) {
        sql.append(SELECT);
        BasicColumnMapper attribute = tableMapper.getIdColumnMapper();
        appendRootTableAlias();
        sql.append(".").append(attribute.getColumnName());
        appendBlank().append(FROM)
                .append("`").append(tableMapper.getTableName()).append("` ");
        appendRootTableAlias();
        int sqlIndex = sql.length();
        appendWhere();
        limit(offset, 1);
        insertJoin(sqlIndex);
        return newPreparedSql();
    }

    protected PreparedSql count() {
        sql.append("select count(");
        BasicColumnMapper attribute = tableMapper.getIdColumnMapper();
        appendRootTableAlias();
        sql.append(".").append(attribute.getColumnName()).append(")");
        appendBlank().append(FROM)
                .append("`")
                .append(tableMapper.getTableName())
                .append("` ");
        appendRootTableAlias();
        int sqlIndex = sql.length();
        appendWhere();
        insertJoin(sqlIndex);
        return newPreparedSql();
    }

    protected StringBuilder appendRootTableAlias() {
        return appendRootTableAlias(sql);
    }

    protected StringBuilder appendRootTableAlias(StringBuilder sql) {
        String table = tableMapper.getTableName();
        return sql.append(table, 0, 1);
    }

    protected StringBuilder appendTableAlias(String table, Object index, StringBuilder sql) {
        return appendBlank(sql).append(table, 0, 1).append(index);
    }

    protected StringBuilder appendBlank() {
        return appendBlank(sql);
    }

    protected StringBuilder appendBlank(StringBuilder sql) {
        return sql.length() == 0 || " (,+-*/=><".indexOf(sql.charAt(sql.length() - 1)) >= 0 ? sql : sql.append(' ');
    }


    protected void appendWhere() {
        if (spec.whereClause() == null || ConstantExpression.TRUE.equals(spec.whereClause())) {
            return;
        }
        sql.append(WHERE);
        appendExpression(spec.whereClause());
    }

    protected void appendExpression(Expression e) {
        appendExpressions(args, e);
    }


    protected void appendExpressions(List<Object> args, Expression e) {
        if (e instanceof ConstantExpression ce) {
            Object value = ce.value();
            boolean isNumber = false;
            if (value != null) {
                Class<?> valueType = value.getClass();
                if (valueType.isPrimitive() || Number.class.isAssignableFrom(valueType)) {
                    isNumber = true;
                }
            }
            if (isNumber) {
                appendBlank().append(value);
            } else {
                appendBlank().append('?');
                args.add(value);
            }
        } else if (e instanceof PathExpression pe) {
            appendBlank();
            appendPath(pe);
        } else if (e instanceof OperatorExpression oe) {
            Operator operator = oe.operator();
            Array<Expression> list = oe.expressions();
            Expression e0 = list.get(0);
            Operator operator0 = getOperator(e0);
            JdbcOperator jdbcOperator = JdbcOperator.of(operator);
            // noinspection EnhancedSwitchMigration
            switch (operator) {
                case NOT:
                    appendOperator(jdbcOperator);
                    sql.append(' ');
                    if (operator0 != null && JdbcOperator.of(operator0).getPrecedence()
                                             > jdbcOperator.getPrecedence()) {
                        sql.append('(');
                        appendExpressions(args, e0);
                        sql.append(')');
                    } else {
                        appendExpressions(args, e0);
                    }
                    break;
                case AND:
                case OR:
                case LIKE:
                case MOD:
                case GT:
                case EQ:
                case NE:
                case GE:
                case LT:
                case LE:
                case ADD:
                case SUBTRACT:
                case MULTIPLY:
                case DIVIDE:
                    appendBlank();
                    if (operator0 != null && JdbcOperator.of(operator0).getPrecedence()
                                             > jdbcOperator.getPrecedence()) {
                        sql.append('(');
                        appendExpressions(args, e0);
                        sql.append(')');
                    } else {
                        appendExpressions(args, e0);
                    }
                    for (int i = 1; i < list.length(); i++) {
                        appendOperator(jdbcOperator);
                        Expression e1 = list.get(i);
                        Operator operator1 = getOperator(e1);
                        if (operator1 != null && JdbcOperator.of(operator1).getPrecedence()
                                                 >= jdbcOperator.getPrecedence()) {
                            sql.append('(');
                            appendExpressions(args, e1);
                            sql.append(')');
                        } else {
                            appendExpressions(args, e1);
                        }
                    }
                    break;
                case LOWER:
                case UPPER:
                case SUBSTRING:
                case TRIM:
                case LENGTH:
                case NULLIF:
                case IF_NULL:
                case ISNULL:
                case MIN:
                case MAX:
                case COUNT:
                case AVG:
                case SUM: {
                    appendOperator(jdbcOperator);
                    String join = "(";
                    for (Expression expression : list) {
                        sql.append(join);
                        appendExpressions(args, expression);
                        join = ",";
                    }
                    sql.append(")");
                    break;
                }
                case IN: {
                    if (list.length() == 1) {
                        appendBlank().append(0);
                    } else {
                        appendBlank();
                        appendExpression(e0);
                        appendOperator(jdbcOperator);
                        char join = '(';
                        for (int i = 1; i < list.length(); i++) {
                            Expression expression = list.get(i);
                            sql.append(join);
                            appendExpressions(args, expression);
                            join = ',';
                        }
                        sql.append(")");
                    }
                    break;
                }
                case BETWEEN:
                    appendBlank();
                    appendExpressions(args, list.get(0));
                    appendOperator(jdbcOperator);
                    appendBlank();
                    appendExpressions(args, list.get(1).operate(AND, list.get(2)));
                    break;
                default:
                    throw new UnsupportedOperationException("unknown operator " + operator);
            }
        } else {
            throw new UnsupportedOperationException("unknown expression type " + e.getClass());
        }
    }

    private void appendOperator(JdbcOperator jdbcOperator) {
        String sign = jdbcOperator.getSign();
        if (Character.isLetter(sign.charAt(0))) {
            appendBlank();
        }
        sql.append(sign);
    }


    protected void appendPath(PathExpression expression) {
        StringBuilder sb = sql;
        int iMax = expression.length() - 1;
        if (iMax == -1)
            return;
        int i = 0;
        if (expression.length() == 1) {
            appendRootTableAlias().append(".");
        }
        Class<?> type = tableMapper.getJavaType();

        PathExpression join = new PathExpression(expression.get(0));

        for (String path : expression.path()) {
            TableMapper info = mappers.getMapper(type);
            ColumnMapper attribute = info.getMapperByAttributeName(path);
            if (i++ == iMax) {
                if (attribute instanceof JoinColumnMapper joinColumnMapper) {
                    sb.append(joinColumnMapper.getJoinColumnName());
                } else if (attribute instanceof BasicColumnMapper basicColumnMapper) {
                    sb.append(basicColumnMapper.getColumnName());
                } else {
                    throw new IllegalStateException();
                }
                return;
            } else {
                joins.putIfAbsent(join, joins.size());
                if (i == iMax) {
                    Integer index = joins.get(join);
                    appendTableAttribute(sb, attribute, index).append('.');
                }
            }
            type = attribute.getJavaType();
            join = join.to(path);
        }
    }

    protected void insertJoin(int sqlIndex) {
        StringBuilder sql = new StringBuilder();

        joins.forEach((k, v) -> {
            ColumnMapper attribute = getAttribute(k);
            TableMapper entityInfo = mappers.getMapper(attribute.getJavaType());
            sql.append(" left join `").append(entityInfo.getTableName()).append("`");

            appendTableAttribute(sql, attribute, v);
            sql.append(" on ");
            PathExpression parent = k.parent();
            if (parent == null) {
                appendRootTableAlias(sql);
            } else {
                Integer parentIndex = joins.get(parent);
                ColumnMapper parentAttribute = getAttribute(parent);
                appendTableAttribute(sql, parentAttribute, parentIndex);
            }
            if (attribute instanceof JoinColumnMapper join) {
                sql.append(".").append(join.getJoinColumnName()).append("=");
                appendTableAttribute(sql, attribute, v);
                String referenced = join.getJoinColumnReferencedName();
                if (referenced.length() == 0) {
                    referenced = entityInfo.getIdColumnMapper().getColumnName();
                }
                sql.append(".").append(referenced);
            } else {
                throw new IllegalStateException();
            }
        });
        this.sql.insert(sqlIndex, sql);

    }

    Operator getOperator(Expression e) {
        return e instanceof OperatorExpression expression ? expression.operator() : null;
    }

    protected StringBuilder appendTableAttribute(StringBuilder sb, ColumnMapper attribute, Integer index) {
        TableMapper information = mappers.getMapper(attribute.getJavaType());
        String tableName = information.getTableName();
        return appendTableAlias(tableName, index, sb);
    }

    protected ColumnMapper getAttribute(PathExpression path) {
        ColumnMapper attribute = null;
        for (String s : path.path()) {
            TableMapper entityInfo = attribute == null
                    ? tableMapper
                    : mappers.getMapper(attribute.getJavaType());
            attribute = entityInfo.getMapperByAttributeName(s);
        }
        return attribute;
    }

    protected void limit(int offset, int maxResults) {
        if (offset >= 0 || maxResults >= 0) {
            sql.append(" limit ")
                    .append(Math.max(offset, 0))
                    .append(',')
                    .append(maxResults < 0 ? Long.MAX_VALUE : maxResults);
        }
    }

    protected void appendSelectedPath() {
        Iterable<Expression> select = spec.selectClause();
        if (select == null || !select.iterator().hasNext()) {
            select = tableMapper.getBasicColumnMappers()
                    .stream()
                    .map(i -> {
                        String fieldName = i.getFieldName();
                        return new PathExpression(fieldName);
                    })
                    .collect(Collectors.toList());
        }
        String delimiter = NONE_DELIMITER;
        for (Expression selection : select) {
            sql.append(delimiter);
            appendExpression(selection);
            delimiter = DELIMITER;
        }
    }


    private void appendGroupBy() {
        Array<Expression> groupBy = spec.groupByClause();
        if (groupBy != null && !groupBy.isEmpty()) {
            sql.append(" group by ");
            boolean first = true;
            for (Expression e : groupBy) {
                if (first) {
                    first = false;
                } else {
                    sql.append(",");
                }
                appendExpression(e);
            }
        }
    }

    protected void appendOrderBy() {
        Array<SortSpecification> orders = spec.sortSpec();
        if (orders != null && !orders.isEmpty()) {
            sql.append(" order by ");
            boolean first = true;
            for (SortSpecification order : orders) {
                if (first) {
                    first = false;
                } else {
                    sql.append(",");
                }
                appendExpression(order.expression());
                sql.append(" ").append(order.desc() ? "desc" : "asc");
            }

        }
    }
}


