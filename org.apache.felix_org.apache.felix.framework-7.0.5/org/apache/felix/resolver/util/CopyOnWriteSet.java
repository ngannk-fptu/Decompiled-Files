/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.resolver.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class CopyOnWriteSet<E>
implements Set<E>,
Cloneable {
    Object[] data;

    public CopyOnWriteSet() {
        this.data = new Object[0];
    }

    public CopyOnWriteSet(CopyOnWriteSet<? extends E> col) {
        this.data = col.data;
    }

    public CopyOnWriteSet(Collection<? extends E> col) {
        this.data = col.toArray(new Object[col.size()]);
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>(){
            int idx = 0;

            @Override
            public boolean hasNext() {
                return this.idx < CopyOnWriteSet.this.data.length;
            }

            @Override
            public E next() {
                return CopyOnWriteSet.this.data[this.idx++];
            }

            @Override
            public void remove() {
                CopyOnWriteSet.this.remove(--this.idx);
            }
        };
    }

    @Override
    public int size() {
        return this.data.length;
    }

    @Override
    public boolean add(E e) {
        Object[] d = this.data;
        if (d.length == 0) {
            this.data = new Object[]{e};
        } else {
            for (Object o : d) {
                if (!(o == null ? e == null : o.equals(e))) continue;
                return false;
            }
            Object[] a = new Object[d.length + 1];
            System.arraycopy(d, 0, a, 0, d.length);
            a[d.length] = e;
            this.data = a;
        }
        return true;
    }

    private void remove(int index) {
        Object[] d = this.data;
        int len = d.length;
        Object[] a = new Object[len - 1];
        int numMoved = len - index - 1;
        if (index > 0) {
            System.arraycopy(d, 0, a, 0, index);
        }
        if (numMoved > 0) {
            System.arraycopy(d, index + 1, a, index, numMoved);
        }
        this.data = a;
    }

    @Override
    public Object[] toArray() {
        return (Object[])this.data.clone();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        int size = this.data.length;
        if (a.length < size) {
            return CopyOnWriteSet.copyOf(this.data, size, a.getClass());
        }
        System.arraycopy(this.data, 0, a, 0, size);
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CopyOnWriteSet)) {
            return false;
        }
        Object[] o1 = this.data;
        Object[] o2 = ((CopyOnWriteSet)o).data;
        if (o1 == o2) {
            return true;
        }
        int l = o1.length;
        if (l != o2.length) {
            return false;
        }
        int i = l;
        block0: while (i-- > 0) {
            Object v1 = o1[i];
            int j = l;
            while (j-- > 0) {
                Object v2 = o2[j];
                if (v1 != v2 && (v1 == null || !v1.equals(v2))) continue;
                continue block0;
            }
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.data);
    }

    public CopyOnWriteSet<E> clone() {
        try {
            return (CopyOnWriteSet)super.clone();
        }
        catch (CloneNotSupportedException exc) {
            InternalError e = new InternalError();
            e.initCause(exc);
            throw e;
        }
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        int index = CopyOnWriteSet.indexOf(o, this.data, this.data.length);
        if (index >= 0) {
            this.remove(index);
            return true;
        }
        return false;
    }

    private static int indexOf(Object o, Object[] d, int len) {
        if (o == null) {
            int i = len;
            while (i-- > 0) {
                if (d[i] != null) continue;
                return i;
            }
        } else {
            int i = len;
            while (i-- > 0) {
                if (!o.equals(d[i])) continue;
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        Object[] cs = c.toArray();
        if (cs.length == 0) {
            return false;
        }
        Object[] elements = this.data;
        int len = elements.length;
        int added = 0;
        for (int i = 0; i < cs.length; ++i) {
            Object e = cs[i];
            if (CopyOnWriteSet.indexOf(e, elements, len) >= 0 || CopyOnWriteSet.indexOf(e, cs, added) >= 0) continue;
            cs[added++] = e;
        }
        if (added > 0) {
            Object[] newElements = CopyOnWriteSet.copyOf(elements, len + added);
            System.arraycopy(cs, 0, newElements, len, added);
            this.data = newElements;
            return true;
        }
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    public static <T> T[] copyOf(T[] original, int newLength) {
        return CopyOnWriteSet.copyOf(original, newLength, original.getClass());
    }

    public static <T, U> T[] copyOf(U[] original, int newLength, Class<? extends T[]> newType) {
        Object[] copy = newType == Object[].class ? new Object[newLength] : (Object[])Array.newInstance(newType.getComponentType(), newLength);
        System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
        return copy;
    }
}

