/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.google.common.base.Ticker
 *  org.joda.time.Duration
 */
package com.atlassian.confluence.plugins.macros.advanced.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.plugins.macros.advanced.analytics.Timer;
import com.atlassian.confluence.plugins.macros.advanced.xhtml.ExcerptType;
import com.google.common.base.Ticker;
import org.joda.time.Duration;

@EventName(value="confluence.macro.metrics.children")
public class ChildrenMacroMetrics {
    private final Duration permittedChildrenFetchTimer;
    private final Duration excerptSumariseTimer;
    private final Duration filterPermittedEntitiesTimer;
    private final Duration renderPageLinkTimer;
    private final int permittedChildrenFetchItemTotal;
    private final int permittedChildrenFetchInvocationCount;
    private final int filterPermittedEntitiesItemTotal;
    private final int filterPermittedEntitiesInvocationCount;
    private final int renderPageLinkInvocationCount;
    private final int excerptSummariseInvocationCount;
    private final ExcerptType excerptType;
    private final int depth;
    private final boolean permissionsExempt;
    private final int renderedLinksTotal;
    private final long renderingTimeMillis;
    private final boolean bulkPermissionsCalled;
    private final boolean bulkPermissionsUpAndRunning;

    ChildrenMacroMetrics(Duration permittedChildrenFetchTimer, Duration excerptSumariseTimer, Duration filterPermittedEntitiesTimer, Duration renderPageLinkTimer, int permittedChildrenFetchItemTotal, int permittedChildrenFetchInvocationCount, int filterPermittedEntitiesItemTotal, int filterPermittedEntitiesInvocationCount, int renderPageLinkInvocationCount, int excerptSummariseInvocationCount, ExcerptType excerptType, int depth, boolean permissionsExempt, int renderedLinksTotal, long renderingTimeMillis, boolean bulkPermissionsCalled, boolean bulkPermissionsUpAndRunning) {
        this.permittedChildrenFetchTimer = permittedChildrenFetchTimer;
        this.excerptSumariseTimer = excerptSumariseTimer;
        this.filterPermittedEntitiesTimer = filterPermittedEntitiesTimer;
        this.renderPageLinkTimer = renderPageLinkTimer;
        this.permittedChildrenFetchItemTotal = permittedChildrenFetchItemTotal;
        this.permittedChildrenFetchInvocationCount = permittedChildrenFetchInvocationCount;
        this.filterPermittedEntitiesItemTotal = filterPermittedEntitiesItemTotal;
        this.filterPermittedEntitiesInvocationCount = filterPermittedEntitiesInvocationCount;
        this.renderPageLinkInvocationCount = renderPageLinkInvocationCount;
        this.excerptSummariseInvocationCount = excerptSummariseInvocationCount;
        this.excerptType = excerptType;
        this.depth = depth;
        this.permissionsExempt = permissionsExempt;
        this.renderedLinksTotal = renderedLinksTotal;
        this.renderingTimeMillis = renderingTimeMillis;
        this.bulkPermissionsCalled = bulkPermissionsCalled;
        this.bulkPermissionsUpAndRunning = bulkPermissionsUpAndRunning;
    }

    public long getPermittedChildrenFetchMillis() {
        return this.permittedChildrenFetchTimer.getMillis();
    }

    public long getExcerptSumariseMillis() {
        return this.excerptSumariseTimer.getMillis();
    }

    public long getFilterPermittedEntitiesMillis() {
        return this.filterPermittedEntitiesTimer.getMillis();
    }

    public long getRenderPageLinkMillis() {
        return this.renderPageLinkTimer.getMillis();
    }

    public int getPermittedChildrenFetchItemTotal() {
        return this.permittedChildrenFetchItemTotal;
    }

    public int getFilterPermittedEntitiesItemTotal() {
        return this.filterPermittedEntitiesItemTotal;
    }

    public int getPermittedChildrenFetchInvocationCount() {
        return this.permittedChildrenFetchInvocationCount;
    }

    public int getFilterPermittedEntitiesInvocationCount() {
        return this.filterPermittedEntitiesInvocationCount;
    }

    public int getRenderPageLinkInvocationCount() {
        return this.renderPageLinkInvocationCount;
    }

    public int getExcerptSummariseInvocationCount() {
        return this.excerptSummariseInvocationCount;
    }

