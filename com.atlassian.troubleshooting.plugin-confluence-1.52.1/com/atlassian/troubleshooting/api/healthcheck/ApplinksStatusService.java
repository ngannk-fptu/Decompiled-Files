/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.api.healthcheck;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public interface ApplinksStatusService {
    public Optional<AppLinksStatus> getStatus();

    public boolean canGetStatus();

    public int getApplinksCount();

    public static class AppLinksStatus {
        private final Map<String, String> failures = new LinkedHashMap<String, String>();

        public Map<String, String> getFailures() {
            return this.failures;
        }

        public void addFailure(String applinkId, String message) {
            this.failures.put(applinkId, message);
        }
    }
}

