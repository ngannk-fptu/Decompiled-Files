/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 */
package com.querydsl.core.types;

import com.google.common.collect.ImmutableList;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryException;
import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.Constant;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExtractorVisitor;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.OperationImpl;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathImpl;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathType;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.PredicateOperation;
import com.querydsl.core.types.PredicateTemplate;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.Template;
import com.querydsl.core.types.TemplateExpression;
import com.querydsl.core.types.TemplateExpressionImpl;
import com.querydsl.core.types.TemplateFactory;
import com.querydsl.core.types.Templates;
import com.querydsl.core.types.ToStringVisitor;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import javax.annotation.Nullable;

public final class ExpressionUtils {
    private static final Templates TEMPLATES = new UnderscoreTemplates();

    public static <T> Operation<T> operation(Class<? extends T> type, Operator operator, Expression<?> ... args) {
        return ExpressionUtils.operation(type, operator, ImmutableList.copyOf((Object[])args));
    }

    public static <T> Operation<T> operation(Class<? extends T> type, Operator operator, ImmutableList<Expression<?>> args) {
        if (type.equals(Boolean.class)) {
            return new PredicateOperation(operator, args);
        }
        return new OperationImpl<T>(type, operator, args);
    }

    public static PredicateOperation predicate(Operator operator, Expression<?> ... args) {
        return ExpressionUtils.predicate(operator, ImmutableList.copyOf((Object[])args));
    }

    public static PredicateOperation predicate(Operator operator, ImmutableList<Expression<?>> args) {
        return new PredicateOperation(operator, args);
    }

    public static <T> Path<T> path(Class<? extends T> type, String variable) {
        return new PathImpl<T>(type, variable);
    }

    public static <T> Path<T> path(Class<? extends T> type, Path<?> parent, String property) {
        return new PathImpl<T>(type, parent, property);
    }

    public static <T> Path<T> path(Class<? extends T> type, PathMetadata metadata) {
        return new PathImpl<T>(type, metadata);
    }

    public static PredicateTemplate predicateTemplate(String template, Object ... args) {
        return ExpressionUtils.predicateTemplate(TemplateFactory.DEFAULT.create(template), ImmutableList.copyOf((Object[])args));
    }

    @Deprecated
    public static PredicateTemplate predicateTemplate(String template, ImmutableList<?> args) {
        return ExpressionUtils.predicateTemplate(TemplateFactory.DEFAULT.create(template), args);
    }

    public static PredicateTemplate predicateTemplate(String template, List<?> args) {
        return ExpressionUtils.predicateTemplate(TemplateFactory.DEFAULT.create(template), args);
    }

    public static PredicateTemplate predicateTemplate(Template template, Object ... args) {
        return ExpressionUtils.predicateTemplate(template, ImmutableList.copyOf((Object[])args));
    }

    @Deprecated
    public static PredicateTemplate predicateTemplate(Template template, ImmutableList<?> args) {
        return new PredicateTemplate(template, args);
    }

    public static PredicateTemplate predicateTemplate(Template template, List<?> args) {
        return new PredicateTemplate(template, ImmutableList.copyOf(args));
    }

    public static <T> TemplateExpression<T> template(Class<? extends T> cl, String template, Object ... args) {
        return ExpressionUtils.template(cl, TemplateFactory.DEFAULT.create(template), ImmutableList.copyOf((Object[])args));
    }

    @Deprecated
    public static <T> TemplateExpression<T> template(Class<? extends T> cl, String template, ImmutableList<?> args) {
        return ExpressionUtils.template(cl, TemplateFactory.DEFAULT.create(template), args);
    }

    public static <T> TemplateExpression<T> template(Class<? extends T> cl, String template, List<?> args) {
        return ExpressionUtils.template(cl, TemplateFactory.DEFAULT.create(template), args);
    }

    public static <T> TemplateExpression<T> template(Class<? extends T> cl, Template template, Object ... args) {
        return ExpressionUtils.template(cl, template, ImmutableList.copyOf((Object[])args));
    }

    @Deprecated
    public static <T> TemplateExpression<T> template(Class<? extends T> cl, Template template, ImmutableList<?> args) {
        if (cl.equals(Boolean.class)) {
            return new PredicateTemplate(template, args);
        }
        return new TemplateExpressionImpl<T>(cl, template, args);
    }

