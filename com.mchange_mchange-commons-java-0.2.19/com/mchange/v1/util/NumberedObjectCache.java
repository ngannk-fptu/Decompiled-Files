/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.util;

import java.util.ArrayList;

public abstract class NumberedObjectCache {
    ArrayList al = new ArrayList();

    public Object getObject(int n) throws Exception {
        Object object = null;
        int n2 = n + 1;
        if (n2 > this.al.size()) {
            this.al.ensureCapacity(n2 * 2);
            int n3 = n2 * 2;
            for (int i = this.al.size(); i < n3; ++i) {
                this.al.add(null);
            }
            object = this.addToCache(n);
        } else {
            object = this.al.get(n);
            if (object == null) {
                object = this.addToCache(n);
            }
        }
        return object;
    }

    private Object addToCache(int n) throws Exception {
        Object object = this.findObject(n);
        this.al.set(n, object);
        return object;
    }

    protected abstract Object findObject(int var1) throws Exception;
}

