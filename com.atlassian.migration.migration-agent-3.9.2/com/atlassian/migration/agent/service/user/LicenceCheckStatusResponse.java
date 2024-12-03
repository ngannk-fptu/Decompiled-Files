/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.user;

import com.atlassian.migration.agent.okhttp.ErrorResponse;
import com.atlassian.migration.agent.service.user.LicenceCheckResult;
import java.util.List;
import lombok.Generated;

public class LicenceCheckStatusResponse {
    private final String taskId;
    private final int percentageComplete;
    private final boolean complete;
    private final List<LicenceCheckResult> licencesCheckResult;
    private final List<ErrorResponse> errors;

    @Generated
    public LicenceCheckStatusResponse(String taskId, int percentageComplete, boolean complete, List<LicenceCheckResult> licencesCheckResult, List<ErrorResponse> errors) {
        this.taskId = taskId;
        this.percentageComplete = percentageComplete;
        this.complete = complete;
        this.licencesCheckResult = licencesCheckResult;
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
    public List<LicenceCheckResult> getLicencesCheckResult() {
        return this.licencesCheckResult;
    }

    @Generated
    public List<ErrorResponse> getErrors() {
        return this.errors;
    }
}

