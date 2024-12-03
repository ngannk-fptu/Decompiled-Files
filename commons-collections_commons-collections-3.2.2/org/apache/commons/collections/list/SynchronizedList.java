/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.list;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import org.apache.commons.collections.collection.SynchronizedCollection;

public class SynchronizedList
extends SynchronizedCollection
implements List {
    private static final long serialVersionUID = -1403835447328619437L;

    public static List decorate(List list) {
        return new SynchronizedList(list);
    }

    protected SynchronizedList(List list) {
        super(list);
    }

    protected SynchronizedList(List list, Object lock) {
        super(list, lock);
    }

    protected List getList() {
        return (List)this.collection;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void add(int index, Object object) {
        Object object2 = this.lock;
        synchronized (object2) {
            this.getList().add(index, object);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean addAll(int index, Collection coll) {
        Object object = this.lock;
        synchronized (object) {
            return this.getList().addAll(index, coll);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object get(int index) {
        Object object = this.lock;
        synchronized (object) {
            return this.getList().get(index);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int indexOf(Object object) {
        Object object2 = this.lock;
        synchronized (object2) {
            return this.getList().indexOf(object);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int lastIndexOf(Object object) {
        Object object2 = this.lock;
        synchronized (object2) {
            return this.getList().lastIndexOf(object);
        }
    }

    public ListIterator listIterator() {
        return this.getList().listIterator();
    }

    public ListIterator listIterator(int index) {
        return this.getList().listIterator(index);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object remove(int index) {
        Object object = this.lock;
        synchronized (object) {
            return this.getList().remove(index);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object set(int index, Object object) {
        Object object2 = this.lock;
        synchronized (object2) {
            return this.getList().set(index, object);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List subList(int fromIndex, int toIndex) {
        Object object = this.lock;
        synchronized (object) {
            List list = this.getList().subList(fromIndex, toIndex);
            return new SynchronizedList(list, this.lock);
        }
    }
}

