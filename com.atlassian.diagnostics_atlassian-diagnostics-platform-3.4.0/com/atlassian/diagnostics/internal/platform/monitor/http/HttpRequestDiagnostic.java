/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal.platform.monitor.http;

import java.time.Duration;
import javax.annotation.Nonnull;

class HttpRequestDiagnostic {
    private final String requestPath;
    private final Duration requestDuration;
    private final String username;

    HttpRequestDiagnostic(@Nonnull String requestPath, @Nonnull String username, @Nonnull Duration requestDuration) {
        this.requestPath = requestPath;
        this.username = username;
        this.requestDuration = requestDuration;
    }

    String getRequestPath() {
        return this.requestPath;
    }

    String getUsername() {
        return this.username;
    }

    Duration getRequestDuration() {
        return this.requestDuration;
    }
}

