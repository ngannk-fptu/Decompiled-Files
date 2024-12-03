/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.business.insights.api.schema.SchemaStatus
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.business.insights.core.rest.model;

import com.atlassian.business.insights.api.schema.SchemaStatus;
import com.atlassian.business.insights.core.rest.model.SchemaResponse;
import com.atlassian.business.insights.core.rest.model.Weekdays;
import com.atlassian.business.insights.core.service.scheduler.ScheduleConfig;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class ConfigExportScheduleResponse {
    private List<Weekdays> days;
    private int repeatIntervalInWeeks;
    private String time;
    private String zoneId;
    private String fromDate;
    private SchemaResponse schema;
    private String nextRunTime;

    public ConfigExportScheduleResponse() {
    }

    @JsonCreator
    public ConfigExportScheduleResponse(@Nonnull @JsonProperty(value="days") List<Weekdays> days, @JsonProperty(value="repeatIntervalInWeeks") int repeatIntervalInWeeks, @Nonnull @JsonProperty(value="time") String time, @Nonnull @JsonProperty(value="zoneId") String zoneId, @Nonnull @JsonProperty(value="fromDate") String fromDate, @Nonnull @JsonProperty(value="schema") SchemaResponse schema, @Nullable @JsonProperty(value="nextRunTime") String nextRunTime) {
        this.days = days;
        this.repeatIntervalInWeeks = repeatIntervalInWeeks;
        this.time = time;
        this.zoneId = zoneId;
        this.fromDate = fromDate;
        this.schema = schema;
        this.nextRunTime = nextRunTime;
    }

    public static ConfigExportScheduleResponse from(@Nonnull ScheduleConfig scheduleConfig, @Nonnull SchemaStatus schemaStatus, @Nullable String nextRunTime) {
        Objects.requireNonNull(scheduleConfig, "scheduleConfig must not be null");
        Objects.requireNonNull(schemaStatus, "schemaStatus must not be null");
        return new ConfigExportScheduleResponse(scheduleConfig.getDays(), scheduleConfig.getRepeatIntervalInWeeks(), scheduleConfig.getTime(), scheduleConfig.getZoneId(), scheduleConfig.getFromDate(), new SchemaResponse(scheduleConfig.getSchemaVersion(), schemaStatus), nextRunTime);
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
    @JsonProperty(value="zoneId")
    public String getZoneId() {
        return this.zoneId;
    }

    @Nonnull
    @JsonProperty(value="fromDate")
    public String getFromDate() {
        return this.fromDate;
    }

    @Nonnull
    @JsonProperty(value="schema")
    public SchemaResponse getSchema() {
        return this.schema;
    }

    @Nullable
    @JsonProperty(value="nextRunTime")
    public String getNextRunTime() {
        return this.nextRunTime;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConfigExportScheduleResponse)) {
            return false;
        }
        ConfigExportScheduleResponse that = (ConfigExportScheduleResponse)o;
        return this.repeatIntervalInWeeks == that.repeatIntervalInWeeks && this.days.equals(that.days) && this.time.equals(that.time) && this.zoneId.equals(that.zoneId) && this.fromDate.equals(that.fromDate) && this.schema.equals(that.schema) && Objects.equals(this.nextRunTime, that.nextRunTime);
    }

    public int hashCode() {
        return Objects.hash(this.days, this.repeatIntervalInWeeks, this.time, this.zoneId, this.fromDate, this.schema, this.nextRunTime);
    }

    public String toString() {
        return "ConfigExportScheduleResponse{days=" + this.days + ", repeatIntervalInWeeks=" + this.repeatIntervalInWeeks + ", time='" + this.time + '\'' + ", zoneId='" + this.zoneId + '\'' + ", fromDate='" + this.fromDate + '\'' + ", schema=" + this.schema + ", nextRunTime='" + this.nextRunTime + '\'' + '}';
    }
}

