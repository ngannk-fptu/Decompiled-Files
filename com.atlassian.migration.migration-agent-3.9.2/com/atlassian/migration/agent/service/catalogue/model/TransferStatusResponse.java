/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.catalogue.model;

import java.io.Serializable;
import java.util.Date;
import lombok.Generated;

public class TransferStatusResponse
implements Serializable {
    private static final long serialVersionUID = -7283729237641710030L;
    private String status;
    private int progressPercentage;
    private String progressMessage;
    private Date lastUpdatedAt;

    @Generated
    TransferStatusResponse(String status, int progressPercentage, String progressMessage, Date lastUpdatedAt) {
        this.status = status;
        this.progressPercentage = progressPercentage;
        this.progressMessage = progressMessage;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    @Generated
    public static TransferStatusResponseBuilder builder() {
        return new TransferStatusResponseBuilder();
    }

    @Generated
    public String getStatus() {
        return this.status;
    }

    @Generated
    public int getProgressPercentage() {
        return this.progressPercentage;
    }

    @Generated
    public String getProgressMessage() {
        return this.progressMessage;
    }

    @Generated
    public Date getLastUpdatedAt() {
        return this.lastUpdatedAt;
    }

    @Generated
    public static class TransferStatusResponseBuilder {
        @Generated
        private String status;
        @Generated
        private int progressPercentage;
        @Generated
        private String progressMessage;
        @Generated
        private Date lastUpdatedAt;

        @Generated
        TransferStatusResponseBuilder() {
        }

        @Generated
        public TransferStatusResponseBuilder status(String status) {
            this.status = status;
            return this;
        }

        @Generated
        public TransferStatusResponseBuilder progressPercentage(int progressPercentage) {
            this.progressPercentage = progressPercentage;
            return this;
        }

        @Generated
        public TransferStatusResponseBuilder progressMessage(String progressMessage) {
            this.progressMessage = progressMessage;
            return this;
        }

        @Generated
        public TransferStatusResponseBuilder lastUpdatedAt(Date lastUpdatedAt) {
            this.lastUpdatedAt = lastUpdatedAt;
            return this;
        }

        @Generated
        public TransferStatusResponse build() {
            return new TransferStatusResponse(this.status, this.progressPercentage, this.progressMessage, this.lastUpdatedAt);
        }

        @Generated
        public String toString() {
            return "TransferStatusResponse.TransferStatusResponseBuilder(status=" + this.status + ", progressPercentage=" + this.progressPercentage + ", progressMessage=" + this.progressMessage + ", lastUpdatedAt=" + this.lastUpdatedAt + ")";
        }
    }
}

