/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.util;

import com.google.inject.internal.util.$ImmutableCollection;
import com.google.inject.internal.util.$Iterators;
import com.google.inject.internal.util.$Lists;
import com.google.inject.internal.util.$Nullable;
import com.google.inject.internal.util.$ObjectArrays;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.internal.util.$UnmodifiableIterator;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class $ImmutableList<E>
extends $ImmutableCollection<E>
implements List<E>,
RandomAccess {
    private static final $ImmutableList<?> EMPTY_IMMUTABLE_LIST = new EmptyImmutableList();

    public static <E> $ImmutableList<E> of() {
        return EMPTY_IMMUTABLE_LIST;
    }

    public static <E> $ImmutableList<E> of(E element) {
        return new RegularImmutableList($ImmutableList.copyIntoArray(element));
    }

    public static <E> $ImmutableList<E> of(E e1, E e2) {
        return new RegularImmutableList($ImmutableList.copyIntoArray(e1, e2));
    }

    public static <E> $ImmutableList<E> of(E e1, E e2, E e3) {
        return new RegularImmutableList($ImmutableList.copyIntoArray(e1, e2, e3));
    }

    public static <E> $ImmutableList<E> of(E e1, E e2, E e3, E e4) {
        return new RegularImmutableList($ImmutableList.copyIntoArray(e1, e2, e3, e4));
    }

    public static <E> $ImmutableList<E> of(E e1, E e2, E e3, E e4, E e5) {
        return new RegularImmutableList($ImmutableList.copyIntoArray(e1, e2, e3, e4, e5));
    }

    public static <E> $ImmutableList<E> of(E ... elements) {
        return elements.length == 0 ? $ImmutableList.of() : new RegularImmutableList($ImmutableList.copyIntoArray(elements));
    }

    public static <E> $ImmutableList<E> copyOf(Iterable<? extends E> elements) {
        if (elements instanceof $ImmutableList) {
            $ImmutableList list = ($ImmutableList)elements;
            return list;
        }
        if (elements instanceof Collection) {
            Collection coll = (Collection)elements;
            return $ImmutableList.copyOfInternal(coll);
        }
        return $ImmutableList.copyOfInternal($Lists.newArrayList(elements));
    }

    public static <E> $ImmutableList<E> copyOf(Iterator<? extends E> elements) {
        return $ImmutableList.copyOfInternal($Lists.newArrayList(elements));
    }

    private static <E> $ImmutableList<E> copyOfInternal(ArrayList<? extends E> list) {
        return list.isEmpty() ? $ImmutableList.of() : new RegularImmutableList($ImmutableList.nullChecked(list.toArray()));
    }

    private static Object[] nullChecked(Object[] array) {
        int len = array.length;
        for (int i = 0; i < len; ++i) {
            if (array[i] != null) continue;
            throw new NullPointerException("at index " + i);
        }
        return array;
    }

    private static <E> $ImmutableList<E> copyOfInternal(Collection<? extends E> collection) {
        int size = collection.size();
        return size == 0 ? $ImmutableList.of() : $ImmutableList.createFromIterable(collection, size);
    }

    private $ImmutableList() {
    }

    @Override
    public abstract $UnmodifiableIterator<E> iterator();

    @Override
    public abstract int indexOf(@$Nullable Object var1);

    @Override
    public abstract int lastIndexOf(@$Nullable Object var1);

    @Override
    public abstract $ImmutableList<E> subList(int var1, int var2);

    @Override
    public final boolean addAll(int index, Collection<? extends E> newElements) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final E set(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void add(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final E remove(int index) {
        throw new UnsupportedOperationException();
    }

    private static Object[] copyIntoArray(Object ... source) {
        Object[] array = new Object[source.length];
        int index = 0;
        for (Object element : source) {
            if (element == null) {
                throw new NullPointerException("at index " + index);
            }
            array[index++] = element;
        }
        return array;
    }

    private static <E> $ImmutableList<E> createFromIterable(Iterable<?> source, int estimatedSize) {
        Object[] array = new Object[estimatedSize];
        int index = 0;
        for (Object element : source) {
            if (index == estimatedSize) {
                estimatedSize = (estimatedSize / 2 + 1) * 3;
                array = $ImmutableList.copyOf(array, estimatedSize);
            }
            if (element == null) {
                throw new NullPointerException("at index " + index);
            }
            array[index++] = element;
        }
        if (index == 0) {
            return $ImmutableList.of();
        }
        if (index != estimatedSize) {
            array = $ImmutableList.copyOf(array, index);
        }
        return new RegularImmutableList(array, 0, index);
    }

    private static Object[] copyOf(Object[] oldArray, int newSize) {
        Object[] newArray = new Object[newSize];
        System.arraycopy(oldArray, 0, newArray, 0, Math.min(oldArray.length, newSize));
        return newArray;
    }

    private void readObject(ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("Use SerializedForm");
    }

    @Override
    Object writeReplace() {
        return new SerializedForm(this.toArray());
    }

    public static <E> Builder<E> builder() {
        return new Builder();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class Builder<E> {
        private final ArrayList<E> contents = $Lists.newArrayList();

        public Builder<E> add(E element) {
            $Preconditions.checkNotNull(element, "element cannot be null");
            this.contents.add(element);
            return this;
        }

        public Builder<E> addAll(Iterable<? extends E> elements) {
            if (elements instanceof Collection) {
                Collection collection = (Collection)elements;
                this.contents.ensureCapacity(this.contents.size() + collection.size());
            }
            for (E elem : elements) {
                $Preconditions.checkNotNull(elem, "elements contains a null");
                this.contents.add(elem);
            }
            return this;
        }

        public $ImmutableList<E> build() {
            return $ImmutableList.copyOf(this.contents);
        }
    }

    private static class SerializedForm
    implements Serializable {
        final Object[] elements;
        private static final long serialVersionUID = 0L;

        SerializedForm(Object[] elements) {
            this.elements = elements;
        }

        Object readResolve() {
            return $ImmutableList.of(this.elements);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class RegularImmutableList<E>
    extends $ImmutableList<E> {
        private final int offset;
        private final int size;
        private final Object[] array;

        private RegularImmutableList(Object[] array, int offset, int size) {
            this.offset = offset;
            this.size = size;
            this.array = array;
        }

        private RegularImmutableList(Object[] array) {
            this(array, 0, array.length);
        }

        @Override
        public int size() {
            return this.size;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean contains(Object target) {
            return this.indexOf(target) != -1;
        }

        @Override
        public $UnmodifiableIterator<E> iterator() {
            return $Iterators.forArray(this.array, this.offset, this.size);
        }

        @Override
        public Object[] toArray() {
            Object[] newArray = new Object[this.size()];
            System.arraycopy(this.array, this.offset, newArray, 0, this.size);
            return newArray;
        }

        @Override
        public <T> T[] toArray(T[] other) {
            if (other.length < this.size) {
                other = $ObjectArrays.newArray(other, this.size);
            } else if (other.length > this.size) {
                other[this.size] = null;
            }
            System.arraycopy(this.array, this.offset, other, 0, this.size);
            return other;
        }

        @Override
        public E get(int index) {
            $Preconditions.checkElementIndex(index, this.size);
            return (E)this.array[index + this.offset];
        }

        @Override
        public int indexOf(Object target) {
            if (target != null) {
                for (int i = this.offset; i < this.offset + this.size; ++i) {
                    if (!this.array[i].equals(target)) continue;
                    return i - this.offset;
                }
            }
            return -1;
        }

        @Override
        public int lastIndexOf(Object target) {
            if (target != null) {
                for (int i = this.offset + this.size - 1; i >= this.offset; --i) {
                    if (!this.array[i].equals(target)) continue;
                    return i - this.offset;
                }
            }
            return -1;
        }

        @Override
        public $ImmutableList<E> subList(int fromIndex, int toIndex) {
            $Preconditions.checkPositionIndexes(fromIndex, toIndex, this.size);
            return fromIndex == toIndex ? $ImmutableList.of() : new RegularImmutableList<E>(this.array, this.offset + fromIndex, toIndex - fromIndex);
        }

        @Override
        public ListIterator<E> listIterator() {
            return this.listIterator(0);
        }

        @Override
        public ListIterator<E> listIterator(final int start) {
            $Preconditions.checkPositionIndex(start, this.size);
            return new ListIterator<E>(){
                int index;
                {
                    this.index = start;
                }

                @Override
                public boolean hasNext() {
                    return this.index < RegularImmutableList.this.size;
                }

                @Override
                public boolean hasPrevious() {
                    return this.index > 0;
                }

                @Override
                public int nextIndex() {
                    return this.index;
                }

                @Override
                public int previousIndex() {
                    return this.index - 1;
                }

                @Override
                public E next() {
                    Object result;
                    try {
                        result = RegularImmutableList.this.get(this.index);
                    }
                    catch (IndexOutOfBoundsException rethrown) {
                        throw new NoSuchElementException();
                    }
                    ++this.index;
                    return result;
                }

                @Override
                public E previous() {
                    Object result;
                    try {
                        result = RegularImmutableList.this.get(this.index - 1);
                    }
                    catch (IndexOutOfBoundsException rethrown) {
                        throw new NoSuchElementException();
                    }
                    --this.index;
                    return result;
                }

                @Override
                public void set(E o) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void add(E o) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        @Override
        public boolean equals(@$Nullable Object object) {
            if (object == this) {
                return true;
            }
            if (!(object instanceof List)) {
                return false;
            }
            List that = (List)object;
            if (this.size() != that.size()) {
                return false;
            }
            int index = this.offset;
            if (object instanceof RegularImmutableList) {
                RegularImmutableList other = (RegularImmutableList)object;
                for (int i = other.offset; i < other.offset + other.size; ++i) {
                    if (this.array[index++].equals(other.array[i])) continue;
                    return false;
                }
            } else {
                for (Object element : that) {
                    if (this.array[index++].equals(element)) continue;
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hashCode = 1;
            for (int i = this.offset; i < this.offset + this.size; ++i) {
                hashCode = 31 * hashCode + this.array[i].hashCode();
            }
            return hashCode;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(this.size() * 16);
            sb.append('[').append(this.array[this.offset]);
            for (int i = this.offset + 1; i < this.offset + this.size; ++i) {
                sb.append(", ").append(this.array[i]);
            }
            return sb.append(']').toString();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class EmptyImmutableList
    extends $ImmutableList<Object> {
        private static final Object[] EMPTY_ARRAY = new Object[0];

        private EmptyImmutableList() {
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
        public boolean contains(Object target) {
            return false;
        }

        @Override
        public $UnmodifiableIterator<Object> iterator() {
            return $Iterators.emptyIterator();
        }

        @Override
        public Object[] toArray() {
            return EMPTY_ARRAY;
        }

        @Override
        public <T> T[] toArray(T[] a) {
            if (a.length > 0) {
                a[0] = null;
            }
            return a;
        }

        @Override
        public Object get(int index) {
            $Preconditions.checkElementIndex(index, 0);
            throw new AssertionError((Object)"unreachable");
        }

        @Override
        public int indexOf(Object target) {
            return -1;
        }

        @Override
        public int lastIndexOf(Object target) {
            return -1;
        }

        @Override
        public $ImmutableList<Object> subList(int fromIndex, int toIndex) {
            $Preconditions.checkPositionIndexes(fromIndex, toIndex, 0);
            return this;
        }

        @Override
        public ListIterator<Object> listIterator() {
            return $Iterators.emptyListIterator();
        }

        @Override
        public ListIterator<Object> listIterator(int start) {
            $Preconditions.checkPositionIndex(start, 0);
            return $Iterators.emptyListIterator();
        }

        @Override
        public boolean containsAll(Collection<?> targets) {
            return targets.isEmpty();
        }

        @Override
        public boolean equals(@$Nullable Object object) {
            if (object instanceof List) {
                List that = (List)object;
                return that.isEmpty();
            }
            return false;
        }

        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        public String toString() {
            return "[]";
        }
    }
}

