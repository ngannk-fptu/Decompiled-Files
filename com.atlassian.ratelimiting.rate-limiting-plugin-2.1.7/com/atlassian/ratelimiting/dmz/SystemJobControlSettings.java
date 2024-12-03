/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ratelimiting.dmz;

import java.time.Duration;

public class SystemJobControlSettings {
    private Duration reportingDbArchivingJobFrequencyDuration;
    private Duration reportingDbRetentionPeriodDuration;
    private Duration bucketCollectionJobFrequencyDuration;
    private Duration bucketCleanupJobFrequencyDuration;
    private Duration settingsReloadJobFrequencyDuration;

    public static SystemJobControlSettingsBuilder builder() {
        return new SystemJobControlSettingsBuilder();
    }

    public Duration getReportingDbArchivingJobFrequencyDuration() {
        return this.reportingDbArchivingJobFrequencyDuration;
    }

    public Duration getReportingDbRetentionPeriodDuration() {
        return this.reportingDbRetentionPeriodDuration;
    }

    public Duration getBucketCollectionJobFrequencyDuration() {
        return this.bucketCollectionJobFrequencyDuration;
    }

    public Duration getBucketCleanupJobFrequencyDuration() {
        return this.bucketCleanupJobFrequencyDuration;
    }

    public Duration getSettingsReloadJobFrequencyDuration() {
        return this.settingsReloadJobFrequencyDuration;
    }

    public void setReportingDbArchivingJobFrequencyDuration(Duration reportingDbArchivingJobFrequencyDuration) {
        this.reportingDbArchivingJobFrequencyDuration = reportingDbArchivingJobFrequencyDuration;
    }

    public void setReportingDbRetentionPeriodDuration(Duration reportingDbRetentionPeriodDuration) {
        this.reportingDbRetentionPeriodDuration = reportingDbRetentionPeriodDuration;
    }

    public void setBucketCollectionJobFrequencyDuration(Duration bucketCollectionJobFrequencyDuration) {
        this.bucketCollectionJobFrequencyDuration = bucketCollectionJobFrequencyDuration;
    }

    public void setBucketCleanupJobFrequencyDuration(Duration bucketCleanupJobFrequencyDuration) {
        this.bucketCleanupJobFrequencyDuration = bucketCleanupJobFrequencyDuration;
    }

