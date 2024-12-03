/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.dao;

import org.springframework.dao.NonTransientDataAccessException;

public class InvalidDataAccessApiUsageException
extends NonTransientDataAccessException {
    public InvalidDataAccessApiUsageException(String msg) {
        super(msg);
    }

    public InvalidDataAccessApiUsageException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

