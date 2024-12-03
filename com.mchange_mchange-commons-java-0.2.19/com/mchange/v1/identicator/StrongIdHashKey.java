/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.identicator;

import com.mchange.v1.identicator.IdHashKey;
import com.mchange.v1.identicator.Identicator;

final class StrongIdHashKey
extends IdHashKey {
    Object keyObj;

    public StrongIdHashKey(Object object, Identicator identicator) {
        super(identicator);
        this.keyObj = object;
    }

    @Override
    public Object getKeyObj() {
        return this.keyObj;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof StrongIdHashKey) {
            return this.id.identical(this.keyObj, ((StrongIdHashKey)object).keyObj);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.id.hash(this.keyObj);
    }
}

