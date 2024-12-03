/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 */
package com.microsoft.sqlserver.jdbc;

import com.google.gson.JsonArray;
import java.time.Instant;

class JWTCertificateEntry {
    private static final long TWENTY_FOUR_HOUR_IN_SECONDS = 86400L;
    private JsonArray certificates;
    private long timeCreatedInSeconds;

    JWTCertificateEntry(JsonArray j) {
        this.certificates = j;
        this.timeCreatedInSeconds = Instant.now().getEpochSecond();
    }

    boolean expired() {
        return Instant.now().getEpochSecond() - this.timeCreatedInSeconds > 86400L;
    }

    JsonArray getCertificates() {
        return this.certificates;
    }
}

