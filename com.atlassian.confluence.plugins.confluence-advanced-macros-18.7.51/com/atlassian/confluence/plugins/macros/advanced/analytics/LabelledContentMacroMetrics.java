/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.base.Ticker
 *  org.joda.time.Duration
 */
package com.atlassian.confluence.plugins.macros.advanced.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.plugins.macros.advanced.analytics.Timer;
import com.atlassian.event.api.EventPublisher;
import com.google.common.base.Ticker;
import org.joda.time.Duration;

@EventName(value="confluence.macro.metrics.contentbylabel")
public class LabelledContentMacroMetrics {
    private final int maxResults;
    private final int resultsCount;
    private final Duration contentSearchDuration;
    private final Duration fetchContentEntitiesDuration;
    private final Duration templateRenderDuration;

    private LabelledContentMacroMetrics(Builder builder) {
        this.maxResults = builder.maxResults;
        this.resultsCount = builder.resultsCount;
        this.contentSearchDuration = builder.contentSearchTimer.duration();
        this.fetchContentEntitiesDuration = builder.fetchContentEntitiesTimer.duration();
        this.templateRenderDuration = builder.templateRenderTimer.duration();
    }

    public int getMaxResults() {
        return this.maxResults;
    }

    public int getResultsCount() {
        return this.resultsCount;
    }

    public long getContentSearchMillis() {
        return this.contentSearchDuration.getMillis();
    }

    public long getFetchContentEntitiesMillis() {
        return this.fetchContentEntitiesDuration.getMillis();
    }

    public long getTemplateRenderMillis() {
        return this.templateRenderDuration.getMillis();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Ticker ticker = Ticker.systemTicker();
        private int maxResults;
        private int resultsCount;
        private Timer contentSearchTimer = new Timer(this.ticker);
        private Timer fetchContentEntitiesTimer = new Timer(this.ticker);
        private Timer templateRenderTimer = new Timer(this.ticker);

        public LabelledContentMacroMetrics build() {
            return new LabelledContentMacroMetrics(this);
        }

        public Builder contentSearchStart(PageRequest pageRequest) {
            this.maxResults = pageRequest.getLimit();
            this.contentSearchTimer.start();
            return this;
        }

        public Builder contentSearchFinish(PageResponse<Content> response) {
            this.contentSearchTimer.stop();
            this.resultsCount = response.size();
            return this;
        }

        public Builder fetchContentEntitiesStart() {
            this.fetchContentEntitiesTimer.start();
            return this;
        }

        public Builder fetchContentEntitiesFinish() {
            this.fetchContentEntitiesTimer.stop();
            return this;
        }

        public Builder templateRenderStart() {
            this.templateRenderTimer.start();
            return this;
        }

        public Builder templateRenderFinish() {
            this.templateRenderTimer.stop();
            return this;
        }

        public void publish(EventPublisher eventPublisher) {
            eventPublisher.publish((Object)this.build());
        }
    }
}

