/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.coalesce;

import com.mchange.v1.identicator.Identicator;
import com.mchange.v2.coalesce.CoalesceChecker;

class CoalesceIdenticator
implements Identicator {
    CoalesceChecker cc;

    CoalesceIdenticator(CoalesceChecker coalesceChecker) {
        this.cc = coalesceChecker;
    }

    @Override
    public boolean identical(Object object, Object object2) {
        return this.cc.checkCoalesce(object, object2);
    }

    @Override
    public int hash(Object object) {
        return this.cc.coalesceHash(object);
    }
}

