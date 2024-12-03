/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.google.common.base.Ticker
 *  org.apache.commons.lang3.time.StopWatch
 *  org.joda.time.Duration
 */
package com.atlassian.confluence.extra.masterdetail.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.google.common.base.Ticker;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.time.StopWatch;
import org.joda.time.Duration;

@EventName(value="confluence.macro.metrics.details-summary")
public class DetailsSummaryMacroMetricsEvent {
    private final Type type;
    private final String macroOutputType;
    private final int labelledContentCount;
    private final int totalExtractedDetailsCount;
    private final int totalFetchedEntityBodySize;
    private final int totalFetchedEntityBodyCount;
    private final int summaryTableLineCount;
    private final int summaryTableHeadingCount;
    private final int summaryTableCommentsCount;
    private final int summaryTableLikesCount;
    private final int maxResultConfigProperty;
    private final Duration totalReportLoadDuration;
    private final Duration contentSearchDuration;
    private final Duration detailsExtractionDuration;
    private final Duration entityBodyFetchDuration;
    private final Duration summaryTableHeaderBuildDuration;
    private final Duration summaryTableBodyBuildDuration;
    private final Duration summaryTableCommentCountDuration;
    private final Duration summaryTableLikeCountDuration;
    private final Duration templateRenderDuration;

    public DetailsSummaryMacroMetricsEvent(Type type, String macroOutputType, int labelledContentCount, int totalExtractedDetailsCount, int totalFetchedEntityBodySize, int totalFetchedEntityBodyCount, int summaryTableLineCount, int summaryTableHeadingCount, int summaryTableCommentsCount, int summaryTableLikesCount, int maxResultConfigProperty, Duration contentSearchDuration, Duration detailsExtractionDuration, Duration entityBodyFetchDuration, Duration summaryTableHeaderBuildDuration, Duration summaryTableBodyBuildDuration, Duration summaryTableCommentCountDuration, Duration summaryTableLikeCountDuration, Duration templateRenderDuration, Duration totalReportLoadDuration) {
        this.type = type;
        this.macroOutputType = macroOutputType;
        this.labelledContentCount = labelledContentCount;
        this.totalExtractedDetailsCount = totalExtractedDetailsCount;
        this.totalFetchedEntityBodySize = totalFetchedEntityBodySize;
        this.totalFetchedEntityBodyCount = totalFetchedEntityBodyCount;
        this.summaryTableLineCount = summaryTableLineCount;
        this.summaryTableHeadingCount = summaryTableHeadingCount;
        this.summaryTableCommentsCount = summaryTableCommentsCount;
        this.summaryTableLikesCount = summaryTableLikesCount;
        this.maxResultConfigProperty = maxResultConfigProperty;
        this.contentSearchDuration = contentSearchDuration;
        this.detailsExtractionDuration = detailsExtractionDuration;
        this.entityBodyFetchDuration = entityBodyFetchDuration;
        this.summaryTableHeaderBuildDuration = summaryTableHeaderBuildDuration;
        this.summaryTableBodyBuildDuration = summaryTableBodyBuildDuration;
        this.summaryTableCommentCountDuration = summaryTableCommentCountDuration;
        this.summaryTableLikeCountDuration = summaryTableLikeCountDuration;
        this.templateRenderDuration = templateRenderDuration;
        this.totalReportLoadDuration = totalReportLoadDuration;
    }

    public int getMaxResultConfigProperty() {
        return this.maxResultConfigProperty;
    }

    public Type getType() {
        return this.type;
    }

    public String getMacroOutputType() {
        return this.macroOutputType;
    }

    public int getLabelledContentCount() {
        return this.labelledContentCount;
    }

    public int getTotalExtractedDetailsCount() {
        return this.totalExtractedDetailsCount;
    }

    public int getTotalFetchedEntityBodySize() {
        return this.totalFetchedEntityBodySize;
    }

    public int getTotalFetchedEntityBodyCount() {
        return this.totalFetchedEntityBodyCount;
    }

    public int getSummaryTableLineCount() {
        return this.summaryTableLineCount;
    }

    public int getSummaryTableHeadingCount() {
        return this.summaryTableHeadingCount;
    }

    public int getSummaryTableCommentsCount() {
        return this.summaryTableCommentsCount;
    }

    public int getSummaryTableLikesCount() {
        return this.summaryTableLikesCount;
    }

    public long getTotalReportLoadMillis() {
        return this.totalReportLoadDuration.getMillis();
    }

    public long getContentSearchDurationMillis() {
        return this.contentSearchDuration.getMillis();
    }

    public long getDetailsExtractionDurationMillis() {
        return this.detailsExtractionDuration.getMillis();
    }

    public long getEntityBodyFetchDurationMillis() {
        return this.entityBodyFetchDuration.getMillis();
    }

    public long getSummaryTableHeaderBuildDurationMillis() {
        return this.summaryTableHeaderBuildDuration.getMillis();
    }

    public long getSummaryTableBodyBuildDurationMillis() {
        return this.summaryTableBodyBuildDuration.getMillis();
    }

    public long getSummaryTableCommentCountDurationMillis() {
        return this.summaryTableCommentCountDuration.getMillis();
    }

    public long getSummaryTableLikeCountDurationMillis() {
        return this.summaryTableLikeCountDuration.getMillis();
    }

    public long getTemplateRenderDurationMillis() {
        return this.templateRenderDuration.getMillis();
    }

    public static Builder builder(Type type) {
        return new Builder(Ticker.systemTicker(), type);
    }

