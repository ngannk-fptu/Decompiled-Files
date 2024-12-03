/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.user;

import com.atlassian.migration.agent.service.user.EmailError;
import com.atlassian.migration.agent.service.user.EmailValidationResult;
import java.util.List;
import lombok.Generated;

public class EmailCheckStatusResponse {
    private final String taskId;
    private final int percentageComplete;
    private final boolean complete;
    private final EmailValidationResult emailValidationResult;
    private final List<EmailError> errors;

    @Generated
    public EmailCheckStatusResponse(String taskId, int percentageComplete, boolean complete, EmailValidationResult emailValidationResult, List<EmailError> errors) {
        this.taskId = taskId;
        this.percentageComplete = percentageComplete;
        this.complete = complete;
        this.emailValidationResult = emailValidationResult;
        this.errors = errors;
    }

    @Generated
    public String getTaskId() {
        return this.taskId;
    }

    @Generated
    public int getPercentageComplete() {
        return this.percentageComplete;
    }

    @Generated
    public boolean isComplete() {
        return this.complete;
    }

    @Generated
    public EmailValidationResult getEmailValidationResult() {
        return this.emailValidationResult;
    }

    @Generated
    public List<EmailError> getErrors() {
        return this.errors;
    }
}

