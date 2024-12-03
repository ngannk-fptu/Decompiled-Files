/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.audit.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.audit.analytics.BaseAnalyticEvent;

@EventName(value="audit.search")
public class SearchEvent
extends BaseAnalyticEvent {
    private final boolean dateFilter;
    private final boolean userFilter;
    private final boolean resourceFilter;
    private final boolean categoryFilter;
    private final boolean actionFilter;
    private final boolean textFilter;
    private final boolean partialResult;
    private final boolean fullScan;
    private final String delegatedType;

    public SearchEvent(boolean dateFilter, boolean userFilter, boolean resourceFilter, boolean categoryFilter, boolean actionFilter, boolean textFilter, boolean partialResult, boolean fullScan, String delegatedType, String pluginVersion) {
        super(pluginVersion);
        this.dateFilter = dateFilter;
        this.userFilter = userFilter;
        this.resourceFilter = resourceFilter;
        this.categoryFilter = categoryFilter;
        this.actionFilter = actionFilter;
        this.textFilter = textFilter;
        this.partialResult = partialResult;
        this.fullScan = fullScan;
        this.delegatedType = delegatedType;
    }

    public boolean isDateFilter() {
        return this.dateFilter;
    }

    public boolean isUserFilter() {
        return this.userFilter;
    }

    public boolean isResourceFilter() {
        return this.resourceFilter;
    }

    public boolean isCategoryFilter() {
        return this.categoryFilter;
    }

    public boolean isActionFilter() {
        return this.actionFilter;
    }

    public boolean isTextFilter() {
        return this.textFilter;
    }

    public boolean isPartialResult() {
        return this.partialResult;
    }

    public boolean isFullScan() {
        return this.fullScan;
    }

    public String getDelegatedType() {
        return this.delegatedType;
    }
}

