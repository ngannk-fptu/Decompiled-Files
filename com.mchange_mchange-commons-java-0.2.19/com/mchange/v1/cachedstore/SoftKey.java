/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.cachedstore;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;

final class SoftKey
extends SoftReference {
    int hash_code;

    SoftKey(Object object, ReferenceQueue referenceQueue) {
        super(object, referenceQueue);
        this.hash_code = object.hashCode();
    }

    public int hashCode() {
        return this.hash_code;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        Object t = this.get();
        if (t == null) {
            return false;
        }
        if (this.getClass() == object.getClass()) {
            SoftKey softKey = (SoftKey)object;
            return t.equals(softKey.get());
        }
        return false;
    }
}