    public String getExcerptType() {
        return this.excerptType.getValue();
    }

    public int getDepth() {
        return this.depth;
    }

    public static Builder builder() {
        return new Builder();
    }

    public long getRenderingTimeMillis() {
        return this.renderingTimeMillis;
    }

    public boolean isBulkPermissionsCalled() {
        return this.bulkPermissionsCalled;
    }

    public boolean isBulkPermissionsUpAndRunning() {
        return this.bulkPermissionsUpAndRunning;
    }

    public int getRenderedLinksTotal() {
        return this.renderedLinksTotal;
    }

    public boolean isPermissionsExempt() {
        return this.permissionsExempt;
    }

    public static class Builder {
        private final Ticker ticker = Ticker.systemTicker();
        private final Timer permittedChildrenFetchTimer = new Timer(this.ticker);
        private final Timer excerptSumariseTimer = new Timer(this.ticker);
        private final Timer filterPermittedEntitiesTimer = new Timer(this.ticker);
        private final Timer renderPageLinkTimer = new Timer(this.ticker);
        private int permittedChildrenFetchItemTotal;
        private int permittedChildrenFetchInvocationCount;
        private int filterPermittedEntitiesItemTotal;
        private int filterPermittedEntitiesInvocationCount;
        private int excerptSummariseInvocationCount;
        private int renderPageLinkInvocationCount;
        private ExcerptType excerptType;
        private int depth;
        private boolean permissionsExempt;
        private int renderedLinksTotal;
        private long renderingTimeMillis;
        private boolean bulkPermissionsCalled;
        private boolean bulkPermissionsUpAndRunning;

        public ChildrenMacroMetrics build() {
            return new ChildrenMacroMetrics(this.permittedChildrenFetchTimer.duration(), this.excerptSumariseTimer.duration(), this.filterPermittedEntitiesTimer.duration(), this.renderPageLinkTimer.duration(), this.permittedChildrenFetchItemTotal, this.permittedChildrenFetchInvocationCount, this.filterPermittedEntitiesItemTotal, this.filterPermittedEntitiesInvocationCount, this.renderPageLinkInvocationCount, this.excerptSummariseInvocationCount, this.excerptType, this.depth, this.permissionsExempt, this.renderedLinksTotal, this.renderingTimeMillis, this.bulkPermissionsCalled, this.bulkPermissionsUpAndRunning);
        }

        public void permittedChildrenFetchStart() {
            this.permittedChildrenFetchTimer.start();
        }

        public void permittedChildrenFetchFinish(int size) {
            this.permittedChildrenFetchTimer.stop();
            this.permittedChildrenFetchItemTotal += size;
            ++this.permittedChildrenFetchInvocationCount;
        }

        public void excerptSummariseStart() {
            this.excerptSumariseTimer.start();
        }

        public void excerptSummariseFinish() {
            this.excerptSumariseTimer.stop();
            ++this.excerptSummariseInvocationCount;
        }

        public void filterPermittedEntitiesStart(int size) {
            this.filterPermittedEntitiesTimer.start();
            this.filterPermittedEntitiesItemTotal += size;
            ++this.filterPermittedEntitiesInvocationCount;
        }

        public void filterPermittedEntitiesFinish() {
            this.filterPermittedEntitiesTimer.stop();
        }

        public void renderPageLinkStart() {
            this.renderPageLinkTimer.start();
        }

        public void renderPageLinkFinish() {
            this.renderPageLinkTimer.stop();
            ++this.renderPageLinkInvocationCount;
        }

        public void renderOptions(ExcerptType excerptType, int depth) {
            this.excerptType = excerptType;
            this.depth = depth;
        }

        public void bulkPermissionsOptions(boolean bulkPermissionsCalled, boolean bulkPermissionsUpAndRunning) {
            this.bulkPermissionsCalled = bulkPermissionsCalled;
            this.bulkPermissionsUpAndRunning = bulkPermissionsUpAndRunning;
        }

        public void setOverallDuration(long duration) {
            this.renderingTimeMillis = duration;
        }

        public void setRenderedLinksTotal(int total) {
            this.renderedLinksTotal = total;
        }

        public void setPermissionsExempt(boolean permissionsExempt) {
            this.permissionsExempt = permissionsExempt;
        }
    }
}

