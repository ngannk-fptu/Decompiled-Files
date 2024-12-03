/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.mapi.external.model;

import com.atlassian.migration.agent.dto.RequestValidationException;
import com.atlassian.migration.agent.mapi.external.model.JobValidationException;
import com.atlassian.migration.agent.service.impl.InvalidPlanException;

public class PublicApiException
extends RuntimeException {
    public static final String GENERIC_EXCEPTION = "Please check Confluence Logs to find the issue or contact support, if the issue persists.";
    private static final String RESPONSE_PARSING_ERROR_MESSAGE = "Json Parsing Failed for jobId = %s.\n(1) Make sure that you have the latest CCMA version installed.\n(2) Check Confluence Logs or contact support, if the issue persists.";
    private static final String DUPLICATE_JOB_MESSAGE = "Attach Request has already been processed for JobId = %s";
    private static final String CLOUD_URL_NOT_FOUND = "Destination cloud URL = %s is either incorrect or is not authorised with server. Please Authorise the CloudSite and retry.";
    private static final String CLOUD_ID_NOT_FOUND = "Destination cloud Id = %s is either incorrect or is not authorised with server. Please Authorise the CloudSite and retry.";

    public PublicApiException() {
        super(GENERIC_EXCEPTION);
    }

    public PublicApiException(String message) {
        super(message);
    }

    public PublicApiException(String message, Exception exception) {
        super(message, exception);
    }

    public static int getPublicApiErrorCode(Exception ex) {
        if (ex instanceof InvalidPlanException || ex instanceof IllegalArgumentException || ex instanceof JobValidationException || ex instanceof RequestValidationException) {
            return 400;
        }
        if (ex instanceof DuplicateRequestException) {
            return 409;
        }
        if (ex instanceof CloudUrlDoesNotExist || ex instanceof CloudIdDoesNotExist) {
            return 400;
        }
        return 500;
    }

    public static class ResourceNotFound
    extends PublicApiException {
        public ResourceNotFound(String message) {
            super(message);
        }
    }

    public static class CloudIdDoesNotExist
    extends PublicApiException {
        public CloudIdDoesNotExist(String cloudId) {
            super(String.format(PublicApiException.CLOUD_ID_NOT_FOUND, cloudId));
        }
    }

    public static class CloudUrlDoesNotExist
    extends PublicApiException {
        public CloudUrlDoesNotExist(String cloudUrl) {
            super(String.format(PublicApiException.CLOUD_URL_NOT_FOUND, cloudUrl));
        }
    }

    public static class DuplicateRequestException
    extends PublicApiException {
        public DuplicateRequestException(String jobId) {
            super(String.format(PublicApiException.DUPLICATE_JOB_MESSAGE, jobId));
        }

        public DuplicateRequestException(String message, String jobId) {
            super(String.format(message, jobId));
        }
    }

    public static class MigrationDefinitionParsingError
    extends PublicApiException {
        public MigrationDefinitionParsingError(String jobId, Exception exception) {
            super(String.format(PublicApiException.RESPONSE_PARSING_ERROR_MESSAGE, jobId), exception);
        }
    }
}

