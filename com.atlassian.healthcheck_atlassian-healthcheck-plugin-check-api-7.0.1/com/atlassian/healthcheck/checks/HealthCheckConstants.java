/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.healthcheck.checks;

public final class HealthCheckConstants {
    public static final String HEALTH_CHECK_BASE_PATH = "rest/healthCheck/1.0";
    public static final String CHECK_DETAILS = "/checkDetails";
    public static final String HEALTH_CHECK_DETAILS_JSON = "rest/healthCheck/1.0/checkDetails.json";
    public static final String HEALTH_CHECK_STATUS_FIELD = "status";
    public static final String HEALTH_CHECK_NAME_FIELD = "name";
    public static final String HEALTH_CHECK_IS_HEALTHY_FIELD = "isHealthy";
    public static final String HEALTH_CHECK_FAILURE_REASON_FIELD = "failureReason";
    public static final String HOST_APPLICATION_STATUS_HEALTH_CHECK = "Host Application Status Check";

    private HealthCheckConstants() {
    }
}

