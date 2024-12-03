/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.jetbrains.annotations.NotNull
 */
package com.atlassian.migration.agent.model.stats;

import com.atlassian.migration.agent.model.stats.AttachmentStats;
import java.sql.Timestamp;
import java.time.Instant;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.jetbrains.annotations.NotNull;

@ParametersAreNonnullByDefault
public class ContentSummary {
    public static final ContentSummary ZERO = ContentSummary.builder().numberOfPages(0L).numberOfBlogs(0L).numberOfAttachments(0L).numberOfDrafts(0L).numberOfTeamCalendars(0L).build();
    @JsonProperty
    private final Long numberOfPages;
    @JsonProperty
    private final Long numberOfBlogs;
    @JsonProperty
    private final Long numberOfDrafts;
    @JsonProperty
    private final Long numberOfAttachments;
    @JsonProperty
    private final Long numberOfTeamCalendars;
    @JsonProperty
    private final Instant lastModified;
    @JsonProperty
    private final AttachmentStats attachments;

    @JsonCreator
    private ContentSummary(@JsonProperty(value="attachments") AttachmentStats attachments, @JsonProperty(value="numberOfPages") Long numberOfPages, @JsonProperty(value="numberOfBlogs") Long numberOfBlogs, @JsonProperty(value="numberOfDrafts") Long numberOfDrafts, @JsonProperty(value="numberOfAttachments") Long numberOfAttachments, @JsonProperty(value="numberOfTeamCalendars") Long numberOfTeamCalendars, @Nullable @JsonProperty(value="lastModified") Instant lastModified) {
        this.numberOfPages = numberOfPages;
        this.numberOfBlogs = numberOfBlogs;
        this.numberOfDrafts = numberOfDrafts;
        this.numberOfAttachments = numberOfAttachments;
        this.numberOfTeamCalendars = numberOfTeamCalendars;
        this.attachments = attachments;
        this.lastModified = lastModified;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getNumberOfPages() {
        return this.numberOfPages;
    }

    public Long getNumberOfBlogs() {
        return this.numberOfBlogs;
    }

    public Long getNumberOfDrafts() {
        return this.numberOfDrafts;
    }

    public Long getNumberOfAttachments() {
        return this.numberOfAttachments;
    }

    public Long getNumberOfTeamCalendars() {
        return this.numberOfTeamCalendars;
    }

    public Instant getLastModified() {
        return this.lastModified;
    }

    public AttachmentStats getAttachments() {
        return this.attachments;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ContentSummary)) {
            return false;
        }
        ContentSummary other = (ContentSummary)o;
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
        Long this$numberOfTeamCalendars = this.getNumberOfTeamCalendars();
        Long other$numberOfTeamCalendars = other.getNumberOfTeamCalendars();
        if (this$numberOfTeamCalendars == null ? other$numberOfTeamCalendars != null : !((Object)this$numberOfTeamCalendars).equals(other$numberOfTeamCalendars)) {
            return false;
        }
        Instant this$lastModified = this.getLastModified();
        Instant other$lastModified = other.getLastModified();
        if (this$lastModified == null ? other$lastModified != null : !((Object)this$lastModified).equals(other$lastModified)) {
            return false;
        }
        AttachmentStats this$attachments = this.getAttachments();
        AttachmentStats other$attachments = other.getAttachments();
        return !(this$attachments == null ? other$attachments != null : !((Object)this$attachments).equals(other$attachments));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof ContentSummary;
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
        Long $numberOfTeamCalendars = this.getNumberOfTeamCalendars();
        result = result * 59 + ($numberOfTeamCalendars == null ? 43 : ((Object)$numberOfTeamCalendars).hashCode());
        Instant $lastModified = this.getLastModified();
        result = result * 59 + ($lastModified == null ? 43 : ((Object)$lastModified).hashCode());
        AttachmentStats $attachments = this.getAttachments();
        result = result * 59 + ($attachments == null ? 43 : ((Object)$attachments).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "ContentSummary(numberOfPages=" + this.getNumberOfPages() + ", numberOfBlogs=" + this.getNumberOfBlogs() + ", numberOfDrafts=" + this.getNumberOfDrafts() + ", numberOfAttachments=" + this.getNumberOfAttachments() + ", numberOfTeamCalendars=" + this.getNumberOfTeamCalendars() + ", lastModified=" + this.getLastModified() + ", attachments=" + this.getAttachments() + ")";
    }

    public static final class Builder {
        private Long numberOfPages;
        private Long numberOfBlogs;
        private Long numberOfDrafts;
        private Long numberOfAttachments;
        private Long numberOfTeamCalendars;
        private Instant lastModified;
        private AttachmentStats attachments = AttachmentStats.ZERO;

        private Builder() {
        }

        @Nonnull
        public Builder numberOfPages(Long numberOfPages) {
            this.numberOfPages = numberOfPages;
            return this;
        }

        @Nonnull
        public Builder numberOfBlogs(Long numberOfBlogs) {
            this.numberOfBlogs = numberOfBlogs;
            return this;
        }

        @Nonnull
        public Builder numberOfDrafts(Long numberOfDrafts) {
            this.numberOfDrafts = numberOfDrafts;
            return this;
        }

        @Nonnull
        public Builder numberOfAttachments(Long numberOfAttachments) {
            this.numberOfAttachments = numberOfAttachments;
            return this;
        }

        @Nonnull
        public Builder numberOfTeamCalendars(Long numberOfTeamCalendars) {
            this.numberOfTeamCalendars = numberOfTeamCalendars;
            return this;
        }

        @Nonnull
        public Builder attachments(AttachmentStats attachments) {
            this.attachments = attachments;
            return this;
        }

        @NotNull
        public Builder lastModified(Timestamp lastModified) {
            if (lastModified != null) {
                this.lastModified = lastModified.toInstant();
            }
            return this;
        }

        public ContentSummary build() {
            return new ContentSummary(this.attachments, this.numberOfPages, this.numberOfBlogs, this.numberOfDrafts, this.numberOfAttachments, this.numberOfTeamCalendars, this.lastModified);
        }
    }
}

