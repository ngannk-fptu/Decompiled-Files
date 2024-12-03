/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.directory.rest.delta;

import java.util.List;

public class GraphDeltaQueryResult<T> {
    private final List<T> results;
    private final String deltaLink;

    public GraphDeltaQueryResult(List<T> results, String deltaLink) {
        this.results = results;
        this.deltaLink = deltaLink;
    }

    public List<T> getResults() {
        return this.results;
    }

    public String getDeltaLink() {
        return this.deltaLink;
    }
}

