/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.dao;

import org.springframework.dao.NonTransientDataAccessResourceException;
import org.springframework.lang.Nullable;

public class DataAccessResourceFailureException
extends NonTransientDataAccessResourceException {
    public DataAccessResourceFailureException(String msg) {
        super(msg);
    }

    public DataAccessResourceFailureException(String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}

