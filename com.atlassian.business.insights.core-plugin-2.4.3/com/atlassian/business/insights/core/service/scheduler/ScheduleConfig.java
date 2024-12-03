/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.business.insights.core.service.scheduler;

import com.atlassian.business.insights.core.rest.model.ConfigExportScheduleRequest;
import com.atlassian.business.insights.core.rest.model.Weekdays;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class ScheduleConfig {
    private List<Weekdays> days;
    private int repeatIntervalInWeeks;
    private String time;
    private String zoneId;
    private String fromDate;
    private String scheduleStartDate;
    private int schemaVersion;

    public ScheduleConfig() {
    }

    @JsonCreator
    public ScheduleConfig(@JsonProperty(value="days") @Nonnull List<Weekdays> days, @JsonProperty(value="repeatIntervalInWeeks") int repeatIntervalInWeeks, @JsonProperty(value="time") @Nonnull String time, @JsonProperty(value="zoneId") @Nonnull String zoneId, @JsonProperty(value="fromDate") @Nonnull String fromDate, @JsonProperty(value="scheduleStartDate") @Nullable String scheduleStartDate, @JsonProperty(value="schemaVersion") int schemaVersion) {
        this.days = Objects.requireNonNull(days);
        this.repeatIntervalInWeeks = repeatIntervalInWeeks;
        this.time = Objects.requireNonNull(time);
        this.zoneId = Objects.requireNonNull(zoneId);
        this.fromDate = Objects.requireNonNull(fromDate);
        this.scheduleStartDate = scheduleStartDate;
        this.schemaVersion = schemaVersion;
    }

    @Nonnull
    @JsonProperty(value="days")
    public List<Weekdays> getDays() {
        return this.days;
    }

    @JsonProperty(value="repeatIntervalInWeeks")
    public int getRepeatIntervalInWeeks() {
        return this.repeatIntervalInWeeks;
    }

    @Nonnull
    @JsonProperty(value="time")
    public String getTime() {
        return this.time;
    }

    @Nonnull
    @JsonProperty(value="fromDate")
    public String getFromDate() {
        return this.fromDate;
    }

    @JsonProperty(value="scheduleStartDate")
    public String getScheduleStartDate() {
        return this.scheduleStartDate;
    }

    @Nonnull
    @JsonProperty(value="zoneId")
    public String getZoneId() {
        return this.zoneId;
    }

    @JsonProperty(value="schemaVersion")
    public int getSchemaVersion() {
        return this.schemaVersion;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ScheduleConfig)) {
            return false;
        }
        ScheduleConfig that = (ScheduleConfig)o;
        return this.repeatIntervalInWeeks == that.repeatIntervalInWeeks && Objects.equals(this.days, that.days) && Objects.equals(this.scheduleStartDate, that.scheduleStartDate) && Objects.equals(this.time, that.time) && Objects.equals(this.fromDate, that.fromDate) && Objects.equals(this.zoneId, that.zoneId) && this.schemaVersion == that.schemaVersion;
    }

    public int hashCode() {
        return Objects.hash(this.days, this.repeatIntervalInWeeks, this.time, this.fromDate, this.scheduleStartDate, this.zoneId, this.schemaVersion);
    }

    public String toString() {
        return "ScheduleConfig{days=" + this.days + ", repeatIntervalInWeeks=" + this.repeatIntervalInWeeks + ", time='" + this.time + '\'' + ", fromDate='" + this.fromDate + '\'' + ", scheduleStartDate='" + this.scheduleStartDate + '\'' + ", schemaVersion='" + this.schemaVersion + '\'' + ", zoneId='" + this.zoneId + '\'' + '}';
    }

    public static class Builder {
        private List<Weekdays> days;
        private Integer repeatIntervalInWeeks;
        private String time;
        private String fromDate;
        private String scheduleStartDate;
        private ZoneId zoneId;
        private int schemaVersion;

        public Builder days(List<Weekdays> days) {
            this.days = days;
            return this;
        }

        public Builder repeatIntervalInWeeks(int repeatIntervalInWeeks) {
            this.repeatIntervalInWeeks = repeatIntervalInWeeks;
            return this;
        }

        public Builder time(String time) {
            this.time = time;
            return this;
        }

        public Builder fromDate(String fromDate) {
            this.fromDate = fromDate;
            return this;
        }

        public Builder zoneId(ZoneId zoneId) {
            this.zoneId = zoneId;
            return this;
        }

        public Builder schemaVersion(int schemaVersion) {
            this.schemaVersion = schemaVersion;
            return this;
        }

        public Builder scheduleStartDate(String scheduleStartDate) {
            this.scheduleStartDate = scheduleStartDate;
            return this;
        }

        public Builder from(@Nonnull ScheduleConfig scheduleConfig) {
            Objects.requireNonNull(scheduleConfig);
            this.days = scheduleConfig.getDays();
            this.zoneId = Optional.of(scheduleConfig.getZoneId()).map(ZoneId::of).orElse(null);
            this.fromDate = scheduleConfig.getFromDate();
            this.time = scheduleConfig.getTime();
            this.repeatIntervalInWeeks = scheduleConfig.getRepeatIntervalInWeeks();
            this.schemaVersion = scheduleConfig.getSchemaVersion();
            return this;
        }

        public Builder from(@Nonnull ConfigExportScheduleRequest configRequest) {
            Objects.requireNonNull(configRequest);
            Objects.requireNonNull(configRequest.getRepeatIntervalInWeeks());
            this.days = configRequest.getDays();
            this.zoneId = Optional.ofNullable(configRequest.getZoneId()).map(ZoneId::of).orElse(null);
            this.fromDate = configRequest.getFromDate();
            this.time = configRequest.getTime();
            this.repeatIntervalInWeeks = Integer.parseInt(configRequest.getRepeatIntervalInWeeks());
            this.schemaVersion = Optional.ofNullable(configRequest.getSchemaVersion()).map(Integer::parseInt).orElse(0);
            return this;
        }

        @Nonnull
        public ScheduleConfig build() {
            return new ScheduleConfig(this.days, this.repeatIntervalInWeeks, this.time, this.zoneId.getId(), this.fromDate, this.scheduleStartDate, this.schemaVersion);
        }
    }
}

