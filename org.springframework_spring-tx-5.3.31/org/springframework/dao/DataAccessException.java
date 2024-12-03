/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.NestedRuntimeException
 *  org.springframework.lang.Nullable
 */
package org.springframework.dao;

import org.springframework.core.NestedRuntimeException;
import org.springframework.lang.Nullable;

public abstract class DataAccessException
extends NestedRuntimeException {
    public DataAccessException(String msg) {
        super(msg);
    }

    public DataAccessException(@Nullable String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}

