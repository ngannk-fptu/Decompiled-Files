/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.microsoft.aad.msal4j;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.aad.msal4j.StringHelper;

class UserDiscoveryResponse {
    @JsonProperty(value="ver")
    private float version;
    @JsonProperty(value="account_type")
    private String accountType;
    @JsonProperty(value="federation_metadata_url")
    private String federationMetadataUrl;
    @JsonProperty(value="federation_protocol")
    private String federationProtocol;
    @JsonProperty(value="federation_active_auth_url")
    private String federationActiveAuthUrl;
    @JsonProperty(value="cloud_audience_urn")
    private String cloudAudienceUrn;

    UserDiscoveryResponse() {
    }

    boolean isAccountFederated() {
        return !StringHelper.isBlank(this.accountType) && this.accountType.equalsIgnoreCase("Federated");
    }

    boolean isAccountManaged() {
        return !StringHelper.isBlank(this.accountType) && this.accountType.equalsIgnoreCase("Managed");
    }

    float version() {
        return this.version;
    }

    String accountType() {
        return this.accountType;
    }

    String federationMetadataUrl() {
        return this.federationMetadataUrl;
    }

    String federationProtocol() {
        return this.federationProtocol;
    }

    String federationActiveAuthUrl() {
        return this.federationActiveAuthUrl;
    }

    String cloudAudienceUrn() {
        return this.cloudAudienceUrn;
    }
}

