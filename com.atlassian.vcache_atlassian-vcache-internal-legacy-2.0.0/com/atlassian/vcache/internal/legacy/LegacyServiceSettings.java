/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.vcache.internal.legacy;

import java.time.Duration;
import java.util.Objects;

public class LegacyServiceSettings {
    private final boolean avoidCasOps;
    private final boolean serializationHack;
    private final Duration lockTimeout;

    LegacyServiceSettings(boolean avoidCasOps, boolean serializationHack, Duration lockTimeout) {
        this.avoidCasOps = avoidCasOps;
        this.serializationHack = serializationHack;
        this.lockTimeout = Objects.requireNonNull(lockTimeout);
    }

    public boolean isAvoidCasOps() {
        return this.avoidCasOps;
    }

    public boolean isSerializationHack() {
        return this.serializationHack;
    }

    public Duration getLockTimeout() {
        return this.lockTimeout;
    }
}

