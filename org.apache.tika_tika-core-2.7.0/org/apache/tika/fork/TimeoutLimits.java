/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.fork;

class TimeoutLimits {
    private final long pulseMS;
    private final long parseTimeoutMS;
    private final long waitTimeoutMS;

    TimeoutLimits(long pulseMS, long parseTimeoutMS, long waitTimeoutMS) {
        this.pulseMS = pulseMS;
        this.parseTimeoutMS = parseTimeoutMS;
        this.waitTimeoutMS = waitTimeoutMS;
    }

    public long getPulseMS() {
        return this.pulseMS;
    }

    public long getParseTimeoutMS() {
        return this.parseTimeoutMS;
    }

    public long getWaitTimeoutMS() {
        return this.waitTimeoutMS;
    }
}

