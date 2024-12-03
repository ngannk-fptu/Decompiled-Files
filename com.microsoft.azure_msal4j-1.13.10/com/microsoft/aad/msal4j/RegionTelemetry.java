/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.aad.msal4j;

enum RegionTelemetry {
    REGION_SOURCE_FAILED_AUTODETECT(1),
    REGION_SOURCE_CACHE(2),
    REGION_SOURCE_ENV_VARIABLE(3),
    REGION_SOURCE_IMDS(4),
    REGION_OUTCOME_DEVELOPER_AUTODETECT_MATCH(1),
    REGION_OUTCOME_DEVELOPER_AUTODETECT_FAILED(2),
    REGION_OUTCOME_DEVELOPER_AUTODETECT_MISMATCH(3),
    REGION_OUTCOME_AUTODETECT_SUCCESS(4),
    REGION_OUTCOME_AUTODETECT_FAILED(5);

    final int telemetryValue;

    private RegionTelemetry(int telemetryValue) {
        this.telemetryValue = telemetryValue;
    }
}

