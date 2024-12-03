/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto.assessment;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class CloudAppDto {
    @JsonProperty
    private final String key;
    @JsonProperty
    private final String name;
    @JsonProperty
    private final String logoUrl;
    @JsonProperty
    private final String cloudUrl;
    @JsonProperty
    private final String installUrl;
    @JsonProperty
    private final String privacyPolicyUrl;
    @JsonProperty
    private final String contactVendorUrl;
    @JsonProperty(value="isInstalled")
    private final boolean installed;

    @JsonCreator
    public CloudAppDto(String key, String name, String logoUrl, String cloudUrl, String installUrl, String privacyPolicyUrl, String contactVendorUrl, boolean installed) {
        this.key = key;
        this.name = name;
        this.logoUrl = logoUrl;
        this.cloudUrl = cloudUrl;
        this.installUrl = installUrl;
        this.privacyPolicyUrl = privacyPolicyUrl;
        this.contactVendorUrl = contactVendorUrl;
        this.installed = installed;
    }

    public String getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }

    public String getLogoUrl() {
        return this.logoUrl;
    }

    public String getCloudUrl() {
        return this.cloudUrl;
    }

    public String getPrivacyPolicyUrl() {
        return this.privacyPolicyUrl;
    }

    public String getContactVendorUrl() {
        return this.contactVendorUrl;
    }

    public String getInstallUrl() {
        return this.installUrl;
    }

    public boolean isInstalled() {
        return this.installed;
    }
}

