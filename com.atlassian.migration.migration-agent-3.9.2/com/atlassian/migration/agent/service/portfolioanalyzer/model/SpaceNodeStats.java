/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.service.portfolioanalyzer.model;

import com.atlassian.migration.agent.model.stats.AttachmentStats;
import com.atlassian.migration.agent.model.stats.ContentSummary;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class SpaceNodeStats {
    public static final SpaceNodeStats ZERO = SpaceNodeStats.builder().numberOfPages(0L).numberOfBlogs(0L).numberOfAttachments(0L).numberOfDrafts(0L).attachmentStats(AttachmentStats.ZERO).build();
    @JsonProperty
    private final Long numberOfPages;
    @JsonProperty
    private final Long numberOfBlogs;
    @JsonProperty
    private final Long numberOfDrafts;
    @JsonProperty
    private final Long numberOfAttachments;
    @JsonProperty
    private final AttachmentStats attachmentStats;

    @JsonCreator
    private SpaceNodeStats(@JsonProperty(value="numberOfPages") Long numberOfPages, @JsonProperty(value="numberOfBlogs") Long numberOfBlogs, @JsonProperty(value="numberOfDrafts") Long numberOfDrafts, @JsonProperty(value="numberOfAttachments") Long numberOfAttachments, @JsonProperty(value="attachmentStats") AttachmentStats attachmentStats) {
        this.numberOfPages = numberOfPages;
        this.numberOfBlogs = numberOfBlogs;
        this.numberOfDrafts = numberOfDrafts;
        this.numberOfAttachments = numberOfAttachments;
        this.attachmentStats = attachmentStats;
    }

    public static SpaceNodeStats from(ContentSummary contentSummary) {
        return SpaceNodeStats.builder().numberOfPages(contentSummary.getNumberOfPages()).numberOfBlogs(contentSummary.getNumberOfBlogs()).numberOfDrafts(contentSummary.getNumberOfDrafts()).numberOfAttachments(contentSummary.getNumberOfAttachments()).attachmentStats(contentSummary.getAttachments()).build();
    }

    @Generated
    public static SpaceNodeStatsBuilder builder() {
        return new SpaceNodeStatsBuilder();
    }

    @Generated
    public Long getNumberOfPages() {
        return this.numberOfPages;
    }

    @Generated
    public Long getNumberOfBlogs() {
        return this.numberOfBlogs;
    }

    @Generated
    public Long getNumberOfDrafts() {
        return this.numberOfDrafts;
    }

    @Generated
    public Long getNumberOfAttachments() {
        return this.numberOfAttachments;
    }

    @Generated
    public AttachmentStats getAttachmentStats() {
        return this.attachmentStats;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SpaceNodeStats)) {
            return false;
        }
        SpaceNodeStats other = (SpaceNodeStats)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$numberOfPages = this.getNumberOfPages();
        Long other$numberOfPages = other.getNumberOfPages();
        if (this$numberOfPages == null ? other$numberOfPages != null : !((Object)this$numberOfPages).equals(other$numberOfPages)) {
            return false;
        }
        Long this$numberOfBlogs = this.getNumberOfBlogs();
        Long other$numberOfBlogs = other.getNumberOfBlogs();
        if (this$numberOfBlogs == null ? other$numberOfBlogs != null : !((Object)this$numberOfBlogs).equals(other$numberOfBlogs)) {
            return false;
        }
        Long this$numberOfDrafts = this.getNumberOfDrafts();
        Long other$numberOfDrafts = other.getNumberOfDrafts();
        if (this$numberOfDrafts == null ? other$numberOfDrafts != null : !((Object)this$numberOfDrafts).equals(other$numberOfDrafts)) {
            return false;
        }
        Long this$numberOfAttachments = this.getNumberOfAttachments();
        Long other$numberOfAttachments = other.getNumberOfAttachments();
        if (this$numberOfAttachments == null ? other$numberOfAttachments != null : !((Object)this$numberOfAttachments).equals(other$numberOfAttachments)) {
            return false;
        }
        AttachmentStats this$attachmentStats = this.getAttachmentStats();
        AttachmentStats other$attachmentStats = other.getAttachmentStats();
        return !(this$attachmentStats == null ? other$attachmentStats != null : !((Object)this$attachmentStats).equals(other$attachmentStats));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof SpaceNodeStats;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $numberOfPages = this.getNumberOfPages();
        result = result * 59 + ($numberOfPages == null ? 43 : ((Object)$numberOfPages).hashCode());
        Long $numberOfBlogs = this.getNumberOfBlogs();
        result = result * 59 + ($numberOfBlogs == null ? 43 : ((Object)$numberOfBlogs).hashCode());
        Long $numberOfDrafts = this.getNumberOfDrafts();
        result = result * 59 + ($numberOfDrafts == null ? 43 : ((Object)$numberOfDrafts).hashCode());
        Long $numberOfAttachments = this.getNumberOfAttachments();
        result = result * 59 + ($numberOfAttachments == null ? 43 : ((Object)$numberOfAttachments).hashCode());
        AttachmentStats $attachmentStats = this.getAttachmentStats();
        result = result * 59 + ($attachmentStats == null ? 43 : ((Object)$attachmentStats).hashCode());
        return result;
    }

    @Generated
    public static class SpaceNodeStatsBuilder {
        @Generated
        private Long numberOfPages;
        @Generated
        private Long numberOfBlogs;
        @Generated
        private Long numberOfDrafts;
        @Generated
        private Long numberOfAttachments;
        @Generated
        private AttachmentStats attachmentStats;

        @Generated
        SpaceNodeStatsBuilder() {
        }

        @Generated
        public SpaceNodeStatsBuilder numberOfPages(Long numberOfPages) {
            this.numberOfPages = numberOfPages;
            return this;
        }

        @Generated
        public SpaceNodeStatsBuilder numberOfBlogs(Long numberOfBlogs) {
            this.numberOfBlogs = numberOfBlogs;
            return this;
        }

        @Generated
        public SpaceNodeStatsBuilder numberOfDrafts(Long numberOfDrafts) {
            this.numberOfDrafts = numberOfDrafts;
            return this;
        }

        @Generated
        public SpaceNodeStatsBuilder numberOfAttachments(Long numberOfAttachments) {
            this.numberOfAttachments = numberOfAttachments;
            return this;
        }

        @Generated
        public SpaceNodeStatsBuilder attachmentStats(AttachmentStats attachmentStats) {
            this.attachmentStats = attachmentStats;
            return this;
        }

        @Generated
        public SpaceNodeStats build() {
            return new SpaceNodeStats(this.numberOfPages, this.numberOfBlogs, this.numberOfDrafts, this.numberOfAttachments, this.attachmentStats);
        }

        @Generated
        public String toString() {
            return "SpaceNodeStats.SpaceNodeStatsBuilder(numberOfPages=" + this.numberOfPages + ", numberOfBlogs=" + this.numberOfBlogs + ", numberOfDrafts=" + this.numberOfDrafts + ", numberOfAttachments=" + this.numberOfAttachments + ", attachmentStats=" + this.attachmentStats + ")";
        }
    }
}

