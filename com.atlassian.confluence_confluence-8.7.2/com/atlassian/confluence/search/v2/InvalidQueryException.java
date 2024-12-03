/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2;

import com.atlassian.confluence.search.v2.InvalidSearchException;

public class InvalidQueryException
extends InvalidSearchException {
    private final String queryKey;

    public InvalidQueryException(String queryKey) {
        super("Unable to resolve smart list query component with key: " + queryKey);
        this.queryKey = queryKey;
    }

    public String getQueryKey() {
        return this.queryKey;
    }
}

