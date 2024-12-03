/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2;

public class SearchTokenExpiredException
extends Exception {
    private final long searchToken;

    public SearchTokenExpiredException(long searchToken) {
        super("searchToken: '" + searchToken + "' has expired.");
        this.searchToken = searchToken;
    }

    public long getSearchToken() {
        return this.searchToken;
    }
}

