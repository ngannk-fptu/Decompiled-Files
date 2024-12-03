/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.dao;

import org.springframework.dao.NonTransientDataAccessException;

public class PermissionDeniedDataAccessException
extends NonTransientDataAccessException {
    public PermissionDeniedDataAccessException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

