/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.config.Schedule
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.builder.EqualsBuilder
 */
package com.atlassian.troubleshooting.stp.hercules;

import com.atlassian.scheduler.config.Schedule;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.builder.EqualsBuilder;

public class LogScanReportSettings {
    private final boolean enabled;
    private final String recipients;
    private final Schedule schedule;

    private LogScanReportSettings(Builder builder) {
        this.enabled = builder.enabled;
        this.recipients = builder.recipients;
        this.schedule = builder.schedule;
    }

    public String getRecipients() {
        return this.recipients;
    }

    public Schedule getSchedule() {
        return this.schedule;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LogScanReportSettings that = (LogScanReportSettings)o;
        return new EqualsBuilder().append(this.enabled, that.enabled).append((Object)this.recipients, (Object)that.recipients).append((Object)this.schedule, (Object)that.schedule).isEquals();
    }

    public int hashCode() {
        int result = this.enabled ? 1 : 0;
        result = 31 * result + (this.recipients != null ? this.recipients.hashCode() : 0);
        result = 31 * result + (this.schedule != null ? this.schedule.hashCode() : 0);
        return result;
    }

    public static class Builder {
        private boolean enabled;
        private String recipients;
        private Schedule schedule;

        public Builder() {
        }

        public Builder(@Nonnull LogScanReportSettings settings) {
            this.recipients = settings.getRecipients();
        }

        @Nonnull
        public LogScanReportSettings build() {
            return new LogScanReportSettings(this);
        }

        @Nonnull
        public Builder enabled(boolean value) {
            this.enabled = value;
            return this;
        }

        @Nonnull
        public Builder recipients(String value) {
            this.recipients = value;
            return this;
        }

        @Nonnull
        public Builder schedule(Schedule value) {
            this.schedule = value;
            return this;
        }
    }
}

