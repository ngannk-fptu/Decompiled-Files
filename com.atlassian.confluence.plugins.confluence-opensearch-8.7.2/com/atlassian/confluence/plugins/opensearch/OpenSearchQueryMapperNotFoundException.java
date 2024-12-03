/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.opensearch;

public class OpenSearchQueryMapperNotFoundException
extends RuntimeException {
    private final String queryKey;

    public OpenSearchQueryMapperNotFoundException(String mappeeKey) {
        super("An OpenSearch mapper could not be found to map a query with key: " + mappeeKey);
        this.queryKey = mappeeKey;
    }

    public String getQueryKey() {
        return this.queryKey;
    }
}

