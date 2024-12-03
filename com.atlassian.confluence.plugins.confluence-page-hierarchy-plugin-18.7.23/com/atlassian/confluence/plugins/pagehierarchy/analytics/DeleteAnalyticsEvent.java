/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.confluence.plugins.pagehierarchy.analytics;

import com.atlassian.analytics.api.annotations.EventName;

@EventName(value="confluence.bulkoperations.delete")
public class DeleteAnalyticsEvent {
    private final boolean deleteHierarchy;
    private final int pages;

    public DeleteAnalyticsEvent(boolean deleteHierarchy, int pages) {
        this.deleteHierarchy = deleteHierarchy;
        this.pages = pages;
    }

    public boolean isDeleteHierarchy() {
        return this.deleteHierarchy;
    }

    public int getPages() {
        return this.pages;
    }
}

