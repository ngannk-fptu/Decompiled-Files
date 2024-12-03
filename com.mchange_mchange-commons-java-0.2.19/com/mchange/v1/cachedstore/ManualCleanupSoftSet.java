/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.cachedstore;

import com.mchange.v1.cachedstore.CachedStoreException;
import com.mchange.v1.cachedstore.SoftKey;
import com.mchange.v1.cachedstore.Vacuumable;
import com.mchange.v1.util.WrapperIterator;
import java.lang.ref.ReferenceQueue;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

class ManualCleanupSoftSet
extends AbstractSet
implements Vacuumable {
    HashSet inner = new HashSet();
    ReferenceQueue queue = new ReferenceQueue();

    ManualCleanupSoftSet() {
    }

    @Override
    public Iterator iterator() {
        return new WrapperIterator(this.inner.iterator(), true){

            @Override
            protected Object transformObject(Object object) {
                SoftKey softKey = (SoftKey)object;
                Object t = softKey.get();
                return t == null ? SKIP_TOKEN : t;
            }
        };
    }

    @Override
    public int size() {
        return this.inner.size();
    }

    @Override
    public boolean contains(Object object) {
        return this.inner.contains(new SoftKey(object, null));
    }

    private ArrayList toArrayList() {
        ArrayList arrayList = new ArrayList(this.size());
        Iterator iterator = this.iterator();
        while (iterator.hasNext()) {
            arrayList.add(iterator.next());
        }
        return arrayList;
    }

    @Override
    public Object[] toArray() {
        return this.toArrayList().toArray();
    }

    @Override
    public Object[] toArray(Object[] objectArray) {
        return this.toArrayList().toArray(objectArray);
    }

    @Override
    public boolean add(Object object) {
        return this.inner.add(new SoftKey(object, this.queue));
    }

    @Override
    public boolean remove(Object object) {
        return this.inner.remove(new SoftKey(object, null));
    }

    @Override
    public void clear() {
        this.inner.clear();
    }

    @Override
    public void vacuum() throws CachedStoreException {
        SoftKey softKey;
        while ((softKey = (SoftKey)this.queue.poll()) != null) {
            this.inner.remove(softKey);
        }
    }
}

