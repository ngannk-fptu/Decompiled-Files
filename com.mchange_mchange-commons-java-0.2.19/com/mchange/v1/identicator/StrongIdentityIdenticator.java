/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.identicator;

import com.mchange.v1.identicator.Identicator;

public class StrongIdentityIdenticator
implements Identicator {
    @Override
    public boolean identical(Object object, Object object2) {
        return object == object2;
    }

    @Override
    public int hash(Object object) {
        return System.identityHashCode(object);
    }
}

