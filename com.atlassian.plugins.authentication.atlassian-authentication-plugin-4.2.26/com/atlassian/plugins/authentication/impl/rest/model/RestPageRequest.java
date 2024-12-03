/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.authentication.impl.rest.model;

import com.atlassian.plugins.authentication.api.config.PageParameters;

public interface RestPageRequest {
    public static final int ALL_RESULTS = -1;

    public int getStart();

    public int getLimit();

    default public boolean isAllResultsQuery() {
        return this.getLimit() == -1;
    }

    public PageParameters toPageParameters();
}