    public static class Builder {
        private final Ticker ticker;
        private final Type type;
        private String macroOutputType;
        private int labelledContentCount;
        private int totalExtractedDetailsCount;
        private int totalFetchedEntityBodySize;
        private int totalFetchedEntityBodyCount;
        private int summaryTableLineCount;
        private int summaryTableHeadingCount;
        private int summaryTableCommentsCount;
        private int summaryTableLikesCount;
        private int maxResultConfigProperty;
        private final Timer contentSearchTimer = new Timer();
        private StopWatch detailsExtractionTimer = new StopWatch();
        private long totalDetailsExtraction = 0L;
        private StopWatch entityBodyFetchTimer = new StopWatch();
        private long totalEntityBodyFetch = 0L;
        private final Timer summaryTableHeadingBuildTimer = new Timer();
        private final Timer summaryTableBodyBuildTimer = new Timer();
        private final Timer summaryTableCommentCountTimer = new Timer();
        private final Timer summaryTableLikeCountTimer = new Timer();
        private final Timer templateRenderTimer = new Timer();
        private final Timer totalReportLoadDuration = new Timer();

        Builder(Ticker ticker, Type type) {
            this.ticker = ticker;
            this.type = type;
        }

        public DetailsSummaryMacroMetricsEvent build() {
            return new DetailsSummaryMacroMetricsEvent(this.type, this.macroOutputType, this.labelledContentCount, this.totalExtractedDetailsCount, this.totalFetchedEntityBodySize, this.totalFetchedEntityBodyCount, this.summaryTableLineCount, this.summaryTableHeadingCount, this.summaryTableCommentsCount, this.summaryTableLikesCount, this.maxResultConfigProperty, this.contentSearchTimer.duration(), Duration.millis((long)this.totalDetailsExtraction), Duration.millis((long)this.totalEntityBodyFetch), this.summaryTableHeadingBuildTimer.duration(), this.summaryTableBodyBuildTimer.duration(), this.summaryTableCommentCountTimer.duration(), this.summaryTableLikeCountTimer.duration(), this.templateRenderTimer.duration(), this.totalReportLoadDuration.duration());
        }

        public Builder maxResultConfig(int maxResultConfigProperty) {
            this.maxResultConfigProperty = maxResultConfigProperty;
            return this;
        }

        public Builder macroOutputType(String macroOutputType) {
            this.macroOutputType = macroOutputType;
            return this;
        }

        public Builder labelledContentCount(int contentCount) {
            this.labelledContentCount = contentCount;
            return this;
        }

        public Builder contentSearchStart() {
            this.contentSearchTimer.start();
            return this;
        }

        public Builder contentSearchFinish() {
            this.contentSearchTimer.stop();
            return this;
        }

        public void startReport() {
            this.totalReportLoadDuration.start();
        }

        public void finishReport() {
            this.totalReportLoadDuration.stop();
        }

        public void detailsExtractionStart() {
            this.detailsExtractionTimer.start();
        }

        public void detailsExtractionFinish(int extractedDetailsCount) {
            this.detailsExtractionTimer.stop();
            this.totalDetailsExtraction += this.detailsExtractionTimer.getTime(TimeUnit.MILLISECONDS);
            this.totalExtractedDetailsCount += extractedDetailsCount;
            this.detailsExtractionTimer.reset();
        }

        public void entityBodyFetchStart() {
            this.entityBodyFetchTimer.start();
        }

        public void entityBodyFetchFinish(int entityBodySize) {
            this.entityBodyFetchTimer.stop();
            this.totalEntityBodyFetch += this.entityBodyFetchTimer.getTime(TimeUnit.MILLISECONDS);
            this.totalFetchedEntityBodySize += entityBodySize;
            ++this.totalFetchedEntityBodyCount;
            this.entityBodyFetchTimer.reset();
        }

        public void templateRenderStart() {
            this.templateRenderTimer.start();
        }

        public void templateRenderFinish() {
            this.templateRenderTimer.stop();
        }

        public void summaryTableBodyBuildStart() {
            this.summaryTableBodyBuildTimer.start();
        }

        public void summaryTableBodyBuildFinish(int lineCount) {
            this.summaryTableBodyBuildTimer.stop();
            this.summaryTableLineCount = lineCount;
        }

        public void summaryTableHeadersBuildStart() {
            this.summaryTableHeadingBuildTimer.start();
        }

        public void summaryTableHeadersBuildFinish(int headerCount) {
            this.summaryTableHeadingBuildTimer.stop();
            this.summaryTableHeadingCount = headerCount;
        }

        public void summaryTableCountCommentsStart() {
            this.summaryTableCommentCountTimer.start();
        }

        public void summaryTableCountCommentsFinish(int commentsCount) {
            this.summaryTableCommentCountTimer.stop();
            this.summaryTableCommentsCount = commentsCount;
        }

        public void summaryTableCountLikesStart() {
            this.summaryTableLikeCountTimer.start();
        }

        public void summaryTableCountLikesFinish(int likesCount) {
            this.summaryTableLikeCountTimer.stop();
            this.summaryTableLikesCount = likesCount;
        }

        private class Timer {
            private long startTimeNanos;
            private long cumulativeDurationNanos;

            private Timer() {
            }

            void start() {
                this.startTimeNanos = Builder.this.ticker.read();
            }

            void stop() {
                this.cumulativeDurationNanos += Builder.this.ticker.read() - this.startTimeNanos;
            }

            Duration duration() {
                Duration duration = Duration.millis((long)TimeUnit.MILLISECONDS.convert(this.cumulativeDurationNanos, TimeUnit.NANOSECONDS));
                return duration;
            }
        }
    }

    public static enum Type {
        MACRO_EXECUTION,
        REST_RESOURCE,
        SERVICE_EXECUTION;

    }
}

