/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.gemini.blueprint.service.importer.support.internal.collection;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import org.eclipse.gemini.blueprint.service.importer.support.internal.collection.DynamicCollection;

public class DynamicList<E>
extends DynamicCollection<E>
implements List<E>,
RandomAccess {
    public DynamicList() {
    }

    public DynamicList(Collection<? extends E> c) {
        super(c);
    }

    public DynamicList(int size) {
        super(size);
    }

    @Override
    public void add(int index, E o) {
        super.add(index, o);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        List list = this.storage;
        synchronized (list) {
            return this.storage.addAll(index, c);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public E get(int index) {
        List list = this.storage;
        synchronized (list) {
            return this.storage.get(index);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int indexOf(Object o) {
        List list = this.storage;
        synchronized (list) {
            return this.storage.indexOf(o);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int lastIndexOf(Object o) {
        List list = this.storage;
        synchronized (list) {
            return this.storage.lastIndexOf(o);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ListIterator<E> listIterator() {
        DynamicListIterator iter = new DynamicListIterator(0);
        Map map = this.iterators;
        synchronized (map) {
            this.iterators.put(iter, null);
        }
        return iter;
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return new DynamicListIterator(index);
    }

    @Override
    public E remove(int index) {
        return super.remove(index);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public E set(int index, E o) {
        List list = this.storage;
        synchronized (list) {
            return this.storage.set(index, o);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        List list = this.storage;
        synchronized (list) {
            return this.storage.subList(fromIndex, toIndex);
        }
    }

    private class DynamicListIterator
    extends DynamicCollection.DynamicIterator
    implements ListIterator<E> {
        protected volatile E headGhost = null;
        protected Boolean hasPrevious = null;
        private boolean previousOperationCalled = true;

        private DynamicListIterator(int index) {
            this.cursor = index;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void add(E o) {
            this.removalAllowed = false;
            List list = DynamicList.this.storage;
            synchronized (list) {
                Object object = this.lock;
                synchronized (object) {
                    DynamicList.this.add(this.cursor, o);
                }
            }
        }

        private boolean unsafeHasPrevious() {
            this.hasPrevious = this.cursor - 1 >= 0 ? Boolean.TRUE : Boolean.FALSE;
            return this.hasPrevious;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean hasPrevious() {
            Object object = this.lock;
            synchronized (object) {
                this.headGhost = null;
                return this.unsafeHasPrevious();
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int nextIndex() {
            Object object = this.lock;
            synchronized (object) {
                return this.cursor;
            }
        }

        @Override
        public E next() {
            this.previousOperationCalled = true;
            return super.next();
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
        public E previous() {
            block25: {
                block24: {
                    try {
                        this.removalAllowed = true;
                        this.previousOperationCalled = false;
                        if (this.hasPrevious != null) ** GOTO lbl29
                        var1_1 = DynamicList.this.storage;
                        // MONITORENTER : var1_1
                        var2_3 = this.lock;
                        // MONITORENTER : var2_3
                        if (!this.unsafeHasPrevious()) break block24;
                        var3_5 = DynamicList.this.storage.get(--this.cursor);
                        // MONITOREXIT : var2_3
                        // MONITOREXIT : var1_1
                        this.hasPrevious = null;
                        var4_8 = this.lock;
                    }
                    catch (Throwable var12_11) {
                        this.hasPrevious = null;
                        var13_12 = this.lock;
                        // MONITORENTER : var13_12
                        this.headGhost = null;
                        // MONITOREXIT : var13_12
                        throw var12_11;
                    }
                    this.headGhost = null;
                    // MONITOREXIT : var4_8
                    return var3_5;
                }
                throw new NoSuchElementException();
lbl29:
                // 1 sources

                if (this.hasPrevious == false) throw new NoSuchElementException();
                var1_2 = DynamicList.this.storage;
                // MONITORENTER : var1_2
                var2_4 = this.lock;
                // MONITORENTER : var2_4
                if (!this.unsafeHasPrevious()) break block25;
                var3_6 = DynamicList.this.storage.get(--this.cursor);
                // MONITOREXIT : var2_4
                // MONITOREXIT : var1_2
                this.hasPrevious = null;
                var4_9 = this.lock;
                this.headGhost = null;
                // MONITOREXIT : var4_9
                return var3_6;
            }
            var3_7 = this.headGhost;
            // MONITOREXIT : var2_4
            // MONITOREXIT : var1_2
            this.hasPrevious = null;
            var4_10 = this.lock;
            // MONITORENTER : var4_10
            this.headGhost = null;
            // MONITOREXIT : var4_10
            return var3_7;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public int previousIndex() {
            Object object = this.lock;
            synchronized (object) {
                return this.cursor - 1;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void set(E o) {
            if (!this.removalAllowed) {
                throw new IllegalStateException();
            }
            List list = DynamicList.this.storage;
            synchronized (list) {
                Object object = this.lock;
                synchronized (object) {
                    int index;
                    int n = index = this.previousOperationCalled ? this.cursor - 1 : this.cursor;
                    if (index < 0) {
                        index = 0;
                    } else {
                        int length = DynamicList.this.storage.size();
                        if (index > length) {
                            index = length;
                        }
                    }
                    DynamicList.this.storage.set(index, o);
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        protected int removalIndex(int cursor) {
            int index;
            int n = index = this.previousOperationCalled ? cursor - 1 : cursor;
            if (index < 0) {
                index = 0;
            } else {
                int length;
                List list = DynamicList.this.storage;
                synchronized (list) {
                    length = DynamicList.this.storage.size();
                }
                if (index > length) {
                    index = length;
                }
            }
            return index;
        }
    }
}

