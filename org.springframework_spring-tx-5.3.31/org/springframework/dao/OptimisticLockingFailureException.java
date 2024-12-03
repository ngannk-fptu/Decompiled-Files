/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.dao;

import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.lang.Nullable;

public class OptimisticLockingFailureException
extends ConcurrencyFailureException {
    public OptimisticLockingFailureException(String msg) {
        super(msg);
    }

    public OptimisticLockingFailureException(String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}

