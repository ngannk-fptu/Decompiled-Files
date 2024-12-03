/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.j2objc.annotations.Weak
 *  javax.annotation.CheckForNull
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.NullnessCasts;
import com.google.common.collect.ParametricNullness;
import com.google.j2objc.annotations.Weak;
import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.LongConsumer;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class CollectSpliterators {
    private CollectSpliterators() {
    }

    static <T> Spliterator<T> indexed(int size, int extraCharacteristics, IntFunction<T> function) {
        return CollectSpliterators.indexed(size, extraCharacteristics, function, null);
    }

    static <T> Spliterator<T> indexed(int size, int extraCharacteristics, IntFunction<T> function, @CheckForNull Comparator<? super T> comparator) {
        if (comparator != null) {
            Preconditions.checkArgument((extraCharacteristics & 4) != 0);
        }
        class WithCharacteristics
        implements Spliterator<T> {
            private final Spliterator.OfInt delegate;
            final /* synthetic */ IntFunction val$function;
            final /* synthetic */ int val$extraCharacteristics;
            final /* synthetic */ Comparator val$comparator;

            WithCharacteristics(Spliterator.OfInt delegate) {
                this.val$function = intFunction;
                this.val$extraCharacteristics = n;
                this.val$comparator = comparator;
                this.delegate = delegate;
            }

            @Override
            public boolean tryAdvance(Consumer<? super T> action) {
                return this.delegate.tryAdvance((int i) -> action.accept((Object)this.val$function.apply(i)));
            }

            @Override
            public void forEachRemaining(Consumer<? super T> action) {
                this.delegate.forEachRemaining((int i) -> action.accept((Object)this.val$function.apply(i)));
            }

            @Override
            @CheckForNull
            public Spliterator<T> trySplit() {
                Spliterator.OfInt split = this.delegate.trySplit();
                return split == null ? null : new WithCharacteristics(split);
            }

            @Override
            public long estimateSize() {
                return this.delegate.estimateSize();
            }

            @Override
            public int characteristics() {
                return 0x4050 | this.val$extraCharacteristics;
            }

            @Override
            @CheckForNull
            public Comparator<? super T> getComparator() {
                if (this.hasCharacteristics(4)) {
                    return this.val$comparator;
                }
                throw new IllegalStateException();
            }
        }
        return new WithCharacteristics(IntStream.range(0, size).spliterator());
    }

    static <InElementT, OutElementT> Spliterator<OutElementT> map(final Spliterator<InElementT> fromSpliterator, final Function<? super InElementT, ? extends OutElementT> function) {
        Preconditions.checkNotNull(fromSpliterator);
        Preconditions.checkNotNull(function);
        return new Spliterator<OutElementT>(){

            @Override
            public boolean tryAdvance(Consumer<? super OutElementT> action) {
                return fromSpliterator.tryAdvance((? super T fromElement) -> action.accept((Object)function.apply(fromElement)));
            }

            @Override
            public void forEachRemaining(Consumer<? super OutElementT> action) {
                fromSpliterator.forEachRemaining((? super T fromElement) -> action.accept((Object)function.apply(fromElement)));
            }

            @Override
            @CheckForNull
            public Spliterator<OutElementT> trySplit() {
                Spliterator fromSplit = fromSpliterator.trySplit();
                return fromSplit != null ? CollectSpliterators.map(fromSplit, function) : null;
            }

            @Override
            public long estimateSize() {
                return fromSpliterator.estimateSize();
            }

            @Override
            public int characteristics() {
                return fromSpliterator.characteristics() & 0xFFFFFEFA;
            }
        };
    }

    static <T> Spliterator<T> filter(final Spliterator<T> fromSpliterator, final Predicate<? super T> predicate) {
        Preconditions.checkNotNull(fromSpliterator);
        Preconditions.checkNotNull(predicate);
        class Splitr
        implements Spliterator<T>,
        Consumer<T> {
            @CheckForNull
            T holder = null;

            Splitr() {
            }

            @Override
            public void accept(@ParametricNullness T t) {
                this.holder = t;
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public boolean tryAdvance(Consumer<? super T> action) {
                while (fromSpliterator.tryAdvance(this)) {
                    try {
                        Object next = NullnessCasts.uncheckedCastNullableTToT(this.holder);
                        if (!predicate.test(next)) continue;
                        action.accept(next);
                        boolean bl = true;
                        return bl;
                    }
                    finally {
                        this.holder = null;
                    }
                }
                return false;
            }

            @Override
            @CheckForNull
            public Spliterator<T> trySplit() {
                Spliterator fromSplit = fromSpliterator.trySplit();
                return fromSplit == null ? null : CollectSpliterators.filter(fromSplit, predicate);
            }

            @Override
            public long estimateSize() {
                return fromSpliterator.estimateSize() / 2L;
            }

            @Override
            @CheckForNull
            public Comparator<? super T> getComparator() {
                return fromSpliterator.getComparator();
            }

            @Override
            public int characteristics() {
                return fromSpliterator.characteristics() & 0x115;
            }
        }
        return new Splitr();
    }

    static <InElementT, OutElementT> Spliterator<OutElementT> flatMap(Spliterator<InElementT> fromSpliterator, Function<? super InElementT, Spliterator<OutElementT>> function, int topCharacteristics, long topSize) {
        Preconditions.checkArgument((topCharacteristics & 0x4000) == 0, "flatMap does not support SUBSIZED characteristic");
        Preconditions.checkArgument((topCharacteristics & 4) == 0, "flatMap does not support SORTED characteristic");
        Preconditions.checkNotNull(fromSpliterator);
        Preconditions.checkNotNull(function);
        return new FlatMapSpliteratorOfObject<InElementT, OutElementT>(null, fromSpliterator, function, topCharacteristics, topSize);
    }

    static <InElementT> Spliterator.OfInt flatMapToInt(Spliterator<InElementT> fromSpliterator, Function<? super InElementT, Spliterator.OfInt> function, int topCharacteristics, long topSize) {
        Preconditions.checkArgument((topCharacteristics & 0x4000) == 0, "flatMap does not support SUBSIZED characteristic");
        Preconditions.checkArgument((topCharacteristics & 4) == 0, "flatMap does not support SORTED characteristic");
        Preconditions.checkNotNull(fromSpliterator);
        Preconditions.checkNotNull(function);
        return new FlatMapSpliteratorOfInt<InElementT>(null, fromSpliterator, function, topCharacteristics, topSize);
    }

    static <InElementT> Spliterator.OfLong flatMapToLong(Spliterator<InElementT> fromSpliterator, Function<? super InElementT, Spliterator.OfLong> function, int topCharacteristics, long topSize) {
        Preconditions.checkArgument((topCharacteristics & 0x4000) == 0, "flatMap does not support SUBSIZED characteristic");
        Preconditions.checkArgument((topCharacteristics & 4) == 0, "flatMap does not support SORTED characteristic");
        Preconditions.checkNotNull(fromSpliterator);
        Preconditions.checkNotNull(function);
        return new FlatMapSpliteratorOfLong<InElementT>(null, fromSpliterator, function, topCharacteristics, topSize);
    }

    static <InElementT> Spliterator.OfDouble flatMapToDouble(Spliterator<InElementT> fromSpliterator, Function<? super InElementT, Spliterator.OfDouble> function, int topCharacteristics, long topSize) {
        Preconditions.checkArgument((topCharacteristics & 0x4000) == 0, "flatMap does not support SUBSIZED characteristic");
        Preconditions.checkArgument((topCharacteristics & 4) == 0, "flatMap does not support SORTED characteristic");
        Preconditions.checkNotNull(fromSpliterator);
        Preconditions.checkNotNull(function);
        return new FlatMapSpliteratorOfDouble<InElementT>(null, fromSpliterator, function, topCharacteristics, topSize);
    }

    static final class FlatMapSpliteratorOfDouble<InElementT>
    extends FlatMapSpliteratorOfPrimitive<InElementT, Double, DoubleConsumer, Spliterator.OfDouble>
    implements Spliterator.OfDouble {
        FlatMapSpliteratorOfDouble(@CheckForNull Spliterator.OfDouble prefix, Spliterator<InElementT> from, Function<? super InElementT, Spliterator.OfDouble> function, int characteristics, long estimatedSize) {
            super(prefix, from, function, FlatMapSpliteratorOfDouble::new, characteristics, estimatedSize);
        }
    }

    static final class FlatMapSpliteratorOfLong<InElementT>
    extends FlatMapSpliteratorOfPrimitive<InElementT, Long, LongConsumer, Spliterator.OfLong>
    implements Spliterator.OfLong {
        FlatMapSpliteratorOfLong(@CheckForNull Spliterator.OfLong prefix, Spliterator<InElementT> from, Function<? super InElementT, Spliterator.OfLong> function, int characteristics, long estimatedSize) {
            super(prefix, from, function, FlatMapSpliteratorOfLong::new, characteristics, estimatedSize);
        }
    }

    static final class FlatMapSpliteratorOfInt<InElementT>
    extends FlatMapSpliteratorOfPrimitive<InElementT, Integer, IntConsumer, Spliterator.OfInt>
    implements Spliterator.OfInt {
        FlatMapSpliteratorOfInt(@CheckForNull Spliterator.OfInt prefix, Spliterator<InElementT> from, Function<? super InElementT, Spliterator.OfInt> function, int characteristics, long estimatedSize) {
            super(prefix, from, function, FlatMapSpliteratorOfInt::new, characteristics, estimatedSize);
        }
    }

    static abstract class FlatMapSpliteratorOfPrimitive<InElementT, OutElementT, OutConsumerT, OutSpliteratorT extends Spliterator.OfPrimitive<OutElementT, OutConsumerT, OutSpliteratorT>>
    extends FlatMapSpliterator<InElementT, OutElementT, OutSpliteratorT>
    implements Spliterator.OfPrimitive<OutElementT, OutConsumerT, OutSpliteratorT> {
        FlatMapSpliteratorOfPrimitive(@CheckForNull OutSpliteratorT prefix, Spliterator<InElementT> from, Function<? super InElementT, OutSpliteratorT> function, FlatMapSpliterator.Factory<InElementT, OutSpliteratorT> factory, int characteristics, long estimatedSize) {
            super(prefix, from, function, factory, characteristics, estimatedSize);
        }

        @Override
        public final boolean tryAdvance(OutConsumerT action) {
            do {
                if (this.prefix != null && ((Spliterator.OfPrimitive)this.prefix).tryAdvance(action)) {
                    if (this.estimatedSize != Long.MAX_VALUE) {
                        --this.estimatedSize;
                    }
                    return true;
                }
                this.prefix = null;
            } while (this.from.tryAdvance((? super T fromElement) -> {
                this.prefix = (Spliterator)this.function.apply(fromElement);
            }));
            return false;
        }

        @Override
        public final void forEachRemaining(OutConsumerT action) {
            if (this.prefix != null) {
                ((Spliterator.OfPrimitive)this.prefix).forEachRemaining(action);
                this.prefix = null;
            }
            this.from.forEachRemaining((? super T fromElement) -> {
                Spliterator.OfPrimitive elements = (Spliterator.OfPrimitive)this.function.apply(fromElement);
                if (elements != null) {
                    elements.forEachRemaining(action);
                }
            });
            this.estimatedSize = 0L;
        }
    }

    static final class FlatMapSpliteratorOfObject<InElementT, OutElementT>
    extends FlatMapSpliterator<InElementT, OutElementT, Spliterator<OutElementT>> {
        FlatMapSpliteratorOfObject(@CheckForNull Spliterator<OutElementT> prefix, Spliterator<InElementT> from, Function<? super InElementT, Spliterator<OutElementT>> function, int characteristics, long estimatedSize) {
            super(prefix, from, function, FlatMapSpliteratorOfObject::new, characteristics, estimatedSize);
        }
    }

    static abstract class FlatMapSpliterator<InElementT, OutElementT, OutSpliteratorT extends Spliterator<OutElementT>>
    implements Spliterator<OutElementT> {
        @CheckForNull
        @Weak
        OutSpliteratorT prefix;
        final Spliterator<InElementT> from;
        final Function<? super InElementT, OutSpliteratorT> function;
        final Factory<InElementT, OutSpliteratorT> factory;
        int characteristics;
        long estimatedSize;

        FlatMapSpliterator(@CheckForNull OutSpliteratorT prefix, Spliterator<InElementT> from, Function<? super InElementT, OutSpliteratorT> function, Factory<InElementT, OutSpliteratorT> factory, int characteristics, long estimatedSize) {
            this.prefix = prefix;
            this.from = from;
            this.function = function;
            this.factory = factory;
            this.characteristics = characteristics;
            this.estimatedSize = estimatedSize;
        }

        @Override
        public final boolean tryAdvance(Consumer<? super OutElementT> action) {
            do {
                if (this.prefix != null && this.prefix.tryAdvance(action)) {
                    if (this.estimatedSize != Long.MAX_VALUE) {
                        --this.estimatedSize;
                    }
                    return true;
                }
                this.prefix = null;
            } while (this.from.tryAdvance((? super T fromElement) -> {
                this.prefix = (Spliterator)this.function.apply(fromElement);
            }));
            return false;
        }

        @Override
        public final void forEachRemaining(Consumer<? super OutElementT> action) {
            if (this.prefix != null) {
                this.prefix.forEachRemaining(action);
                this.prefix = null;
            }
            this.from.forEachRemaining((? super T fromElement) -> {
                Spliterator elements = (Spliterator)this.function.apply(fromElement);
                if (elements != null) {
                    elements.forEachRemaining(action);
                }
            });
            this.estimatedSize = 0L;
        }

        @CheckForNull
        public final OutSpliteratorT trySplit() {
            Spliterator<InElementT> fromSplit = this.from.trySplit();
            if (fromSplit != null) {
                int splitCharacteristics = this.characteristics & 0xFFFFFFBF;
                long estSplitSize = this.estimateSize();
                if (estSplitSize < Long.MAX_VALUE) {
                    this.estimatedSize -= (estSplitSize /= 2L);
                    this.characteristics = splitCharacteristics;
                }
                OutSpliteratorT result = this.factory.newFlatMapSpliterator(this.prefix, fromSplit, this.function, splitCharacteristics, estSplitSize);
                this.prefix = null;
                return result;
            }
            if (this.prefix != null) {
                OutSpliteratorT result = this.prefix;
                this.prefix = null;
                return result;
            }
            return null;
        }

        @Override
        public final long estimateSize() {
            if (this.prefix != null) {
                this.estimatedSize = Math.max(this.estimatedSize, this.prefix.estimateSize());
            }
            return Math.max(this.estimatedSize, 0L);
        }

        @Override
        public final int characteristics() {
            return this.characteristics;
        }

        @FunctionalInterface
        static interface Factory<InElementT, OutSpliteratorT extends Spliterator<?>> {
            public OutSpliteratorT newFlatMapSpliterator(@CheckForNull OutSpliteratorT var1, Spliterator<InElementT> var2, Function<? super InElementT, OutSpliteratorT> var3, int var4, long var5);
        }
    }
}

