/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.migration.agent.entity.ExecutionStatus;
import java.util.Optional;

public enum MigrationStatus {
    READY,
    IN_PROGRESS,
    CANCELLING,
    SUCCESS,
    FAILED,
    INCOMPLETE,
    TIMED_OUT,
    CANCELLED;


    public static MigrationStatus convertStatusToMigrationStatus(ExecutionStatus execStatus, Optional<ExecutionStatus> parentStatus) {
        switch (execStatus) {
            case CREATED: {
                return READY;
            }
            case DONE: {
                return SUCCESS;
            }
            case FAILED: {
                return FAILED;
            }
            case STOPPED: {
                return CANCELLED;
            }
            case VALIDATING: 
            case RUNNING: {
                return parentStatus.orElse(null) == ExecutionStatus.STOPPING ? CANCELLING : IN_PROGRESS;
            }
            case STOPPING: {
                return CANCELLING;
            }
            case INCOMPLETE: {
                return INCOMPLETE;
            }
        }
        throw new IllegalArgumentException("Unknown execution status " + execStatus.name());
    }
}

