/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.coalesce;

import com.mchange.v2.coalesce.Coalescer;
import com.mchange.v2.coalesce.CoalescerIterator;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map;

class AbstractWeakCoalescer
implements Coalescer {
    Map wcoalesced;

    AbstractWeakCoalescer(Map map) {
        this.wcoalesced = map;
    }

    @Override
    public Object coalesce(Object object) {
        Object object2 = null;
        WeakReference weakReference = (WeakReference)this.wcoalesced.get(object);
        if (weakReference != null) {
            object2 = weakReference.get();
        }
        if (object2 == null) {
            this.wcoalesced.put(object, new WeakReference<Object>(object));
            object2 = object;
        }
        return object2;
    }

    @Override
    public int countCoalesced() {
        return this.wcoalesced.size();
    }

    @Override
    public Iterator iterator() {
        return new CoalescerIterator(this.wcoalesced.keySet().iterator());
    }
}

