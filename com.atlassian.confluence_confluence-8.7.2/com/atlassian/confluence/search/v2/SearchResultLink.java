/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2;

public class SearchResultLink {
    private final String url;
    private final String alias;

    public SearchResultLink(String url, String alias) {
        this.url = url;
        this.alias = alias;
    }

    public String getUrl() {
        return this.url;
    }

    public String getAlias() {
        return this.alias;
    }
}

