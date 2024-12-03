/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.plugin.rest.entity.page;

public interface RestPageRequest {
    public static final int ALL_RESULTS = -1;

    public int getStart();

    public int getLimit();

    default public boolean isAllResultsQuery() {
        return this.getLimit() == -1;
    }
}

