/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.concurrent.NotNull
 *  com.google.common.base.Function
 *  com.google.common.base.Functions
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.base.Supplier
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Iterators
 *  javax.annotation.Nullable
 */
package com.atlassian.fugue;

import com.atlassian.fugue.Function2;
import com.atlassian.fugue.Iterables;
import com.atlassian.fugue.Option;
import com.atlassian.fugue.Pair;
import com.atlassian.fugue.WeakMemoizer;
import com.atlassian.util.concurrent.NotNull;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import java.io.Serializable;
import java.util.Iterator;
import javax.annotation.Nullable;

public class Functions {
    private Functions() {
    }

    public static <A, B, C> Function<A, C> compose(Function<? super B, ? extends C> g, Function<? super A, ? extends B> f) {
        return new FunctionComposition<A, B, C>(g, f);
    }

    public static <F, T> T fold(Function2<? super T, F, T> f, T zero, Iterable<? extends F> elements) {
        T currentValue = zero;
        for (F element : elements) {
            currentValue = f.apply(currentValue, element);
        }
        return currentValue;
    }

    public static <F, S, T extends S> T fold(Function<Pair<S, F>, T> f, T zero, Iterable<? extends F> elements) {
        return (T)Functions.fold(Functions.toFunction2(f), zero, elements);
    }

    public static <A, B> Function<Function<A, B>, B> apply(final A arg) {
        return new Function<Function<A, B>, B>(){

            public B apply(Function<A, B> f) {
                return f.apply(arg);
            }

            public String toString() {
                return "Apply";
            }
        };
    }

    public static <A, B> Function<Function<A, B>, B> apply(final Supplier<A> lazyA) {
        return new Function<Function<A, B>, B>(){

            public B apply(Function<A, B> f) {
                return f.apply(lazyA.get());
            }

            public String toString() {
                return "ApplySupplier";
            }
        };
    }

    public static <A, B> Function<A, Option<B>> isInstanceOf(Class<B> cls) {
        return new InstanceOf(cls);
    }

    public static <A, B> Function<A, Option<B>> partial(Predicate<? super A> p, Function<? super A, ? extends B> f) {
        return new Partial<A, B>(p, f);
    }

    public static <A, B, C> Function<A, Option<C>> composeOption(Function<? super B, ? extends Option<? extends C>> bc, Function<? super A, ? extends Option<? extends B>> ab) {
        return new PartialComposer(ab, bc);
    }

    public static <A, B, C> Function2<A, B, C> toFunction2(final Function<Pair<A, B>, C> fpair) {
        Preconditions.checkNotNull(fpair);
        return new Function2<A, B, C>(){

            @Override
            public C apply(A a, B b) {
                return fpair.apply(Pair.pair(a, b));
            }

            public String toString() {
                return "ToFunction2";
            }
        };
    }

    public static <A, B, C> Function<A, Function<B, C>> curried(Function2<A, B, C> f2) {
        Preconditions.checkNotNull(f2);
        return new CurriedFunction<A, B, C>(f2);
    }

    public static <A, B, C> Function<B, Function<A, C>> flip(Function<A, Function<B, C>> f2) {
        Preconditions.checkNotNull(f2);
        return new FlippedFunction<A, B, C>(f2);
    }

    public static <A, B> Function<A, Option<B>> matches(Function<? super A, ? extends Option<? extends B>> f1, Function<? super A, ? extends Option<? extends B>> f2) {
        Matcher<A, B> result = Functions.matcher(f1, f2);
        return result;
    }

    public static <A, B> Function<A, Option<B>> matches(Function<? super A, ? extends Option<? extends B>> f1, Function<? super A, ? extends Option<? extends B>> f2, Function<? super A, ? extends Option<? extends B>> f3) {
        Matcher<A, B> result = Functions.matcher(f1, f2, f3);
        return result;
    }

