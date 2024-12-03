/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.ElementTypesAreNonnullByDefault;
import com.google.common.base.Function;
import com.google.common.base.NullnessCasts;
import com.google.common.base.Objects;
import com.google.common.base.ParametricNullness;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import java.io.Serializable;
import java.util.Map;
import javax.annotation.CheckForNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public final class Functions {
    private Functions() {
    }

    public static Function<Object, String> toStringFunction() {
        return ToStringFunction.INSTANCE;
    }

    public static <E> Function<E, E> identity() {
        return IdentityFunction.INSTANCE;
    }

    public static <K, V> Function<K, V> forMap(Map<K, V> map) {
        return new FunctionForMapNoDefault<K, V>(map);
    }

    public static <K, V> Function<K, V> forMap(Map<K, ? extends V> map, @ParametricNullness V defaultValue) {
        return new ForMapWithDefault<K, V>(map, defaultValue);
    }

    public static <A, B, C> Function<A, C> compose(Function<B, C> g, Function<A, ? extends B> f) {
        return new FunctionComposition<A, B, C>(g, f);
    }

    public static <T> Function<T, Boolean> forPredicate(Predicate<T> predicate) {
        return new PredicateFunction(predicate);
    }

    public static <E> Function<@Nullable Object, E> constant(@ParametricNullness E value) {
        return new ConstantFunction<E>(value);
    }

    public static <F, T> Function<F, T> forSupplier(Supplier<T> supplier) {
        return new SupplierFunction(supplier);
    }

    private static class SupplierFunction<F, T>
    implements Function<F, T>,
    Serializable {
        private final Supplier<T> supplier;
        private static final long serialVersionUID = 0L;

        private SupplierFunction(Supplier<T> supplier) {
            this.supplier = Preconditions.checkNotNull(supplier);
        }

        @Override
        @ParametricNullness
        public T apply(@ParametricNullness F input) {
            return this.supplier.get();
        }

        @Override
        public boolean equals(@CheckForNull Object obj) {
            if (obj instanceof SupplierFunction) {
                SupplierFunction that = (SupplierFunction)obj;
                return this.supplier.equals(that.supplier);
            }
            return false;
        }

        public int hashCode() {
            return this.supplier.hashCode();
        }

        public String toString() {
            return "Functions.forSupplier(" + this.supplier + ")";
        }
    }

    private static class ConstantFunction<E>
    implements Function<Object, E>,
    Serializable {
        @ParametricNullness
        private final E value;
        private static final long serialVersionUID = 0L;

        public ConstantFunction(@ParametricNullness E value) {
            this.value = value;
        }

        @Override
        @ParametricNullness
        public E apply(@CheckForNull Object from) {
            return this.value;
        }

        @Override
        public boolean equals(@CheckForNull Object obj) {
            if (obj instanceof ConstantFunction) {
                ConstantFunction that = (ConstantFunction)obj;
                return Objects.equal(this.value, that.value);
            }
            return false;
        }

        public int hashCode() {
            return this.value == null ? 0 : this.value.hashCode();
        }

        public String toString() {
            return "Functions.constant(" + this.value + ")";
        }
    }

    private static class PredicateFunction<T>
    implements Function<T, Boolean>,
    Serializable {
        private final Predicate<T> predicate;
        private static final long serialVersionUID = 0L;

        private PredicateFunction(Predicate<T> predicate) {
            this.predicate = Preconditions.checkNotNull(predicate);
        }

        @Override
        public Boolean apply(@ParametricNullness T t) {
            return this.predicate.apply(t);
        }

        @Override
        public boolean equals(@CheckForNull Object obj) {
            if (obj instanceof PredicateFunction) {
                PredicateFunction that = (PredicateFunction)obj;
                return this.predicate.equals(that.predicate);
            }
            return false;
        }

        public int hashCode() {
            return this.predicate.hashCode();
        }

        public String toString() {
            return "Functions.forPredicate(" + this.predicate + ")";
        }
    }

    private static class FunctionComposition<A, B, C>
    implements Function<A, C>,
    Serializable {
        private final Function<B, C> g;
        private final Function<A, ? extends B> f;
        private static final long serialVersionUID = 0L;

        public FunctionComposition(Function<B, C> g, Function<A, ? extends B> f) {
            this.g = Preconditions.checkNotNull(g);
            this.f = Preconditions.checkNotNull(f);
        }

        @Override
        @ParametricNullness
        public C apply(@ParametricNullness A a) {
            return this.g.apply(this.f.apply(a));
        }

        @Override
        public boolean equals(@CheckForNull Object obj) {
            if (obj instanceof FunctionComposition) {
                FunctionComposition that = (FunctionComposition)obj;
                return this.f.equals(that.f) && this.g.equals(that.g);
            }
            return false;
        }

        public int hashCode() {
            return this.f.hashCode() ^ this.g.hashCode();
        }

        public String toString() {
            return this.g + "(" + this.f + ")";
        }
    }

    private static class ForMapWithDefault<K, V>
    implements Function<K, V>,
    Serializable {
        final Map<K, ? extends V> map;
        @ParametricNullness
        final V defaultValue;
        private static final long serialVersionUID = 0L;

        ForMapWithDefault(Map<K, ? extends V> map, @ParametricNullness V defaultValue) {
            this.map = Preconditions.checkNotNull(map);
            this.defaultValue = defaultValue;
        }

        @Override
        @ParametricNullness
        public V apply(@ParametricNullness K key) {
            V result = this.map.get(key);
            return result != null || this.map.containsKey(key) ? NullnessCasts.uncheckedCastNullableTToT(result) : this.defaultValue;
        }

        @Override
        public boolean equals(@CheckForNull Object o) {
            if (o instanceof ForMapWithDefault) {
                ForMapWithDefault that = (ForMapWithDefault)o;
                return this.map.equals(that.map) && Objects.equal(this.defaultValue, that.defaultValue);
            }
            return false;
        }

        public int hashCode() {
            return Objects.hashCode(this.map, this.defaultValue);
        }

        public String toString() {
            return "Functions.forMap(" + this.map + ", defaultValue=" + this.defaultValue + ")";
        }
    }

    private static class FunctionForMapNoDefault<K, V>
    implements Function<K, V>,
    Serializable {
        final Map<K, V> map;
        private static final long serialVersionUID = 0L;

        FunctionForMapNoDefault(Map<K, V> map) {
            this.map = Preconditions.checkNotNull(map);
        }

        @Override
        @ParametricNullness
        public V apply(@ParametricNullness K key) {
            V result = this.map.get(key);
            Preconditions.checkArgument(result != null || this.map.containsKey(key), "Key '%s' not present in map", key);
            return NullnessCasts.uncheckedCastNullableTToT(result);
        }

        @Override
        public boolean equals(@CheckForNull Object o) {
            if (o instanceof FunctionForMapNoDefault) {
                FunctionForMapNoDefault that = (FunctionForMapNoDefault)o;
                return this.map.equals(that.map);
            }
            return false;
        }

        public int hashCode() {
            return this.map.hashCode();
        }

        public String toString() {
            return "Functions.forMap(" + this.map + ")";
        }
    }

    private static enum IdentityFunction implements Function<Object, Object>
    {
        INSTANCE;


        @Override
        @CheckForNull
        public Object apply(@CheckForNull Object o) {
            return o;
        }

        public String toString() {
            return "Functions.identity()";
        }
    }

    private static enum ToStringFunction implements Function<Object, String>
    {
        INSTANCE;


        @Override
        public String apply(Object o) {
            Preconditions.checkNotNull(o);
            return o.toString();
        }

        public String toString() {
            return "Functions.toStringFunction()";
        }
    }
}

