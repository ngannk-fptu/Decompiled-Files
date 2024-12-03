/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.identicator;

import com.mchange.v1.identicator.IdHashKey;
import com.mchange.v1.identicator.Identicator;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

final class WeakIdHashKey
extends IdHashKey {
    Ref keyRef;
    int hash;

    public WeakIdHashKey(Object object, Identicator identicator, ReferenceQueue referenceQueue) {
        super(identicator);
        if (object == null) {
            throw new UnsupportedOperationException("Collection does not accept nulls!");
        }
        this.keyRef = new Ref(object, referenceQueue);
        this.hash = identicator.hash(object);
    }

    public Ref getInternalRef() {
        return this.keyRef;
    }

    @Override
    public Object getKeyObj() {
        return this.keyRef.get();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof WeakIdHashKey) {
            WeakIdHashKey weakIdHashKey = (WeakIdHashKey)object;
            if (this.keyRef == weakIdHashKey.keyRef) {
                return true;
            }
            Object t = this.keyRef.get();
            Object t2 = weakIdHashKey.keyRef.get();
            if (t == null || t2 == null) {
                return false;
            }
            return this.id.identical(t, t2);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.hash;
    }

    class Ref
    extends WeakReference {
        public Ref(Object object, ReferenceQueue referenceQueue) {
            super(object, referenceQueue);
        }

        WeakIdHashKey getKey() {
            return WeakIdHashKey.this;
        }
    }
}

