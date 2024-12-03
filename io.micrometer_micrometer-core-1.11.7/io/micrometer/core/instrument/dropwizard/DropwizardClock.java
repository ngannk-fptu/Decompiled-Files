/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.codahale.metrics.Clock
 */
package io.micrometer.core.instrument.dropwizard;

import io.micrometer.core.instrument.Clock;

public class DropwizardClock
extends com.codahale.metrics.Clock {
    private final Clock micrometerClock;

    public DropwizardClock(Clock micrometerClock) {
        this.micrometerClock = micrometerClock;
    }

    public long getTick() {
        return this.micrometerClock.monotonicTime();
    }

    public long getTime() {
        return this.micrometerClock.wallTime();
    }
}