    public void setSettingsReloadJobFrequencyDuration(Duration settingsReloadJobFrequencyDuration) {
        this.settingsReloadJobFrequencyDuration = settingsReloadJobFrequencyDuration;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SystemJobControlSettings)) {
            return false;
        }
        SystemJobControlSettings other = (SystemJobControlSettings)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Duration this$reportingDbArchivingJobFrequencyDuration = this.getReportingDbArchivingJobFrequencyDuration();
        Duration other$reportingDbArchivingJobFrequencyDuration = other.getReportingDbArchivingJobFrequencyDuration();
        if (this$reportingDbArchivingJobFrequencyDuration == null ? other$reportingDbArchivingJobFrequencyDuration != null : !((Object)this$reportingDbArchivingJobFrequencyDuration).equals(other$reportingDbArchivingJobFrequencyDuration)) {
            return false;
        }
        Duration this$reportingDbRetentionPeriodDuration = this.getReportingDbRetentionPeriodDuration();
        Duration other$reportingDbRetentionPeriodDuration = other.getReportingDbRetentionPeriodDuration();
        if (this$reportingDbRetentionPeriodDuration == null ? other$reportingDbRetentionPeriodDuration != null : !((Object)this$reportingDbRetentionPeriodDuration).equals(other$reportingDbRetentionPeriodDuration)) {
            return false;
        }
        Duration this$bucketCollectionJobFrequencyDuration = this.getBucketCollectionJobFrequencyDuration();
        Duration other$bucketCollectionJobFrequencyDuration = other.getBucketCollectionJobFrequencyDuration();
        if (this$bucketCollectionJobFrequencyDuration == null ? other$bucketCollectionJobFrequencyDuration != null : !((Object)this$bucketCollectionJobFrequencyDuration).equals(other$bucketCollectionJobFrequencyDuration)) {
            return false;
        }
        Duration this$bucketCleanupJobFrequencyDuration = this.getBucketCleanupJobFrequencyDuration();
        Duration other$bucketCleanupJobFrequencyDuration = other.getBucketCleanupJobFrequencyDuration();
        if (this$bucketCleanupJobFrequencyDuration == null ? other$bucketCleanupJobFrequencyDuration != null : !((Object)this$bucketCleanupJobFrequencyDuration).equals(other$bucketCleanupJobFrequencyDuration)) {
            return false;
        }
        Duration this$settingsReloadJobFrequencyDuration = this.getSettingsReloadJobFrequencyDuration();
        Duration other$settingsReloadJobFrequencyDuration = other.getSettingsReloadJobFrequencyDuration();
        return !(this$settingsReloadJobFrequencyDuration == null ? other$settingsReloadJobFrequencyDuration != null : !((Object)this$settingsReloadJobFrequencyDuration).equals(other$settingsReloadJobFrequencyDuration));
    }

    protected boolean canEqual(Object other) {
        return other instanceof SystemJobControlSettings;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Duration $reportingDbArchivingJobFrequencyDuration = this.getReportingDbArchivingJobFrequencyDuration();
        result = result * 59 + ($reportingDbArchivingJobFrequencyDuration == null ? 43 : ((Object)$reportingDbArchivingJobFrequencyDuration).hashCode());
        Duration $reportingDbRetentionPeriodDuration = this.getReportingDbRetentionPeriodDuration();
        result = result * 59 + ($reportingDbRetentionPeriodDuration == null ? 43 : ((Object)$reportingDbRetentionPeriodDuration).hashCode());
        Duration $bucketCollectionJobFrequencyDuration = this.getBucketCollectionJobFrequencyDuration();
        result = result * 59 + ($bucketCollectionJobFrequencyDuration == null ? 43 : ((Object)$bucketCollectionJobFrequencyDuration).hashCode());
        Duration $bucketCleanupJobFrequencyDuration = this.getBucketCleanupJobFrequencyDuration();
        result = result * 59 + ($bucketCleanupJobFrequencyDuration == null ? 43 : ((Object)$bucketCleanupJobFrequencyDuration).hashCode());
        Duration $settingsReloadJobFrequencyDuration = this.getSettingsReloadJobFrequencyDuration();
        result = result * 59 + ($settingsReloadJobFrequencyDuration == null ? 43 : ((Object)$settingsReloadJobFrequencyDuration).hashCode());
        return result;
    }

    public String toString() {
        return "SystemJobControlSettings(reportingDbArchivingJobFrequencyDuration=" + this.getReportingDbArchivingJobFrequencyDuration() + ", reportingDbRetentionPeriodDuration=" + this.getReportingDbRetentionPeriodDuration() + ", bucketCollectionJobFrequencyDuration=" + this.getBucketCollectionJobFrequencyDuration() + ", bucketCleanupJobFrequencyDuration=" + this.getBucketCleanupJobFrequencyDuration() + ", settingsReloadJobFrequencyDuration=" + this.getSettingsReloadJobFrequencyDuration() + ")";
    }

    public SystemJobControlSettings() {
    }

    public SystemJobControlSettings(Duration reportingDbArchivingJobFrequencyDuration, Duration reportingDbRetentionPeriodDuration, Duration bucketCollectionJobFrequencyDuration, Duration bucketCleanupJobFrequencyDuration, Duration settingsReloadJobFrequencyDuration) {
        this.reportingDbArchivingJobFrequencyDuration = reportingDbArchivingJobFrequencyDuration;
        this.reportingDbRetentionPeriodDuration = reportingDbRetentionPeriodDuration;
        this.bucketCollectionJobFrequencyDuration = bucketCollectionJobFrequencyDuration;
        this.bucketCleanupJobFrequencyDuration = bucketCleanupJobFrequencyDuration;
        this.settingsReloadJobFrequencyDuration = settingsReloadJobFrequencyDuration;
    }

    public static class SystemJobControlSettingsBuilder {
        private Duration reportingDbArchivingJobFrequencyDuration;
        private Duration reportingDbRetentionPeriodDuration;
        private Duration bucketCollectionJobFrequencyDuration;
        private Duration bucketCleanupJobFrequencyDuration;
        private Duration settingsReloadJobFrequencyDuration;

        SystemJobControlSettingsBuilder() {
        }

        public SystemJobControlSettingsBuilder reportingDbArchivingJobFrequencyDuration(Duration reportingDbArchivingJobFrequencyDuration) {
            this.reportingDbArchivingJobFrequencyDuration = reportingDbArchivingJobFrequencyDuration;
            return this;
        }

        public SystemJobControlSettingsBuilder reportingDbRetentionPeriodDuration(Duration reportingDbRetentionPeriodDuration) {
            this.reportingDbRetentionPeriodDuration = reportingDbRetentionPeriodDuration;
            return this;
        }

        public SystemJobControlSettingsBuilder bucketCollectionJobFrequencyDuration(Duration bucketCollectionJobFrequencyDuration) {
            this.bucketCollectionJobFrequencyDuration = bucketCollectionJobFrequencyDuration;
            return this;
        }

        public SystemJobControlSettingsBuilder bucketCleanupJobFrequencyDuration(Duration bucketCleanupJobFrequencyDuration) {
            this.bucketCleanupJobFrequencyDuration = bucketCleanupJobFrequencyDuration;
            return this;
        }

        public SystemJobControlSettingsBuilder settingsReloadJobFrequencyDuration(Duration settingsReloadJobFrequencyDuration) {
            this.settingsReloadJobFrequencyDuration = settingsReloadJobFrequencyDuration;
            return this;
        }

        public SystemJobControlSettings build() {
            return new SystemJobControlSettings(this.reportingDbArchivingJobFrequencyDuration, this.reportingDbRetentionPeriodDuration, this.bucketCollectionJobFrequencyDuration, this.bucketCleanupJobFrequencyDuration, this.settingsReloadJobFrequencyDuration);
        }

        public String toString() {
            return "SystemJobControlSettings.SystemJobControlSettingsBuilder(reportingDbArchivingJobFrequencyDuration=" + this.reportingDbArchivingJobFrequencyDuration + ", reportingDbRetentionPeriodDuration=" + this.reportingDbRetentionPeriodDuration + ", bucketCollectionJobFrequencyDuration=" + this.bucketCollectionJobFrequencyDuration + ", bucketCleanupJobFrequencyDuration=" + this.bucketCleanupJobFrequencyDuration + ", settingsReloadJobFrequencyDuration=" + this.settingsReloadJobFrequencyDuration + ")";
        }
    }
}

