/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.lang.PotentiallySecondaryException
 */
package com.mchange.v2.resourcepool;

import com.mchange.lang.PotentiallySecondaryException;

public class ResourcePoolException
extends PotentiallySecondaryException {
    public ResourcePoolException(String msg, Throwable t) {
        super(msg, t);
    }

    public ResourcePoolException(Throwable t) {
        super(t);
    }

    public ResourcePoolException(String msg) {
        super(msg);
    }

    public ResourcePoolException() {
    }
}

