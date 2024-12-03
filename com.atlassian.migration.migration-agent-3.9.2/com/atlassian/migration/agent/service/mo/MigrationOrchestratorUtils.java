/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.mo;

import com.atlassian.migration.agent.okhttp.HttpServiceException;

public class MigrationOrchestratorUtils {
    private MigrationOrchestratorUtils() {
    }

    public static boolean isAuthorizationCause(Exception e) {
        return e instanceof HttpServiceException && 401 == ((HttpServiceException)e).getStatusCode();
    }
}

