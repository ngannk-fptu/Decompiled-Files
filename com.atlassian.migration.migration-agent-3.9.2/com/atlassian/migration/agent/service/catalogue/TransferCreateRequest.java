/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonIgnoreProperties
 *  com.fasterxml.jackson.annotation.JsonInclude
 *  com.fasterxml.jackson.annotation.JsonInclude$Include
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.service.catalogue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonInclude(value=JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class TransferCreateRequest {
    @JsonProperty(value="operationKey")
    private final List<String> operationKey;

    @Generated
    public List<String> getOperationKey() {
        return this.operationKey;
    }

    @Generated
    public TransferCreateRequest(List<String> operationKey) {
        this.operationKey = operationKey;
    }
}

