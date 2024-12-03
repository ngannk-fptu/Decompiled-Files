/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.contributors;

import com.atlassian.confluence.user.ConfluenceUser;

public class VersionContributorSummary {
    private final long contentId;
    private final ConfluenceUser contributor;

    public VersionContributorSummary(Long originalVersionId, Long contentId, ConfluenceUser contributor) {
        this(originalVersionId != null ? originalVersionId : contentId, contributor);
    }

    public VersionContributorSummary(long contentId, ConfluenceUser contributor) {
        this.contentId = contentId;
        this.contributor = contributor;
    }

    public long getContentId() {
        return this.contentId;
    }

    public ConfluenceUser getContributor() {
        return this.contributor;
    }
}

