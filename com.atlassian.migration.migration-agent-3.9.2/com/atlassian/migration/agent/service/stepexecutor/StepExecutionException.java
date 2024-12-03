/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.stepexecutor;

import com.atlassian.migration.agent.service.MigrationErrorCode;
import com.atlassian.migration.agent.service.impl.StepType;
import lombok.Generated;

public class StepExecutionException
extends RuntimeException {
    private final MigrationErrorCode errorCode;
    private final StepType stepType;
    private final String migrationId;

    public StepExecutionException(MigrationErrorCode errorCode, StepType stepType, String migrationId, String message) {
        super(message);
        this.errorCode = errorCode;
        this.migrationId = migrationId;
        this.stepType = stepType;
    }

    public StepExecutionException(MigrationErrorCode errorCode, StepType stepType, String migrationId, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.migrationId = migrationId;
        this.stepType = stepType;
    }

    @Generated
    public MigrationErrorCode getErrorCode() {
        return this.errorCode;
    }

    @Generated
    public StepType getStepType() {
        return this.stepType;
    }

    @Generated
    public String getMigrationId() {
        return this.migrationId;
    }
}

