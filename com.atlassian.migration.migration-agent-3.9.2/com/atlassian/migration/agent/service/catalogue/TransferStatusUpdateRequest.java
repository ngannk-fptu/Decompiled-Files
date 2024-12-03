/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonIgnoreProperties
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.catalogue;

import com.atlassian.migration.agent.entity.TransferStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Generated;

@JsonIgnoreProperties(ignoreUnknown=true)
public class TransferStatusUpdateRequest {
    private final TransferStatus status;
    private final String statusMessage;

    @Generated
    public TransferStatusUpdateRequest(TransferStatus status, String statusMessage) {
        this.status = status;
        this.statusMessage = statusMessage;
    }

    @Generated
    public TransferStatus getStatus() {
        return this.status;
    }

    @Generated
    public String getStatusMessage() {
        return this.statusMessage;
    }
}

