/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.dao;

import org.springframework.dao.TransientDataAccessException;

public class QueryTimeoutException
extends TransientDataAccessException {
    public QueryTimeoutException(String msg) {
        super(msg);
    }

    public QueryTimeoutException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

