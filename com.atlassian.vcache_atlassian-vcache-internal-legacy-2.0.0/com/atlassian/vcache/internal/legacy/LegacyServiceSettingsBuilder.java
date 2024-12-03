/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.vcache.internal.legacy;

import com.atlassian.vcache.internal.legacy.LegacyServiceSettings;
import java.time.Duration;
import java.util.Objects;

public class LegacyServiceSettingsBuilder {
    private boolean avoidCasOps;
    private boolean serializationHack;
    private Duration lockTimeout = Duration.ofSeconds(30L);

    public LegacyServiceSettings build() {
        return new LegacyServiceSettings(this.avoidCasOps, this.serializationHack, this.lockTimeout);
    }

    public LegacyServiceSettingsBuilder enableAvoidCasOperations() {
        this.avoidCasOps = true;
        return this;
    }

    public LegacyServiceSettingsBuilder enableSerializationHack() {
        this.serializationHack = true;
        return this;
    }

    public LegacyServiceSettingsBuilder lockTimeout(Duration lockTimeout) {
        this.lockTimeout = Objects.requireNonNull(lockTimeout);
        return this;
    }
}

