/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.resourcepool;

import com.mchange.v2.resourcepool.ResourcePoolException;

public class CannotAcquireResourceException
extends ResourcePoolException {
    public CannotAcquireResourceException(String msg, Throwable t) {
        super(msg, t);
    }

    public CannotAcquireResourceException(Throwable t) {
        super(t);
    }

    public CannotAcquireResourceException(String msg) {
        super(msg);
    }

    public CannotAcquireResourceException() {
    }
}

