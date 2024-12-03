/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.dao;

import org.springframework.dao.InvalidDataAccessResourceUsageException;

public class IncorrectUpdateSemanticsDataAccessException
extends InvalidDataAccessResourceUsageException {
    public IncorrectUpdateSemanticsDataAccessException(String msg) {
        super(msg);
    }

    public IncorrectUpdateSemanticsDataAccessException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public boolean wasDataUpdated() {
        return true;
    }
}

