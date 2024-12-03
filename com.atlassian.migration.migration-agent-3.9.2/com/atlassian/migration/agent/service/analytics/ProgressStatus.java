/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.analytics;

public enum ProgressStatus {
    SUCCESS("SUCCESS"),
    FAILED("FAILED"),
    INCOMPLETE("INCOMPLETE"),
    TIMED_OUT("TIMED_OUT"),
    CANCELLED("CANCELLED");

    private final String statusName;

    private ProgressStatus(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusName() {
        return this.statusName;
    }
}

