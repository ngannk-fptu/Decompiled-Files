/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.AsynchronousPreferred
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.base.Ticker
 *  org.joda.time.Duration
 */
package com.atlassian.confluence.extra.attachments.metrics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.extra.attachments.metrics.Timer;
import com.atlassian.event.api.AsynchronousPreferred;
import com.atlassian.event.api.EventPublisher;
import com.google.common.base.Ticker;
import org.joda.time.Duration;

public class AttachmentsMacroMetrics {
    private final Timer searchAttachmentsTimer = new Timer(Ticker.systemTicker());
    private final Timer filterAndSortAttachmentsTimer = new Timer(Ticker.systemTicker());
    private final Timer templateModelBuildTimer = new Timer(Ticker.systemTicker());
    private final Timer templateRenderTimer = new Timer(Ticker.systemTicker());
    private int unfilteredAttachmentsCount = 0;
    private int filteredAttachmentsCount = 0;

    public void publishTo(EventPublisher eventPublisher) {
        eventPublisher.publish((Object)this.buildEvent());
    }

    private Event buildEvent() {
        return new Event(this.searchAttachmentsTimer.duration(), this.unfilteredAttachmentsCount, this.filterAndSortAttachmentsTimer.duration(), this.filteredAttachmentsCount, this.templateModelBuildTimer.duration(), this.templateRenderTimer.duration());
    }

    public AttachmentsMacroMetrics searchAttachmentsStart() {
        this.searchAttachmentsTimer.start();
        return this;
    }

    public AttachmentsMacroMetrics searchAttachmentsFinish(int attachmentCount) {
        this.searchAttachmentsTimer.stop();
        this.unfilteredAttachmentsCount = attachmentCount;
        return this;
    }

    public AttachmentsMacroMetrics filterAndSortAttachmentsStart() {
        this.filterAndSortAttachmentsTimer.start();
        return this;
    }

    public AttachmentsMacroMetrics filterAndSortAttachmentsFinish(int attachmentCount) {
        this.filterAndSortAttachmentsTimer.stop();
        this.filteredAttachmentsCount = attachmentCount;
        return this;
    }

    public AttachmentsMacroMetrics templateModelBuildStart() {
        this.templateModelBuildTimer.start();
        return this;
    }

    public AttachmentsMacroMetrics templateModelBuildFinish() {
        this.templateModelBuildTimer.stop();
        return this;
    }

    public AttachmentsMacroMetrics templateRenderStart() {
        this.templateRenderTimer.start();
        return this;
    }

    public AttachmentsMacroMetrics templateRenderFinish() {
        this.templateRenderTimer.stop();
        return this;
    }

    @AsynchronousPreferred
    @EventName(value="confluence.macro.metrics.attachments")
    public static class Event {
        private final Duration searchAttachmentsDuration;
        private final int unfilteredAttachmentsCount;
        private final Duration filterAndSortAttachmentsDuration;
        private final int filteredAttachmentsCount;
        private final Duration templateModelBuildDuration;
        private final Duration templateRenderDuration;

        public Event(Duration searchAttachmentsDuration, int unfilteredAttachmentsCount, Duration filterAndSortAttachmentsDuration, int filteredAttachmentsCount, Duration templateModelBuildDuration, Duration templateRenderDuration) {
            this.searchAttachmentsDuration = searchAttachmentsDuration;
            this.unfilteredAttachmentsCount = unfilteredAttachmentsCount;
            this.filterAndSortAttachmentsDuration = filterAndSortAttachmentsDuration;
            this.filteredAttachmentsCount = filteredAttachmentsCount;
            this.templateModelBuildDuration = templateModelBuildDuration;
            this.templateRenderDuration = templateRenderDuration;
        }

        public long getSearchAttachmentsMillis() {
            return this.searchAttachmentsDuration.getMillis();
        }

        public int getUnfilteredAttachmentsCount() {
            return this.unfilteredAttachmentsCount;
        }

        public long getFilterAndSortAttachmentsMillis() {
            return this.filterAndSortAttachmentsDuration.getMillis();
        }

        public int getFilteredAttachmentsCount() {
            return this.filteredAttachmentsCount;
        }

        public long getTemplateModelBuildMillis() {
            return this.templateModelBuildDuration.getMillis();
        }

        public long getTemplateRenderMillis() {
            return this.templateRenderDuration.getMillis();
        }
    }
}

