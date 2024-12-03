/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.email;

import lombok.Generated;

public class CsvDomainUploadStatus {
    private final int records;
    private final String errorMessage;
    private final Result result;

    @Generated
    CsvDomainUploadStatus(int records, String errorMessage, Result result) {
        this.records = records;
        this.errorMessage = errorMessage;
        this.result = result;
    }

    @Generated
    public static CsvDomainUploadStatusBuilder builder() {
        return new CsvDomainUploadStatusBuilder();
    }

    @Generated
    public int getRecords() {
        return this.records;
    }

    @Generated
    public String getErrorMessage() {
        return this.errorMessage;
    }

    @Generated
    public Result getResult() {
        return this.result;
    }

    @Generated
    public static class CsvDomainUploadStatusBuilder {
        @Generated
        private int records;
        @Generated
        private String errorMessage;
        @Generated
        private Result result;

        @Generated
        CsvDomainUploadStatusBuilder() {
        }

        @Generated
        public CsvDomainUploadStatusBuilder records(int records) {
            this.records = records;
            return this;
        }

        @Generated
        public CsvDomainUploadStatusBuilder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        @Generated
        public CsvDomainUploadStatusBuilder result(Result result) {
            this.result = result;
            return this;
        }

        @Generated
        public CsvDomainUploadStatus build() {
            return new CsvDomainUploadStatus(this.records, this.errorMessage, this.result);
        }

        @Generated
        public String toString() {
            return "CsvDomainUploadStatus.CsvDomainUploadStatusBuilder(records=" + this.records + ", errorMessage=" + this.errorMessage + ", result=" + (Object)((Object)this.result) + ")";
        }
    }

    public static enum Result {
        SUCCESS,
        RECORD_INSERT_FAILED,
        RECORD_VALIDATION_FAILED,
        RECORD_FAILED;

    }
}

