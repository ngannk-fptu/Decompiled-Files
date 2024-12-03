/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.resourcepool;

import com.mchange.v2.resourcepool.ResourcePoolException;

public class TimeoutException
extends ResourcePoolException {
    public TimeoutException(String msg, Throwable t) {
        super(msg, t);
    }

    public TimeoutException(Throwable t) {
        super(t);
    }

    public TimeoutException(String msg) {
        super(msg);
    }

    public TimeoutException() {
    }
}

