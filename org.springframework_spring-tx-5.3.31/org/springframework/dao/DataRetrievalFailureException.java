/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.dao;

import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.lang.Nullable;

public class DataRetrievalFailureException
extends NonTransientDataAccessException {
    public DataRetrievalFailureException(String msg) {
        super(msg);
    }

    public DataRetrievalFailureException(String msg, @Nullable Throwable cause) {
        super(msg, cause);
    }
}

