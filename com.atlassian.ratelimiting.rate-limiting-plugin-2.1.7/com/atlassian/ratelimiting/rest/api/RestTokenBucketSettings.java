/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.codehaus.jackson.annotate.JsonAutoDetect$Visibility
 */
package com.atlassian.ratelimiting.rest.api;

import com.atlassian.ratelimiting.dmz.TokenBucketSettings;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.codehaus.jackson.annotate.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY)
public class RestTokenBucketSettings {
    private int capacity = -1;
    private int fillRate = -1;
    private int intervalFrequency = 1;
    private String intervalTimeUnit = ChronoUnit.SECONDS.name();

    public RestTokenBucketSettings(@Nonnull TokenBucketSettings tokenBucketSettings) {
        Objects.requireNonNull(tokenBucketSettings, "tokenBucketSettings");
        this.capacity = tokenBucketSettings.getCapacity();
        this.fillRate = tokenBucketSettings.getFillRate();
        this.intervalFrequency = tokenBucketSettings.getIntervalFrequency();
        this.intervalTimeUnit = tokenBucketSettings.getIntervalTimeUnit().name();
    }

    public int getCapacity() {
        return this.capacity;
    }

    public int getFillRate() {
        return this.fillRate;
    }

    public int getIntervalFrequency() {
        return this.intervalFrequency;
    }

    public String getIntervalTimeUnit() {
        return this.intervalTimeUnit;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setFillRate(int fillRate) {
        this.fillRate = fillRate;
    }

    public void setIntervalFrequency(int intervalFrequency) {
        this.intervalFrequency = intervalFrequency;
    }

    public void setIntervalTimeUnit(String intervalTimeUnit) {
        this.intervalTimeUnit = intervalTimeUnit;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RestTokenBucketSettings)) {
            return false;
        }
        RestTokenBucketSettings other = (RestTokenBucketSettings)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getCapacity() != other.getCapacity()) {
            return false;
        }
        if (this.getFillRate() != other.getFillRate()) {
            return false;
        }
        if (this.getIntervalFrequency() != other.getIntervalFrequency()) {
            return false;
        }
        String this$intervalTimeUnit = this.getIntervalTimeUnit();
        String other$intervalTimeUnit = other.getIntervalTimeUnit();
        return !(this$intervalTimeUnit == null ? other$intervalTimeUnit != null : !this$intervalTimeUnit.equals(other$intervalTimeUnit));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RestTokenBucketSettings;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getCapacity();
        result = result * 59 + this.getFillRate();
        result = result * 59 + this.getIntervalFrequency();
        String $intervalTimeUnit = this.getIntervalTimeUnit();
        result = result * 59 + ($intervalTimeUnit == null ? 43 : $intervalTimeUnit.hashCode());
        return result;
    }

    public String toString() {
        return "RestTokenBucketSettings(capacity=" + this.getCapacity() + ", fillRate=" + this.getFillRate() + ", intervalFrequency=" + this.getIntervalFrequency() + ", intervalTimeUnit=" + this.getIntervalTimeUnit() + ")";
    }

    public RestTokenBucketSettings() {
    }

    public RestTokenBucketSettings(int capacity, int fillRate, int intervalFrequency, String intervalTimeUnit) {
        this.capacity = capacity;
        this.fillRate = fillRate;
        this.intervalFrequency = intervalFrequency;
        this.intervalTimeUnit = intervalTimeUnit;
    }
}

