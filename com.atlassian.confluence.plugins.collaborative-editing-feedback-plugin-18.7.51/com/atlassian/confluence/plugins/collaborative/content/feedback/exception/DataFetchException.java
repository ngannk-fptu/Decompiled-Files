/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.collaborative.content.feedback.exception;

public class DataFetchException
extends RuntimeException {
    private static final String QUERY_ERROR_MESSAGE = "Error fetching data from the database";
    private static final String TOO_MANY_REQUESTS_ERROR_MESSAGE = "Too many concurrent requests. Fetching of content data is rejected";
    private static final String TIMEOUT_ERROR_MESSAGE = "Timeout on data fetching operation for content";
    private static final String EXECUTION_ERROR_MESSAGE = "Execution of the data fetching operation failed";
    private static final String FILE_ERROR_MESSAGE = "File operation failed for content";
    private static final String TOO_MANY_FILES_MESSAGE = "Limit is reached for the total number of report files. Report is not created";
    private static final String INVALID_CONTENT_ID_MESSAGE = "Invalid content id. Valid content id is a long value, greater than 0";
    private static final String CONTENT_DOES_NOT_EXIST_MESSAGE = "Content does not exist";
    private static final int QUERY_ERROR = 0;
    private static final int TOO_MANY_REQUESTS_ERROR = 1;
    private static final int TIMEOUT_ERROR = 2;
    private static final int EXECUTION_ERROR = 3;
    private static final int FILE_ERROR = 4;
    private static final int TOO_MANY_FILES_ERROR = 5;
    private static final int INVALID_CONTENT_ID_ERROR = 6;
    private static final int CONTENT_DOES_NOT_EXIST_ERROR = 7;
    private final int error;
    private final long contentId;

    protected DataFetchException(int error, long contentId, String message) {
        super(message);
        this.error = error;
        this.contentId = contentId;
    }

    protected DataFetchException(int error, long contentId, String message, Throwable cause) {
        super(message, cause);
        this.error = error;
        this.contentId = contentId;
    }

    public int getError() {
        return this.error;
    }

    public long getContentId() {
        return this.contentId;
    }

    public static DataFetchException queryError(long contentId, Exception e) {
        return new DataFetchException(0, contentId, QUERY_ERROR_MESSAGE, e);
    }

    public static DataFetchException queryError(String explanation, long contentId, Exception e) {
        return new DataFetchException(0, contentId, "Error fetching data from the database: " + explanation, e);
    }

    public static DataFetchException tooManyRequests(long contentId, Exception e) {
        return new DataFetchException(1, contentId, TOO_MANY_REQUESTS_ERROR_MESSAGE, e);
    }

    public static DataFetchException timeout(long contentId, Exception e) {
        return new DataFetchException(2, contentId, TIMEOUT_ERROR_MESSAGE, e);
    }

    public static DataFetchException executionError(long contentId, Exception e) {
        return new DataFetchException(3, contentId, EXECUTION_ERROR_MESSAGE, e);
    }

    public static DataFetchException fileError(String explanation, long contentId, Exception e) {
        return new DataFetchException(4, contentId, "File operation failed for content. " + explanation, e);
    }

    public static DataFetchException tooManyFiles(long contentId) {
        return new DataFetchException(5, contentId, TOO_MANY_FILES_MESSAGE);
    }

    public static DataFetchException invalidContentId(long contentId) {
        return new DataFetchException(6, contentId, INVALID_CONTENT_ID_MESSAGE);
    }

    public static DataFetchException contentNotFound(long contentId) {
        return new DataFetchException(7, contentId, CONTENT_DOES_NOT_EXIST_MESSAGE);
    }
}

