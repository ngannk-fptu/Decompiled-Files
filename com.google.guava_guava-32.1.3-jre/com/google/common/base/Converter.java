/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CheckReturnValue
 *  com.google.errorprone.annotations.ForOverride
 *  com.google.errorprone.annotations.InlineMe
 *  com.google.errorprone.annotations.concurrent.LazyInit
 *  com.google.j2objc.annotations.RetainedWith
 *  javax.annotation.CheckForNull
 */
package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.ElementTypesAreNonnullByDefault;
import com.google.common.base.Function;
import com.google.common.base.NullnessCasts;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CheckReturnValue;
import com.google.errorprone.annotations.ForOverride;
import com.google.errorprone.annotations.InlineMe;
import com.google.errorprone.annotations.concurrent.LazyInit;
import com.google.j2objc.annotations.RetainedWith;
import java.io.Serializable;
import java.util.Iterator;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class Converter<A, B>
implements Function<A, B> {
    private final boolean handleNullAutomatically;
    @LazyInit
    @CheckForNull
    @RetainedWith
    private transient Converter<B, A> reverse;

    protected Converter() {
        this(true);
    }

    Converter(boolean handleNullAutomatically) {
        this.handleNullAutomatically = handleNullAutomatically;
    }

    @ForOverride
    protected abstract B doForward(A var1);

    @ForOverride
    protected abstract A doBackward(B var1);

    @CheckForNull
    public final B convert(@CheckForNull A a) {
        return this.correctedDoForward(a);
    }

    @CheckForNull
    B correctedDoForward(@CheckForNull A a) {
        if (this.handleNullAutomatically) {
            return a == null ? null : (B)Preconditions.checkNotNull(this.doForward(a));
        }
        return this.unsafeDoForward(a);
    }

    @CheckForNull
    A correctedDoBackward(@CheckForNull B b) {
        if (this.handleNullAutomatically) {
            return b == null ? null : (A)Preconditions.checkNotNull(this.doBackward(b));
        }
        return this.unsafeDoBackward(b);
    }

    @CheckForNull
    private B unsafeDoForward(@CheckForNull A a) {
        return this.doForward(NullnessCasts.uncheckedCastNullableTToT(a));
    }

    @CheckForNull
    private A unsafeDoBackward(@CheckForNull B b) {
        return this.doBackward(NullnessCasts.uncheckedCastNullableTToT(b));
    }

    public Iterable<B> convertAll(final Iterable<? extends A> fromIterable) {
        Preconditions.checkNotNull(fromIterable, "fromIterable");
        return new Iterable<B>(){

            @Override
            public Iterator<B> iterator() {
                return new Iterator<B>(){
                    private final Iterator<? extends A> fromIterator;
                    {
                        this.fromIterator = fromIterable.iterator();
                    }

                    @Override
                    public boolean hasNext() {
                        return this.fromIterator.hasNext();
                    }

                    @Override
                    public B next() {
                        return Converter.this.convert(this.fromIterator.next());
                    }

                    @Override
                    public void remove() {
                        this.fromIterator.remove();
                    }
                };
            }
        };
    }

    @CheckReturnValue
    public Converter<B, A> reverse() {
        Converter<A, B> result = this.reverse;
        return result == null ? (this.reverse = new ReverseConverter(this)) : result;
    }

    @Override
    public final <C> Converter<A, C> andThen(Converter<B, C> secondConverter) {
        return this.doAndThen(secondConverter);
    }

    <C> Converter<A, C> doAndThen(Converter<B, C> secondConverter) {
        return new ConverterComposition(this, Preconditions.checkNotNull(secondConverter));
    }

    @Override
    @Deprecated
    @InlineMe(replacement="this.convert(a)")
    public final B apply(A a) {
        return this.convert(a);
    }

    @Override
    public boolean equals(@CheckForNull Object object) {
        return super.equals(object);
    }

    public static <A, B> Converter<A, B> from(Function<? super A, ? extends B> forwardFunction, Function<? super B, ? extends A> backwardFunction) {
        return new FunctionBasedConverter(forwardFunction, backwardFunction);
    }

    public static <T> Converter<T, T> identity() {
        return IdentityConverter.INSTANCE;
    }

    private static final class IdentityConverter<T>
    extends Converter<T, T>
    implements Serializable {
        static final IdentityConverter<?> INSTANCE = new IdentityConverter();
        private static final long serialVersionUID = 0L;

        private IdentityConverter() {
        }

        @Override
        protected T doForward(T t) {
            return t;
        }

        @Override
        protected T doBackward(T t) {
            return t;
        }

        public IdentityConverter<T> reverse() {
            return this;
        }

        @Override
        <S> Converter<T, S> doAndThen(Converter<T, S> otherConverter) {
            return Preconditions.checkNotNull(otherConverter, "otherConverter");
        }

        public String toString() {
            return "Converter.identity()";
        }

        private Object readResolve() {
            return INSTANCE;
        }
    }

    private static final class FunctionBasedConverter<A, B>
    extends Converter<A, B>
    implements Serializable {
        private final Function<? super A, ? extends B> forwardFunction;
        private final Function<? super B, ? extends A> backwardFunction;

        private FunctionBasedConverter(Function<? super A, ? extends B> forwardFunction, Function<? super B, ? extends A> backwardFunction) {
            this.forwardFunction = Preconditions.checkNotNull(forwardFunction);
            this.backwardFunction = Preconditions.checkNotNull(backwardFunction);
        }

        @Override
        protected B doForward(A a) {
            return this.forwardFunction.apply(a);
        }

        @Override
        protected A doBackward(B b) {
            return this.backwardFunction.apply(b);
        }

        @Override
        public boolean equals(@CheckForNull Object object) {
            if (object instanceof FunctionBasedConverter) {
                FunctionBasedConverter that = (FunctionBasedConverter)object;
                return this.forwardFunction.equals(that.forwardFunction) && this.backwardFunction.equals(that.backwardFunction);
            }
            return false;
        }

        public int hashCode() {
            return this.forwardFunction.hashCode() * 31 + this.backwardFunction.hashCode();
        }

        public String toString() {
            return "Converter.from(" + this.forwardFunction + ", " + this.backwardFunction + ")";
        }
    }

    private static final class ConverterComposition<A, B, C>
    extends Converter<A, C>
    implements Serializable {
        final Converter<A, B> first;
        final Converter<B, C> second;
        private static final long serialVersionUID = 0L;

        ConverterComposition(Converter<A, B> first, Converter<B, C> second) {
            this.first = first;
            this.second = second;
        }

        @Override
        protected C doForward(A a) {
            throw new AssertionError();
        }

        @Override
        protected A doBackward(C c) {
            throw new AssertionError();
        }

        @Override
        @CheckForNull
        C correctedDoForward(@CheckForNull A a) {
            return this.second.correctedDoForward(this.first.correctedDoForward(a));
        }

        @Override
        @CheckForNull
        A correctedDoBackward(@CheckForNull C c) {
            return this.first.correctedDoBackward(this.second.correctedDoBackward(c));
        }

        @Override
        public boolean equals(@CheckForNull Object object) {
            if (object instanceof ConverterComposition) {
                ConverterComposition that = (ConverterComposition)object;
                return this.first.equals(that.first) && this.second.equals(that.second);
            }
            return false;
        }

        public int hashCode() {
            return 31 * this.first.hashCode() + this.second.hashCode();
        }

        public String toString() {
            return this.first + ".andThen(" + this.second + ")";
        }
    }

    private static final class ReverseConverter<A, B>
    extends Converter<B, A>
    implements Serializable {
        final Converter<A, B> original;
        private static final long serialVersionUID = 0L;

        ReverseConverter(Converter<A, B> original) {
            this.original = original;
        }

        @Override
        protected A doForward(B b) {
            throw new AssertionError();
        }

        @Override
        protected B doBackward(A a) {
            throw new AssertionError();
        }

        @Override
        @CheckForNull
        A correctedDoForward(@CheckForNull B b) {
            return this.original.correctedDoBackward(b);
        }

        @Override
        @CheckForNull
        B correctedDoBackward(@CheckForNull A a) {
            return this.original.correctedDoForward(a);
        }

        @Override
        public Converter<A, B> reverse() {
            return this.original;
        }

        @Override
        public boolean equals(@CheckForNull Object object) {
            if (object instanceof ReverseConverter) {
                ReverseConverter that = (ReverseConverter)object;
                return this.original.equals(that.original);
            }
            return false;
        }

        public int hashCode() {
            return ~this.original.hashCode();
        }

        public String toString() {
            return this.original + ".reverse()";
        }
    }
}

