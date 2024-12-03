/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Tuple
 *  javax.persistence.criteria.CollectionJoin
 *  javax.persistence.criteria.CompoundSelection
 *  javax.persistence.criteria.CriteriaBuilder$Case
 *  javax.persistence.criteria.CriteriaBuilder$Coalesce
 *  javax.persistence.criteria.CriteriaBuilder$In
 *  javax.persistence.criteria.CriteriaBuilder$SimpleCase
 *  javax.persistence.criteria.CriteriaBuilder$Trimspec
 *  javax.persistence.criteria.CriteriaDelete
 *  javax.persistence.criteria.CriteriaQuery
 *  javax.persistence.criteria.CriteriaUpdate
 *  javax.persistence.criteria.Expression
 *  javax.persistence.criteria.Join
 *  javax.persistence.criteria.ListJoin
 *  javax.persistence.criteria.MapJoin
 *  javax.persistence.criteria.Order
 *  javax.persistence.criteria.ParameterExpression
 *  javax.persistence.criteria.Path
 *  javax.persistence.criteria.Predicate
 *  javax.persistence.criteria.Predicate$BooleanOperator
 *  javax.persistence.criteria.Root
 *  javax.persistence.criteria.Selection
 *  javax.persistence.criteria.SetJoin
 *  javax.persistence.criteria.Subquery
 */
package org.hibernate.query.criteria.internal;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import javax.persistence.Tuple;
import javax.persistence.criteria.CollectionJoin;
import javax.persistence.criteria.CompoundSelection;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.SetJoin;
import javax.persistence.criteria.Subquery;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.internal.CollectionJoinImplementor;
import org.hibernate.query.criteria.internal.CriteriaDeleteImpl;
import org.hibernate.query.criteria.internal.CriteriaQueryImpl;
import org.hibernate.query.criteria.internal.CriteriaUpdateImpl;
import org.hibernate.query.criteria.internal.ExpressionImplementor;
import org.hibernate.query.criteria.internal.JoinImplementor;
import org.hibernate.query.criteria.internal.ListJoinImplementor;
import org.hibernate.query.criteria.internal.MapJoinImplementor;
import org.hibernate.query.criteria.internal.OrderImpl;
import org.hibernate.query.criteria.internal.PathImplementor;
import org.hibernate.query.criteria.internal.SetJoinImplementor;
import org.hibernate.query.criteria.internal.expression.BinaryArithmeticOperation;
import org.hibernate.query.criteria.internal.expression.CoalesceExpression;
import org.hibernate.query.criteria.internal.expression.CompoundSelectionImpl;
import org.hibernate.query.criteria.internal.expression.ConcatExpression;
import org.hibernate.query.criteria.internal.expression.LiteralExpression;
import org.hibernate.query.criteria.internal.expression.NullLiteralExpression;
import org.hibernate.query.criteria.internal.expression.NullifExpression;
import org.hibernate.query.criteria.internal.expression.ParameterExpressionImpl;
import org.hibernate.query.criteria.internal.expression.SearchedCaseExpression;
import org.hibernate.query.criteria.internal.expression.SimpleCaseExpression;
import org.hibernate.query.criteria.internal.expression.SizeOfPluralAttributeExpression;
import org.hibernate.query.criteria.internal.expression.SubqueryComparisonModifierExpression;
import org.hibernate.query.criteria.internal.expression.UnaryArithmeticOperation;
import org.hibernate.query.criteria.internal.expression.function.AbsFunction;
import org.hibernate.query.criteria.internal.expression.function.AggregationFunction;
import org.hibernate.query.criteria.internal.expression.function.BasicFunctionExpression;
import org.hibernate.query.criteria.internal.expression.function.CurrentDateFunction;
import org.hibernate.query.criteria.internal.expression.function.CurrentTimeFunction;
import org.hibernate.query.criteria.internal.expression.function.CurrentTimestampFunction;
import org.hibernate.query.criteria.internal.expression.function.LengthFunction;
import org.hibernate.query.criteria.internal.expression.function.LocateFunction;
import org.hibernate.query.criteria.internal.expression.function.LowerFunction;
import org.hibernate.query.criteria.internal.expression.function.ParameterizedFunctionExpression;
import org.hibernate.query.criteria.internal.expression.function.SqrtFunction;
import org.hibernate.query.criteria.internal.expression.function.SubstringFunction;
import org.hibernate.query.criteria.internal.expression.function.TrimFunction;
import org.hibernate.query.criteria.internal.expression.function.UpperFunction;
import org.hibernate.query.criteria.internal.path.PluralAttributePath;
import org.hibernate.query.criteria.internal.path.RootImpl;
import org.hibernate.query.criteria.internal.predicate.BetweenPredicate;
import org.hibernate.query.criteria.internal.predicate.BooleanAssertionPredicate;
import org.hibernate.query.criteria.internal.predicate.BooleanExpressionPredicate;
import org.hibernate.query.criteria.internal.predicate.BooleanStaticAssertionPredicate;
import org.hibernate.query.criteria.internal.predicate.ComparisonPredicate;
import org.hibernate.query.criteria.internal.predicate.CompoundPredicate;
import org.hibernate.query.criteria.internal.predicate.ExistsPredicate;
import org.hibernate.query.criteria.internal.predicate.InPredicate;
import org.hibernate.query.criteria.internal.predicate.IsEmptyPredicate;
import org.hibernate.query.criteria.internal.predicate.LikePredicate;
import org.hibernate.query.criteria.internal.predicate.MemberOfPredicate;
import org.hibernate.query.criteria.internal.predicate.NullnessPredicate;

