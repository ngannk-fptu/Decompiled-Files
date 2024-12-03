/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.annotations.Internal
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.event.events.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.annotations.Internal;
import com.atlassian.event.api.AsynchronousPreferred;

@AsynchronousPreferred
@EventName(value="confluence.shared-drafts.draftPublished")
@Internal
public class SharedDraftPublishedEvent {
    private final String contributorCount;
    private final boolean newPage;

    public SharedDraftPublishedEvent(int contributorCount, boolean newPage) {
        this.newPage = newPage;
        this.contributorCount = contributorCount <= 8 ? String.valueOf(contributorCount) : "8+";
    }

    public String getContributorCount() {
        return this.contributorCount;
    }

    public boolean isNewPage() {
        return this.newPage;
    }
}

