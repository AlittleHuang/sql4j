package github.alittlehuang.sql4j.jpa;

import github.alittlehuang.sql4j.dsl.expression.ConstantExpression;
import github.alittlehuang.sql4j.dsl.expression.Operator;
import github.alittlehuang.sql4j.dsl.expression.OperatorExpression;
import github.alittlehuang.sql4j.dsl.expression.PathExpression;
import github.alittlehuang.sql4j.dsl.util.Array;
import github.alittlehuang.sql4j.dsl.util.TypeCastUtil;

import javax.persistence.criteria.*;
import java.util.HashMap;
import java.util.Map;

public class JpaPredicateBuilder<T> {

    protected Root<T> root;

    protected CriteriaBuilder cb;

    protected final Map<PathExpression, FetchParent<?, ?>> fetched = new HashMap<>();

    public JpaPredicateBuilder() {
    }

    public Predicate toPredicate(github.alittlehuang.sql4j.dsl.expression.Expression expression) {
        javax.persistence.criteria.Expression<?> result = toExpression(expression);
        if (result instanceof Predicate) {
            return (Predicate) result;
        }
        return cb.isTrue(cast(toExpression(expression)));
    }

    public javax.persistence.criteria.Expression<?> toExpression(github.alittlehuang.sql4j.dsl.expression.Expression expression) {
        if (expression instanceof ConstantExpression) {
            return cb.literal(((ConstantExpression) expression).value());
        }
        if (expression instanceof PathExpression) {
            return getPath((PathExpression) expression);
        }
        if (expression instanceof OperatorExpression) {
            Array<? extends github.alittlehuang.sql4j.dsl.expression.Expression> expressions = ((OperatorExpression) expression).expressions();
            Operator operator = ((OperatorExpression) expression).operator();
            javax.persistence.criteria.Expression<?> e0 = toExpression(expressions.get(0));
            switch (operator) {
                case NOT:
                    return cb.not(cast(e0));
                case AND: {
                    javax.persistence.criteria.Expression<Boolean> res = cast(e0);
                    for (int i = 1; i < expressions.length(); i++) {
                        res = cb.and(res, cast(toExpression(expressions.get(i))));
                    }
                    return res;
                }
                case OR: {
                    javax.persistence.criteria.Expression<Boolean> res = cast(e0);
                    for (int i = 1; i < expressions.length(); i++) {
                        res = cb.or(res, cast(toExpression(expressions.get(i))));
                    }
                    return res;
                }
                case GT: {
                    github.alittlehuang.sql4j.dsl.expression.Expression e1 = expressions.get(1);
                    if (e1 instanceof ConstantExpression) {
                        ConstantExpression cv = (ConstantExpression) e1;
                        if (cv.value() instanceof Number) {
                            return cb.gt(cast(e0), (Number) cv.value());
                        } else if (cv.value() instanceof Comparable) {
                            Comparable<Object> value = TypeCastUtil.cast(cv.value());
                            return cb.greaterThan(cast(e0), value);
                        }
                    }
                    return cb.gt(cast(e0), cast(toExpression(e1)));
                }
                case EQ: {
                    github.alittlehuang.sql4j.dsl.expression.Expression e1 = expressions.get(1);
                    if (e1 instanceof ConstantExpression) {
                        return cb.equal(cast(e0), ((ConstantExpression) e1).value());
                    }
                    return cb.equal(e0, toExpression(e1));
                }
                case NE: {
                    github.alittlehuang.sql4j.dsl.expression.Expression e1 = expressions.get(1);
                    if (e1 instanceof ConstantExpression) {
                        ConstantExpression cv = (ConstantExpression) e1;
                        return cb.notEqual(e0, cv.value());
                    }
                    return cb.notEqual(e0, toExpression(e1));
                }
                case GE: {
                    github.alittlehuang.sql4j.dsl.expression.Expression e1 = expressions.get(1);
                    if (e1 instanceof ConstantExpression) {
                        ConstantExpression cv = (ConstantExpression) e1;
                        if (cv.value() instanceof Number) {
                            return cb.ge(cast(e0), (Number) cv.value());
                        } else if (cv.value() instanceof Comparable) {
                            Comparable<Object> comparable = TypeCastUtil.cast(cv.value());
                            return cb.greaterThanOrEqualTo(cast(e0), comparable);
                        }
                    }
                    return cb.ge(cast(e0), cast(toExpression(e1)));
                }
                case LT: {
                    github.alittlehuang.sql4j.dsl.expression.Expression e1 = expressions.get(1);
                    if (e1 instanceof ConstantExpression) {
                        ConstantExpression cv = (ConstantExpression) e1;
                        Object ve1 = cv.value();
                        if (ve1 instanceof Number) {
                            return cb.lt(cast(e0), (Number) ve1);
                        } else if (ve1 instanceof Comparable) {
                            Comparable<Object> ve11 = TypeCastUtil.cast(ve1);
                            return cb.lessThan(cast(e0), ve11);
                        }
                    }
                    return cb.lt(cast(e0), cast(toExpression(e1)));
                }
                case LE: {
                    github.alittlehuang.sql4j.dsl.expression.Expression e1 = expressions.get(1);
                    if (e1 instanceof ConstantExpression) {
                        ConstantExpression cv = (ConstantExpression) e1;
                        Object ve1 = cv.value();
                        if (ve1 instanceof Number) {
                            return cb.le(cast(e0), (Number) ve1);
                        } else if (ve1 instanceof Comparable) {
                            Comparable<Object> ve11 = TypeCastUtil.cast(ve1);
                            return cb.lessThanOrEqualTo(cast(e0), ve11);
                        }
                    }
                    return cb.le(cast(e0), cast(toExpression(e1)));
                }
                case LIKE: {
                    github.alittlehuang.sql4j.dsl.expression.Expression e1 = expressions.get(1);
                    if (e1 instanceof ConstantExpression) {
                        Object value = ((ConstantExpression) e1).value();
                        if (value instanceof String) {
                            return cb.like(cast(e0), value.toString());
                        }
                    }
                    return cb.like(cast(e0), cast(toExpression(e1)));
                }
                case ISNULL:
                    return cb.isNull(e0);
                case IN: {
                    if (expressions.length() > 1) {
                        CriteriaBuilder.In<Object> in = cb.in(e0);
                        for (int i = 1; i < expressions.length(); i++) {
                            github.alittlehuang.sql4j.dsl.expression.Expression e1 = expressions.get(i);
                            if (e1 instanceof ConstantExpression) {
                                ConstantExpression cv = (ConstantExpression) e1;
                                in = in.value(cv.value());
                            } else {
                                in = in.value(toExpression(e1));
                            }
                        }
                        return in;
                    } else {
                        return cb.literal(false);
                    }
                }
                case BETWEEN: {
                    github.alittlehuang.sql4j.dsl.expression.Expression se1 = expressions.get(1);
                    github.alittlehuang.sql4j.dsl.expression.Expression se2 = expressions.get(2);
                    if (se1 instanceof ConstantExpression
                        && se2 instanceof ConstantExpression) {
                        ConstantExpression cv1 = (ConstantExpression) se1;
                        ConstantExpression cv2 = (ConstantExpression) se2;
                        if (cv1.value() instanceof Comparable
                            && cv2.value() instanceof Comparable) {
                            Comparable<Object> v1 = TypeCastUtil.cast(cv1.value());
                            Comparable<Object> v2 = TypeCastUtil.cast(cv2.value());
                            return cb.between(cast(e0), v1, v2);
                        }
                    }
                    return cb.between(
                            cast(e0),
                            cast(toExpression(se1)),
                            cast(toExpression(se2))
                    );
                }
                case LOWER:
                    return cb.lower(cast(e0));
                case UPPER:
                    return cb.upper(cast(e0));
                case SUBSTRING: {
                    if (expressions.length() == 2) {
                        github.alittlehuang.sql4j.dsl.expression.Expression se1 = expressions.get(1);
                        if (se1 instanceof ConstantExpression) {
                            ConstantExpression cv1 = (ConstantExpression) se1;
                            if (cv1.value() instanceof Number) {
                                return cb.substring(cast(e0), ((Number) cv1.value()).intValue());
                            }
                        }
                        return cb.substring(cast(e0), cast(toExpression(se1)));
                    } else if (expressions.length() == 3) {
                        github.alittlehuang.sql4j.dsl.expression.Expression se1 = expressions.get(1);
                        github.alittlehuang.sql4j.dsl.expression.Expression se2 = expressions.get(2);
                        if (se1 instanceof ConstantExpression
                            && se2 instanceof ConstantExpression) {
                            ConstantExpression cv1 = (ConstantExpression) se1;
                            ConstantExpression cv2 = (ConstantExpression) se2;
                            Object n1 = cv1.value();
                            Object n2 = cv2.value();
                            if (n1 instanceof Number
                                && n2 instanceof Number) {
                                return cb.substring(cast(e0), ((Number) n1).intValue(), ((Number) n2).intValue());
                            }
                        }
                        return cb.substring(
                                cast(e0),
                                cast(toExpression(se1)),
                                cast(toExpression(se2))
                        );
                    } else {
                        throw new IllegalArgumentException("argument length error");
                    }
                }
                case TRIM:
                    return cb.trim(cast(e0));
                case LENGTH:
                    return cb.length(cast(e0));
                case ADD: {
                    github.alittlehuang.sql4j.dsl.expression.Expression e1 = expressions.get(1);
                    if (e1 instanceof ConstantExpression) {
                        ConstantExpression cv1 = (ConstantExpression) e1;
                        if ((cv1).value() instanceof Number) {
                            return cb.sum(cast(e0), (Number) cv1.value());
                        }
                    }
                    return cb.sum(cast(e0), cast(toExpression(e1)));
                }
                case SUBTRACT: {
                    github.alittlehuang.sql4j.dsl.expression.Expression e1 = expressions.get(1);
                    if (e1 instanceof ConstantExpression) {
                        Object number = ((ConstantExpression) e1).value();
                        if (number instanceof Number) {
                            return cb.diff(cast(e0), (Number) number);
                        }
                    }
                    return cb.diff(cast(e0), cast(toExpression(e1)));
                }
                case MULTIPLY: {
                    github.alittlehuang.sql4j.dsl.expression.Expression e1 = expressions.get(1);
                    if (e1 instanceof ConstantExpression) {
                        ConstantExpression cv1 = (ConstantExpression) e1;
                        Object value = cv1.value();
                        if (value instanceof Number) {
                            return cb.prod(cast(e0), (Number) value);
                        }
                    }
                    return cb.prod(cast(e0), cast(toExpression(e1)));
                }
                case DIVIDE: {
                    github.alittlehuang.sql4j.dsl.expression.Expression e1 = expressions.get(1);
                    if (e1 instanceof ConstantExpression) {
                        Object number = ((ConstantExpression) e1).value();
                        if (number instanceof Number) {
                            return cb.quot(cast(e0), (Number) number);
                        }
                    }
                    return cb.quot(cast(e0), cast(toExpression(e1)));
                }
                case MOD: {
                    github.alittlehuang.sql4j.dsl.expression.Expression e1 = expressions.get(1);
                    if (e1 instanceof ConstantExpression) {
                        ConstantExpression cv1 = (ConstantExpression) e1;
                        if (cv1.value() instanceof Integer) {
                            return cb.mod(cast(e0), ((Integer) cv1.value()));
                        }
                    }
                    return cb.mod(cast(e0), cast(toExpression(e1)));
                }
                case NULLIF: {
                    github.alittlehuang.sql4j.dsl.expression.Expression e1 = expressions.get(1);
                    if (e1 instanceof ConstantExpression) {
                        ConstantExpression cv = (ConstantExpression) e1;
                        return cb.nullif(cast(e0), ((Integer) cv.value()));
                    }
                    return cb.nullif(e0, toExpression(e1));
                }
                case IF_NULL: {
                    github.alittlehuang.sql4j.dsl.expression.Expression e1 = expressions.get(1);
                    if (e1 instanceof ConstantExpression) {
                        ConstantExpression cv = (ConstantExpression) e1;
                        return cb.coalesce(cast(e0), ((Integer) cv.value()));
                    }
                    return cb.coalesce(e0, toExpression(e1));
                }
                case MIN:
                    return cb.min(cast(e0));
                case MAX:
                    return cb.max(cast(e0));
                case COUNT:
                    return cb.count(cast(e0));
                case AVG:
                    return cb.avg(cast(e0));
                case SUM:
                    return cb.sum(cast(e0));
                default:
                    throw new UnsupportedOperationException(operator.name());
            }
        } else {
            throw new UnsupportedOperationException("unknown expression type " + expression.getClass());
        }
    }

    public static <T> javax.persistence.criteria.Expression<T>
    cast(javax.persistence.criteria.Expression<?> expression) {
        return TypeCastUtil.cast(expression);
    }

    protected Path<?> getPath(PathExpression expression) {
        From<?, ?> r = root;
        int size = expression.length();
        for (int i = 0; i < size; i++) {
            String s = expression.get(i);
            if (i != size - 1) {
                r = join(expression.offset(i + 1));
            } else {
                return r.get(s);
            }
        }

        return r;
    }

    private Join<?, ?> join(PathExpression offset) {
        return (Join<?, ?>) fetched.compute(offset, (k, v) -> {
            if (v instanceof Join<?, ?>) {
                return v;
            } else {
                From<?, ?> r = root;
                for (String s : offset.path()) {
                    r = r.join(s, JoinType.LEFT);
                }
                return r;
            }
        });
    }
}