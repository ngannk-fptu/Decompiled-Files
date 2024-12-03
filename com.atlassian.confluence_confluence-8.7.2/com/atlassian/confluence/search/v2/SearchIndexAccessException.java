/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2;

public class SearchIndexAccessException
extends RuntimeException {
    private static final long serialVersionUID = -6735028616263409341L;

    public SearchIndexAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public SearchIndexAccessException(String message) {
        super(message);
    }
}