    public static <A, B> Function<A, Option<B>> matches(Function<? super A, ? extends Option<? extends B>> f1, Function<? super A, ? extends Option<? extends B>> f2, Function<? super A, ? extends Option<? extends B>> f3, Function<? super A, ? extends Option<? extends B>> f4) {
        Matcher result = new Matcher(ImmutableList.of(f1, f2, f3, f4));
        return result;
    }

    public static <A, B> Function<A, Option<B>> matches(Function<? super A, ? extends Option<? extends B>> f1, Function<? super A, ? extends Option<? extends B>> f2, Function<? super A, ? extends Option<? extends B>> f3, Function<? super A, ? extends Option<? extends B>> f4, Function<? super A, ? extends Option<? extends B>> f5, Function<? super A, ? extends Option<? extends B>> ... fs) {
        Matcher result = new Matcher(com.google.common.collect.Iterables.concat((Iterable)ImmutableList.of(f1, f2, f3, f4, f5), (Iterable)ImmutableList.copyOf((Object[])fs)));
        return result;
    }

    private static <A, B> Matcher<A, B> matcher(Function<? super A, ? extends Option<? extends B>> ... fs) {
        return new Matcher(ImmutableList.copyOf((Object[])fs));
    }

    public static <A, B> Function<A, Option<B>> mapNullToOption(Function<? super A, ? extends B> f) {
        return Functions.compose(Functions.nullToOption(), f);
    }

    public static <A> Function<A, Option<A>> nullToOption() {
        return new ToOption();
    }

    @Deprecated
    public static <A, B> Function<A, Option<B>> lift(Function<? super A, ? extends B> f) {
        return Functions.mapNullToOption(f);
    }

    public static <A, B> Function<A, B> weakMemoize(Function<A, B> f) {
        return WeakMemoizer.weakMemoizer(f);
    }

    static <D, R> Function<D, R> fromSupplier(@NotNull Supplier<R> supplier) {
        return new FromSupplier(supplier);
    }

    static <T> Function<T, T> identity() {
        return com.google.common.base.Functions.identity();
    }

    static <A> Function<A, Iterator<A>> singletonIterator() {
        return new Function<A, Iterator<A>>(){

            public Iterator<A> apply(A a) {
                return Iterators.singletonIterator(a);
            }
        };
    }

    static <A, B> Function<A, B> constant(final B constant) {
        return new Function<A, B>(){

            public B apply(A from) {
                return constant;
            }
        };
    }

    static class ToOption<A>
    implements Function<A, Option<A>> {
        ToOption() {
        }

        public Option<A> apply(A from) {
            return Option.option(from);
        }
    }

    static class FromSupplier<D, R>
    implements Function<D, R> {
        private final Supplier<R> supplier;

        FromSupplier(Supplier<R> supplier) {
            this.supplier = (Supplier)Preconditions.checkNotNull(supplier, (Object)"supplier");
        }

        public R apply(D ignore) {
            return (R)this.supplier.get();
        }

        public String toString() {
            return "FromSupplier";
        }

        public int hashCode() {
            return this.supplier.hashCode();
        }
    }

    static class Matcher<A, B>
    implements Function<A, Option<B>> {
        private final Iterable<Function<? super A, ? extends Option<? extends B>>> fs;

        Matcher(Iterable<Function<? super A, ? extends Option<? extends B>>> fs) {
            this.fs = (Iterable)Preconditions.checkNotNull(fs);
            Preconditions.checkState((!Iterables.isEmpty().apply(this.fs) ? 1 : 0) != 0);
        }

        public Option<B> apply(A a) {
            for (Function<? super A, ? extends Option<? extends B>> function : this.fs) {
                Option b = (Option)function.apply(a);
                if (!b.isDefined()) continue;
                return b;
            }
            return Option.none();
        }

        public String toString() {
            return "Matcher";
        }

        public int hashCode() {
            return this.fs.hashCode();
        }
    }

