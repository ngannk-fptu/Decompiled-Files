/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.coalesce;

import com.mchange.v2.coalesce.Coalescer;
import com.mchange.v2.coalesce.CoalescerIterator;
import java.util.Iterator;
import java.util.Map;

class AbstractStrongCoalescer
implements Coalescer {
    Map coalesced;

    AbstractStrongCoalescer(Map map) {
        this.coalesced = map;
    }

    @Override
    public Object coalesce(Object object) {
        Object object2 = this.coalesced.get(object);
        if (object2 == null) {
            this.coalesced.put(object, object);
            object2 = object;
        }
        return object2;
    }

    @Override
    public int countCoalesced() {
        return this.coalesced.size();
    }

    @Override
    public Iterator iterator() {
        return new CoalescerIterator(this.coalesced.keySet().iterator());
    }
}

