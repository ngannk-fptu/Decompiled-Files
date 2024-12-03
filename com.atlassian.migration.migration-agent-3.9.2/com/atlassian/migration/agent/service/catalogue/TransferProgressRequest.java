/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonIgnoreProperties
 *  com.fasterxml.jackson.annotation.JsonInclude
 *  com.fasterxml.jackson.annotation.JsonInclude$Include
 *  com.fasterxml.jackson.annotation.JsonProperty
 *  lombok.Generated
 */
package com.atlassian.migration.agent.service.catalogue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.Generated;

@JsonInclude(value=JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class TransferProgressRequest {
    @JsonProperty(value="progressPercentage")
    private final Integer progressPercentage;
    @JsonProperty(value="progressMessage")
    private final String progressMessage;
    @JsonProperty(value="progressProperties")
    private final Map<String, Object> progressProperties;

    @Generated
    public TransferProgressRequest(Integer progressPercentage, String progressMessage, Map<String, Object> progressProperties) {
        this.progressPercentage = progressPercentage;
        this.progressMessage = progressMessage;
        this.progressProperties = progressProperties;
    }

    @Generated
    public Integer getProgressPercentage() {
        return this.progressPercentage;
    }

    @Generated
    public String getProgressMessage() {
        return this.progressMessage;
    }

    @Generated
    public Map<String, Object> getProgressProperties() {
        return this.progressProperties;
    }
}

