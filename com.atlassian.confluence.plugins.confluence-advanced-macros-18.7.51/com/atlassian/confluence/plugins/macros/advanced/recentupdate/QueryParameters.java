/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.macros.advanced.recentupdate;

public class QueryParameters {
    private final String labels;
    private final String authors;
    private final String spaceKeys;
    private final String contentTypes;

    public QueryParameters(String labels, String authors, String contentTypes, String spaceKeys) {
        this.labels = labels;
        this.authors = authors;
        this.contentTypes = contentTypes;
        this.spaceKeys = spaceKeys;
    }

    public String getLabels() {
        return this.labels;
    }

    public String getAuthors() {
        return this.authors;
    }

    public String getContentTypes() {
        return this.contentTypes;
    }

    public String getSpaceKeys() {
        return this.spaceKeys;
    }
}

