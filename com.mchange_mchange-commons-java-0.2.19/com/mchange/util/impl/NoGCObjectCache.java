/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.util.impl;

import com.mchange.util.ObjectCache;
import java.util.Hashtable;

public abstract class NoGCObjectCache
implements ObjectCache {
    Hashtable store = new Hashtable();

    @Override
    public Object find(Object object) throws Exception {
        Object object2 = this.store.get(object);
        if (object2 == null || this.isDirty(object, object2)) {
            object2 = this.createFromKey(object);
            this.store.put(object, object2);
        }
        return object2;
    }

    protected boolean isDirty(Object object, Object object2) {
        return false;
    }

    protected abstract Object createFromKey(Object var1) throws Exception;
}

