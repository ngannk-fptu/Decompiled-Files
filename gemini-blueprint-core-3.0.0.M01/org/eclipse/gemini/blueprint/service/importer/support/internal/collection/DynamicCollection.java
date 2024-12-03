/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.service.importer.support.internal.collection;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.WeakHashMap;

public class DynamicCollection<E>
extends AbstractCollection<E> {
    protected final Object iteratorsLock = new Object();
    protected final List<E> storage;
    protected final Map<DynamicIterator, Object> iterators;

    public DynamicCollection() {
        this(16);
    }

    public DynamicCollection(int size) {
        this.storage = new ArrayList(size);
        this.iterators = new WeakHashMap<DynamicIterator, Object>(4);
    }

    public DynamicCollection(Collection<? extends E> c) {
        this(c.size());
        this.addAll(c);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Iterator<E> iterator() {
        DynamicIterator iter = new DynamicIterator();
        Object object = this.iteratorsLock;
        synchronized (object) {
            this.iterators.put(iter, null);
        }
        return iter;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clear() {
        List<E> list = this.storage;
        synchronized (list) {
            this.storage.clear();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int size() {
        List<E> list = this.storage;
        synchronized (list) {
            return this.storage.size();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean add(E o) {
        List<E> list = this.storage;
        synchronized (list) {
            return this.storage.add(o);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean addAll(Collection<? extends E> c) {
        List<E> list = this.storage;
        synchronized (list) {
            return this.storage.addAll(c);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean contains(Object o) {
        List<E> list = this.storage;
        synchronized (list) {
            return this.storage.contains(o);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        List<E> list = this.storage;
        synchronized (list) {
            return this.storage.containsAll(c);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isEmpty() {
        List<E> list = this.storage;
        synchronized (list) {
            return this.storage.isEmpty();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean remove(Object o) {
        List<E> list = this.storage;
        synchronized (list) {
            int index = this.storage.indexOf(o);
            if (index == -1) {
                return false;
            }
            this.remove(index);
            return true;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected E remove(int index) {
        Object o = null;
        List<E> list = this.storage;
        synchronized (list) {
            Object object = this.iteratorsLock;
            synchronized (object) {
                o = this.storage.remove(index);
                for (Map.Entry<DynamicIterator, Object> entry : this.iterators.entrySet()) {
                    DynamicIterator dynamicIterator = entry.getKey();
                    Object object2 = dynamicIterator.lock;
                    synchronized (object2) {
                        if (index < dynamicIterator.cursor) {
                            --dynamicIterator.cursor;
                        } else {
                            dynamicIterator.removeObject(index, o);
                        }
                    }
                }
            }
        }
        return o;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void add(int index, E o) {
        List<E> list = this.storage;
        synchronized (list) {
            Object object = this.iteratorsLock;
            synchronized (object) {
                this.storage.add(index, o);
                for (Map.Entry<DynamicIterator, Object> entry : this.iterators.entrySet()) {
                    DynamicIterator dynamicIterator = entry.getKey();
                    Object object2 = dynamicIterator.lock;
                    synchronized (object2) {
                        if (index < dynamicIterator.cursor) {
                            ++dynamicIterator.cursor;
                        }
                    }
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object[] toArray() {
        List<E> list = this.storage;
        synchronized (list) {
            return this.storage.toArray();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public <T> T[] toArray(T[] a) {
        List<E> list = this.storage;
        synchronized (list) {
            return this.storage.toArray(a);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String toString() {
        List<E> list = this.storage;
        synchronized (list) {
            return this.storage.toString();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected int indexOf(Object o) {
        List<E> list = this.storage;
        synchronized (list) {
            return this.storage.indexOf(o);
        }
    }

    protected class DynamicIterator
    implements Iterator<E> {
        protected volatile int cursor = 0;
        protected volatile E tailGhost = null;
        protected final Object lock = new Object();
        protected Boolean hasNext = null;
        protected boolean removalAllowed = false;

        protected DynamicIterator() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean hasNext() {
            List list = DynamicCollection.this.storage;
            synchronized (list) {
                Object object = DynamicCollection.this.iteratorsLock;
                synchronized (object) {
                    Object object2 = this.lock;
                    synchronized (object2) {
                        this.tailGhost = null;
                        return this.unsafeHasNext();
                    }
                }
            }
        }

        protected boolean unsafeHasNext() {
            this.hasNext = this.cursor < DynamicCollection.this.storage.size() ? Boolean.TRUE : Boolean.FALSE;
            return this.hasNext;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Unable to fully structure code
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         * Converted monitor instructions to comments
         * Lifted jumps to return sites
         */
        @Override
        public E next() {
            block31: {
                block30: {
                    try {
                        this.removalAllowed = true;
                        if (this.hasNext != null) ** GOTO lbl31
                        var1_1 = DynamicCollection.this.storage;
                        // MONITORENTER : var1_1
                        var2_3 = DynamicCollection.this.iteratorsLock;
                        // MONITORENTER : var2_3
                        var3_5 = this.lock;
                        // MONITORENTER : var3_5
                        if (!this.unsafeHasNext()) break block30;
                        var4_7 = DynamicCollection.this.storage.get(this.cursor++);
                        // MONITOREXIT : var3_5
                        // MONITOREXIT : var2_3
                        // MONITOREXIT : var1_1
                        this.hasNext = null;
                        var5_10 = this.lock;
                    }
                    catch (Throwable var15_13) {
                        this.hasNext = null;
                        var16_14 = this.lock;
                        // MONITORENTER : var16_14
                        this.tailGhost = null;
                        // MONITOREXIT : var16_14
                        throw var15_13;
                    }
                    this.tailGhost = null;
                    // MONITOREXIT : var5_10
                    return var4_7;
                }
                throw new NoSuchElementException();
lbl31:
                // 1 sources

                if (this.hasNext == false) throw new NoSuchElementException();
                var1_2 = DynamicCollection.this.storage;
                // MONITORENTER : var1_2
                var2_4 = DynamicCollection.this.iteratorsLock;
                // MONITORENTER : var2_4
                var3_6 = this.lock;
                // MONITORENTER : var3_6
                if (!this.unsafeHasNext()) break block31;
                var4_8 = DynamicCollection.this.storage.get(this.cursor++);
                // MONITOREXIT : var3_6
                // MONITOREXIT : var2_4
                // MONITOREXIT : var1_2
                this.hasNext = null;
                var5_11 = this.lock;
                this.tailGhost = null;
                // MONITOREXIT : var5_11
                return var4_8;
            }
            var4_9 = this.tailGhost;
            // MONITOREXIT : var3_6
            // MONITOREXIT : var2_4
            // MONITOREXIT : var1_2
            this.hasNext = null;
            var5_12 = this.lock;
            // MONITORENTER : var5_12
            this.tailGhost = null;
            // MONITOREXIT : var5_12
            return var4_9;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void remove() {
            int cursorCopy;
            if (this.removalAllowed) {
                this.removalAllowed = false;
                Object object = this.lock;
                synchronized (object) {
                    cursorCopy = this.cursor;
                }
            } else {
                throw new IllegalStateException();
            }
            DynamicCollection.this.remove(this.removalIndex(cursorCopy));
        }

        protected int removalIndex(int cursor) {
            return cursor - 1;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        void removeObject(int index, E o) {
            Object object = this.lock;
            synchronized (object) {
                this.tailGhost = o;
            }
        }
    }
}

