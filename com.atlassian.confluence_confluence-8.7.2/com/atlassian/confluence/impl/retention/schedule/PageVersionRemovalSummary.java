/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.retention.schedule;

import java.util.Objects;

public class PageVersionRemovalSummary {
    private long pagesRemovedByGlobalRules;
    private long pagesRemovedBySpaceRules;

    public PageVersionRemovalSummary(long pagesRemovedByGlobalRules, long pagesRemovedBySpaceRules) {
        this.pagesRemovedByGlobalRules = pagesRemovedByGlobalRules;
        this.pagesRemovedBySpaceRules = pagesRemovedBySpaceRules;
    }

    public long getPagesRemovedByGlobalRules() {
        return this.pagesRemovedByGlobalRules;
    }

    public void setPagesRemovedByGlobalRules(long pagesRemovedByGlobalRules) {
        this.pagesRemovedByGlobalRules = pagesRemovedByGlobalRules;
    }

    public long getPagesRemovedBySpaceRules() {
        return this.pagesRemovedBySpaceRules;
    }

    public void setPagesRemovedBySpaceRules(long pagesRemovedBySpaceRules) {
        this.pagesRemovedBySpaceRules = pagesRemovedBySpaceRules;
    }

    public int hashCode() {
        return Objects.hash(this.pagesRemovedByGlobalRules, this.pagesRemovedByGlobalRules);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof PageVersionRemovalSummary)) {
            return false;
        }
        PageVersionRemovalSummary pageVersionRemovalSummary = (PageVersionRemovalSummary)obj;
        return Objects.equals(pageVersionRemovalSummary.pagesRemovedByGlobalRules, this.pagesRemovedByGlobalRules) && Objects.equals(pageVersionRemovalSummary.pagesRemovedBySpaceRules, this.pagesRemovedBySpaceRules);
    }
}

