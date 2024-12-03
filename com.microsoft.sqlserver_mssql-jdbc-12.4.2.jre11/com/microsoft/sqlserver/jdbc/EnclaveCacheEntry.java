/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.BaseAttestationRequest;
import com.microsoft.sqlserver.jdbc.EnclaveSession;
import java.time.Instant;

class EnclaveCacheEntry {
    private static final long EIGHT_HOURS_IN_SECONDS = 28800L;
    private BaseAttestationRequest bar;
    private EnclaveSession es;
    private long timeCreatedInSeconds;

    EnclaveCacheEntry(BaseAttestationRequest b, EnclaveSession e) {
        this.bar = b;
        this.es = e;
        this.timeCreatedInSeconds = Instant.now().getEpochSecond();
    }

    boolean expired() {
        return Instant.now().getEpochSecond() - this.timeCreatedInSeconds > 28800L;
    }

    BaseAttestationRequest getBaseAttestationRequest() {
        return this.bar;
    }

    EnclaveSession getEnclaveSession() {
        return this.es;
    }
}