    public static <T> TemplateExpression<T> template(Class<? extends T> cl, Template template, List<?> args) {
        if (cl.equals(Boolean.class)) {
            return new PredicateTemplate(template, ImmutableList.copyOf(args));
        }
        return new TemplateExpressionImpl<T>(cl, template, ImmutableList.copyOf(args));
    }

    public static <T> Expression<T> all(CollectionExpression<?, ? super T> col) {
        return new OperationImpl(col.getParameter(0), (Operator)Ops.QuantOps.ALL, ImmutableList.of(col));
    }

    public static <T> Expression<T> any(CollectionExpression<?, ? super T> col) {
        return new OperationImpl(col.getParameter(0), (Operator)Ops.QuantOps.ANY, ImmutableList.of(col));
    }

    public static <T> Expression<T> all(SubQueryExpression<? extends T> col) {
        return new OperationImpl(col.getType(), (Operator)Ops.QuantOps.ALL, ImmutableList.of(col));
    }

    public static <T> Expression<T> any(SubQueryExpression<? extends T> col) {
        return new OperationImpl(col.getType(), (Operator)Ops.QuantOps.ANY, ImmutableList.of(col));
    }

    @Nullable
    public static Predicate allOf(Collection<Predicate> exprs) {
        Predicate rv = null;
        for (Predicate b : exprs) {
            if (b == null) continue;
            rv = rv == null ? b : ExpressionUtils.and(rv, b);
        }
        return rv;
    }

    @Nullable
    public static Predicate allOf(Predicate ... exprs) {
        Predicate rv = null;
        for (Predicate b : exprs) {
            if (b == null) continue;
            rv = rv == null ? b : ExpressionUtils.and(rv, b);
        }
        return rv;
    }

    public static Predicate and(Predicate left, Predicate right) {
        left = (Predicate)ExpressionUtils.extract(left);
        right = (Predicate)ExpressionUtils.extract(right);
        if (left == null) {
            return right;
        }
        if (right == null) {
            return left;
        }
        return ExpressionUtils.predicate((Operator)Ops.AND, left, right);
    }

    @Nullable
    public static Predicate anyOf(Collection<Predicate> exprs) {
        Predicate rv = null;
        for (Predicate b : exprs) {
            if (b == null) continue;
            rv = rv == null ? b : ExpressionUtils.or(rv, b);
        }
        return rv;
    }

    @Nullable
    public static Predicate anyOf(Predicate ... exprs) {
        Predicate rv = null;
        for (Predicate b : exprs) {
            if (b == null) continue;
            rv = rv == null ? b : ExpressionUtils.or(rv, b);
        }
        return rv;
    }

    public static <D> Expression<D> as(Expression<D> source, Path<D> alias) {
        return ExpressionUtils.operation(alias.getType(), (Operator)Ops.ALIAS, source, alias);
    }

    public static <D> Expression<D> as(Expression<D> source, String alias) {
        return ExpressionUtils.as(source, ExpressionUtils.path(source.getType(), alias));
    }

    public static Expression<Long> count(Expression<?> source) {
        return ExpressionUtils.operation(Long.class, (Operator)Ops.AggOps.COUNT_AGG, source);
    }

    public static <D> Predicate eqConst(Expression<D> left, D constant) {
        return ExpressionUtils.eq(left, ConstantImpl.create(constant));
    }

    public static <D> Predicate eq(Expression<D> left, Expression<? extends D> right) {
        return ExpressionUtils.predicate((Operator)Ops.EQ, left, right);
    }

    public static <D> Predicate in(Expression<D> left, CollectionExpression<?, ? extends D> right) {
        return ExpressionUtils.predicate((Operator)Ops.IN, left, right);
    }

    public static <D> Predicate in(Expression<D> left, SubQueryExpression<? extends D> right) {
        return ExpressionUtils.predicate((Operator)Ops.IN, left, right);
    }

    public static <D> Predicate in(Expression<D> left, Collection<? extends D> right) {
        if (right.size() == 1) {
            return ExpressionUtils.eqConst(left, right.iterator().next());
        }
        return ExpressionUtils.predicate((Operator)Ops.IN, left, ConstantImpl.create(right));
    }

    public static <D> Predicate inAny(Expression<D> left, Iterable<? extends Collection<? extends D>> lists) {
        BooleanBuilder rv = new BooleanBuilder();
        for (Collection<D> collection : lists) {
            rv.or(ExpressionUtils.in(left, collection));
        }
        return rv;
    }

