/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.search.v2.lucene;

public class SearchTokenExpiredException
extends Exception {
    private final long searchToken;

    public SearchTokenExpiredException(long searchToken) {
        super("Search token has expired: " + searchToken);
        this.searchToken = searchToken;
    }

    public long getSearchToken() {
        return this.searchToken;
    }
}

