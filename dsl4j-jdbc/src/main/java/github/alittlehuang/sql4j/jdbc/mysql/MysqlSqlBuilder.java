package github.alittlehuang.sql4j.jdbc.mysql;


import github.alittlehuang.sql4j.dsl.builder.LockModeType;
import github.alittlehuang.sql4j.dsl.expression.*;
import github.alittlehuang.sql4j.dsl.support.QuerySpecification;
import github.alittlehuang.sql4j.dsl.util.Array;
import github.alittlehuang.sql4j.dsl.util.Assert;
import github.alittlehuang.sql4j.jdbc.mapper.*;
import github.alittlehuang.sql4j.jdbc.sql.PreparedSql;
import github.alittlehuang.sql4j.jdbc.sql.PreparedSqlBuilder;
import github.alittlehuang.sql4j.jdbc.sql.SelectedPreparedSql;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static github.alittlehuang.sql4j.dsl.expression.Operator.AND;


public class MysqlSqlBuilder implements PreparedSqlBuilder {

    protected final QuerySpecification criteria;
    protected final TableMapper rootEntityInfo;
    protected final TableMapperFactory mappers;

    public MysqlSqlBuilder(QuerySpecification criteria, Class<?> javaType, TableMapperFactory mappers) {
        this.criteria = criteria;
        this.mappers = mappers;
        this.rootEntityInfo = getTableMapper(javaType);
    }

    @Override
    public SelectedPreparedSql getEntityList(int offset, int maxResultant, LockModeType lockModeType) {
        return new EntityBuilder().getEntityList(offset, maxResultant, lockModeType);
    }

    @Override
    public PreparedSql getObjectsList(int offset, int maxResultant, LockModeType lockModeType) {
        return new Builder().getObjectsList(offset, maxResultant, lockModeType);
    }

    @Override
    public PreparedSql exist(int offset) {
        return new Builder().exist(offset);
    }

    @Override
    public PreparedSql count() {
        return new Builder().count();
    }

    private TableMapper getTableMapper(ColumnMapper attribute) {
        return getTableMapper(attribute.getJavaType());
    }

    private TableMapper getTableMapper(Class<?> clazz) {
        TableMapper info = mappers.getMapper(clazz);
        Assert.notNull(info, "the type " + clazz + " is not an entity type");
        return info;
    }

    private class EntityBuilder extends Builder implements SelectedPreparedSql {
        protected final List<PathExpression> selectedPath = new ArrayList<>();

        protected SelectedPreparedSql getEntityList(int offset, int maxResult, LockModeType lockModeType) {
            sql.append("select ");
            appendEntityPath();
            appendFetchPath();
            appendQueryConditions(offset, maxResult);
            appendLockModeType(lockModeType);
            return this;
        }

        protected void appendEntityPath() {
            String join = "";
            for (BasicColumnMapper basicAttribute : rootEntityInfo.getBasicColumnMappers()) {
                sql.append(join);
                PathExpression path = new PathExpression(basicAttribute.getFieldName());
                appendPath(path);
                selectedPath.add(path);
                join = ",";
            }
        }

        protected void appendFetchPath() {
            Array<PathExpression> fetchClause = criteria.fetchClause();
            if (fetchClause != null) {
                for (PathExpression fetch : fetchClause) {
                    ColumnMapper attribute = getAttribute(fetch);
                    TableMapper entityInfo = getTableMapper(attribute);
                    for (BasicColumnMapper basicAttribute : entityInfo.getBasicColumnMappers()) {
                        sql.append(",");
                        PathExpression path = fetch.to(basicAttribute.getFieldName());
                        appendPath(path);
                        selectedPath.add(path);
                    }
                }
            }
        }

        @Override
        public List<PathExpression> getSelectedPath() {
            return selectedPath;
        }
    }

    protected class Builder implements PreparedSql {
        protected final StringBuilder sql = new StringBuilder();
        protected final List<Object> args = new ArrayList<>();
        protected final Map<PathExpression, Integer> joins = new LinkedHashMap<>();

        protected PreparedSql getObjectsList(int offset, int maxResult, LockModeType lockModeType) {
            sql.append("select ");
            appendSelectedPath();
            appendBlank()
                    .append("from `")
                    .append(rootEntityInfo.getTableName())
                    .append("` ");
            appendRootTableAlias();
            int sqlIndex = sql.length();
            appendWhere();
            appendGroupBy();
            appendOrderBy();
            limit(offset, maxResult);
            insertJoin(sqlIndex);
            appendLockModeType(lockModeType);
            return this;
        }

        protected void appendLockModeType(LockModeType lockModeType) {
            if (lockModeType == LockModeType.PESSIMISTIC_READ) {
                sql.append(" for share");
            } else if (lockModeType == LockModeType.PESSIMISTIC_WRITE) {
                sql.append(" for update");
            } else if (lockModeType == LockModeType.PESSIMISTIC_FORCE_INCREMENT) {
                sql.append(" for update nowait");
            }
        }

