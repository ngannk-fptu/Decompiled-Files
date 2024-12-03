/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentStatus
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.api.model.content.ContentStatus;
import java.util.Date;

public final class ContributionStatus {
    public static final String DRAFT_STATUS = "draft";
    public static final String UNPUBLISHED_CHANGES_STATUS = "unpublished";
    public static final String CURRENT_STATUS = "current";
    public static final String UNKNOWN_STATUS = "unknown";
    private Long contentId;
    private Long latestVersionId;
    private Long relationId;
    private String contentStatus;
    private Date lastModifiedDate;

    public ContributionStatus() {
    }

    public ContributionStatus(Long contentId, Long latestVersionId, Long relationId, String contentStatus, Date lastModifiedDate) {
        this.contentId = contentId;
        this.latestVersionId = latestVersionId;
        this.relationId = relationId;
        this.contentStatus = contentStatus;
        this.lastModifiedDate = lastModifiedDate == null ? null : new Date(lastModifiedDate.getTime());
    }

    public long getContentId() {
        return this.contentId;
    }

    public long getLatestVersionId() {
        return this.latestVersionId;
    }

    public long getRelationId() {
        return this.relationId;
    }

    public String getContentStatus() {
        return this.contentStatus;
    }

    public Date getLastModifiedDate() {
        if (this.lastModifiedDate == null) {
            return null;
        }
        return new Date(this.lastModifiedDate.getTime());
    }

    public String getStatus() {
        if (ContentStatus.DRAFT.serialise().equals(this.contentStatus)) {
            if (this.latestVersionId == null) {
                return DRAFT_STATUS;
            }
            return UNPUBLISHED_CHANGES_STATUS;
        }
        if (ContentStatus.CURRENT.serialise().equals(this.contentStatus)) {
            return CURRENT_STATUS;
        }
        return UNKNOWN_STATUS;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ContributionStatus that = (ContributionStatus)o;
        if (this.contentId != null ? !this.contentId.equals(that.contentId) : that.contentId != null) {
            return false;
        }
        if (this.latestVersionId != null ? !this.latestVersionId.equals(that.latestVersionId) : that.latestVersionId != null) {
            return false;
        }
        if (this.relationId != null ? !this.relationId.equals(that.relationId) : that.relationId != null) {
            return false;
        }
        if (this.contentStatus != null ? !this.contentStatus.equals(that.contentStatus) : that.contentStatus != null) {
            return false;
        }
        return this.lastModifiedDate != null ? this.lastModifiedDate.equals(that.lastModifiedDate) : that.lastModifiedDate == null;
    }

    public int hashCode() {
        int result = this.contentId != null ? this.contentId.hashCode() : 0;
        result = 31 * result + (this.latestVersionId != null ? this.latestVersionId.hashCode() : 0);
        result = 31 * result + (this.relationId != null ? this.relationId.hashCode() : 0);
        result = 31 * result + (this.contentStatus != null ? this.contentStatus.hashCode() : 0);
        result = 31 * result + (this.lastModifiedDate != null ? this.lastModifiedDate.hashCode() : 0);
        return result;
    }

    public String toString() {
        return String.format("content: %s (latest=%s) relation: %s %s %s", this.contentId, this.latestVersionId, this.relationId, this.contentStatus, this.lastModifiedDate);
    }
}

