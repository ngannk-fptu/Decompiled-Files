/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.util;

import com.google.inject.internal.util.$Function;
import com.google.inject.internal.util.$Nullable;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.internal.util.$UnmodifiableIterator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class $Iterators {
    static final Iterator<Object> EMPTY_ITERATOR = new $UnmodifiableIterator<Object>(){

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Object next() {
            throw new NoSuchElementException();
        }
    };
    private static final ListIterator<Object> EMPTY_LIST_ITERATOR = new ListIterator<Object>(){

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public boolean hasPrevious() {
            return false;
        }

        @Override
        public int nextIndex() {
            return 0;
        }

        @Override
        public int previousIndex() {
            return -1;
        }

        @Override
        public Object next() {
            throw new NoSuchElementException();
        }

        @Override
        public Object previous() {
            throw new NoSuchElementException();
        }

        @Override
        public void set(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    };

    private $Iterators() {
    }

    public static <T> $UnmodifiableIterator<T> emptyIterator() {
        return ($UnmodifiableIterator)EMPTY_ITERATOR;
    }

    public static <T> ListIterator<T> emptyListIterator() {
        return EMPTY_LIST_ITERATOR;
    }

    public static <T> $UnmodifiableIterator<T> unmodifiableIterator(final Iterator<T> iterator) {
        $Preconditions.checkNotNull(iterator);
        return new $UnmodifiableIterator<T>(){

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public T next() {
                return iterator.next();
            }
        };
    }

    public static String toString(Iterator<?> iterator) {
        if (!iterator.hasNext()) {
            return "[]";
        }
        StringBuilder builder = new StringBuilder();
        builder.append('[').append(iterator.next());
        while (iterator.hasNext()) {
            builder.append(", ").append(iterator.next());
        }
        return builder.append(']').toString();
    }

    public static <T> T getOnlyElement(Iterator<T> iterator) {
        T first = iterator.next();
        if (!iterator.hasNext()) {
            return first;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("expected one element but was: <" + first);
        for (int i = 0; i < 4 && iterator.hasNext(); ++i) {
            sb.append(", " + iterator.next());
        }
        if (iterator.hasNext()) {
            sb.append(", ...");
        }
        sb.append(">");
        throw new IllegalArgumentException(sb.toString());
    }

    public static <T> Iterator<T> concat(final Iterator<? extends Iterator<? extends T>> inputs) {
        $Preconditions.checkNotNull(inputs);
        return new Iterator<T>(){
            Iterator<? extends T> current = $Iterators.emptyIterator();
            Iterator<? extends T> removeFrom;

            @Override
            public boolean hasNext() {
                while (!this.current.hasNext() && inputs.hasNext()) {
                    this.current = (Iterator)inputs.next();
                }
                return this.current.hasNext();
            }

            @Override
            public T next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.removeFrom = this.current;
                return this.current.next();
            }

            @Override
            public void remove() {
                $Preconditions.checkState(this.removeFrom != null, "no calls to next() since last call to remove()");
                this.removeFrom.remove();
                this.removeFrom = null;
            }
        };
    }

    public static <F, T> Iterator<T> transform(final Iterator<F> fromIterator, final $Function<? super F, ? extends T> function) {
        $Preconditions.checkNotNull(fromIterator);
        $Preconditions.checkNotNull(function);
        return new Iterator<T>(){

            @Override
            public boolean hasNext() {
                return fromIterator.hasNext();
            }

            @Override
            public T next() {
                Object from = fromIterator.next();
                return function.apply(from);
            }

            @Override
            public void remove() {
                fromIterator.remove();
            }
        };
    }

    public static <T> $UnmodifiableIterator<T> forArray(final T ... array) {
        return new $UnmodifiableIterator<T>(){
            final int length;
            int i;
            {
                this.length = array.length;
                this.i = 0;
            }

            @Override
            public boolean hasNext() {
                return this.i < this.length;
            }

            @Override
            public T next() {
                try {
                    Object t = array[this.i];
                    ++this.i;
                    return t;
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    throw new NoSuchElementException();
                }
            }
        };
    }

    public static <T> $UnmodifiableIterator<T> forArray(final T[] array, final int offset, int length) {
        $Preconditions.checkArgument(length >= 0);
        final int end = offset + length;
        $Preconditions.checkPositionIndexes(offset, end, array.length);
        return new $UnmodifiableIterator<T>(){
            int i;
            {
                this.i = offset;
            }

            @Override
            public boolean hasNext() {
                return this.i < end;
            }

            @Override
            public T next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                return array[this.i++];
            }
        };
    }

    public static <T> $UnmodifiableIterator<T> singletonIterator(final @$Nullable T value) {
        return new $UnmodifiableIterator<T>(){
            boolean done;

            @Override
            public boolean hasNext() {
                return !this.done;
            }

            @Override
            public T next() {
                if (this.done) {
                    throw new NoSuchElementException();
                }
                this.done = true;
                return value;
            }
        };
    }

    public static <T> Enumeration<T> asEnumeration(final Iterator<T> iterator) {
        $Preconditions.checkNotNull(iterator);
        return new Enumeration<T>(){

            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public T nextElement() {
                return iterator.next();
            }
        };
    }
}

