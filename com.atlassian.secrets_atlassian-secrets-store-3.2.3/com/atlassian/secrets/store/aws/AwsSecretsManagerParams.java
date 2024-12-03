/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.atlassian.secrets.store.aws;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AwsSecretsManagerParams {
    private String region;
    private String secretId;
    private String secretPointer;
    private String endpointOverride;

    public AwsSecretsManagerParams(@JsonProperty(value="region", required=true) String region, @JsonProperty(value="secretId", required=true) String secretId, @JsonProperty(value="secretPointer") String secretPointer, @JsonProperty(value="endpointOverride") String endpointOverride) {
        this.region = region;
        this.secretId = secretId;
        this.secretPointer = secretPointer;
        this.endpointOverride = endpointOverride;
    }

    public String getRegion() {
        return this.region;
    }

    public String getSecretId() {
        return this.secretId;
    }

    public String getSecretPointer() {
        return this.secretPointer;
    }

    public String getEndpointOverride() {
        return this.endpointOverride;
    }

    public String toString() {
        return "SecretsManagerParams{region='" + this.region + '\'' + ", secretId='" + this.secretId + '\'' + ", secretPointer='" + this.secretPointer + '\'' + ", endpointOverride='" + this.endpointOverride + '\'' + '}';
    }
}

