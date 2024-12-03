/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.util.impl;

import com.mchange.util.ObjectCache;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

public abstract class SoftReferenceObjectCache
implements ObjectCache {
    Map store = new HashMap();

    @Override
    public synchronized Object find(Object object) throws Exception {
        Object object2;
        Reference reference = (Reference)this.store.get(object);
        if (reference == null || (object2 = reference.get()) == null || this.isDirty(object, object2)) {
            object2 = this.createFromKey(object);
            this.store.put(object, new SoftReference(object2));
        }
        return object2;
    }

    protected boolean isDirty(Object object, Object object2) {
        return false;
    }

    protected abstract Object createFromKey(Object var1) throws Exception;
}

