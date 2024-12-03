/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import org.apache.xmlbeans.SimpleValue;

public class XmlSimpleList<T>
implements List<T>,
Serializable {
    private static final long serialVersionUID = 1L;
    private final List<T> underlying;

    public XmlSimpleList(List<T> list) {
        this.underlying = list;
    }

    @Override
    public int size() {
        return this.underlying.size();
    }

    @Override
    public boolean isEmpty() {
        return this.underlying.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.underlying.contains(o);
    }

    @Override
    public boolean containsAll(Collection coll) {
        return this.underlying.containsAll(coll);
    }

    @Override
    public Object[] toArray() {
        return this.underlying.toArray(new Object[0]);
    }

    @Override
    public <X> X[] toArray(X[] a) {
        return this.underlying.toArray(a);
    }

    @Override
    public boolean add(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection coll) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public T get(int index) {
        return this.underlying.get(index);
    }

    @Override
    public T set(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, Object element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        return this.underlying.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.underlying.lastIndexOf(o);
    }

    @Override
    public boolean addAll(int index, Collection c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<T> subList(int from, int to) {
        return new XmlSimpleList<T>(this.underlying.subList(from, to));
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>(){
            final Iterator<T> i;
            {
                this.i = XmlSimpleList.this.underlying.iterator();
            }

            @Override
            public boolean hasNext() {
                return this.i.hasNext();
            }

            @Override
            public T next() {
                return this.i.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public ListIterator<T> listIterator() {
        return this.listIterator(0);
    }

    @Override
    public ListIterator<T> listIterator(final int index) {
        return new ListIterator<T>(){
            final ListIterator<T> i;
            {
                this.i = XmlSimpleList.this.underlying.listIterator(index);
            }

            @Override
            public boolean hasNext() {
                return this.i.hasNext();
            }

            @Override
            public T next() {
                return this.i.next();
            }

            @Override
            public boolean hasPrevious() {
                return this.i.hasPrevious();
            }

            @Override
            public T previous() {
                return this.i.previous();
            }

            @Override
            public int nextIndex() {
                return this.i.nextIndex();
            }

            @Override
            public int previousIndex() {
                return this.i.previousIndex();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void set(Object o) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void add(Object o) {
                throw new UnsupportedOperationException();
            }
        };
    }

    private String stringValue(Object o) {
        if (o instanceof SimpleValue) {
            return ((SimpleValue)o).getStringValue();
        }
        return o.toString();
    }

    public String toString() {
        int size = this.underlying.size();
        if (size == 0) {
            return "";
        }
        String first = this.stringValue(this.underlying.get(0));
        if (size == 1) {
            return first;
        }
        StringBuilder result = new StringBuilder(first);
        for (int i = 1; i < size; ++i) {
            result.append(' ');
            result.append(this.stringValue(this.underlying.get(i)));
        }
        return result.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof XmlSimpleList)) {
            return false;
        }
        XmlSimpleList xmlSimpleList = (XmlSimpleList)o;
        List<T> underlying2 = xmlSimpleList.underlying;
        int size = this.underlying.size();
        if (size != underlying2.size()) {
            return false;
        }
        for (int i = 0; i < size; ++i) {
            if (Objects.equals(this.underlying.get(i), underlying2.get(i))) continue;
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        for (T item : this.underlying) {
            hash *= 19;
            hash += item.hashCode();
        }
        return hash;
    }
}

