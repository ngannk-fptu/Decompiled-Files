/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.service.catalogue.model;

import java.util.List;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class TransferResponseList {
    @JsonProperty
    private final List<TransferResponse> transfers;

    @JsonCreator
    public TransferResponseList(List<TransferResponse> transfers) {
        this.transfers = transfers;
    }

    @Generated
    public List<TransferResponse> getTransfers() {
        return this.transfers;
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    public static class TransferResponse {
        @JsonProperty(value="operationKey")
        private final String operationKey;
        @JsonProperty(value="transferId")
        private final String transferId;

        @JsonCreator
        public TransferResponse(@JsonProperty(value="operationKey") String operationKey, @JsonProperty(value="transferId") String transferId) {
            this.operationKey = operationKey;
            this.transferId = transferId;
        }

        @Generated
        public String getOperationKey() {
            return this.operationKey;
        }

        @Generated
        public String getTransferId() {
            return this.transferId;
        }
    }
}

