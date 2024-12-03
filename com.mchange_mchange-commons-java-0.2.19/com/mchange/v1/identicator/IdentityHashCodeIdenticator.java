/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.identicator;

import com.mchange.v1.identicator.Identicator;

public class IdentityHashCodeIdenticator
implements Identicator {
    public static IdentityHashCodeIdenticator INSTANCE = new IdentityHashCodeIdenticator();

    @Override
    public boolean identical(Object object, Object object2) {
        return System.identityHashCode(object) == System.identityHashCode(object2);
    }

    @Override
    public int hash(Object object) {
        return System.identityHashCode(object);
    }
}

