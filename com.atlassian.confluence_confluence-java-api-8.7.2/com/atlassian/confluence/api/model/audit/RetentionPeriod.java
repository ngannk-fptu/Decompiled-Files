/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.api.model.audit;

import com.atlassian.annotations.ExperimentalApi;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@ExperimentalApi
@JsonIgnoreProperties(ignoreUnknown=true)
@Deprecated
public class RetentionPeriod {
    @JsonProperty
    private ChronoUnit units;
    @JsonProperty
    private int number;

    @JsonCreator
    private RetentionPeriod(@JsonProperty(value="number") int number, @JsonProperty(value="units") ChronoUnit units) {
        this.units = units;
        this.number = number;
    }

    public static RetentionPeriod of(int number, ChronoUnit units) {
        return new RetentionPeriod(number, units);
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public ChronoUnit getUnits() {
        return this.units;
    }

    public void setUnits(ChronoUnit units) {
        this.units = units;
    }

    public boolean isLongerThan(RetentionPeriod other) {
        return this.getUnits().getDuration().toMillis() * (long)this.getNumber() > other.getUnits().getDuration().toMillis() * (long)other.getNumber();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RetentionPeriod period = (RetentionPeriod)o;
        return this.number == period.number && this.units == period.units;
    }

    public int hashCode() {
        return Objects.hash(this.units, this.number);
    }
}

