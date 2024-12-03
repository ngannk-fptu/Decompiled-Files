/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.codehaus.jackson.annotate.JsonAutoDetect$Visibility
 */
package com.atlassian.ratelimiting.rest.api;

import org.codehaus.jackson.annotate.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY)
public class RestExemptionsLimitInfo {
    private long maxAllowed;
    private long current;
    private boolean maxReached;

    public static RestExemptionsLimitInfoBuilder builder() {
        return new RestExemptionsLimitInfoBuilder();
    }

    public long getMaxAllowed() {
        return this.maxAllowed;
    }

    public long getCurrent() {
        return this.current;
    }

    public boolean isMaxReached() {
        return this.maxReached;
    }

    public void setMaxAllowed(long maxAllowed) {
        this.maxAllowed = maxAllowed;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public void setMaxReached(boolean maxReached) {
        this.maxReached = maxReached;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RestExemptionsLimitInfo)) {
            return false;
        }
        RestExemptionsLimitInfo other = (RestExemptionsLimitInfo)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getMaxAllowed() != other.getMaxAllowed()) {
            return false;
        }
        if (this.getCurrent() != other.getCurrent()) {
            return false;
        }
        return this.isMaxReached() == other.isMaxReached();
    }

    protected boolean canEqual(Object other) {
        return other instanceof RestExemptionsLimitInfo;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $maxAllowed = this.getMaxAllowed();
        result = result * 59 + (int)($maxAllowed >>> 32 ^ $maxAllowed);
        long $current = this.getCurrent();
        result = result * 59 + (int)($current >>> 32 ^ $current);
        result = result * 59 + (this.isMaxReached() ? 79 : 97);
        return result;
    }

    public String toString() {
        return "RestExemptionsLimitInfo(maxAllowed=" + this.getMaxAllowed() + ", current=" + this.getCurrent() + ", maxReached=" + this.isMaxReached() + ")";
    }

    public RestExemptionsLimitInfo(long maxAllowed, long current, boolean maxReached) {
        this.maxAllowed = maxAllowed;
        this.current = current;
        this.maxReached = maxReached;
    }

    public RestExemptionsLimitInfo() {
    }

    public static class RestExemptionsLimitInfoBuilder {
        private long maxAllowed;
        private long current;
        private boolean maxReached;

        RestExemptionsLimitInfoBuilder() {
        }

        public RestExemptionsLimitInfoBuilder maxAllowed(long maxAllowed) {
            this.maxAllowed = maxAllowed;
            return this;
        }

        public RestExemptionsLimitInfoBuilder current(long current) {
            this.current = current;
            return this;
        }

        public RestExemptionsLimitInfoBuilder maxReached(boolean maxReached) {
            this.maxReached = maxReached;
            return this;
        }

        public RestExemptionsLimitInfo build() {
            return new RestExemptionsLimitInfo(this.maxAllowed, this.current, this.maxReached);
        }

        public String toString() {
            return "RestExemptionsLimitInfo.RestExemptionsLimitInfoBuilder(maxAllowed=" + this.maxAllowed + ", current=" + this.current + ", maxReached=" + this.maxReached + ")";
        }
    }
}

