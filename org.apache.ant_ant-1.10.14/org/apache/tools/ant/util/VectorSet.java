/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.Vector;

public final class VectorSet<E>
extends Vector<E> {
    private static final long serialVersionUID = 1L;
    private final HashSet<E> set = new HashSet();

    public VectorSet() {
    }

    public VectorSet(int initialCapacity) {
        super(initialCapacity);
    }

    public VectorSet(int initialCapacity, int capacityIncrement) {
        super(initialCapacity, capacityIncrement);
    }

    public VectorSet(Collection<? extends E> c) {
        if (c != null) {
            this.addAll(c);
        }
    }

    @Override
    public synchronized boolean add(E o) {
        if (!this.set.contains(o)) {
            this.doAdd(this.size(), o);
            return true;
        }
        return false;
    }

    @Override
    public void add(int index, E o) {
        this.doAdd(index, o);
    }

    private synchronized void doAdd(int index, E o) {
        if (this.set.add(o)) {
            int count = this.size();
            this.ensureCapacity(count + 1);
            if (index != count) {
                System.arraycopy(this.elementData, index, this.elementData, index + 1, count - index);
            }
            this.elementData[index] = o;
            ++this.elementCount;
        }
    }

    @Override
    public synchronized void addElement(E o) {
        this.doAdd(this.size(), o);
    }

    @Override
    public synchronized boolean addAll(Collection<? extends E> c) {
        boolean changed = false;
        for (E e : c) {
            changed |= this.add(e);
        }
        return changed;
    }

    @Override
    public synchronized boolean addAll(int index, Collection<? extends E> c) {
        LinkedList<E> toAdd = new LinkedList<E>();
        for (E e : c) {
            if (!this.set.add(e)) continue;
            toAdd.add(e);
        }
        if (toAdd.isEmpty()) {
            return false;
        }
        int count = this.size();
        this.ensureCapacity(count + toAdd.size());
        if (index != count) {
            System.arraycopy(this.elementData, index, this.elementData, index + toAdd.size(), count - index);
        }
        for (Object o : toAdd) {
            this.elementData[index++] = o;
        }
        this.elementCount += toAdd.size();
        return true;
    }

    @Override
    public synchronized void clear() {
        super.clear();
        this.set.clear();
    }

    @Override
    public Object clone() {
        VectorSet vs = (VectorSet)super.clone();
        vs.set.addAll(this.set);
        return vs;
    }

    @Override
    public synchronized boolean contains(Object o) {
        return this.set.contains(o);
    }

    @Override
    public synchronized boolean containsAll(Collection<?> c) {
        return this.set.containsAll(c);
    }

    @Override
    public void insertElementAt(E o, int index) {
        this.doAdd(index, o);
    }

    @Override
    public synchronized E remove(int index) {
        Object o = this.get(index);
        this.remove(o);
        return o;
    }

    @Override
    public boolean remove(Object o) {
        return this.doRemove(o);
    }

    private synchronized boolean doRemove(Object o) {
        if (this.set.remove(o)) {
            int index = this.indexOf(o);
            if (index < this.elementData.length - 1) {
                System.arraycopy(this.elementData, index + 1, this.elementData, index, this.elementData.length - index - 1);
            }
            --this.elementCount;
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean removeAll(Collection<?> c) {
        boolean changed = false;
        for (Object o : c) {
            changed |= this.remove(o);
        }
        return changed;
    }

    @Override
    public synchronized void removeAllElements() {
        this.set.clear();
        super.removeAllElements();
    }

    @Override
    public boolean removeElement(Object o) {
        return this.doRemove(o);
    }

    @Override
    public synchronized void removeElementAt(int index) {
        this.remove(this.get(index));
    }

    @Override
    public synchronized void removeRange(int fromIndex, int toIndex) {
        while (toIndex > fromIndex) {
            this.remove(--toIndex);
        }
    }

    @Override
    public synchronized boolean retainAll(Collection<?> c) {
        if (!(c instanceof Set)) {
            c = new HashSet(c);
        }
        LinkedList l = new LinkedList();
        for (Object o : this) {
            if (c.contains(o)) continue;
            l.addLast(o);
        }
        if (!l.isEmpty()) {
            this.removeAll(l);
            return true;
        }
        return false;
    }

    @Override
    public synchronized E set(int index, E o) {
        Object orig = this.get(index);
        if (this.set.add(o)) {
            this.elementData[index] = o;
            this.set.remove(orig);
        } else {
            int oldIndexOfO = this.indexOf(o);
            this.remove(o);
            this.remove(orig);
            this.add(oldIndexOfO > index ? index : index - 1, o);
        }
        return orig;
    }

    @Override
    public void setElementAt(E o, int index) {
        this.set(index, o);
    }
}

