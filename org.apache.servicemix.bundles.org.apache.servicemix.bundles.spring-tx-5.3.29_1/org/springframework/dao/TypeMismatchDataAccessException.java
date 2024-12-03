/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.dao;

import org.springframework.dao.InvalidDataAccessResourceUsageException;

public class TypeMismatchDataAccessException
extends InvalidDataAccessResourceUsageException {
    public TypeMismatchDataAccessException(String msg) {
        super(msg);
    }

    public TypeMismatchDataAccessException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

