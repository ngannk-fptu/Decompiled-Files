/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.util;

import com.google.inject.internal.util.$AbstractIterator;
import com.google.inject.internal.util.$Collections2;
import com.google.inject.internal.util.$Hashing;
import com.google.inject.internal.util.$ImmutableCollection;
import com.google.inject.internal.util.$Iterators;
import com.google.inject.internal.util.$Lists;
import com.google.inject.internal.util.$Nullable;
import com.google.inject.internal.util.$ObjectArrays;
import com.google.inject.internal.util.$Preconditions;
import com.google.inject.internal.util.$UnmodifiableIterator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class $ImmutableSet<E>
extends $ImmutableCollection<E>
implements Set<E> {
    private static final $ImmutableSet<?> EMPTY_IMMUTABLE_SET = new EmptyImmutableSet();

    public static <E> $ImmutableSet<E> of() {
        return EMPTY_IMMUTABLE_SET;
    }

    public static <E> $ImmutableSet<E> of(E element) {
        return new SingletonImmutableSet<E>(element, element.hashCode());
    }

    public static <E> $ImmutableSet<E> of(E ... elements) {
        switch (elements.length) {
            case 0: {
                return $ImmutableSet.of();
            }
            case 1: {
                return $ImmutableSet.of(elements[0]);
            }
        }
        return $ImmutableSet.create(Arrays.asList(elements), elements.length);
    }

    public static <E> $ImmutableSet<E> copyOf(Iterable<? extends E> elements) {
        if (elements instanceof $ImmutableSet) {
            $ImmutableSet set = ($ImmutableSet)elements;
            return set;
        }
        return $ImmutableSet.copyOfInternal($Collections2.toCollection(elements));
    }

    public static <E> $ImmutableSet<E> copyOf(Iterator<? extends E> elements) {
        ArrayList<? extends E> list = $Lists.newArrayList(elements);
        return $ImmutableSet.copyOfInternal(list);
    }

    private static <E> $ImmutableSet<E> copyOfInternal(Collection<? extends E> collection) {
        switch (collection.size()) {
            case 0: {
                return $ImmutableSet.of();
            }
            case 1: {
                return $ImmutableSet.of(collection.iterator().next());
            }
        }
        return $ImmutableSet.create(collection, collection.size());
    }

    $ImmutableSet() {
    }

    boolean isHashCodeFast() {
        return false;
    }

    @Override
    public boolean equals(@$Nullable Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof $ImmutableSet && this.isHashCodeFast() && (($ImmutableSet)object).isHashCodeFast() && this.hashCode() != object.hashCode()) {
            return false;
        }
        return $Collections2.setEquals(this, object);
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        for (Object o : this) {
            hashCode += o.hashCode();
        }
        return hashCode;
    }

    @Override
    public abstract $UnmodifiableIterator<E> iterator();

    @Override
    public String toString() {
        if (this.isEmpty()) {
            return "[]";
        }
        Iterator iterator = this.iterator();
        StringBuilder result = new StringBuilder(this.size() * 16);
        result.append('[').append(iterator.next().toString());
        for (int i = 1; i < this.size(); ++i) {
            result.append(", ").append(iterator.next().toString());
        }
        return result.append(']').toString();
    }

    private static <E> $ImmutableSet<E> create(Iterable<? extends E> iterable, int count) {
        int tableSize = $Hashing.chooseTableSize(count);
        Object[] table = new Object[tableSize];
        int mask = tableSize - 1;
        ArrayList<E> elements = new ArrayList<E>(count);
        int hashCode = 0;
        block0: for (E element : iterable) {
            int hash = element.hashCode();
            int i = $Hashing.smear(hash);
            while (true) {
                int index;
                Object value;
                if ((value = table[index = i & mask]) == null) {
                    table[index] = element;
                    elements.add(element);
                    hashCode += hash;
                    continue block0;
                }
                if (value.equals(element)) continue block0;
                ++i;
            }
        }
        return elements.size() == 1 ? new SingletonImmutableSet(elements.get(0), hashCode) : new RegularImmutableSet(elements.toArray(), hashCode, table, mask);
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
        final ArrayList<E> contents = $Lists.newArrayList();

        public Builder<E> add(E element) {
            $Preconditions.checkNotNull(element, "element cannot be null");
            this.contents.add(element);
            return this;
        }

        public Builder<E> add(E ... elements) {
            $Preconditions.checkNotNull(elements, "elements cannot be null");
            List<E> elemsAsList = Arrays.asList(elements);
            $Preconditions.checkContentsNotNull(elemsAsList, "elements cannot contain null");
            this.contents.addAll(elemsAsList);
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

        public Builder<E> addAll(Iterator<? extends E> elements) {
            while (elements.hasNext()) {
                E element = elements.next();
                $Preconditions.checkNotNull(element, "element cannot be null");
                this.contents.add(element);
            }
            return this;
        }

        public $ImmutableSet<E> build() {
            return $ImmutableSet.copyOf(this.contents);
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
            return $ImmutableSet.of(this.elements);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static abstract class TransformedImmutableSet<D, E>
    extends $ImmutableSet<E> {
        final D[] source;
        final int hashCode;

        TransformedImmutableSet(D[] source, int hashCode) {
            this.source = source;
            this.hashCode = hashCode;
        }

        abstract E transform(D var1);

        @Override
        public int size() {
            return this.source.length;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public $UnmodifiableIterator<E> iterator() {
            $AbstractIterator iterator = new $AbstractIterator<E>(){
                int index = 0;

                @Override
                protected E computeNext() {
                    return this.index < TransformedImmutableSet.this.source.length ? TransformedImmutableSet.this.transform(TransformedImmutableSet.this.source[this.index++]) : this.endOfData();
                }
            };
            return $Iterators.unmodifiableIterator(iterator);
        }

        @Override
        public Object[] toArray() {
            return this.toArray(new Object[this.size()]);
        }

        @Override
        public <T> T[] toArray(T[] array) {
            int size = this.size();
            if (array.length < size) {
                array = $ObjectArrays.newArray(array, size);
            } else if (array.length > size) {
                array[size] = null;
            }
            for (int i = 0; i < this.source.length; ++i) {
                array[i] = this.transform(this.source[i]);
            }
            return array;
        }

        @Override
        public final int hashCode() {
            return this.hashCode;
        }

        @Override
        boolean isHashCodeFast() {
            return true;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class RegularImmutableSet<E>
    extends ArrayImmutableSet<E> {
        final Object[] table;
        final int mask;
        final int hashCode;

        RegularImmutableSet(Object[] elements, int hashCode, Object[] table, int mask) {
            super(elements);
            this.table = table;
            this.mask = mask;
            this.hashCode = hashCode;
        }

        @Override
        public boolean contains(Object target) {
            if (target == null) {
                return false;
            }
            int i = $Hashing.smear(target.hashCode());
            Object candidate;
            while ((candidate = this.table[i & this.mask]) != null) {
                if (candidate.equals(target)) {
                    return true;
                }
                ++i;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return this.hashCode;
        }

        @Override
        boolean isHashCodeFast() {
            return true;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static abstract class ArrayImmutableSet<E>
    extends $ImmutableSet<E> {
        final Object[] elements;

        ArrayImmutableSet(Object[] elements) {
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
            return $Iterators.forArray(this.elements);
        }

        @Override
        public Object[] toArray() {
            Object[] array = new Object[this.size()];
            System.arraycopy(this.elements, 0, array, 0, this.size());
            return array;
        }

        @Override
        public <T> T[] toArray(T[] array) {
            int size = this.size();
            if (array.length < size) {
                array = $ObjectArrays.newArray(array, size);
            } else if (array.length > size) {
                array[size] = null;
            }
            System.arraycopy(this.elements, 0, array, 0, size);
            return array;
        }

        @Override
        public boolean containsAll(Collection<?> targets) {
            if (targets == this) {
                return true;
            }
            if (!(targets instanceof ArrayImmutableSet)) {
                return super.containsAll(targets);
            }
            if (targets.size() > this.size()) {
                return false;
            }
            for (Object target : ((ArrayImmutableSet)targets).elements) {
                if (this.contains(target)) continue;
                return false;
            }
            return true;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class SingletonImmutableSet<E>
    extends $ImmutableSet<E> {
        final E element;
        final int hashCode;

        SingletonImmutableSet(E element, int hashCode) {
            this.element = element;
            this.hashCode = hashCode;
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean contains(Object target) {
            return this.element.equals(target);
        }

        @Override
        public $UnmodifiableIterator<E> iterator() {
            return $Iterators.singletonIterator(this.element);
        }

        @Override
        public Object[] toArray() {
            return new Object[]{this.element};
        }

        @Override
        public <T> T[] toArray(T[] array) {
            if (array.length == 0) {
                array = $ObjectArrays.newArray(array, 1);
            } else if (array.length > 1) {
                array[1] = null;
            }
            array[0] = this.element;
            return array;
        }

        @Override
        public boolean equals(@$Nullable Object object) {
            if (object == this) {
                return true;
            }
            if (object instanceof Set) {
                Set that = (Set)object;
                return that.size() == 1 && this.element.equals(that.iterator().next());
            }
            return false;
        }

        @Override
        public final int hashCode() {
            return this.hashCode;
        }

        @Override
        boolean isHashCodeFast() {
            return true;
        }

        @Override
        public String toString() {
            String elementToString = this.element.toString();
            return new StringBuilder(elementToString.length() + 2).append('[').append(elementToString).append(']').toString();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static final class EmptyImmutableSet
    extends $ImmutableSet<Object> {
        private static final Object[] EMPTY_ARRAY = new Object[0];

        private EmptyImmutableSet() {
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
        public boolean containsAll(Collection<?> targets) {
            return targets.isEmpty();
        }

        @Override
        public boolean equals(@$Nullable Object object) {
            if (object instanceof Set) {
                Set that = (Set)object;
                return that.isEmpty();
            }
            return false;
        }

        @Override
        public final int hashCode() {
            return 0;
        }

        @Override
        boolean isHashCodeFast() {
            return true;
        }

        @Override
        public String toString() {
            return "[]";
        }
    }
}

