/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.api.model.pagination.PageResponse;

public interface PaginationServiceSupportActionAware {
    default public boolean isSupportPaginationService() {
        return false;
    }

    default public <T> PageResponse<T> getPageResponse() {
        return null;
    }

    default public long getItemsCount() {
        return 0L;
    }
}

