/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.identicator;

import com.mchange.v1.identicator.Identicator;

abstract class IdHashKey {
    Identicator id;

    public IdHashKey(Identicator identicator) {
        this.id = identicator;
    }

    public abstract Object getKeyObj();

    public Identicator getIdenticator() {
        return this.id;
    }

    public abstract boolean equals(Object var1);

    public abstract int hashCode();
}

