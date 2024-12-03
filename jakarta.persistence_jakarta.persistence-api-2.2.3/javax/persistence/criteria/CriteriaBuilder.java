/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence.criteria;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.persistence.Tuple;
import javax.persistence.criteria.CollectionJoin;
import javax.persistence.criteria.CompoundSelection;
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

public interface CriteriaBuilder {
    public CriteriaQuery<Object> createQuery();

    public <T> CriteriaQuery<T> createQuery(Class<T> var1);

    public CriteriaQuery<Tuple> createTupleQuery();

    public <T> CriteriaUpdate<T> createCriteriaUpdate(Class<T> var1);

    public <T> CriteriaDelete<T> createCriteriaDelete(Class<T> var1);

    public <Y> CompoundSelection<Y> construct(Class<Y> var1, Selection<?> ... var2);

    public CompoundSelection<Tuple> tuple(Selection<?> ... var1);

    public CompoundSelection<Object[]> array(Selection<?> ... var1);

    public Order asc(Expression<?> var1);

    public Order desc(Expression<?> var1);

    public <N extends Number> Expression<Double> avg(Expression<N> var1);

    public <N extends Number> Expression<N> sum(Expression<N> var1);

    public Expression<Long> sumAsLong(Expression<Integer> var1);

    public Expression<Double> sumAsDouble(Expression<Float> var1);

    public <N extends Number> Expression<N> max(Expression<N> var1);

    public <N extends Number> Expression<N> min(Expression<N> var1);

    public <X extends Comparable<? super X>> Expression<X> greatest(Expression<X> var1);

    public <X extends Comparable<? super X>> Expression<X> least(Expression<X> var1);

    public Expression<Long> count(Expression<?> var1);

    public Expression<Long> countDistinct(Expression<?> var1);

    public Predicate exists(Subquery<?> var1);

    public <Y> Expression<Y> all(Subquery<Y> var1);

    public <Y> Expression<Y> some(Subquery<Y> var1);

    public <Y> Expression<Y> any(Subquery<Y> var1);

    public Predicate and(Expression<Boolean> var1, Expression<Boolean> var2);

    public Predicate and(Predicate ... var1);

    public Predicate or(Expression<Boolean> var1, Expression<Boolean> var2);

    public Predicate or(Predicate ... var1);

    public Predicate not(Expression<Boolean> var1);

    public Predicate conjunction();

    public Predicate disjunction();

    public Predicate isTrue(Expression<Boolean> var1);

    public Predicate isFalse(Expression<Boolean> var1);

    public Predicate isNull(Expression<?> var1);

    public Predicate isNotNull(Expression<?> var1);

    public Predicate equal(Expression<?> var1, Expression<?> var2);

    public Predicate equal(Expression<?> var1, Object var2);

    public Predicate notEqual(Expression<?> var1, Expression<?> var2);

    public Predicate notEqual(Expression<?> var1, Object var2);

    public <Y extends Comparable<? super Y>> Predicate greaterThan(Expression<? extends Y> var1, Expression<? extends Y> var2);

    public <Y extends Comparable<? super Y>> Predicate greaterThan(Expression<? extends Y> var1, Y var2);

    public <Y extends Comparable<? super Y>> Predicate greaterThanOrEqualTo(Expression<? extends Y> var1, Expression<? extends Y> var2);

    public <Y extends Comparable<? super Y>> Predicate greaterThanOrEqualTo(Expression<? extends Y> var1, Y var2);

    public <Y extends Comparable<? super Y>> Predicate lessThan(Expression<? extends Y> var1, Expression<? extends Y> var2);

    public <Y extends Comparable<? super Y>> Predicate lessThan(Expression<? extends Y> var1, Y var2);

    public <Y extends Comparable<? super Y>> Predicate lessThanOrEqualTo(Expression<? extends Y> var1, Expression<? extends Y> var2);

    public <Y extends Comparable<? super Y>> Predicate lessThanOrEqualTo(Expression<? extends Y> var1, Y var2);

    public <Y extends Comparable<? super Y>> Predicate between(Expression<? extends Y> var1, Expression<? extends Y> var2, Expression<? extends Y> var3);

    public <Y extends Comparable<? super Y>> Predicate between(Expression<? extends Y> var1, Y var2, Y var3);

    public Predicate gt(Expression<? extends Number> var1, Expression<? extends Number> var2);

    public Predicate gt(Expression<? extends Number> var1, Number var2);

