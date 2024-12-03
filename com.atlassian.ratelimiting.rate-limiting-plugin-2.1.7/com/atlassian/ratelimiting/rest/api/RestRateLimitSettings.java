/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.codehaus.jackson.annotate.JsonAutoDetect$Visibility
 */
package com.atlassian.ratelimiting.rest.api;

import com.atlassian.ratelimiting.dmz.RateLimitingMode;
import com.atlassian.ratelimiting.dmz.TokenBucketSettings;
import java.time.temporal.ChronoUnit;
import org.codehaus.jackson.annotate.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY)
public class RestRateLimitSettings {
    private RestRateLimitMode mode;
    private int defaultCapacity = -1;
    private int defaultFillRate = -1;
    private int defaultIntervalFrequency = 1;
    private String defaultIntervalTimeUnit = ChronoUnit.SECONDS.name();

    public static RestRateLimitSettings valueOf(RateLimitingMode status, TokenBucketSettings bucketSettings) {
        return new RestRateLimitSettings(RestRateLimitMode.valueOf(status.name()), bucketSettings.getCapacity(), bucketSettings.getFillRate(), bucketSettings.getIntervalFrequency(), bucketSettings.getIntervalTimeUnit().name());
    }

    public RestRateLimitMode getMode() {
        return this.mode;
    }

    public int getDefaultCapacity() {
        return this.defaultCapacity;
    }

    public int getDefaultFillRate() {
        return this.defaultFillRate;
    }

    public int getDefaultIntervalFrequency() {
        return this.defaultIntervalFrequency;
    }

    public String getDefaultIntervalTimeUnit() {
        return this.defaultIntervalTimeUnit;
    }

    public void setMode(RestRateLimitMode mode) {
        this.mode = mode;
    }

    public void setDefaultCapacity(int defaultCapacity) {
        this.defaultCapacity = defaultCapacity;
    }

    public void setDefaultFillRate(int defaultFillRate) {
        this.defaultFillRate = defaultFillRate;
    }

    public void setDefaultIntervalFrequency(int defaultIntervalFrequency) {
        this.defaultIntervalFrequency = defaultIntervalFrequency;
    }

    public void setDefaultIntervalTimeUnit(String defaultIntervalTimeUnit) {
        this.defaultIntervalTimeUnit = defaultIntervalTimeUnit;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RestRateLimitSettings)) {
            return false;
        }
        RestRateLimitSettings other = (RestRateLimitSettings)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getDefaultCapacity() != other.getDefaultCapacity()) {
            return false;
        }
        if (this.getDefaultFillRate() != other.getDefaultFillRate()) {
            return false;
        }
        if (this.getDefaultIntervalFrequency() != other.getDefaultIntervalFrequency()) {
            return false;
        }
        RestRateLimitMode this$mode = this.getMode();
        RestRateLimitMode other$mode = other.getMode();
        if (this$mode == null ? other$mode != null : !((Object)((Object)this$mode)).equals((Object)other$mode)) {
            return false;
        }
        String this$defaultIntervalTimeUnit = this.getDefaultIntervalTimeUnit();
        String other$defaultIntervalTimeUnit = other.getDefaultIntervalTimeUnit();
        return !(this$defaultIntervalTimeUnit == null ? other$defaultIntervalTimeUnit != null : !this$defaultIntervalTimeUnit.equals(other$defaultIntervalTimeUnit));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RestRateLimitSettings;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getDefaultCapacity();
        result = result * 59 + this.getDefaultFillRate();
        result = result * 59 + this.getDefaultIntervalFrequency();
        RestRateLimitMode $mode = this.getMode();
        result = result * 59 + ($mode == null ? 43 : ((Object)((Object)$mode)).hashCode());
        String $defaultIntervalTimeUnit = this.getDefaultIntervalTimeUnit();
        result = result * 59 + ($defaultIntervalTimeUnit == null ? 43 : $defaultIntervalTimeUnit.hashCode());
        return result;
    }

    public String toString() {
        return "RestRateLimitSettings(mode=" + (Object)((Object)this.getMode()) + ", defaultCapacity=" + this.getDefaultCapacity() + ", defaultFillRate=" + this.getDefaultFillRate() + ", defaultIntervalFrequency=" + this.getDefaultIntervalFrequency() + ", defaultIntervalTimeUnit=" + this.getDefaultIntervalTimeUnit() + ")";
    }

    public RestRateLimitSettings() {
    }

    public RestRateLimitSettings(RestRateLimitMode mode, int defaultCapacity, int defaultFillRate, int defaultIntervalFrequency, String defaultIntervalTimeUnit) {
        this.mode = mode;
        this.defaultCapacity = defaultCapacity;
        this.defaultFillRate = defaultFillRate;
        this.defaultIntervalFrequency = defaultIntervalFrequency;
        this.defaultIntervalTimeUnit = defaultIntervalTimeUnit;
    }

    public static enum RestRateLimitMode {
        ON,
        OFF,
        DRY_RUN;

    }
}

