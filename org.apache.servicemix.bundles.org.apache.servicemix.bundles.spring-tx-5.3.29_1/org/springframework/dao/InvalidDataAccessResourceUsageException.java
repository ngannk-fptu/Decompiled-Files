/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.dao;

import org.springframework.dao.NonTransientDataAccessException;

public class InvalidDataAccessResourceUsageException
extends NonTransientDataAccessException {
    public InvalidDataAccessResourceUsageException(String msg) {
        super(msg);
    }

    public InvalidDataAccessResourceUsageException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

