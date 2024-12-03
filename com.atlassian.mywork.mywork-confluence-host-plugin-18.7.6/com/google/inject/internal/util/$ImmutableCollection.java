/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.util;

import com.google.inject.internal.util.$Nullable;
import com.google.inject.internal.util.$ObjectArrays;
import com.google.inject.internal.util.$UnmodifiableIterator;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class $ImmutableCollection<E>
implements Collection<E>,
Serializable {
    static final $ImmutableCollection<Object> EMPTY_IMMUTABLE_COLLECTION = new EmptyImmutableCollection();
    private static final Object[] EMPTY_ARRAY = new Object[0];
    private static final $UnmodifiableIterator<Object> EMPTY_ITERATOR = new $UnmodifiableIterator<Object>(){

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Object next() {
            throw new NoSuchElementException();
        }
    };

    $ImmutableCollection() {
    }

    @Override
    public abstract $UnmodifiableIterator<E> iterator();

    @Override
    public Object[] toArray() {
        Object[] newArray = new Object[this.size()];
        return this.toArray(newArray);
    }

    @Override
    public <T> T[] toArray(T[] other) {
        int size = this.size();
        if (other.length < size) {
            other = $ObjectArrays.newArray(other, size);
        } else if (other.length > size) {
            other[size] = null;
        }
        int index = 0;
        Iterator i$ = this.iterator();
        while (i$.hasNext()) {
            Object element;
            Object elementAsT = element = i$.next();
            other[index++] = elementAsT;
        }
        return other;
    }

    @Override
    public boolean contains(@$Nullable Object object) {
        if (object == null) {
            return false;
        }
        for (Object element : this) {
            if (!element.equals(object)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> targets) {
        for (Object target : targets) {
            if (this.contains(target)) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(this.size() * 16);
        sb.append('[');
        Iterator i = this.iterator();
        if (i.hasNext()) {
            sb.append(i.next());
        }
        while (i.hasNext()) {
            sb.append(", ");
            sb.append(i.next());
        }
        return sb.append(']').toString();
    }

    @Override
    public final boolean add(E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean remove(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean addAll(Collection<? extends E> newElements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean removeAll(Collection<?> oldElements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean retainAll(Collection<?> elementsToKeep) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void clear() {
        throw new UnsupportedOperationException();
    }

    Object writeReplace() {
        return new SerializedForm(this.toArray());
    }

    private static class SerializedForm
    implements Serializable {
        final Object[] elements;
        private static final long serialVersionUID = 0L;

        SerializedForm(Object[] elements) {
            this.elements = elements;
        }

        Object readResolve() {
            return this.elements.length == 0 ? EMPTY_IMMUTABLE_COLLECTION : new ArrayImmutableCollection<Object>((Object[])this.elements.clone());
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class ArrayImmutableCollection<E>
    extends $ImmutableCollection<E> {
        private final E[] elements;

        ArrayImmutableCollection(E[] elements) {
            this.elements = elements;
        }

        @Override
        public int size() {
            return this.elements.length;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public $UnmodifiableIterator<E> iterator() {
            return new $UnmodifiableIterator<E>(){
                int i = 0;

                @Override
                public boolean hasNext() {
                    return this.i < ArrayImmutableCollection.this.elements.length;
                }

                @Override
                public E next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    return ArrayImmutableCollection.this.elements[this.i++];
                }
            };
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class EmptyImmutableCollection
    extends $ImmutableCollection<Object> {
        private EmptyImmutableCollection() {
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public boolean contains(@$Nullable Object object) {
            return false;
        }

        @Override
        public $UnmodifiableIterator<Object> iterator() {
            return EMPTY_ITERATOR;
        }

        @Override
        public Object[] toArray() {
            return EMPTY_ARRAY;
        }

        @Override
        public <T> T[] toArray(T[] array) {
            if (array.length > 0) {
                array[0] = null;
            }
            return array;
        }
    }
}