    public Predicate ge(Expression<? extends Number> var1, Expression<? extends Number> var2);

    public Predicate ge(Expression<? extends Number> var1, Number var2);

    public Predicate lt(Expression<? extends Number> var1, Expression<? extends Number> var2);

    public Predicate lt(Expression<? extends Number> var1, Number var2);

    public Predicate le(Expression<? extends Number> var1, Expression<? extends Number> var2);

    public Predicate le(Expression<? extends Number> var1, Number var2);

    public <N extends Number> Expression<N> neg(Expression<N> var1);

    public <N extends Number> Expression<N> abs(Expression<N> var1);

    public <N extends Number> Expression<N> sum(Expression<? extends N> var1, Expression<? extends N> var2);

    public <N extends Number> Expression<N> sum(Expression<? extends N> var1, N var2);

    public <N extends Number> Expression<N> sum(N var1, Expression<? extends N> var2);

    public <N extends Number> Expression<N> prod(Expression<? extends N> var1, Expression<? extends N> var2);

    public <N extends Number> Expression<N> prod(Expression<? extends N> var1, N var2);

    public <N extends Number> Expression<N> prod(N var1, Expression<? extends N> var2);

    public <N extends Number> Expression<N> diff(Expression<? extends N> var1, Expression<? extends N> var2);

    public <N extends Number> Expression<N> diff(Expression<? extends N> var1, N var2);

    public <N extends Number> Expression<N> diff(N var1, Expression<? extends N> var2);

    public Expression<Number> quot(Expression<? extends Number> var1, Expression<? extends Number> var2);

    public Expression<Number> quot(Expression<? extends Number> var1, Number var2);

    public Expression<Number> quot(Number var1, Expression<? extends Number> var2);

    public Expression<Integer> mod(Expression<Integer> var1, Expression<Integer> var2);

    public Expression<Integer> mod(Expression<Integer> var1, Integer var2);

    public Expression<Integer> mod(Integer var1, Expression<Integer> var2);

    public Expression<Double> sqrt(Expression<? extends Number> var1);

    public Expression<Long> toLong(Expression<? extends Number> var1);

    public Expression<Integer> toInteger(Expression<? extends Number> var1);

    public Expression<Float> toFloat(Expression<? extends Number> var1);

    public Expression<Double> toDouble(Expression<? extends Number> var1);

    public Expression<BigDecimal> toBigDecimal(Expression<? extends Number> var1);

    public Expression<BigInteger> toBigInteger(Expression<? extends Number> var1);

    public Expression<String> toString(Expression<Character> var1);

    public <T> Expression<T> literal(T var1);

    public <T> Expression<T> nullLiteral(Class<T> var1);

    public <T> ParameterExpression<T> parameter(Class<T> var1);

    public <T> ParameterExpression<T> parameter(Class<T> var1, String var2);

    public <C extends Collection<?>> Predicate isEmpty(Expression<C> var1);

    public <C extends Collection<?>> Predicate isNotEmpty(Expression<C> var1);

    public <C extends Collection<?>> Expression<Integer> size(Expression<C> var1);

    public <C extends Collection<?>> Expression<Integer> size(C var1);

    public <E, C extends Collection<E>> Predicate isMember(Expression<E> var1, Expression<C> var2);

    public <E, C extends Collection<E>> Predicate isMember(E var1, Expression<C> var2);

    public <E, C extends Collection<E>> Predicate isNotMember(Expression<E> var1, Expression<C> var2);

    public <E, C extends Collection<E>> Predicate isNotMember(E var1, Expression<C> var2);

    public <V, M extends Map<?, V>> Expression<Collection<V>> values(M var1);

    public <K, M extends Map<K, ?>> Expression<Set<K>> keys(M var1);

    public Predicate like(Expression<String> var1, Expression<String> var2);

    public Predicate like(Expression<String> var1, String var2);

    public Predicate like(Expression<String> var1, Expression<String> var2, Expression<Character> var3);

    public Predicate like(Expression<String> var1, Expression<String> var2, char var3);

    public Predicate like(Expression<String> var1, String var2, Expression<Character> var3);

    public Predicate like(Expression<String> var1, String var2, char var3);

    public Predicate notLike(Expression<String> var1, Expression<String> var2);

    public Predicate notLike(Expression<String> var1, String var2);

    public Predicate notLike(Expression<String> var1, Expression<String> var2, Expression<Character> var3);