    public static Predicate isNull(Expression<?> left) {
        return ExpressionUtils.predicate((Operator)Ops.IS_NULL, left);
    }

    public static Predicate isNotNull(Expression<?> left) {
        return ExpressionUtils.predicate((Operator)Ops.IS_NOT_NULL, left);
    }

    public static Expression<String> likeToRegex(Expression<String> expr) {
        return ExpressionUtils.likeToRegex(expr, true);
    }

    public static Expression<String> likeToRegex(Expression<String> expr, boolean matchStartAndEnd) {
        Operation o;
        if (expr instanceof Constant) {
            String like = expr.toString();
            StringBuilder rv = new StringBuilder(like.length() + 4);
            if (matchStartAndEnd && !like.startsWith("%")) {
                rv.append('^');
            }
            for (int i = 0; i < like.length(); ++i) {
                char ch = like.charAt(i);
                if (ch == '.' || ch == '*' || ch == '?') {
                    rv.append('\\');
                } else {
                    if (ch == '%') {
                        rv.append(".*");
                        continue;
                    }
                    if (ch == '_') {
                        rv.append('.');
                        continue;
                    }
                }
                rv.append(ch);
            }
            if (matchStartAndEnd && !like.endsWith("%")) {
                rv.append('$');
            }
            if (!like.equals(rv.toString())) {
                return ConstantImpl.create(rv.toString());
            }
        } else if (expr instanceof Operation && (o = (Operation)expr).getOperator() == Ops.CONCAT) {
            Expression<String> lhs = ExpressionUtils.likeToRegex(o.getArg(0), false);
            Expression<String> rhs = ExpressionUtils.likeToRegex(o.getArg(1), false);
            if (lhs != o.getArg(0) || rhs != o.getArg(1)) {
                return ExpressionUtils.operation(String.class, (Operator)Ops.CONCAT, lhs, rhs);
            }
        }
        return expr;
    }

    public static <T> Expression<T> list(Class<T> clazz, Expression<?> ... exprs) {
        return ExpressionUtils.list(clazz, ImmutableList.copyOf((Object[])exprs));
    }

    public static <T> Expression<T> list(Class<T> clazz, List<? extends Expression<?>> exprs) {
        Expression<?> rv = exprs.get(0);
        if (exprs.size() == 1) {
            rv = ExpressionUtils.operation(clazz, (Operator)Ops.SINGLETON, rv, exprs.get(0));
        } else {
            for (int i = 1; i < exprs.size(); ++i) {
                rv = ExpressionUtils.operation(clazz, (Operator)Ops.LIST, rv, exprs.get(i));
            }
        }
        return rv;
    }

    public static Expression<String> regexToLike(Expression<String> expr) {
        Operation o;
        if (expr instanceof Constant) {
            String str = expr.toString();
            StringBuilder rv = new StringBuilder(str.length() + 2);
            boolean escape = false;
            for (int i = 0; i < str.length(); ++i) {
                char ch = str.charAt(i);
                if (!escape && ch == '.') {
                    if (i < str.length() - 1 && str.charAt(i + 1) == '*') {
                        rv.append('%');
                        ++i;
                        continue;
                    }
                    rv.append('_');
                    continue;
                }
                if (!escape && ch == '\\') {
                    escape = true;
                    continue;
                }
                if (!(escape || ch != '[' && ch != ']' && ch != '^' && ch != '.' && ch != '*')) {
                    throw new QueryException("'" + str + "' can't be converted to like form");
                }
                if (escape && (ch == 'd' || ch == 'D' || ch == 's' || ch == 'S' || ch == 'w' || ch == 'W')) {
                    throw new QueryException("'" + str + "' can't be converted to like form");
                }
                rv.append(ch);
                escape = false;
            }
            if (!rv.toString().equals(str)) {
                return ConstantImpl.create(rv.toString());
            }
        } else if (expr instanceof Operation && (o = (Operation)expr).getOperator() == Ops.CONCAT) {
            Expression<String> lhs = ExpressionUtils.regexToLike(o.getArg(0));
            Expression<String> rhs = ExpressionUtils.regexToLike(o.getArg(1));
            if (lhs != o.getArg(0) || rhs != o.getArg(1)) {
                return ExpressionUtils.operation(String.class, (Operator)Ops.CONCAT, lhs, rhs);
            }
        }
        return expr;
    }

