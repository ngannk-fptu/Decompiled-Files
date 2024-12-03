/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.constructs.nonstop.concurrency;

import net.sf.ehcache.constructs.nonstop.NonStopCacheException;

public class InvalidLockStateAfterRejoinException
extends NonStopCacheException {
    public InvalidLockStateAfterRejoinException() {
        this((Throwable)null);
    }

    public InvalidLockStateAfterRejoinException(Throwable cause) {
        this("Invalid lock state as locks are flushed after rejoin. The cluster rejoined and locks were acquired before rejoin.", cause);
    }

    public InvalidLockStateAfterRejoinException(String message, Throwable cause) {
        super(message, cause);
    }
}

