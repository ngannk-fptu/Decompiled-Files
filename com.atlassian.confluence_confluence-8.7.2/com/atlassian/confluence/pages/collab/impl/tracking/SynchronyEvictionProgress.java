/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.pages.collab.impl.tracking;

import com.atlassian.confluence.pages.collab.impl.tracking.SynchronyEvictionSearchType;
import com.atlassian.confluence.pages.collab.impl.tracking.SynchronyEvictionType;
import com.google.common.base.Preconditions;
import java.util.concurrent.TimeUnit;

public class SynchronyEvictionProgress {
    private SynchronyEvictionType evictionType;
    private Integer thresholdHours;
    private Integer evictionLimit;
    private Long evictionStartNanos;
    private SynchronyEvictionSearchType searchType;
    private Integer searchLimit;
    private Long searchStartNanos;
    private Integer removalNumberOfContent;
    private Long removalStartNanos;

    public Integer getThresholdHours() {
        return this.thresholdHours;
    }

    public SynchronyEvictionProgress setThresholdHours(Integer thresholdHours) {
        this.thresholdHours = thresholdHours;
        return this;
    }

    public Integer getEvictionLimit() {
        return this.evictionLimit;
    }

    public Integer getSearchLimit() {
        return this.searchLimit;
    }

    public SynchronyEvictionProgress setSearchLimit(Integer searchLimit) {
        this.searchLimit = searchLimit;
        return this;
    }

    public SynchronyEvictionProgress setEvictionLimit(Integer evictionLimit) {
        this.evictionLimit = evictionLimit;
        return this;
    }

    public SynchronyEvictionType getEvictionType() {
        return this.evictionType;
    }

    public SynchronyEvictionProgress setEvictionType(SynchronyEvictionType evictionType) {
        this.evictionType = evictionType;
        return this;
    }

    public SynchronyEvictionSearchType getSearchType() {
        return this.searchType;
    }

    public SynchronyEvictionProgress setSearchType(SynchronyEvictionSearchType searchType) {
        this.searchType = searchType;
        return this;
    }

    public Integer getRemovalNumberOfContent() {
        return this.removalNumberOfContent;
    }

    public SynchronyEvictionProgress setRemovalNumberOfContent(Integer removalNumberOfContent) {
        this.removalNumberOfContent = removalNumberOfContent;
        return this;
    }

    public SynchronyEvictionProgress startEvictionTimer() {
        this.evictionStartNanos = System.nanoTime();
        return this;
    }

    public SynchronyEvictionProgress startSearchTimer() {
        this.searchStartNanos = System.nanoTime();
        return this;
    }

    public SynchronyEvictionProgress startRemovalTimer() {
        this.removalStartNanos = System.nanoTime();
        return this;
    }

    public long millisPassedFromEvictionStart() {
        Preconditions.checkState((this.evictionStartNanos != null ? 1 : 0) != 0);
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - this.evictionStartNanos);
    }

    public long millisPassedFromSearchStart() {
        Preconditions.checkState((this.searchStartNanos != null ? 1 : 0) != 0);
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - this.searchStartNanos);
    }

    public long millisPassedFromRemovalStart() {
        Preconditions.checkState((this.removalStartNanos != null ? 1 : 0) != 0);
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - this.removalStartNanos);
    }

    public SynchronyEvictionProgress mergeWith(SynchronyEvictionProgress other) {
        if (this.evictionType == null) {
            this.evictionType = other.evictionType;
        }
        if (this.thresholdHours == null) {
            this.thresholdHours = other.thresholdHours;
        }
        if (this.evictionLimit == null) {
            this.evictionLimit = other.evictionLimit;
        }
        if (this.evictionStartNanos == null) {
            this.evictionStartNanos = other.evictionStartNanos;
        }
        if (this.searchType == null) {
            this.searchType = other.searchType;
        }
        if (this.searchLimit == null) {
            this.searchLimit = other.searchLimit;
        }
        if (this.searchStartNanos == null) {
            this.searchStartNanos = other.searchStartNanos;
        }
        if (this.removalNumberOfContent == null) {
            this.removalNumberOfContent = other.removalNumberOfContent;
        }
        if (this.removalStartNanos == null) {
            this.removalStartNanos = other.removalStartNanos;
        }
        return this;
    }
}

