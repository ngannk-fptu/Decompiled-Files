/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.ForOverride
 *  javax.annotation.CheckForNull
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.ElementTypesAreNonnullByDefault;
import com.google.common.base.Function;
import com.google.common.base.FunctionalEquivalence;
import com.google.common.base.Objects;
import com.google.common.base.PairwiseEquivalence;
import com.google.common.base.ParametricNullness;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.errorprone.annotations.ForOverride;
import java.io.Serializable;
import java.util.function.BiPredicate;
import javax.annotation.CheckForNull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class Equivalence<T>
implements BiPredicate<T, T> {
    protected Equivalence() {
    }

    public final boolean equivalent(@CheckForNull T a, @CheckForNull T b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return this.doEquivalent(a, b);
    }

    @Override
    @Deprecated
    public final boolean test(@CheckForNull T t, @CheckForNull T u) {
        return this.equivalent(t, u);
    }

    @ForOverride
    protected abstract boolean doEquivalent(T var1, T var2);

    public final int hash(@CheckForNull T t) {
        if (t == null) {
            return 0;
        }
        return this.doHash(t);
    }

    @ForOverride
    protected abstract int doHash(T var1);

    public final <F> Equivalence<F> onResultOf(Function<? super F, ? extends @Nullable T> function) {
        return new FunctionalEquivalence<F, T>(function, this);
    }

    public final <S extends T> Wrapper<S> wrap(@ParametricNullness S reference) {
        return new Wrapper(this, reference);
    }

    @GwtCompatible(serializable=true)
    public final <S extends T> Equivalence<Iterable<S>> pairwise() {
        return new PairwiseEquivalence(this);
    }

    public final Predicate<@Nullable T> equivalentTo(@CheckForNull T target) {
        return new EquivalentToPredicate<T>(this, target);
    }

    public static Equivalence<Object> equals() {
        return Equals.INSTANCE;
    }

    public static Equivalence<Object> identity() {
        return Identity.INSTANCE;
    }

    static final class Identity
    extends Equivalence<Object>
    implements Serializable {
        static final Identity INSTANCE = new Identity();
        private static final long serialVersionUID = 1L;

        Identity() {
        }

        @Override
        protected boolean doEquivalent(Object a, Object b) {
            return false;
        }

        @Override
        protected int doHash(Object o) {
            return System.identityHashCode(o);
        }

        private Object readResolve() {
            return INSTANCE;
        }
    }

    static final class Equals
    extends Equivalence<Object>
    implements Serializable {
        static final Equals INSTANCE = new Equals();
        private static final long serialVersionUID = 1L;

        Equals() {
        }

        @Override
        protected boolean doEquivalent(Object a, Object b) {
            return a.equals(b);
        }

        @Override
        protected int doHash(Object o) {
            return o.hashCode();
        }

        private Object readResolve() {
            return INSTANCE;
        }
    }

    private static final class EquivalentToPredicate<T>
    implements Predicate<T>,
    Serializable {
        private final Equivalence<T> equivalence;
        @CheckForNull
        private final T target;
        private static final long serialVersionUID = 0L;

        EquivalentToPredicate(Equivalence<T> equivalence, @CheckForNull T target) {
            this.equivalence = Preconditions.checkNotNull(equivalence);
            this.target = target;
        }

        @Override
        public boolean apply(@CheckForNull T input) {
            return this.equivalence.equivalent(input, this.target);
        }

        @Override
        public boolean equals(@CheckForNull Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof EquivalentToPredicate) {
                EquivalentToPredicate that = (EquivalentToPredicate)obj;
                return this.equivalence.equals(that.equivalence) && Objects.equal(this.target, that.target);
            }
            return false;
        }

        public int hashCode() {
            return Objects.hashCode(this.equivalence, this.target);
        }

        public String toString() {
            return this.equivalence + ".equivalentTo(" + this.target + ")";
        }
    }

    public static final class Wrapper<T>
    implements Serializable {
        private final Equivalence<? super @NonNull T> equivalence;
        @ParametricNullness
        private final T reference;
        private static final long serialVersionUID = 0L;

        private Wrapper(Equivalence<? super @NonNull T> equivalence, @ParametricNullness T reference) {
            this.equivalence = Preconditions.checkNotNull(equivalence);
            this.reference = reference;
        }

        @ParametricNullness
        public T get() {
            return this.reference;
        }

        public boolean equals(@CheckForNull Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof Wrapper) {
                Wrapper that = (Wrapper)obj;
                if (this.equivalence.equals(that.equivalence)) {
                    Equivalence<T> equivalence = this.equivalence;
                    return equivalence.equivalent(this.reference, that.reference);
                }
            }
            return false;
        }

        public int hashCode() {
            return this.equivalence.hash(this.reference);
        }

        public String toString() {
            return this.equivalence + ".wrap(" + this.reference + ")";
        }
    }
}

