/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.zdu.rest.dto;

public enum ClusterState {
    STABLE,
    READY_TO_UPGRADE,
    MIXED,
    READY_TO_RUN_UPGRADE_TASKS,
    RUNNING_UPGRADE_TASKS,
    UPGRADE_TASKS_FAILED;


    public boolean canStart() {
        return this == STABLE;
    }

    public boolean canCancel() {
        return this == READY_TO_UPGRADE;
    }

    public boolean canFinalize() {
        return this == READY_TO_RUN_UPGRADE_TASKS;
    }

    public boolean canRetry() {
        return this == UPGRADE_TASKS_FAILED;
    }
}

