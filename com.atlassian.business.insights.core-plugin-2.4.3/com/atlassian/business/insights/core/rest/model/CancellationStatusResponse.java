/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.business.insights.core.rest.model;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.sal.api.message.I18nResolver;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class CancellationStatusResponse {
    @VisibleForTesting
    public static final String CANCEL_REQUEST_WARNING_JOB_COMPLETED_MESSAGE_KEY = "data-pipeline.full.export.cancellation.job.already.completed.status.message";
    @VisibleForTesting
    public static final String CANCEL_REQUEST_WARNING_JOB_DOESNT_EXIST_MESSAGE_KEY = "data-pipeline.full.export.cancellation.job.does.not.exist.status.message";
    @VisibleForTesting
    public static final String CANCEL_REQUEST_OK_MESSAGE_KEY = "data-pipeline.full.export.cancellation.ok.status.message";
    @VisibleForTesting
    public static final String CANCEL_REQUEST_ERROR_MESSAGE_KEY = "data-pipeline.full.export.cancellation.error.status.message";
    private Integer jobId;
    private Status status;
    private String messageKey;
    private String message;

    public CancellationStatusResponse() {
    }

    @JsonCreator
    CancellationStatusResponse(@JsonProperty(value="jobId") Integer jobId, @JsonProperty(value="status") Status status, @JsonProperty(value="messageKey") String messageKey, @JsonProperty(value="message") String message) {
        this.jobId = jobId;
        this.status = status;
        this.messageKey = messageKey;
        this.message = message;
    }

    public static CancellationStatusResponse getCancellationSuccessfulStatusResponse(Integer jobId, I18nResolver i18nResolver) {
        return new CancellationStatusResponse(jobId, Status.OK, CANCEL_REQUEST_OK_MESSAGE_KEY, i18nResolver.getText(CANCEL_REQUEST_OK_MESSAGE_KEY));
    }

    public static CancellationStatusResponse getCancellationJobAlreadyCompletedStatusResponse(Integer jobId, I18nResolver i18nResolver) {
        return new CancellationStatusResponse(jobId, Status.WARNING, CANCEL_REQUEST_WARNING_JOB_COMPLETED_MESSAGE_KEY, i18nResolver.getText(CANCEL_REQUEST_WARNING_JOB_COMPLETED_MESSAGE_KEY));
    }

    public static CancellationStatusResponse getCancellationJobDoesntExistStatusResponse(Integer jobId, I18nResolver i18nResolver) {
        return new CancellationStatusResponse(jobId, Status.WARNING, CANCEL_REQUEST_WARNING_JOB_DOESNT_EXIST_MESSAGE_KEY, i18nResolver.getText(CANCEL_REQUEST_WARNING_JOB_DOESNT_EXIST_MESSAGE_KEY));
    }

    public static CancellationStatusResponse getCancellationErrorStatusResponse(Integer jobId, I18nResolver i18nResolver) {
        return new CancellationStatusResponse(jobId, Status.ERROR, CANCEL_REQUEST_ERROR_MESSAGE_KEY, i18nResolver.getText(CANCEL_REQUEST_ERROR_MESSAGE_KEY));
    }

    @JsonProperty
    public Integer getJobId() {
        return this.jobId;
    }

    @JsonProperty
    public Status getStatus() {
        return this.status;
    }

    @JsonProperty
    public String getMessageKey() {
        return this.messageKey;
    }

    @JsonProperty
    public String getMessage() {
        return this.message;
    }

    @VisibleForTesting
    public static enum Status {
        OK,
        WARNING,
        ERROR;

    }
}

