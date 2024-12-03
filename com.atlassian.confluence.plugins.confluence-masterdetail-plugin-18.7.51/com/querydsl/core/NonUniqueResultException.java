/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core;

import com.querydsl.core.QueryException;

public class NonUniqueResultException
extends QueryException {
    private static final long serialVersionUID = -1757423191400510323L;

    public NonUniqueResultException() {
        super("Only one result is allowed for fetchOne calls");
    }

    public NonUniqueResultException(String message) {
        super(message);
    }
}

