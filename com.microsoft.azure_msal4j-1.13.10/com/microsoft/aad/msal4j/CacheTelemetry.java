/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.aad.msal4j;

enum CacheTelemetry {
    REFRESH_CACHE_NOT_USED(0),
    REFRESH_FORCE_REFRESH(1),
    REFRESH_NO_ACCESS_TOKEN(2),
    REFRESH_ACCESS_TOKEN_EXPIRED(3),
    REFRESH_REFRESH_IN(4);

    final int telemetryValue;

    private CacheTelemetry(int telemetryValue) {
        this.telemetryValue = telemetryValue;
    }
}