public class CriteriaBuilderImpl
implements HibernateCriteriaBuilder,
Serializable {
    private final SessionFactoryImpl sessionFactory;

    public CriteriaBuilderImpl(SessionFactoryImpl sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public SessionFactoryImpl getEntityManagerFactory() {
        return this.sessionFactory;
    }

    public CriteriaQuery<Object> createQuery() {
        return new CriteriaQueryImpl<Object>(this, Object.class);
    }

    public <T> CriteriaQuery<T> createQuery(Class<T> resultClass) {
        return new CriteriaQueryImpl<T>(this, resultClass);
    }

    public CriteriaQuery<Tuple> createTupleQuery() {
        return new CriteriaQueryImpl<Tuple>(this, Tuple.class);
    }

    public <T> CriteriaUpdate<T> createCriteriaUpdate(Class<T> targetEntity) {
        return new CriteriaUpdateImpl(this);
    }

    public <T> CriteriaDelete<T> createCriteriaDelete(Class<T> targetEntity) {
        return new CriteriaDeleteImpl(this);
    }

    void checkMultiselect(List<Selection<?>> selections) {
        HashSet<String> aliases = new HashSet<String>(CollectionHelper.determineProperSizing(selections.size()));
        for (Selection<?> selection : selections) {
            boolean added;
            if (selection.isCompoundSelection()) {
                if (selection.getJavaType().isArray()) {
                    throw new IllegalArgumentException("Selection items in a multi-select cannot contain compound array-valued elements");
                }
                if (Tuple.class.isAssignableFrom(selection.getJavaType())) {
                    throw new IllegalArgumentException("Selection items in a multi-select cannot contain compound tuple-valued elements");
                }
            }
            if (!StringHelper.isNotEmpty(selection.getAlias()) || (added = aliases.add(selection.getAlias()))) continue;
            throw new IllegalArgumentException("Multi-select expressions defined duplicate alias : " + selection.getAlias());
        }
    }

    public CompoundSelection<Tuple> tuple(Selection<?> ... selections) {
        return this.tuple(Arrays.asList(selections));
    }

    public CompoundSelection<Tuple> tuple(List<Selection<?>> selections) {
        this.checkMultiselect(selections);
        return new CompoundSelectionImpl<Tuple>(this, Tuple.class, selections);
    }

    public CompoundSelection<Object[]> array(Selection<?> ... selections) {
        return this.array(Arrays.asList(selections));
    }

    public CompoundSelection<Object[]> array(List<Selection<?>> selections) {
        return this.array(Object[].class, selections);
    }

    public <Y> CompoundSelection<Y> array(Class<Y> type, List<Selection<?>> selections) {
        this.checkMultiselect(selections);
        return new CompoundSelectionImpl<Y>(this, type, selections);
    }

    public <Y> CompoundSelection<Y> construct(Class<Y> result, Selection<?> ... selections) {
        return this.construct(result, Arrays.asList(selections));
    }

    public <Y> CompoundSelection<Y> construct(Class<Y> result, List<Selection<?>> selections) {
        this.checkMultiselect(selections);
        return new CompoundSelectionImpl<Y>(this, result, selections);
    }

    public Order asc(Expression<?> x) {
        return new OrderImpl(x, true);
    }

    public Order desc(Expression<?> x) {
        return new OrderImpl(x, false);
    }

    @Override
    public Order asc(Expression<?> x, boolean nullsFirst) {
        return new OrderImpl(x, true, nullsFirst);
    }

    @Override
    public Order desc(Expression<?> x, boolean nullsFirst) {
        return new OrderImpl(x, false, nullsFirst);
    }

    public Predicate wrap(Expression<Boolean> expression) {
        if (Predicate.class.isInstance(expression)) {
            return (Predicate)expression;
        }
        if (PathImplementor.class.isInstance(expression)) {
            return new BooleanAssertionPredicate(this, expression, Boolean.TRUE);
        }
        return new BooleanExpressionPredicate(this, expression);
    }

    public Predicate not(Expression<Boolean> expression) {
        return this.wrap(expression).not();
    }

    public Predicate and(Expression<Boolean> x, Expression<Boolean> y) {
        return new CompoundPredicate(this, Predicate.BooleanOperator.AND, x, y);
    }

    public Predicate or(Expression<Boolean> x, Expression<Boolean> y) {
        return new CompoundPredicate(this, Predicate.BooleanOperator.OR, x, y);
    }

    public Predicate and(Predicate ... restrictions) {
        return new CompoundPredicate(this, Predicate.BooleanOperator.AND, (Expression<Boolean>[])restrictions);
    }

    public Predicate or(Predicate ... restrictions) {
        return new CompoundPredicate(this, Predicate.BooleanOperator.OR, (Expression<Boolean>[])restrictions);
    }

    public Predicate conjunction() {
        return new CompoundPredicate(this, Predicate.BooleanOperator.AND);
    }

    public Predicate disjunction() {
        return new CompoundPredicate(this, Predicate.BooleanOperator.OR);
    }

    public Predicate isTrue(Expression<Boolean> expression) {
        if (CompoundPredicate.class.isInstance(expression)) {
            CompoundPredicate predicate = (CompoundPredicate)expression;
            if (predicate.getExpressions().size() == 0) {
                return new BooleanStaticAssertionPredicate(this, predicate.getOperator() == Predicate.BooleanOperator.AND);
            }
            return predicate;
        }
        if (Predicate.class.isInstance(expression)) {
            return (Predicate)expression;
        }
        return new BooleanAssertionPredicate(this, expression, Boolean.TRUE);
    }

    public Predicate isFalse(Expression<Boolean> expression) {
        if (CompoundPredicate.class.isInstance(expression)) {
            CompoundPredicate predicate = (CompoundPredicate)expression;
            if (predicate.getExpressions().size() == 0) {
                return new BooleanStaticAssertionPredicate(this, predicate.getOperator() == Predicate.BooleanOperator.OR);
            }
            predicate.not();
            return predicate;
        }
        if (Predicate.class.isInstance(expression)) {
            Predicate predicate = (Predicate)expression;
            predicate.not();
            return predicate;
        }
        return new BooleanAssertionPredicate(this, expression, Boolean.FALSE);
    }

    public Predicate isNull(Expression<?> x) {
        return new NullnessPredicate(this, x);
    }

    public Predicate isNotNull(Expression<?> x) {
        return this.isNull(x).not();
    }

    public Predicate equal(Expression<?> x, Expression<?> y) {
        return new ComparisonPredicate(this, ComparisonPredicate.ComparisonOperator.EQUAL, x, y);
    }

    public Predicate notEqual(Expression<?> x, Expression<?> y) {
        return new ComparisonPredicate(this, ComparisonPredicate.ComparisonOperator.NOT_EQUAL, x, y);
    }

    public Predicate equal(Expression<?> x, Object y) {
        return new ComparisonPredicate(this, ComparisonPredicate.ComparisonOperator.EQUAL, x, y);
    }

    public Predicate notEqual(Expression<?> x, Object y) {
        return new ComparisonPredicate(this, ComparisonPredicate.ComparisonOperator.NOT_EQUAL, x, y);
    }

    public <Y extends Comparable<? super Y>> Predicate greaterThan(Expression<? extends Y> x, Expression<? extends Y> y) {
        return new ComparisonPredicate(this, ComparisonPredicate.ComparisonOperator.GREATER_THAN, x, y);
    }

    public <Y extends Comparable<? super Y>> Predicate lessThan(Expression<? extends Y> x, Expression<? extends Y> y) {
        return new ComparisonPredicate(this, ComparisonPredicate.ComparisonOperator.LESS_THAN, x, y);
    }

    public <Y extends Comparable<? super Y>> Predicate greaterThanOrEqualTo(Expression<? extends Y> x, Expression<? extends Y> y) {
        return new ComparisonPredicate(this, ComparisonPredicate.ComparisonOperator.GREATER_THAN_OR_EQUAL, x, y);
    }

    public <Y extends Comparable<? super Y>> Predicate lessThanOrEqualTo(Expression<? extends Y> x, Expression<? extends Y> y) {
        return new ComparisonPredicate(this, ComparisonPredicate.ComparisonOperator.LESS_THAN_OR_EQUAL, x, y);
    }

    public <Y extends Comparable<? super Y>> Predicate greaterThan(Expression<? extends Y> x, Y y) {
        return new ComparisonPredicate(this, ComparisonPredicate.ComparisonOperator.GREATER_THAN, x, y);
    }

    public <Y extends Comparable<? super Y>> Predicate lessThan(Expression<? extends Y> x, Y y) {
        return new ComparisonPredicate(this, ComparisonPredicate.ComparisonOperator.LESS_THAN, x, y);
    }

    public <Y extends Comparable<? super Y>> Predicate greaterThanOrEqualTo(Expression<? extends Y> x, Y y) {
        return new ComparisonPredicate(this, ComparisonPredicate.ComparisonOperator.GREATER_THAN_OR_EQUAL, x, y);
    }

    public <Y extends Comparable<? super Y>> Predicate lessThanOrEqualTo(Expression<? extends Y> x, Y y) {
        return new ComparisonPredicate(this, ComparisonPredicate.ComparisonOperator.LESS_THAN_OR_EQUAL, x, y);
    }

    public Predicate gt(Expression<? extends Number> x, Expression<? extends Number> y) {
        return new ComparisonPredicate(this, ComparisonPredicate.ComparisonOperator.GREATER_THAN, x, y);
    }

    public Predicate lt(Expression<? extends Number> x, Expression<? extends Number> y) {
        return new ComparisonPredicate(this, ComparisonPredicate.ComparisonOperator.LESS_THAN, x, y);
    }

    public Predicate ge(Expression<? extends Number> x, Expression<? extends Number> y) {
        return new ComparisonPredicate(this, ComparisonPredicate.ComparisonOperator.GREATER_THAN_OR_EQUAL, x, y);
    }

    public Predicate le(Expression<? extends Number> x, Expression<? extends Number> y) {
        return new ComparisonPredicate(this, ComparisonPredicate.ComparisonOperator.LESS_THAN_OR_EQUAL, x, y);
    }

    public Predicate gt(Expression<? extends Number> x, Number y) {
        return new ComparisonPredicate(this, ComparisonPredicate.ComparisonOperator.GREATER_THAN, x, y);
    }

    public Predicate lt(Expression<? extends Number> x, Number y) {
        return new ComparisonPredicate(this, ComparisonPredicate.ComparisonOperator.LESS_THAN, x, y);
    }

    public Predicate ge(Expression<? extends Number> x, Number y) {
        return new ComparisonPredicate(this, ComparisonPredicate.ComparisonOperator.GREATER_THAN_OR_EQUAL, x, y);
    }

    public Predicate le(Expression<? extends Number> x, Number y) {
        return new ComparisonPredicate(this, ComparisonPredicate.ComparisonOperator.LESS_THAN_OR_EQUAL, x, y);
    }

    public <Y extends Comparable<? super Y>> Predicate between(Expression<? extends Y> expression, Y lowerBound, Y upperBound) {
        return new BetweenPredicate<Y>(this, expression, lowerBound, upperBound);
    }

    public <Y extends Comparable<? super Y>> Predicate between(Expression<? extends Y> expression, Expression<? extends Y> lowerBound, Expression<? extends Y> upperBound) {
        return new BetweenPredicate<Y>(this, expression, lowerBound, upperBound);
    }

    public <T> CriteriaBuilder.In<T> in(Expression<? extends T> expression) {
        return new InPredicate<T>(this, expression);
    }

    public <T> CriteriaBuilder.In<T> in(Expression<? extends T> expression, Expression<? extends T> ... values) {
        return new InPredicate<T>(this, expression, values);
    }

    public <T> CriteriaBuilder.In<T> in(Expression<? extends T> expression, T ... values) {
        return new InPredicate<T>(this, expression, values);
    }

    public <T> CriteriaBuilder.In<T> in(Expression<? extends T> expression, Collection<T> values) {
        return new InPredicate<T>(this, expression, values);
    }

    public Predicate like(Expression<String> matchExpression, Expression<String> pattern) {
        return new LikePredicate(this, matchExpression, pattern);
    }

    public Predicate like(Expression<String> matchExpression, Expression<String> pattern, Expression<Character> escapeCharacter) {
        return new LikePredicate(this, matchExpression, pattern, escapeCharacter);
    }

    public Predicate like(Expression<String> matchExpression, Expression<String> pattern, char escapeCharacter) {
        return new LikePredicate(this, matchExpression, pattern, escapeCharacter);
    }

    public Predicate like(Expression<String> matchExpression, String pattern) {
        return new LikePredicate(this, matchExpression, pattern);
    }

    public Predicate like(Expression<String> matchExpression, String pattern, Expression<Character> escapeCharacter) {
        return new LikePredicate(this, matchExpression, pattern, escapeCharacter);
    }

    public Predicate like(Expression<String> matchExpression, String pattern, char escapeCharacter) {
        return new LikePredicate(this, matchExpression, pattern, escapeCharacter);
    }

    public Predicate notLike(Expression<String> matchExpression, Expression<String> pattern) {
        return this.like(matchExpression, pattern).not();
    }

    public Predicate notLike(Expression<String> matchExpression, Expression<String> pattern, Expression<Character> escapeCharacter) {
        return this.like(matchExpression, pattern, escapeCharacter).not();
    }

    public Predicate notLike(Expression<String> matchExpression, Expression<String> pattern, char escapeCharacter) {
        return this.like(matchExpression, pattern, escapeCharacter).not();
    }

    public Predicate notLike(Expression<String> matchExpression, String pattern) {
        return this.like(matchExpression, pattern).not();
    }

    public Predicate notLike(Expression<String> matchExpression, String pattern, Expression<Character> escapeCharacter) {
        return this.like(matchExpression, pattern, escapeCharacter).not();
    }

    public Predicate notLike(Expression<String> matchExpression, String pattern, char escapeCharacter) {
        return this.like(matchExpression, pattern, escapeCharacter).not();
    }

    public <T> ParameterExpression<T> parameter(Class<T> paramClass) {
        return new ParameterExpressionImpl<T>(this, paramClass);
    }

    public <T> ParameterExpression<T> parameter(Class<T> paramClass, String name) {
        return new ParameterExpressionImpl<T>(this, paramClass, name);
    }

    public <T> Expression<T> literal(T value) {
        if (value == null) {
            throw new IllegalArgumentException("literal value cannot be null");
        }
        return new LiteralExpression<T>(this, value);
    }

    public <T> Expression<T> nullLiteral(Class<T> resultClass) {
        return new NullLiteralExpression<T>(this, resultClass);
    }

    public <N extends Number> Expression<Double> avg(Expression<N> x) {
        return new AggregationFunction.AVG(this, x);
    }

    public <N extends Number> Expression<N> sum(Expression<N> x) {
        return new AggregationFunction.SUM<N>(this, x);
    }

    public Expression<Long> sumAsLong(Expression<Integer> x) {
        return new AggregationFunction.SUM<Long>(this, x, Long.class);
    }

    public Expression<Double> sumAsDouble(Expression<Float> x) {
        return new AggregationFunction.SUM<Double>(this, x, Double.class);
    }

    public <N extends Number> Expression<N> max(Expression<N> x) {
        return new AggregationFunction.MAX<N>(this, x);
    }

    public <N extends Number> Expression<N> min(Expression<N> x) {
        return new AggregationFunction.MIN<N>(this, x);
    }

    public <X extends Comparable<? super X>> Expression<X> greatest(Expression<X> x) {
        return new AggregationFunction.GREATEST<X>(this, x);
    }

    public <X extends Comparable<? super X>> Expression<X> least(Expression<X> x) {
        return new AggregationFunction.LEAST<X>(this, x);
    }

    public Expression<Long> count(Expression<?> x) {
        return new AggregationFunction.COUNT(this, x, false);
    }

    public Expression<Long> countDistinct(Expression<?> x) {
        return new AggregationFunction.COUNT(this, x, true);
    }

    public <T> Expression<T> function(String name, Class<T> returnType, Expression<?> ... arguments) {
        return new ParameterizedFunctionExpression<T>(this, returnType, name, arguments);
    }

    public <T> Expression<T> function(String name, Class<T> returnType) {
        return new BasicFunctionExpression<T>(this, returnType, name);
    }

    public <N extends Number> Expression<N> abs(Expression<N> expression) {
        return new AbsFunction(this, expression);
    }

    public Expression<Double> sqrt(Expression<? extends Number> expression) {
        return new SqrtFunction(this, expression);
    }

    public Expression<Date> currentDate() {
        return new CurrentDateFunction(this);
    }

    public Expression<Timestamp> currentTimestamp() {
        return new CurrentTimestampFunction(this);
    }

    public Expression<Time> currentTime() {
        return new CurrentTimeFunction(this);
    }

    public Expression<String> substring(Expression<String> value, Expression<Integer> start) {
        return new SubstringFunction(this, value, start);
    }

    public Expression<String> substring(Expression<String> value, int start) {
        return new SubstringFunction(this, value, start);
    }

    public Expression<String> substring(Expression<String> value, Expression<Integer> start, Expression<Integer> length) {
        return new SubstringFunction(this, value, start, length);
    }

    public Expression<String> substring(Expression<String> value, int start, int length) {
        return new SubstringFunction(this, value, start, length);
    }

    public Expression<String> trim(Expression<String> trimSource) {
        return new TrimFunction(this, trimSource);
    }

    public Expression<String> trim(CriteriaBuilder.Trimspec trimspec, Expression<String> trimSource) {
        return new TrimFunction(this, trimspec, trimSource);
    }

    public Expression<String> trim(Expression<Character> trimCharacter, Expression<String> trimSource) {
        return new TrimFunction(this, trimCharacter, trimSource);
    }

    public Expression<String> trim(CriteriaBuilder.Trimspec trimspec, Expression<Character> trimCharacter, Expression<String> trimSource) {
        return new TrimFunction(this, trimspec, trimCharacter, trimSource);
    }

    public Expression<String> trim(char trimCharacter, Expression<String> trimSource) {
        return new TrimFunction(this, trimCharacter, trimSource);
    }

    public Expression<String> trim(CriteriaBuilder.Trimspec trimspec, char trimCharacter, Expression<String> trimSource) {
        return new TrimFunction(this, trimspec, trimCharacter, trimSource);
    }

    public Expression<String> lower(Expression<String> value) {
        return new LowerFunction(this, value);
    }

    public Expression<String> upper(Expression<String> value) {
        return new UpperFunction(this, value);
    }

    public Expression<Integer> length(Expression<String> value) {
        return new LengthFunction(this, value);
    }

    public Expression<Integer> locate(Expression<String> string, Expression<String> pattern) {
        return new LocateFunction(this, pattern, string);
    }

    public Expression<Integer> locate(Expression<String> string, Expression<String> pattern, Expression<Integer> start) {
        return new LocateFunction(this, pattern, string, start);
    }

    public Expression<Integer> locate(Expression<String> string, String pattern) {
        return new LocateFunction(this, pattern, string);
    }

    public Expression<Integer> locate(Expression<String> string, String pattern, int start) {
        return new LocateFunction(this, pattern, string, start);
    }

    public <N extends Number> Expression<N> neg(Expression<N> expression) {
        return new UnaryArithmeticOperation<N>(this, UnaryArithmeticOperation.Operation.UNARY_MINUS, expression);
    }

    public <N extends Number> Expression<N> sum(Expression<? extends N> expression1, Expression<? extends N> expression2) {
        if (expression1 == null || expression2 == null) {
            throw new IllegalArgumentException("arguments to sum() cannot be null");
        }
        Class<Number> resultType = BinaryArithmeticOperation.determineResultType(expression1.getJavaType(), expression2.getJavaType());
        return new BinaryArithmeticOperation<N>(this, resultType, BinaryArithmeticOperation.Operation.ADD, expression1, expression2);
    }

    public <N extends Number> Expression<N> prod(Expression<? extends N> expression1, Expression<? extends N> expression2) {
        if (expression1 == null || expression2 == null) {
            throw new IllegalArgumentException("arguments to prod() cannot be null");
        }
        Class<Number> resultType = BinaryArithmeticOperation.determineResultType(expression1.getJavaType(), expression2.getJavaType());
        return new BinaryArithmeticOperation<N>(this, resultType, BinaryArithmeticOperation.Operation.MULTIPLY, expression1, expression2);
    }

    public <N extends Number> Expression<N> diff(Expression<? extends N> expression1, Expression<? extends N> expression2) {
        if (expression1 == null || expression2 == null) {
            throw new IllegalArgumentException("arguments to diff() cannot be null");
        }
        Class<Number> resultType = BinaryArithmeticOperation.determineResultType(expression1.getJavaType(), expression2.getJavaType());
        return new BinaryArithmeticOperation<N>(this, resultType, BinaryArithmeticOperation.Operation.SUBTRACT, expression1, expression2);
    }

    public <N extends Number> Expression<N> sum(Expression<? extends N> expression, N n) {
        if (expression == null || n == null) {
            throw new IllegalArgumentException("arguments to sum() cannot be null");
        }
        Class<Number> resultType = BinaryArithmeticOperation.determineResultType(expression.getJavaType(), n.getClass());
        return new BinaryArithmeticOperation<N>(this, resultType, BinaryArithmeticOperation.Operation.ADD, expression, n);
    }

    public <N extends Number> Expression<N> prod(Expression<? extends N> expression, N n) {
        if (expression == null || n == null) {
            throw new IllegalArgumentException("arguments to prod() cannot be null");
        }
        Class<Number> resultType = BinaryArithmeticOperation.determineResultType(expression.getJavaType(), n.getClass());
        return new BinaryArithmeticOperation<N>(this, resultType, BinaryArithmeticOperation.Operation.MULTIPLY, expression, n);
    }

    public <N extends Number> Expression<N> diff(Expression<? extends N> expression, N n) {
        if (expression == null || n == null) {
            throw new IllegalArgumentException("arguments to diff() cannot be null");
        }
        Class<Number> resultType = BinaryArithmeticOperation.determineResultType(expression.getJavaType(), n.getClass());
        return new BinaryArithmeticOperation<N>(this, resultType, BinaryArithmeticOperation.Operation.SUBTRACT, expression, n);
    }

    public <N extends Number> Expression<N> sum(N n, Expression<? extends N> expression) {
        if (expression == null || n == null) {
            throw new IllegalArgumentException("arguments to sum() cannot be null");
        }
        Class<Number> resultType = BinaryArithmeticOperation.determineResultType(n.getClass(), expression.getJavaType());
        return new BinaryArithmeticOperation<N>(this, resultType, BinaryArithmeticOperation.Operation.ADD, n, expression);
    }

    public <N extends Number> Expression<N> prod(N n, Expression<? extends N> expression) {
        if (n == null || expression == null) {
            throw new IllegalArgumentException("arguments to prod() cannot be null");
        }
        Class<Number> resultType = BinaryArithmeticOperation.determineResultType(n.getClass(), expression.getJavaType());
        return new BinaryArithmeticOperation<N>(this, resultType, BinaryArithmeticOperation.Operation.MULTIPLY, n, expression);
    }

    public <N extends Number> Expression<N> diff(N n, Expression<? extends N> expression) {
        if (n == null || expression == null) {
            throw new IllegalArgumentException("arguments to diff() cannot be null");
        }
        Class<Number> resultType = BinaryArithmeticOperation.determineResultType(n.getClass(), expression.getJavaType());
        return new BinaryArithmeticOperation<N>(this, resultType, BinaryArithmeticOperation.Operation.SUBTRACT, n, expression);
    }

    public Expression<Number> quot(Expression<? extends Number> expression1, Expression<? extends Number> expression2) {
        if (expression1 == null || expression2 == null) {
            throw new IllegalArgumentException("arguments to quot() cannot be null");
        }
        Class<Number> resultType = BinaryArithmeticOperation.determineResultType(expression1.getJavaType(), expression2.getJavaType(), true);
        return new BinaryArithmeticOperation<Number>(this, resultType, BinaryArithmeticOperation.Operation.DIVIDE, (Number)expression1, (Expression<Number>)expression2);
    }

    public Expression<Number> quot(Expression<? extends Number> expression, Number number) {
        if (expression == null || number == null) {
            throw new IllegalArgumentException("arguments to quot() cannot be null");
        }
        Class<Number> resultType = BinaryArithmeticOperation.determineResultType(expression.getJavaType(), number.getClass(), true);
        return new BinaryArithmeticOperation<Number>(this, resultType, BinaryArithmeticOperation.Operation.DIVIDE, expression, number);
    }

    public Expression<Number> quot(Number number, Expression<? extends Number> expression) {
        if (expression == null || number == null) {
            throw new IllegalArgumentException("arguments to quot() cannot be null");
        }
        Class<Number> resultType = BinaryArithmeticOperation.determineResultType(number.getClass(), expression.getJavaType(), true);
        return new BinaryArithmeticOperation<Number>(this, resultType, BinaryArithmeticOperation.Operation.DIVIDE, number, expression);
    }

    public Expression<Integer> mod(Expression<Integer> expression1, Expression<Integer> expression2) {
        if (expression1 == null || expression2 == null) {
            throw new IllegalArgumentException("arguments to mod() cannot be null");
        }
        return new BinaryArithmeticOperation<Integer>(this, Integer.class, BinaryArithmeticOperation.Operation.MOD, (Integer)expression1, expression2);
    }

    public Expression<Integer> mod(Expression<Integer> expression, Integer integer) {
        if (expression == null || integer == null) {
            throw new IllegalArgumentException("arguments to mod() cannot be null");
        }
        return new BinaryArithmeticOperation<Integer>(this, Integer.class, BinaryArithmeticOperation.Operation.MOD, expression, integer);
    }

    public Expression<Integer> mod(Integer integer, Expression<Integer> expression) {
        if (integer == null || expression == null) {
            throw new IllegalArgumentException("arguments to mod() cannot be null");
        }
        return new BinaryArithmeticOperation<Integer>(this, Integer.class, BinaryArithmeticOperation.Operation.MOD, integer, expression);
    }

    public ExpressionImplementor<Long> toLong(Expression<? extends Number> expression) {
        return ((ExpressionImplementor)expression).asLong();
    }

    public ExpressionImplementor<Integer> toInteger(Expression<? extends Number> expression) {
        return ((ExpressionImplementor)expression).asInteger();
    }

    public ExpressionImplementor<Float> toFloat(Expression<? extends Number> expression) {
        return ((ExpressionImplementor)expression).asFloat();
    }

    public ExpressionImplementor<Double> toDouble(Expression<? extends Number> expression) {
        return ((ExpressionImplementor)expression).asDouble();
    }

    public ExpressionImplementor<BigDecimal> toBigDecimal(Expression<? extends Number> expression) {
        return ((ExpressionImplementor)expression).asBigDecimal();
    }

    public ExpressionImplementor<BigInteger> toBigInteger(Expression<? extends Number> expression) {
        return ((ExpressionImplementor)expression).asBigInteger();
    }

    public ExpressionImplementor<String> toString(Expression<Character> characterExpression) {
        return ((ExpressionImplementor)characterExpression).asString();
    }

    public <X, T, V extends T> Join<X, V> treat(Join<X, T> join, Class<V> type) {
        return this.treat(join, type, (j, t) -> ((JoinImplementor)j).treatAs((Class)t));
    }

    public <X, T, E extends T> CollectionJoin<X, E> treat(CollectionJoin<X, T> join, Class<E> type) {
        return this.treat((Join<X, T>)join, (Class)type, (BiFunction)(j, t) -> ((CollectionJoinImplementor)j).treatAs((Class)t));
    }

    public <X, T, E extends T> SetJoin<X, E> treat(SetJoin<X, T> join, Class<E> type) {
        return this.treat((Join<X, T>)join, (Class)type, (BiFunction)(j, t) -> ((SetJoinImplementor)j).treatAs((Class)t));
    }

    public <X, T, E extends T> ListJoin<X, E> treat(ListJoin<X, T> join, Class<E> type) {
        return this.treat((Join<X, T>)join, (Class)type, (BiFunction)(j, t) -> ((ListJoinImplementor)join).treatAs(type));
    }

    public <X, K, T, V extends T> MapJoin<X, K, V> treat(MapJoin<X, K, T> join, Class<V> type) {
        return this.treat((Join<X, T>)join, type, (BiFunction<Join<X, T>, Class<V>, K>)((BiFunction<Join, Class, MapJoinImplementor>)(j, t) -> ((MapJoinImplementor)join).treatAs(type)));
    }

    public <X, T extends X> Path<T> treat(Path<X> path, Class<T> type) {
        return ((PathImplementor)path).treatAs(type);
    }

    public <X, T extends X> Root<T> treat(Root<X> root, Class<T> type) {
        return ((RootImpl)root).treatAs((Class)type);
    }

    public Predicate exists(Subquery<?> subquery) {
        return new ExistsPredicate(this, subquery);
    }

    public <Y> Expression<Y> all(Subquery<Y> subquery) {
        return new SubqueryComparisonModifierExpression<Y>(this, subquery.getJavaType(), subquery, SubqueryComparisonModifierExpression.Modifier.ALL);
    }

    public <Y> Expression<Y> some(Subquery<Y> subquery) {
        return new SubqueryComparisonModifierExpression<Y>(this, subquery.getJavaType(), subquery, SubqueryComparisonModifierExpression.Modifier.SOME);
    }

    public <Y> Expression<Y> any(Subquery<Y> subquery) {
        return new SubqueryComparisonModifierExpression<Y>(this, subquery.getJavaType(), subquery, SubqueryComparisonModifierExpression.Modifier.ANY);
    }

    public <Y> Expression<Y> coalesce(Expression<? extends Y> exp1, Expression<? extends Y> exp2) {
        return this.coalesce((Class<Y>)null, exp1, (Y)exp2);
    }

    public <Y> Expression<Y> coalesce(Class<Y> type, Expression<? extends Y> exp1, Expression<? extends Y> exp2) {
        return new CoalesceExpression<Y>(this, type).value(exp1).value(exp2);
    }

    public <Y> Expression<Y> coalesce(Expression<? extends Y> exp1, Y exp2) {
        return this.coalesce((Class)null, exp1, exp2);
    }

    public <Y> Expression<Y> coalesce(Class<Y> type, Expression<? extends Y> exp1, Y exp2) {
        return new CoalesceExpression<Y>(this, type).value(exp1).value(exp2);
    }

    public <T> CriteriaBuilder.Coalesce<T> coalesce() {
        return this.coalesce(null);
    }

    public <T> CriteriaBuilder.Coalesce<T> coalesce(Class<T> type) {
        return new CoalesceExpression<T>(this, type);
    }

    public Expression<String> concat(Expression<String> string1, Expression<String> string2) {
        return new ConcatExpression(this, string1, string2);
    }

    public Expression<String> concat(Expression<String> string1, String string2) {
        return new ConcatExpression(this, string1, string2);
    }

    public Expression<String> concat(String string1, Expression<String> string2) {
        return new ConcatExpression(this, string1, string2);
    }

    public <Y> Expression<Y> nullif(Expression<Y> exp1, Expression<?> exp2) {
        return this.nullif(null, exp1, (Y)exp2);
    }

    public <Y> Expression<Y> nullif(Class<Y> type, Expression<Y> exp1, Expression<?> exp2) {
        return new NullifExpression<Y>(this, type, exp1, exp2);
    }

    public <Y> Expression<Y> nullif(Expression<Y> exp1, Y exp2) {
        return this.nullif(null, exp1, exp2);
    }

    public <Y> Expression<Y> nullif(Class<Y> type, Expression<Y> exp1, Y exp2) {
        return new NullifExpression<Y>(this, type, exp1, exp2);
    }

    public <C, R> CriteriaBuilder.SimpleCase<C, R> selectCase(Expression<? extends C> expression) {
        return this.selectCase(null, expression);
    }

    public <C, R> CriteriaBuilder.SimpleCase<C, R> selectCase(Class<R> type, Expression<? extends C> expression) {
        return new SimpleCaseExpression<C, R>(this, type, expression);
    }

    public <R> CriteriaBuilder.Case<R> selectCase() {
        return this.selectCase((Class)null);
    }

    public <R> CriteriaBuilder.Case<R> selectCase(Class<R> type) {
        return new SearchedCaseExpression<R>(this, type);
    }

    public <C extends Collection<?>> Expression<Integer> size(C c) {
        int size = c == null ? 0 : c.size();
        return new LiteralExpression<Integer>(this, Integer.class, size);
    }

    public <C extends Collection<?>> Expression<Integer> size(Expression<C> exp) {
        if (LiteralExpression.class.isInstance(exp)) {
            return this.size((Collection)((LiteralExpression)exp).getLiteral());
        }
        if (PluralAttributePath.class.isInstance(exp)) {
            return new SizeOfPluralAttributeExpression(this, (PluralAttributePath)exp);
        }
        throw new IllegalArgumentException("unknown collection expression type [" + exp.getClass().getName() + "]");
    }

    public <V, M extends Map<?, V>> Expression<Collection<V>> values(M map) {
        return new LiteralExpression<Collection<V>>(this, map.values());
    }

    public <K, M extends Map<K, ?>> Expression<Set<K>> keys(M map) {
        return new LiteralExpression<Set<K>>(this, map.keySet());
    }

    public <C extends Collection<?>> Predicate isEmpty(Expression<C> collectionExpression) {
        if (PluralAttributePath.class.isInstance(collectionExpression)) {
            return new IsEmptyPredicate(this, (PluralAttributePath)collectionExpression);
        }
        throw new IllegalArgumentException("unknown collection expression type [" + collectionExpression.getClass().getName() + "]");
    }

    public <C extends Collection<?>> Predicate isNotEmpty(Expression<C> collectionExpression) {
        return this.isEmpty(collectionExpression).not();
    }

    public <E, C extends Collection<E>> Predicate isMember(E e, Expression<C> collectionExpression) {
        if (!PluralAttributePath.class.isInstance(collectionExpression)) {
            throw new IllegalArgumentException("unknown collection expression type [" + collectionExpression.getClass().getName() + "]");
        }
        return new MemberOfPredicate(this, e, (PluralAttributePath)collectionExpression);
    }

    public <E, C extends Collection<E>> Predicate isNotMember(E e, Expression<C> cExpression) {
        return this.isMember(e, cExpression).not();
    }

    public <E, C extends Collection<E>> Predicate isMember(Expression<E> elementExpression, Expression<C> collectionExpression) {
        if (!PluralAttributePath.class.isInstance(collectionExpression)) {
            throw new IllegalArgumentException("unknown collection expression type [" + collectionExpression.getClass().getName() + "]");
        }
        return new MemberOfPredicate(this, elementExpression, (PluralAttributePath)collectionExpression);
    }

    public <E, C extends Collection<E>> Predicate isNotMember(Expression<E> eExpression, Expression<C> cExpression) {
        return this.isMember(eExpression, cExpression).not();
    }

    @Override
    public <M extends Map<?, ?>> Predicate isMapEmpty(Expression<M> mapExpression) {
        if (PluralAttributePath.class.isInstance(mapExpression)) {
            return new IsEmptyPredicate(this, (PluralAttributePath)mapExpression);
        }
        throw new IllegalArgumentException("unknown collection expression type [" + mapExpression.getClass().getName() + "]");
    }

    @Override
    public <M extends Map<?, ?>> Predicate isMapNotEmpty(Expression<M> mapExpression) {
        return this.isMapEmpty(mapExpression).not();
    }

    @Override
    public <M extends Map<?, ?>> Expression<Integer> mapSize(Expression<M> mapExpression) {
        if (LiteralExpression.class.isInstance(mapExpression)) {
            return this.mapSize((Map)((LiteralExpression)mapExpression).getLiteral());
        }
        if (PluralAttributePath.class.isInstance(mapExpression)) {
            return new SizeOfPluralAttributeExpression(this, (PluralAttributePath)mapExpression);
        }
        throw new IllegalArgumentException("unknown collection expression type [" + mapExpression.getClass().getName() + "]");
    }

    @Override
    public <M extends Map<?, ?>> Expression<Integer> mapSize(M map) {
        int size = map == null ? 0 : map.size();
        return new LiteralExpression<Integer>(this, Integer.class, size);
    }

    private <X, T, V extends T, K extends JoinImplementor> K treat(Join<X, T> join, Class<V> type, BiFunction<Join<X, T>, Class<V>, K> f) {
        Set joins = join.getParent().getJoins();
        JoinImplementor treatAs = (JoinImplementor)f.apply(join, type);
        joins.add(treatAs);
        return (K)treatAs;
    }
}