    private static class FlippedFunction<A, B, C>
    implements Function<B, Function<A, C>> {
        private final Function<A, Function<B, C>> f2;

        FlippedFunction(Function<A, Function<B, C>> f2) {
            this.f2 = f2;
        }

        public Function<A, C> apply(final B b) {
            return new Function<A, C>(){

                public C apply(A a) {
                    return ((Function)FlippedFunction.this.f2.apply(a)).apply(b);
                }
            };
        }

        public String toString() {
            return "FlippedFunction";
        }

        public int hashCode() {
            return this.f2.hashCode();
        }
    }

    private static class CurriedFunction<A, B, C>
    implements Function<A, Function<B, C>> {
        private final Function2<A, B, C> f2;

        CurriedFunction(Function2<A, B, C> f2) {
            this.f2 = f2;
        }

        public Function<B, C> apply(final A a) {
            return new Function<B, C>(){

                public C apply(B b) {
                    return CurriedFunction.this.f2.apply(a, b);
                }
            };
        }

        public String toString() {
            return "CurriedFunction";
        }

        public int hashCode() {
            return this.f2.hashCode();
        }
    }

    static class PartialComposer<A, B, C>
    implements Function<A, Option<C>> {
        private final Function<? super A, ? extends Option<? extends B>> ab;
        private final Function<? super B, ? extends Option<? extends C>> bc;

        PartialComposer(Function<? super A, ? extends Option<? extends B>> ab, Function<? super B, ? extends Option<? extends C>> bc) {
            this.ab = (Function)Preconditions.checkNotNull(ab);
            this.bc = (Function)Preconditions.checkNotNull(bc);
        }

        public Option<C> apply(A a) {
            return ((Option)this.ab.apply(a)).flatMap(this.bc);
        }

        public String toString() {
            return "PartialComposer";
        }

        public int hashCode() {
            return this.bc.hashCode() ^ this.ab.hashCode();
        }
    }

    static class Partial<A, B>
    implements Function<A, Option<B>> {
        private final Predicate<? super A> p;
        private final Function<? super A, ? extends B> f;

        Partial(Predicate<? super A> p, Function<? super A, ? extends B> f) {
            this.p = (Predicate)Preconditions.checkNotNull(p);
            this.f = (Function)Preconditions.checkNotNull(f);
        }

        public Option<B> apply(A a) {
            return this.p.apply(a) ? Option.option(this.f.apply(a)) : Option.none();
        }

        public String toString() {
            return "Partial";
        }

        public int hashCode() {
            return this.f.hashCode() ^ this.p.hashCode();
        }
    }

    static class InstanceOf<A, B>
    implements Function<A, Option<B>> {
        private final Class<B> cls;
        static final long serialVersionUID = 0L;

        InstanceOf(Class<B> cls) {
            this.cls = (Class)Preconditions.checkNotNull(cls);
        }

        public Option<B> apply(A a) {
            return this.cls.isAssignableFrom(a.getClass()) ? Option.some(this.cls.cast(a)) : Option.none();
        }

        public String toString() {
            return "InstanceOf";
        }

        public int hashCode() {
            return this.cls.hashCode();
        }
    }

    private static class FunctionComposition<A, B, C>
    implements Function<A, C>,
    Serializable {
        private final Function<? super B, ? extends C> g;
        private final Function<? super A, ? extends B> f;
        private static final long serialVersionUID = 0L;

        FunctionComposition(Function<? super B, ? extends C> g, Function<? super A, ? extends B> f) {
            this.g = (Function)Preconditions.checkNotNull(g);
            this.f = (Function)Preconditions.checkNotNull(f);
        }

        public C apply(@Nullable A a) {
            return (C)this.g.apply(this.f.apply(a));
        }

        public boolean equals(@Nullable Object obj) {
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
            return this.g.toString() + "(" + this.f.toString() + ")";
        }
    }
}

