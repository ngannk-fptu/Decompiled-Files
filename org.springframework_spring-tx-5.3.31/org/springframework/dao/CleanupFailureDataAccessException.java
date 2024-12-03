/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.dao;

import org.springframework.dao.NonTransientDataAccessException;

public class CleanupFailureDataAccessException
extends NonTransientDataAccessException {
    public CleanupFailureDataAccessException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

