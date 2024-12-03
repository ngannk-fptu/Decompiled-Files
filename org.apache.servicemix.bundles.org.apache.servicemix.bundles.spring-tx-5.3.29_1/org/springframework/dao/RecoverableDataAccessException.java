/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.dao;

import org.springframework.dao.DataAccessException;

public class RecoverableDataAccessException
extends DataAccessException {
    public RecoverableDataAccessException(String msg) {
        super(msg);
    }

    public RecoverableDataAccessException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

