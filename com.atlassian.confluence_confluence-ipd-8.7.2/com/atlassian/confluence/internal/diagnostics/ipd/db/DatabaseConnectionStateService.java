/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.diagnostics.ipd.db;

import java.time.Duration;
import java.util.Optional;

public interface DatabaseConnectionStateService {
    public Optional<Duration> getLatency();

    public DatabaseConnectionState getState();

    public static enum DatabaseConnectionState {
        CONNECTED(1L),
        DISCONNECTED(0L);

        private final long value;

        private DatabaseConnectionState(long value) {
            this.value = value;
        }

        public long getValue() {
            return this.value;
        }
    }
}