    public static <D> Predicate neConst(Expression<D> left, D constant) {
        return ExpressionUtils.ne(left, ConstantImpl.create(constant));
    }

    public static <D> Predicate ne(Expression<D> left, Expression<? super D> right) {
        return ExpressionUtils.predicate((Operator)Ops.NE, left, right);
    }

    public static <D> Predicate notIn(Expression<D> left, CollectionExpression<?, ? extends D> right) {
        return ExpressionUtils.predicate((Operator)Ops.NOT_IN, left, right);
    }

    public static <D> Predicate notIn(Expression<D> left, SubQueryExpression<? extends D> right) {
        return ExpressionUtils.predicate((Operator)Ops.NOT_IN, left, right);
    }

    public static <D> Predicate notIn(Expression<D> left, Collection<? extends D> right) {
        if (right.size() == 1) {
            return ExpressionUtils.neConst(left, right.iterator().next());
        }
        return ExpressionUtils.predicate((Operator)Ops.NOT_IN, left, ConstantImpl.create(right));
    }

    public static <D> Predicate notInAny(Expression<D> left, Iterable<? extends Collection<? extends D>> lists) {
        BooleanBuilder rv = new BooleanBuilder();
        for (Collection<D> collection : lists) {
            rv.and(ExpressionUtils.notIn(left, collection));
        }
        return rv;
    }

    public static Predicate or(Predicate left, Predicate right) {
        left = (Predicate)ExpressionUtils.extract(left);
        right = (Predicate)ExpressionUtils.extract(right);
        if (left == null) {
            return right;
        }
        if (right == null) {
            return left;
        }
        return ExpressionUtils.predicate((Operator)Ops.OR, left, right);
    }

    public static ImmutableList<Expression<?>> distinctList(Expression<?> ... args) {
        ImmutableList.Builder builder = ImmutableList.builder();
        HashSet set = new HashSet(args.length);
        for (Expression<?> arg : args) {
            if (!set.add(arg)) continue;
            builder.add(arg);
        }
        return builder.build();
    }

    public static ImmutableList<Expression<?>> distinctList(Expression<?>[] ... args) {
        ImmutableList.Builder builder = ImmutableList.builder();
        HashSet set = new HashSet();
        Expression<?>[][] expressionArray = args;
        int n = expressionArray.length;
        for (int i = 0; i < n; ++i) {
            Expression<?>[] arr;
            for (Expression<?> arg : arr = expressionArray[i]) {
                if (!set.add(arg)) continue;
                builder.add(arg);
            }
        }
        return builder.build();
    }

    public static <T> Expression<T> extract(Expression<T> expr) {
        if (expr != null) {
            Class<?> clazz = expr.getClass();
            if (clazz == PathImpl.class || clazz == PredicateOperation.class || clazz == ConstantImpl.class) {
                return expr;
            }
            return (Expression)expr.accept(ExtractorVisitor.DEFAULT, null);
        }
        return null;
    }

    public static String createRootVariable(Path<?> path, int suffix) {
        String variable = path.accept(ToStringVisitor.DEFAULT, TEMPLATES);
        return variable + "_" + suffix;
    }

    public static String createRootVariable(Path<?> path) {
        return path.accept(ToStringVisitor.DEFAULT, TEMPLATES);
    }

    public static Expression<?> toExpression(Object o) {
        if (o instanceof Expression) {
            return (Expression)o;
        }
        return ConstantImpl.create(o);
    }

    public static Expression<String> toLower(Expression<String> stringExpression) {
        if (stringExpression instanceof Constant) {
            Constant constantExpression = (Constant)stringExpression;
            return ConstantImpl.create(((String)constantExpression.getConstant()).toLowerCase());
        }
        return ExpressionUtils.operation(String.class, (Operator)Ops.LOWER, stringExpression);
    }

    public static Expression<?> orderBy(List<OrderSpecifier<?>> args) {
        return ExpressionUtils.operation(Object.class, (Operator)Ops.ORDER, ConstantImpl.create(args));
    }

    private ExpressionUtils() {
    }

    private static final class UnderscoreTemplates
    extends Templates {
        private UnderscoreTemplates() {
            this.add(PathType.PROPERTY, "{0}_{1}");
            this.add(PathType.COLLECTION_ANY, "{0}");
            this.add(PathType.LISTVALUE, "{0}_{1}");
            this.add(PathType.LISTVALUE_CONSTANT, "{0}_{1}");
        }
    }
}

