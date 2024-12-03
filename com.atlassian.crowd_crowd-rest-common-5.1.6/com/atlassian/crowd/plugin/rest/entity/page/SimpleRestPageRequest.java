/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.plugin.rest.entity.page;

import com.atlassian.crowd.plugin.rest.entity.page.RestPageRequest;

public class SimpleRestPageRequest
implements RestPageRequest {
    public static final SimpleRestPageRequest ALL_RESULTS_REQUEST = new SimpleRestPageRequest(0, -1);
    private final int start;
    private final int limit;

    public SimpleRestPageRequest(int start, int limit) {
        this.start = start;
        this.limit = limit;
    }

    @Override
    public int getStart() {
        return this.start;
    }

    @Override
    public int getLimit() {
        return this.limit;
    }
}

