/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.diagnostics.ipd.db;

import java.time.Duration;
import java.util.Optional;

public interface DatabaseLatencyMeter {
    public Optional<Duration> measure();
}

