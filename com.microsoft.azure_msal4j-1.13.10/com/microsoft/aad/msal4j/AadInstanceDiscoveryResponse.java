/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.microsoft.aad.msal4j;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.aad.msal4j.InstanceDiscoveryMetadataEntry;

class AadInstanceDiscoveryResponse {
    @JsonProperty(value="tenant_discovery_endpoint")
    private String tenantDiscoveryEndpoint;
    @JsonProperty(value="metadata")
    private InstanceDiscoveryMetadataEntry[] metadata;
    @JsonProperty(value="error_description")
    private String errorDescription;
    @JsonProperty(value="error_codes")
    private long[] errorCodes;
    @JsonProperty(value="error")
    private String error;
    @JsonProperty(value="correlation_id")
    private String correlationId;

    AadInstanceDiscoveryResponse() {
    }

    String tenantDiscoveryEndpoint() {
        return this.tenantDiscoveryEndpoint;
    }

    InstanceDiscoveryMetadataEntry[] metadata() {
        return this.metadata;
    }

    String errorDescription() {
        return this.errorDescription;
    }

    long[] errorCodes() {
        return this.errorCodes;
    }

    String error() {
        return this.error;
    }

    String correlationId() {
        return this.correlationId;
    }
}

