/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.constructs.nonstop.concurrency;

import net.sf.ehcache.constructs.nonstop.NonStopCacheException;

public class LockOperationTimedOutNonstopException
extends NonStopCacheException {
    public LockOperationTimedOutNonstopException(String message) {
        super(message);
    }
}