    public Predicate notLike(Expression<String> var1, Expression<String> var2, char var3);

    public Predicate notLike(Expression<String> var1, String var2, Expression<Character> var3);

    public Predicate notLike(Expression<String> var1, String var2, char var3);

    public Expression<String> concat(Expression<String> var1, Expression<String> var2);

    public Expression<String> concat(Expression<String> var1, String var2);

    public Expression<String> concat(String var1, Expression<String> var2);

    public Expression<String> substring(Expression<String> var1, Expression<Integer> var2);

    public Expression<String> substring(Expression<String> var1, int var2);

    public Expression<String> substring(Expression<String> var1, Expression<Integer> var2, Expression<Integer> var3);

    public Expression<String> substring(Expression<String> var1, int var2, int var3);

    public Expression<String> trim(Expression<String> var1);

    public Expression<String> trim(Trimspec var1, Expression<String> var2);

    public Expression<String> trim(Expression<Character> var1, Expression<String> var2);

    public Expression<String> trim(Trimspec var1, Expression<Character> var2, Expression<String> var3);

    public Expression<String> trim(char var1, Expression<String> var2);

    public Expression<String> trim(Trimspec var1, char var2, Expression<String> var3);

    public Expression<String> lower(Expression<String> var1);

    public Expression<String> upper(Expression<String> var1);

    public Expression<Integer> length(Expression<String> var1);

    public Expression<Integer> locate(Expression<String> var1, Expression<String> var2);

    public Expression<Integer> locate(Expression<String> var1, String var2);

    public Expression<Integer> locate(Expression<String> var1, Expression<String> var2, Expression<Integer> var3);

    public Expression<Integer> locate(Expression<String> var1, String var2, int var3);

    public Expression<Date> currentDate();

    public Expression<Timestamp> currentTimestamp();

    public Expression<Time> currentTime();

    public <T> In<T> in(Expression<? extends T> var1);

    public <Y> Expression<Y> coalesce(Expression<? extends Y> var1, Expression<? extends Y> var2);

    public <Y> Expression<Y> coalesce(Expression<? extends Y> var1, Y var2);

    public <Y> Expression<Y> nullif(Expression<Y> var1, Expression<?> var2);

    public <Y> Expression<Y> nullif(Expression<Y> var1, Y var2);

    public <T> Coalesce<T> coalesce();

    public <C, R> SimpleCase<C, R> selectCase(Expression<? extends C> var1);

    public <R> Case<R> selectCase();

    public <T> Expression<T> function(String var1, Class<T> var2, Expression<?> ... var3);

    public <X, T, V extends T> Join<X, V> treat(Join<X, T> var1, Class<V> var2);

    public <X, T, E extends T> CollectionJoin<X, E> treat(CollectionJoin<X, T> var1, Class<E> var2);

    public <X, T, E extends T> SetJoin<X, E> treat(SetJoin<X, T> var1, Class<E> var2);

    public <X, T, E extends T> ListJoin<X, E> treat(ListJoin<X, T> var1, Class<E> var2);

    public <X, K, T, V extends T> MapJoin<X, K, V> treat(MapJoin<X, K, T> var1, Class<V> var2);

    public <X, T extends X> Path<T> treat(Path<X> var1, Class<T> var2);

    public <X, T extends X> Root<T> treat(Root<X> var1, Class<T> var2);

    public static interface Case<R>
    extends Expression<R> {
        public Case<R> when(Expression<Boolean> var1, R var2);

        public Case<R> when(Expression<Boolean> var1, Expression<? extends R> var2);

        public Expression<R> otherwise(R var1);

        public Expression<R> otherwise(Expression<? extends R> var1);
    }

    public static interface SimpleCase<C, R>
    extends Expression<R> {
        public Expression<C> getExpression();

        public SimpleCase<C, R> when(C var1, R var2);

        public SimpleCase<C, R> when(C var1, Expression<? extends R> var2);

        public Expression<R> otherwise(R var1);

        public Expression<R> otherwise(Expression<? extends R> var1);
    }

    public static interface Coalesce<T>
    extends Expression<T> {
        public Coalesce<T> value(T var1);

        public Coalesce<T> value(Expression<? extends T> var1);
    }

    public static interface In<T>
    extends Predicate {
        public Expression<T> getExpression();

        public In<T> value(T var1);

        public In<T> value(Expression<? extends T> var1);
    }

    public static enum Trimspec {
        LEADING,
        TRAILING,
        BOTH;

    }
}

