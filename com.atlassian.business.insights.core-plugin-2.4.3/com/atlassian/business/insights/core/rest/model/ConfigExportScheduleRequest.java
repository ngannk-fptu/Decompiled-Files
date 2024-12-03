/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.business.insights.core.rest.model;

import com.atlassian.business.insights.core.rest.model.Weekdays;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class ConfigExportScheduleRequest {
    private List<Weekdays> days;
    private String repeatIntervalInWeeks;
    private String time;
    private String zoneId;
    private String fromDate;
    private String schemaVersion;

    public ConfigExportScheduleRequest() {
    }

    @JsonCreator
    public ConfigExportScheduleRequest(@Nullable @JsonProperty(value="days") List<Weekdays> days, @Nullable @JsonProperty(value="repeatIntervalInWeeks") String repeatIntervalInWeeks, @Nullable @JsonProperty(value="time") String time, @Nullable @JsonProperty(value="zoneId") String zoneId, @Nullable @JsonProperty(value="fromDate") String fromDate, @Nullable @JsonProperty(value="schemaVersion") String schemaVersion) {
        this.days = days;
        this.repeatIntervalInWeeks = repeatIntervalInWeeks;
        this.time = time;
        this.zoneId = zoneId;
        this.fromDate = fromDate;
        this.schemaVersion = schemaVersion;
    }

    @Nullable
    @JsonProperty(value="days")
    public List<Weekdays> getDays() {
        return this.days;
    }

    @Nullable
    @JsonProperty(value="repeatIntervalInWeeks")
    public String getRepeatIntervalInWeeks() {
        return this.repeatIntervalInWeeks;
    }

    @Nullable
    @JsonProperty(value="time")
    public String getTime() {
        return this.time;
    }

    @Nullable
    @JsonProperty(value="zoneId")
    public String getZoneId() {
        return this.zoneId;
    }

    @Nullable
    @JsonProperty(value="fromDate")
    public String getFromDate() {
        return this.fromDate;
    }

    @Nullable
    @JsonProperty(value="schemaVersion")
    public String getSchemaVersion() {
        return this.schemaVersion;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ConfigExportScheduleRequest that = (ConfigExportScheduleRequest)o;
        return Objects.equals(this.days, that.days) && Objects.equals(this.repeatIntervalInWeeks, that.repeatIntervalInWeeks) && Objects.equals(this.time, that.time) && Objects.equals(this.zoneId, that.zoneId) && Objects.equals(this.fromDate, that.fromDate);
    }

    public int hashCode() {
        return Objects.hash(this.days, this.repeatIntervalInWeeks, this.time, this.zoneId, this.fromDate);
    }

    public String toString() {
        return "ConfigExportScheduleRequest{days=" + this.days + ", repeatIntervalInWeeks=" + this.repeatIntervalInWeeks + ", time='" + this.time + '\'' + ", zoneId='" + this.zoneId + '\'' + ", fromDate='" + this.fromDate + '\'' + '}';
    }
}

