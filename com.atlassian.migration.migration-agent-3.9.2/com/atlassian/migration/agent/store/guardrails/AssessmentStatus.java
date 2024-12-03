/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.jetbrains.annotations.Nullable
 */
package com.atlassian.migration.agent.store.guardrails;

import com.atlassian.migration.agent.store.guardrails.InstanceAssessmentStatus;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.jetbrains.annotations.Nullable;

public class AssessmentStatus {
    @JsonProperty
    @Nullable
    String completedDate = "";
    @JsonProperty
    InstanceAssessmentStatus status;
    @JsonProperty
    @Nullable
    String progress = null;

    @JsonCreator
    public AssessmentStatus(@Nullable String completedDate, InstanceAssessmentStatus status, @Nullable String progress) {
        this.completedDate = completedDate;
        this.status = status;
        this.progress = progress;
    }

    @Nullable
    @Generated
    public String getCompletedDate() {
        return this.completedDate;
    }

    @Generated
    public InstanceAssessmentStatus getStatus() {
        return this.status;
    }

    @Nullable
    @Generated
    public String getProgress() {
        return this.progress;
    }

    @Generated
    public void setCompletedDate(@Nullable String completedDate) {
        this.completedDate = completedDate;
    }

    @Generated
    public void setStatus(InstanceAssessmentStatus status) {
        this.status = status;
    }

    @Generated
    public void setProgress(@Nullable String progress) {
        this.progress = progress;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AssessmentStatus)) {
            return false;
        }
        AssessmentStatus other = (AssessmentStatus)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$completedDate = this.getCompletedDate();
        String other$completedDate = other.getCompletedDate();
        if (this$completedDate == null ? other$completedDate != null : !this$completedDate.equals(other$completedDate)) {
            return false;
        }
        InstanceAssessmentStatus this$status = this.getStatus();
        InstanceAssessmentStatus other$status = other.getStatus();
        if (this$status == null ? other$status != null : !((Object)((Object)this$status)).equals((Object)other$status)) {
            return false;
        }
        String this$progress = this.getProgress();
        String other$progress = other.getProgress();
        return !(this$progress == null ? other$progress != null : !this$progress.equals(other$progress));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof AssessmentStatus;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $completedDate = this.getCompletedDate();
        result = result * 59 + ($completedDate == null ? 43 : $completedDate.hashCode());
        InstanceAssessmentStatus $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : ((Object)((Object)$status)).hashCode());
        String $progress = this.getProgress();
        result = result * 59 + ($progress == null ? 43 : $progress.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "AssessmentStatus(completedDate=" + this.getCompletedDate() + ", status=" + (Object)((Object)this.getStatus()) + ", progress=" + this.getProgress() + ")";
    }

    @Generated
    public AssessmentStatus() {
    }
}

