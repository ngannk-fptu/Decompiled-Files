/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.aad.msal4j;

import com.microsoft.aad.msal4j.Event;
import com.microsoft.aad.msal4j.ITelemetry;

class TelemetryHelper
implements AutoCloseable {
    private Event eventToEnd;
    private String requestId;
    private String clientId;
    private ITelemetry telemetry;
    private Boolean shouldFlush;

    TelemetryHelper(ITelemetry telemetry, String requestId, String clientId, Event event, Boolean shouldFlush) {
        this.telemetry = telemetry;
        this.requestId = requestId;
        this.clientId = clientId;
        this.eventToEnd = event;
        this.shouldFlush = shouldFlush;
        if (telemetry != null) {
            telemetry.startEvent(requestId, event);
        }
    }

    @Override
    public void close() {
        if (this.telemetry != null) {
            this.telemetry.stopEvent(this.requestId, this.eventToEnd);
            if (this.shouldFlush.booleanValue()) {
                this.telemetry.flush(this.requestId, this.clientId);
            }
        }
    }
}

