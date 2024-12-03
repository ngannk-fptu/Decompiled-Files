/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.opensearch;

public class OpenSearchSortMapperNotFoundException
extends RuntimeException {
    private final String sortKey;

    public OpenSearchSortMapperNotFoundException(String sortKey) {
        super("An OpenSearch mapper could not be found to map a sort with key: " + sortKey);
        this.sortKey = sortKey;
    }

    public String getSortKey() {
        return this.sortKey;
    }
}

