/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ratelimiting.dmz;

import com.atlassian.ratelimiting.dmz.RateLimitingMode;
import com.atlassian.ratelimiting.dmz.SystemJobControlSettings;
import com.atlassian.ratelimiting.dmz.TokenBucketSettings;
import java.time.Duration;

public class SystemRateLimitingSettings {
    public static final SystemRateLimitingSettings RATE_LIMITING_DISABLED = new Builder().mode(RateLimitingMode.OFF).build();
    private RateLimitingMode mode;
    private TokenBucketSettings bucketSettings;
    private SystemJobControlSettings jobControlSettings;

    private SystemRateLimitingSettings() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder copy() {
        return new Builder().mode(this.mode).bucketSettings(this.bucketSettings).jobControlSettings(this.jobControlSettings);
    }

    public RateLimitingMode getMode() {
        return this.mode;
    }

    public TokenBucketSettings getBucketSettings() {
        return this.bucketSettings;
    }

    public SystemJobControlSettings getJobControlSettings() {
        return this.jobControlSettings;
    }

    public void setMode(RateLimitingMode mode) {
        this.mode = mode;
    }

    public void setBucketSettings(TokenBucketSettings bucketSettings) {
        this.bucketSettings = bucketSettings;
    }

    public void setJobControlSettings(SystemJobControlSettings jobControlSettings) {
        this.jobControlSettings = jobControlSettings;
    }

    public String toString() {
        return "SystemRateLimitingSettings(mode=" + (Object)((Object)this.getMode()) + ", bucketSettings=" + this.getBucketSettings() + ", jobControlSettings=" + this.getJobControlSettings() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof SystemRateLimitingSettings)) {
            return false;
        }
        SystemRateLimitingSettings other = (SystemRateLimitingSettings)o;
        if (!other.canEqual(this)) {
            return false;
        }
        RateLimitingMode this$mode = this.getMode();
        RateLimitingMode other$mode = other.getMode();
        if (this$mode == null ? other$mode != null : !((Object)((Object)this$mode)).equals((Object)other$mode)) {
            return false;
        }
        TokenBucketSettings this$bucketSettings = this.getBucketSettings();
        TokenBucketSettings other$bucketSettings = other.getBucketSettings();
        if (this$bucketSettings == null ? other$bucketSettings != null : !((Object)this$bucketSettings).equals(other$bucketSettings)) {
            return false;
        }
        SystemJobControlSettings this$jobControlSettings = this.getJobControlSettings();
        SystemJobControlSettings other$jobControlSettings = other.getJobControlSettings();
        return !(this$jobControlSettings == null ? other$jobControlSettings != null : !((Object)this$jobControlSettings).equals(other$jobControlSettings));
    }

    protected boolean canEqual(Object other) {
        return other instanceof SystemRateLimitingSettings;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        RateLimitingMode $mode = this.getMode();
        result = result * 59 + ($mode == null ? 43 : ((Object)((Object)$mode)).hashCode());
        TokenBucketSettings $bucketSettings = this.getBucketSettings();
        result = result * 59 + ($bucketSettings == null ? 43 : ((Object)$bucketSettings).hashCode());
        SystemJobControlSettings $jobControlSettings = this.getJobControlSettings();
        result = result * 59 + ($jobControlSettings == null ? 43 : ((Object)$jobControlSettings).hashCode());
        return result;
    }

    public static class Builder {
        private RateLimitingMode mode;
        private TokenBucketSettings bucketSettings;
        private SystemJobControlSettings jobControlSettings = new SystemJobControlSettings();

        public Builder mode(RateLimitingMode mode) {
            this.mode = mode;
            return this;
        }

        public Builder bucketSettings(TokenBucketSettings bucketSettings) {
            this.bucketSettings = bucketSettings;
            return this;
        }

        public Builder jobControlSettings(SystemJobControlSettings jobControlSettings) {
            this.jobControlSettings = jobControlSettings;
            return this;
        }

        public Builder bucketCollectionJobFrequencyDuration(Duration bucketCollectionJobFrequencyDuration) {
            this.jobControlSettings.setBucketCollectionJobFrequencyDuration(bucketCollectionJobFrequencyDuration);
            return this;
        }

        public Builder bucketCleanupJobFrequencyDuration(Duration bucketCleanupJobFrequencyDuration) {
            this.jobControlSettings.setBucketCleanupJobFrequencyDuration(bucketCleanupJobFrequencyDuration);
            return this;
        }

        public Builder reportingDbRetentionPeriodDuration(Duration reportingDbRetentionPeriodDuration) {
            this.jobControlSettings.setReportingDbRetentionPeriodDuration(reportingDbRetentionPeriodDuration);
            return this;
        }

        public Builder settingsReloadPeriodDuration(Duration settingsReloadPeriodDuration) {
            this.jobControlSettings.setSettingsReloadJobFrequencyDuration(settingsReloadPeriodDuration);
            return this;
        }

        public Builder reportingDbArchivingJobFrequencyDuration(Duration reportingDbArchivingJobFrequencyDuration) {
            this.jobControlSettings.setReportingDbArchivingJobFrequencyDuration(reportingDbArchivingJobFrequencyDuration);
            return this;
        }

        public SystemRateLimitingSettings build() {
            SystemRateLimitingSettings settings = new SystemRateLimitingSettings();
            settings.mode = this.mode;
            settings.bucketSettings = this.bucketSettings;
            settings.jobControlSettings = this.jobControlSettings;
            return settings;
        }
    }
}

