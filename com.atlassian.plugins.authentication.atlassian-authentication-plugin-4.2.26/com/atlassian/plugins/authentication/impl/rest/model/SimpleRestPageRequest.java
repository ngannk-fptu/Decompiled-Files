/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.authentication.impl.rest.model;

import com.atlassian.plugins.authentication.api.config.PageParameters;
import com.atlassian.plugins.authentication.impl.rest.model.RestPageRequest;

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

    @Override
    public PageParameters toPageParameters() {
        return new PageParameters(this.start, this.limitPlusOne());
    }

    public int limitPlusOne() {
        return this.limit == -1 || this.limit == Integer.MAX_VALUE ? this.limit : this.limit + 1;
    }
}

