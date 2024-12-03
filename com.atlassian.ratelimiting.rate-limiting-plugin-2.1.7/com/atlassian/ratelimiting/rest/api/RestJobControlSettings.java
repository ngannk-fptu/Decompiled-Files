/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.codehaus.jackson.annotate.JsonAutoDetect$Visibility
 */
package com.atlassian.ratelimiting.rest.api;

import com.atlassian.ratelimiting.dmz.SystemJobControlSettings;
import org.codehaus.jackson.annotate.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY)
public class RestJobControlSettings {
    private String reportingDbArchivingJobFrequencyDuration;
    private String reportingDbRetentionPeriodDuration;
    private String bucketCollectionJobFrequencyDuration;
    private String bucketCleanupJobFrequencyDuration;

    public RestJobControlSettings(SystemJobControlSettings systemJobControlSettings) {
        this.reportingDbArchivingJobFrequencyDuration = systemJobControlSettings.getReportingDbArchivingJobFrequencyDuration().toString();
        this.reportingDbRetentionPeriodDuration = systemJobControlSettings.getReportingDbRetentionPeriodDuration().toString();
        this.bucketCleanupJobFrequencyDuration = systemJobControlSettings.getBucketCleanupJobFrequencyDuration().toString();
        this.bucketCollectionJobFrequencyDuration = systemJobControlSettings.getBucketCollectionJobFrequencyDuration().toString();
    }

    public String getReportingDbArchivingJobFrequencyDuration() {
        return this.reportingDbArchivingJobFrequencyDuration;
    }

    public String getReportingDbRetentionPeriodDuration() {
        return this.reportingDbRetentionPeriodDuration;
    }

    public String getBucketCollectionJobFrequencyDuration() {
        return this.bucketCollectionJobFrequencyDuration;
    }

    public String getBucketCleanupJobFrequencyDuration() {
        return this.bucketCleanupJobFrequencyDuration;
    }

    public void setReportingDbArchivingJobFrequencyDuration(String reportingDbArchivingJobFrequencyDuration) {
        this.reportingDbArchivingJobFrequencyDuration = reportingDbArchivingJobFrequencyDuration;
    }

    public void setReportingDbRetentionPeriodDuration(String reportingDbRetentionPeriodDuration) {
        this.reportingDbRetentionPeriodDuration = reportingDbRetentionPeriodDuration;
    }

    public void setBucketCollectionJobFrequencyDuration(String bucketCollectionJobFrequencyDuration) {
        this.bucketCollectionJobFrequencyDuration = bucketCollectionJobFrequencyDuration;
    }

    public void setBucketCleanupJobFrequencyDuration(String bucketCleanupJobFrequencyDuration) {
        this.bucketCleanupJobFrequencyDuration = bucketCleanupJobFrequencyDuration;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RestJobControlSettings)) {
            return false;
        }
        RestJobControlSettings other = (RestJobControlSettings)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$reportingDbArchivingJobFrequencyDuration = this.getReportingDbArchivingJobFrequencyDuration();
        String other$reportingDbArchivingJobFrequencyDuration = other.getReportingDbArchivingJobFrequencyDuration();
        if (this$reportingDbArchivingJobFrequencyDuration == null ? other$reportingDbArchivingJobFrequencyDuration != null : !this$reportingDbArchivingJobFrequencyDuration.equals(other$reportingDbArchivingJobFrequencyDuration)) {
            return false;
        }
        String this$reportingDbRetentionPeriodDuration = this.getReportingDbRetentionPeriodDuration();
        String other$reportingDbRetentionPeriodDuration = other.getReportingDbRetentionPeriodDuration();
        if (this$reportingDbRetentionPeriodDuration == null ? other$reportingDbRetentionPeriodDuration != null : !this$reportingDbRetentionPeriodDuration.equals(other$reportingDbRetentionPeriodDuration)) {
            return false;
        }
        String this$bucketCollectionJobFrequencyDuration = this.getBucketCollectionJobFrequencyDuration();
        String other$bucketCollectionJobFrequencyDuration = other.getBucketCollectionJobFrequencyDuration();
        if (this$bucketCollectionJobFrequencyDuration == null ? other$bucketCollectionJobFrequencyDuration != null : !this$bucketCollectionJobFrequencyDuration.equals(other$bucketCollectionJobFrequencyDuration)) {
            return false;
        }
        String this$bucketCleanupJobFrequencyDuration = this.getBucketCleanupJobFrequencyDuration();
        String other$bucketCleanupJobFrequencyDuration = other.getBucketCleanupJobFrequencyDuration();
        return !(this$bucketCleanupJobFrequencyDuration == null ? other$bucketCleanupJobFrequencyDuration != null : !this$bucketCleanupJobFrequencyDuration.equals(other$bucketCleanupJobFrequencyDuration));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RestJobControlSettings;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $reportingDbArchivingJobFrequencyDuration = this.getReportingDbArchivingJobFrequencyDuration();
        result = result * 59 + ($reportingDbArchivingJobFrequencyDuration == null ? 43 : $reportingDbArchivingJobFrequencyDuration.hashCode());
        String $reportingDbRetentionPeriodDuration = this.getReportingDbRetentionPeriodDuration();
        result = result * 59 + ($reportingDbRetentionPeriodDuration == null ? 43 : $reportingDbRetentionPeriodDuration.hashCode());
        String $bucketCollectionJobFrequencyDuration = this.getBucketCollectionJobFrequencyDuration();
        result = result * 59 + ($bucketCollectionJobFrequencyDuration == null ? 43 : $bucketCollectionJobFrequencyDuration.hashCode());
        String $bucketCleanupJobFrequencyDuration = this.getBucketCleanupJobFrequencyDuration();
        result = result * 59 + ($bucketCleanupJobFrequencyDuration == null ? 43 : $bucketCleanupJobFrequencyDuration.hashCode());
        return result;
    }

    public String toString() {
        return "RestJobControlSettings(reportingDbArchivingJobFrequencyDuration=" + this.getReportingDbArchivingJobFrequencyDuration() + ", reportingDbRetentionPeriodDuration=" + this.getReportingDbRetentionPeriodDuration() + ", bucketCollectionJobFrequencyDuration=" + this.getBucketCollectionJobFrequencyDuration() + ", bucketCleanupJobFrequencyDuration=" + this.getBucketCleanupJobFrequencyDuration() + ")";
    }

    public RestJobControlSettings() {
    }

    public RestJobControlSettings(String reportingDbArchivingJobFrequencyDuration, String reportingDbRetentionPeriodDuration, String bucketCollectionJobFrequencyDuration, String bucketCleanupJobFrequencyDuration) {
        this.reportingDbArchivingJobFrequencyDuration = reportingDbArchivingJobFrequencyDuration;
        this.reportingDbRetentionPeriodDuration = reportingDbRetentionPeriodDuration;
        this.bucketCollectionJobFrequencyDuration = bucketCollectionJobFrequencyDuration;
        this.bucketCleanupJobFrequencyDuration = bucketCleanupJobFrequencyDuration;
    }
}

