/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.IHttpClient;
import com.microsoft.aad.msal4j.ServerSideTelemetry;
import com.microsoft.aad.msal4j.TelemetryManager;
import java.util.concurrent.ExecutorService;

class ServiceBundle {
    private ExecutorService executorService;
    private TelemetryManager telemetryManager;
    private IHttpClient httpClient;
    private ServerSideTelemetry serverSideTelemetry;

    ServiceBundle(ExecutorService executorService, IHttpClient httpClient, TelemetryManager telemetryManager) {
        this.executorService = executorService;
        this.telemetryManager = telemetryManager;
        this.httpClient = httpClient;
        this.serverSideTelemetry = new ServerSideTelemetry();
    }

    ExecutorService getExecutorService() {
        return this.executorService;
    }

    TelemetryManager getTelemetryManager() {
        return this.telemetryManager;
    }

    IHttpClient getHttpClient() {
        return this.httpClient;
    }

    ServerSideTelemetry getServerSideTelemetry() {
        return this.serverSideTelemetry;
    }
}

