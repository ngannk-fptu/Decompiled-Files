/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.dao;

import org.springframework.dao.IncorrectResultSizeDataAccessException;

public class EmptyResultDataAccessException
extends IncorrectResultSizeDataAccessException {
    public EmptyResultDataAccessException(int expectedSize) {
        super(expectedSize, 0);
    }

    public EmptyResultDataAccessException(String msg, int expectedSize) {
        super(msg, expectedSize, 0);
    }

    public EmptyResultDataAccessException(String msg, int expectedSize, Throwable ex) {
        super(msg, expectedSize, 0, ex);
    }
}

