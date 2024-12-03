/*
 * Decompiled with CFR 0.152.
 */
package io.atlassian.fugue;

import io.atlassian.fugue.Option;
import io.atlassian.fugue.Options;
import io.atlassian.fugue.Pair;
import io.atlassian.fugue.Unit;
import java.io.Serializable;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Functions {
    private Functions() {
    }

    public static <A, B, C> Function<A, C> compose(Function<? super B, ? extends C> g, Function<? super A, ? extends B> f) {
        return new FunctionComposition<A, B, C>(g, f);
    }

    public static <A, B, C> Function<C, B> ap(Function<C, A> ca, Function<C, Function<A, B>> cab) {
        return m -> ca.andThen((Function)cab.apply(m)).apply(m);
    }

    public static <F, T> T fold(BiFunction<? super T, F, T> f, T zero, Iterable<? extends F> elements) {
        T currentValue = zero;
        for (F element : elements) {
            currentValue = f.apply(currentValue, element);
        }
        return currentValue;
    }

    public static <F, S, T extends S> T fold(Function<Pair<S, F>, T> f, T zero, Iterable<? extends F> elements) {
        return (T)Functions.fold(Functions.toBiFunction(f), zero, elements);
    }

    public static <A, B> Function<Function<A, B>, B> apply(final A arg) {
        return new Function<Function<A, B>, B>(){

            @Override
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

            @Override
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

    public static <A, B, C> BiFunction<A, B, C> toBiFunction(final Function<Pair<A, B>, C> fpair) {
        Objects.requireNonNull(fpair);
        return new BiFunction<A, B, C>(){

            @Override
            public C apply(A a, B b) {
                return fpair.apply(Pair.pair(a, b));
            }

            public String toString() {
                return "ToBiFunction";
            }
        };
    }

    public static <A, B, C> Function<A, Function<B, C>> curried(BiFunction<A, B, C> f2) {
        Objects.requireNonNull(f2);
        return new CurriedFunction<A, B, C>(f2);
    }

    public static <A, B, C> Function<B, Function<A, C>> flip(Function<A, Function<B, C>> f2) {
        Objects.requireNonNull(f2);
        return new FlippedFunction<A, B, C>(f2);
    }

    public static <A, B> Function<A, Option<B>> mapNullToOption(Function<? super A, ? extends B> f) {
        return Functions.compose(Functions.nullToOption(), f);
    }

    public static <A> Function<A, Option<A>> nullToOption() {
        return Options.toOption();
    }

    public static <A, B> Function<A, B> weakMemoize(Function<A, B> f) {
        return WeakMemoizer.weakMemoizer(f);
    }

    static <D, R> Function<D, R> fromSupplier(Supplier<R> supplier) {
        return new FromSupplier(supplier);
    }

    public static <D> Function<D, Unit> fromConsumer(Consumer<D> consumer) {
        return new FromConsumer<D>(consumer);
    }

    public static <A> Function<A, A> identity() {
        return IdentityFunction.INSTANCE;
    }

    public static <A, B> Function<A, B> constant(B constant) {
        return from -> constant;
    }

    public static <A, B> Function<A, Option<B>> forMap(Map<A, B> map) {
        return a -> Option.option(map.get(a));
    }

    public static <A, B> Function<A, B> forMapWithDefault(Map<A, B> map, B defaultValue) {
        return Functions.forMap(map).andThen(o -> o.getOrElse(defaultValue));
    }

    public static <A, B> Function<A, Option<B>> matches(Function<? super A, ? extends Option<? extends B>> f1, Function<? super A, ? extends Option<? extends B>> f2) {
        return Functions.matcher(f1, f2);
    }

    public static <A, B> Function<A, Option<B>> matches(Function<? super A, ? extends Option<? extends B>> f1, Function<? super A, ? extends Option<? extends B>> f2, Function<? super A, ? extends Option<? extends B>> f3) {
        return Functions.matcher(f1, f2, f3);
    }

    public static <A, B> Function<A, Option<B>> matches(Function<? super A, ? extends Option<? extends B>> f1, Function<? super A, ? extends Option<? extends B>> f2, Function<? super A, ? extends Option<? extends B>> f3, Function<? super A, ? extends Option<? extends B>> f4) {
        return new Matcher(Arrays.asList(f1, f2, f3, f4));
    }

    @SafeVarargs
    public static <A, B> Function<A, Option<B>> matches(Function<? super A, ? extends Option<? extends B>> f1, Function<? super A, ? extends Option<? extends B>> f2, Function<? super A, ? extends Option<? extends B>> f3, Function<? super A, ? extends Option<? extends B>> f4, Function<? super A, ? extends Option<? extends B>> f5, Function<? super A, ? extends Option<? extends B>> ... fs) {
        Function[] matchingFunctions = new Function[5 + fs.length];
        matchingFunctions[0] = f1;
        matchingFunctions[1] = f2;
        matchingFunctions[2] = f3;
        matchingFunctions[3] = f4;
        matchingFunctions[4] = f5;
        System.arraycopy(fs, 0, matchingFunctions, 5, fs.length);
        return new Matcher(Arrays.asList(matchingFunctions));
    }

    @SafeVarargs
    private static <A, B> Matcher<A, B> matcher(Function<? super A, ? extends Option<? extends B>> ... fs) {
        Function[] dest = new Function[fs.length];
        System.arraycopy(fs, 0, dest, 0, fs.length);
        for (Function<A, Option<B>> function : fs) {
            if (function != null) continue;
            throw new NullPointerException("function value was null");
        }
        return new Matcher(Arrays.asList(dest));
    }

    static <A> Predicate<A> countingPredicate(final int n) {
        if (n < 0) {
            throw new IllegalArgumentException("n must be positive");
        }
        return new Predicate<A>(){
            int count;
            {
                this.count = n;
            }

            @Override
            public boolean test(A a) {
                return --this.count >= 0;
            }
        };
    }

    static final class WeakMemoizer<A, B>
    implements Function<A, B> {
        private final ConcurrentMap<A, MappedReference<A, B>> map;
        private final ReferenceQueue<B> queue = new ReferenceQueue();
        private final Function<A, B> delegate;

        static <A, B> WeakMemoizer<A, B> weakMemoizer(Function<A, B> delegate) {
            return new WeakMemoizer<A, B>(delegate);
        }

        WeakMemoizer(Function<A, B> delegate) {
            this.map = new ConcurrentHashMap<A, MappedReference<A, B>>();
            this.delegate = Objects.requireNonNull(delegate, "delegate");
        }

        @Override
        public B apply(A descriptor) {
            this.expungeStaleEntries();
            Objects.requireNonNull(descriptor, "descriptor");
            while (true) {
                MappedReference reference;
                if ((reference = (MappedReference)this.map.get(descriptor)) != null) {
                    Object value = reference.get();
                    if (value != null) {
                        return (B)value;
                    }
                    this.map.remove(descriptor, reference);
                }
                this.map.putIfAbsent(descriptor, new MappedReference<A, B>(descriptor, this.delegate.apply(descriptor), this.queue));
            }
        }

        private void expungeStaleEntries() {
            MappedReference ref;
            while ((ref = (MappedReference)this.queue.poll()) != null) {
                Object key = ref.getDescriptor();
                if (key == null) continue;
                this.map.remove(key, ref);
            }
        }

        static final class MappedReference<K, V>
        extends WeakReference<V> {
            private final K key;

            public MappedReference(K key, V value, ReferenceQueue<? super V> q) {
                super(Objects.requireNonNull(value, "value"), q);
                this.key = Objects.requireNonNull(key, "key");
            }

            final K getDescriptor() {
                return this.key;
            }
        }
    }

    static class Matcher<A, B>
    implements Function<A, Option<B>> {
        private final Iterable<Function<? super A, ? extends Option<? extends B>>> fs;

        Matcher(Iterable<Function<? super A, ? extends Option<? extends B>>> fs) {
            this.fs = Objects.requireNonNull(fs);
            if (!fs.iterator().hasNext()) {
                throw new IllegalArgumentException("Condition must be true but returned false instead");
            }
        }

        @Override
        public Option<B> apply(A a) {
            for (Function<A, Option<B>> f : this.fs) {
                Option<B> b = f.apply(a);
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

    private static enum IdentityFunction implements Function<Object, Object>
    {
        INSTANCE;


        @Override
        public Object apply(Object o) {
            return o;
        }

        public String toString() {
            return "identity";
        }
    }

    private static class FromConsumer<D>
    implements Function<D, Unit> {
        private final Consumer<D> consumer;

        FromConsumer(Consumer<D> consumer) {
            this.consumer = Objects.requireNonNull(consumer);
        }

        @Override
        public Unit apply(D d) {
            this.consumer.accept(d);
            return Unit.Unit();
        }

        public String toString() {
            return "FromConsumer";
        }

        public int hashCode() {
            return this.consumer.hashCode();
        }
    }

    static class FromSupplier<D, R>
    implements Function<D, R> {
        private final Supplier<R> supplier;

        FromSupplier(Supplier<R> supplier) {
            this.supplier = Objects.requireNonNull(supplier, "supplier");
        }

        @Override
        public R apply(D ignore) {
            return this.supplier.get();
        }

        public String toString() {
            return "FromSupplier";
        }

        public int hashCode() {
            return this.supplier.hashCode();
        }
    }

    private static class FlippedFunction<A, B, C>
    implements Function<B, Function<A, C>> {
        private final Function<A, Function<B, C>> f2;

        FlippedFunction(Function<A, Function<B, C>> f2) {
            this.f2 = f2;
        }

        @Override
        public Function<A, C> apply(B b) {
            return a -> this.f2.apply(a).apply(b);
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
        private final BiFunction<A, B, C> f2;

        CurriedFunction(BiFunction<A, B, C> f2) {
            this.f2 = f2;
        }

        @Override
        public Function<B, C> apply(A a) {
            return b -> this.f2.apply(a, b);
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
            this.ab = Objects.requireNonNull(ab);
            this.bc = Objects.requireNonNull(bc);
        }

        @Override
        public Option<C> apply(A a) {
            return this.ab.apply(a).flatMap(this.bc);
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
            this.p = Objects.requireNonNull(p);
            this.f = Objects.requireNonNull(f);
        }

        @Override
        public Option<B> apply(A a) {
            return this.p.test(a) ? Option.option(this.f.apply(a)) : Option.none();
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
            this.cls = Objects.requireNonNull(cls);
        }

        @Override
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
            this.g = Objects.requireNonNull(g);
            this.f = Objects.requireNonNull(f);
        }

        @Override
        public C apply(A a) {
            return this.g.apply(this.f.apply(a));
        }

        public boolean equals(Object obj) {
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

