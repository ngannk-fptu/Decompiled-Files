/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.dao;

import org.springframework.dao.TransientDataAccessException;
import org.springframework.lang.Nullable;

public class ConcurrencyFailureException
extends TransientDataAccessException {
    public ConcurrencyFailureException(String msg) {
        super(msg);
    }

    public ConcurrencyFailureException(String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}

