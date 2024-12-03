/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.coalesce;

import com.mchange.v2.coalesce.Coalescer;
import java.util.Iterator;

class SyncedCoalescer
implements Coalescer {
    Coalescer inner;

    public SyncedCoalescer(Coalescer coalescer) {
        this.inner = coalescer;
    }

    @Override
    public synchronized Object coalesce(Object object) {
        return this.inner.coalesce(object);
    }

    @Override
    public synchronized int countCoalesced() {
        return this.inner.countCoalesced();
    }

    @Override
    public synchronized Iterator iterator() {
        return this.inner.iterator();
    }
}

