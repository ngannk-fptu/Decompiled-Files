/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.model.pagination;

import com.atlassian.confluence.api.model.pagination.LimitedRequest;

public interface SkipDiscardLimitedRequest
extends LimitedRequest {
    public boolean shouldSkipDiscardingThreshold();
}

