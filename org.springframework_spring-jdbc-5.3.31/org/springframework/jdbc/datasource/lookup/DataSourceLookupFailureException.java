/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.dao.NonTransientDataAccessException
 */
package org.springframework.jdbc.datasource.lookup;

import org.springframework.dao.NonTransientDataAccessException;

public class DataSourceLookupFailureException
extends NonTransientDataAccessException {
    public DataSourceLookupFailureException(String msg) {
        super(msg);
    }

    public DataSourceLookupFailureException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

