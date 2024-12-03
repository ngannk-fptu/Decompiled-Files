/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 */
package com.atlassian.confluence.plugins.cql.impl;

import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageRequest;

public class CQLPaginationLimits {
    static final int BODY_EXPANDED_LIMIT = 50;
    static final int DB_REQUIRED_LIMIT = 200;
    static final int INDEX_ONLY_LIMIT = 1000;

    public static LimitedRequest limitRequest(PageRequest pageRequest, Expansions expansions) {
        return LimitedRequestImpl.create((PageRequest)pageRequest, (int)CQLPaginationLimits.getMaxLimit(expansions));
    }

    private static int getMaxLimit(Expansions expansions) {
        int descendantsDivisor;
        int n = descendantsDivisor = expansions.canExpand("descendants") ? 2 : 1;
        if (expansions.isEmpty()) {
            return 1000;
        }
        if (expansions.canExpand("body")) {
            return 50 / descendantsDivisor;
        }
        return 200 / descendantsDivisor;
    }
}