        protected void appendQueryConditions(int offset, int maxResult) {
            appendBlank()
                    .append("from `")
                    .append(rootEntityInfo.getTableName())
                    .append("` ");
            appendRootTableAlias();
            int sqlIndex = sql.length();
            appendWhere();
            appendOrderBy();
            limit(offset, maxResult);
            insertJoin(sqlIndex);
        }

        protected PreparedSql exist(int offset) {
            sql.append("select ");
            BasicColumnMapper attribute = rootEntityInfo.getIdColumnMapper();
            appendRootTableAlias();
            sql.append(".`").append(attribute.getColumnName()).append("`");
            appendBlank()
                    .append("from `")
                    .append(rootEntityInfo.getTableName())
                    .append("` ");
            appendRootTableAlias();
            int sqlIndex = sql.length();
            appendWhere();
            limit(offset, 1);
            insertJoin(sqlIndex);
            return this;
        }

        protected PreparedSql count() {
            sql.append("select count(");
            BasicColumnMapper attribute = rootEntityInfo.getIdColumnMapper();
            appendRootTableAlias();
            sql.append(".`").append(attribute.getColumnName()).append("`)");
            appendBlank()
                    .append("from `")
                    .append(rootEntityInfo.getTableName())
                    .append("` ");
            appendRootTableAlias();
            int sqlIndex = sql.length();
            appendWhere();
            insertJoin(sqlIndex);
            return this;
        }

        @Override
        public String getSql() {
            return sql.toString();
        }

        @Override
        public List<Object> getArgs() {
            return args;
        }

        protected StringBuilder appendRootTableAlias() {
            return appendRootTableAlias(sql);
        }

        protected StringBuilder appendRootTableAlias(StringBuilder sql) {
            String table = rootEntityInfo.getTableName();
            return sql.append(table, 0, 1);
        }

        protected StringBuilder appendTableAlias(String table, Object index, StringBuilder sql) {
            return sql.append(table, 0, 1).append(index);
        }

        protected StringBuilder appendBlank() {
            return sql.length() == 0 || " (,+-*/=><".indexOf(sql.charAt(sql.length() - 1)) >= 0 ? sql : sql.append(' ');
        }


        protected void appendWhere() {
            if (criteria.whereClause() == null || ConstantExpression.TRUE.equals(criteria.whereClause())) {
                return;
            }
            sql.append(" where ");
            appendExpression(criteria.whereClause());
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
            Class<?> type = MysqlSqlBuilder.this.rootEntityInfo.getJavaType();

            PathExpression join = new PathExpression(expression.get(0));

            for (String path : expression.path()) {
                TableMapper info = getTableMapper(type);
                ColumnMapper attribute = info.getMapperByAttributeName(path);
                if (i++ == iMax) {
                    if (attribute instanceof JoinColumnMapper joinColumnMapper) {
                        sb.append('`').append(joinColumnMapper.getJoinColumnName()).append('`');
                    } else if (attribute instanceof BasicColumnMapper basicColumnMapper) {
                        sb.append('`').append(basicColumnMapper.getColumnName()).append('`');
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
                TableMapper entityInfo = getTableMapper(attribute);
                sql.append(" left join `").append(entityInfo.getTableName()).append("` ");

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
                    sql.append(".`").append(join.getJoinColumnName()).append("`=");
                    appendTableAttribute(sql, attribute, v);
                    String referenced = join.getJoinColumnReferencedName();
                    if (referenced.length() == 0) {
                        referenced = entityInfo.getIdColumnMapper().getColumnName();
                    }
                    sql.append(".`").append(referenced).append('`');
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
            TableMapper information = getTableMapper(attribute.getJavaType());
            String tableName = information.getTableName();
            return appendTableAlias(tableName, index, sb);
        }

        protected ColumnMapper getAttribute(PathExpression path) {
            ColumnMapper attribute = null;
            for (String s : path.path()) {
                TableMapper entityInfo = attribute == null
                        ? rootEntityInfo
                        : getTableMapper(attribute);
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
            Iterable<Expression> select = criteria.selectClause();
            if (select == null || !select.iterator().hasNext()) {
                select = rootEntityInfo.getBasicColumnMappers()
                        .stream()
                        .map(i -> {
                            String fieldName = i.getFieldName();
                            return new PathExpression(fieldName);
                        })
                        .collect(Collectors.toList());
            }
            String join = "";
            for (Expression selection : select) {
                sql.append(join);
                appendExpression(selection);
                join = ",";
            }
        }


        private void appendGroupBy() {
            Array<Expression> groupBy = criteria.groupByClause();
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
            Array<SortSpecification> orders = criteria.sortSpec();
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

}