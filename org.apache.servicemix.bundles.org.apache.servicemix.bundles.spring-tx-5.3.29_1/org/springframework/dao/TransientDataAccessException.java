/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.lang.Nullable;

public abstract class TransientDataAccessException
extends DataAccessException {
    public TransientDataAccessException(String msg) {
        super(msg);
    }

    public TransientDataAccessException(String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}

