/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.collections;

import aQute.lib.collections.IteratorList;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.SortedSet;

public class SortedList<T>
implements SortedSet<T>,
List<T> {
    private static final SortedList<?> EMPTY = new SortedList();
    private final T[] list;
    private final int start;
    private final int end;
    private final Comparator<? super T> comparator;
    private Class<?> type;

    public SortedList(Collection<? extends Comparable<? super T>> x) {
        this(x, 0, x.size(), null);
    }

    public SortedList(Collection<? extends T> x, Comparator<? super T> cmp) {
        this(x, 0, x.size(), cmp);
    }

    @SafeVarargs
    public SortedList(Comparable<? super T> ... x) {
        this((Object[])x, 0, x.length, null);
    }

    public SortedList(Comparator<? super T> cmp, T ... x) {
        this(x, 0, x.length, cmp);
    }

    private SortedList(SortedList<T> other, int start, int end) {
        this.list = other.list;
        this.comparator = other.comparator;
        this.start = start;
        this.end = end;
    }

    public SortedList(T[] x, int start, int end, Comparator<? super T> cmp) {
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (start < 0 || start >= x.length) {
            throw new IllegalArgumentException("Start is not in list");
        }
        if (end < 0 || end > x.length) {
            throw new IllegalArgumentException("End is not in list");
        }
        this.list = Arrays.copyOf(x, x.length, Object[].class);
        Arrays.sort(this.list, start, end, cmp);
        this.start = start;
        this.end = end;
        this.comparator = cmp;
    }

    public SortedList(Collection<? extends T> x, int start, int end, Comparator<? super T> cmp) {
        if (start > end) {
            int tmp = start;
            start = end;
            end = tmp;
        }
        if (start < 0 || start > x.size()) {
            throw new IllegalArgumentException("Start is not in list");
        }
        if (end < 0 || end > x.size()) {
            throw new IllegalArgumentException("End is not in list");
        }
        this.list = x.toArray();
        Arrays.sort(this.list, start, end, cmp);
        this.start = start;
        this.end = end;
        this.comparator = cmp;
    }

    private SortedList() {
        this.list = null;
        this.start = 0;
        this.end = 0;
        this.comparator = null;
    }

    @Override
    public int size() {
        return this.end - this.start;
    }

    @Override
    public boolean isEmpty() {
        return this.start == this.end;
    }

    @Override
    public boolean contains(Object o) {
        assert (this.type == null || this.type.isInstance(o));
        for (int i = this.start; i < this.end; ++i) {
            if (this.compare(o, this.list[i]) != 0) continue;
            return true;
        }
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return new It(this.start);
    }

    @Override
    public Object[] toArray() {
        if (this.list == null) {
            return new Object[0];
        }
        if (this.start == 0 && this.end == this.list.length) {
            return (Object[])this.list.clone();
        }
        if (this.type != null) {
            return this.toArray((X[])((Object[])Array.newInstance(this.type, 0)));
        }
        return this.toArray((X[])new Object[0]);
    }

    @Override
    public <X> X[] toArray(X[] a) {
        int size = this.size();
        if (a.length < size) {
            a = (Object[])Array.newInstance(a.getClass().getComponentType(), size);
        }
        System.arraycopy(this.list, this.start, a, 0, size);
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }

    @Override
    public boolean add(T e) {
        throw new UnsupportedOperationException("Immutable");
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Immutable");
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        if (c.isEmpty()) {
            return true;
        }
        if (this.isEmpty()) {
            return false;
        }
        for (Object el : c) {
            if (this.contains(el)) continue;
            return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException("Immutable");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Immutable");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Immutable");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Immutable");
    }

    @Override
    public Comparator<? super T> comparator() {
        return this.comparator;
    }

    private int compare(T o1, T o2) {
        if (this.comparator == null) {
            return ((Comparable)o1).compareTo(o2);
        }
        return this.comparator.compare(o1, o2);
    }

    public boolean isSubSet() {
        return this.start > 0 && this.end < this.list.length;
    }

    @Override
    public SortedList<T> subSet(T fromElement, T toElement) {
        int start = this.find(fromElement);
        int end = this.find(toElement);
        if (this.isSubSet() && (start < 0 || end < 0)) {
            throw new IllegalArgumentException("This list is a subset");
        }
        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = this.list.length;
        }
        return this.subList(start, end);
    }

    @Override
    public int indexOf(Object o) {
        assert (this.type == null || this.type.isInstance(o));
        int n = this.find(o);
        if (n < this.end && this.compare(o, this.list[n]) == 0) {
            return n;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        assert (this.type == null || this.type.isInstance(o));
        int n = this.find(o);
        if (n >= this.end || this.compare(o, this.list[n]) != 0) {
            return -1;
        }
        while (n < this.end - 1 && this.compare(o, this.list[n + 1]) == 0) {
            ++n;
        }
        return n;
    }

    private int find(Object toElement) {
        int i;
        for (i = this.start; i < this.end && this.compare(toElement, this.list[i]) > 0; ++i) {
        }
        return i;
    }

    @Override
    public SortedSet<T> tailSet(T fromElement) {
        int i = this.find(fromElement);
        return this.subList(i - this.start, this.end - this.start);
    }

    @Override
    public SortedList<T> headSet(T toElement) {
        int i = this.find(toElement);
        return this.subList(this.start, i - this.start);
    }

    @Override
    public T first() {
        if (this.isEmpty()) {
            throw new NoSuchElementException("first");
        }
        return this.get(0);
    }

    @Override
    public T last() {
        if (this.isEmpty()) {
            throw new NoSuchElementException("last");
        }
        return this.get(this.size() - 1);
    }

    @Override
    @Deprecated
    public boolean addAll(int index, Collection<? extends T> c) {
        throw new UnsupportedOperationException("Immutable");
    }

    @Override
    public T get(int index) {
        if ((index += this.start) >= this.end) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return this.list[index];
    }

    @Override
    @Deprecated
    public T set(int index, T element) {
        throw new UnsupportedOperationException("Immutable");
    }

    @Override
    @Deprecated
    public void add(int index, T element) {
        throw new UnsupportedOperationException("Immutable");
    }

    @Override
    @Deprecated
    public T remove(int index) {
        throw new UnsupportedOperationException("Immutable");
    }

    @Override
    public ListIterator<T> listIterator() {
        return new It(this.start);
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return new It(index + this.start);
    }

    @Override
    public SortedList<T> subList(int fromIndex, int toIndex) {
        if ((toIndex += this.start) < (fromIndex += this.start)) {
            int tmp = toIndex;
            toIndex = fromIndex;
            fromIndex = tmp;
        }
        toIndex = Math.max(0, toIndex);
        toIndex = Math.min(toIndex, this.end);
        fromIndex = Math.max(0, fromIndex);
        if ((fromIndex = Math.min(fromIndex, this.end)) == this.start && toIndex == this.end) {
            return this;
        }
        if (toIndex == fromIndex) {
            return EMPTY;
        }
        return new SortedList<T>(this, fromIndex, toIndex);
    }

    @Override
    @Deprecated
    public boolean equals(Object other) {
        return super.equals(other);
    }

    @Override
    @Deprecated
    public int hashCode() {
        return super.hashCode();
    }

    public boolean isEqual(SortedList<T> list) {
        if (this.size() != list.size()) {
            return false;
        }
        int as = this.start;
        int al = this.size();
        for (int bs = list.start; as < al && bs < al; ++as, ++bs) {
            if (this.compare(this.list[as], this.list[bs]) == 0) continue;
            return false;
        }
        return true;
    }

    public Class<?> getType() {
        return this.type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        String del = "";
        for (T t : this) {
            sb.append(del);
            sb.append(t);
            del = ", ";
        }
        sb.append("]");
        return sb.toString();
    }

    public boolean hasDuplicates() {
        if (this.list.length < 2) {
            return false;
        }
        T prev = this.list[0];
        for (int i = 1; i < this.list.length; ++i) {
            if (this.compare(prev, this.list[i]) == 0) {
                return true;
            }
            prev = this.list[i];
        }
        return false;
    }

    public static <T extends Comparable<? super T>> SortedList<T> fromIterator(Iterator<? extends T> it) {
        IteratorList<? extends T> l = new IteratorList<T>(it);
        return new SortedList<T>(l);
    }

    public static <T> SortedList<T> fromIterator(Iterator<? extends T> it, Comparator<? super T> cmp) {
        IteratorList<? extends T> l = new IteratorList<T>(it);
        return new SortedList<T>(l, cmp);
    }

    public static <T> SortedSet<T> empty() {
        return EMPTY;
    }

    private class It
    implements ListIterator<T> {
        private int n;

        It(int n) {
            this.n = n;
        }

        @Override
        public boolean hasNext() {
            return this.n < SortedList.this.end;
        }

        @Override
        public T next() throws NoSuchElementException {
            if (!this.hasNext()) {
                throw new NoSuchElementException("");
            }
            return SortedList.this.list[this.n++];
        }

        @Override
        public boolean hasPrevious() {
            return this.n > SortedList.this.start;
        }

        @Override
        public T previous() {
            assert (this.n > SortedList.this.start);
            return SortedList.this.list[--this.n];
        }

        @Override
        public int nextIndex() {
            return this.n - SortedList.this.start;
        }

        @Override
        public int previousIndex() {
            return this.n - 1 - SortedList.this.start;
        }

        @Override
        @Deprecated
        public void remove() {
            throw new UnsupportedOperationException("Immutable");
        }

        @Override
        @Deprecated
        public void set(T e) {
            throw new UnsupportedOperationException("Immutable");
        }

        @Override
        @Deprecated
        public void add(T e) {
            throw new UnsupportedOperationException("Immutable");
        }
    }
}

