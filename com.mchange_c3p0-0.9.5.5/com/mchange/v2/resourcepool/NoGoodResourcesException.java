/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.resourcepool;

import com.mchange.v2.resourcepool.ResourcePoolException;

public class NoGoodResourcesException
extends ResourcePoolException {
    public NoGoodResourcesException(String msg, Throwable t) {
        super(msg, t);
    }

    public NoGoodResourcesException(Throwable t) {
        super(t);
    }

    public NoGoodResourcesException(String msg) {
        super(msg);
    }

    public NoGoodResourcesException() {
    }
}

