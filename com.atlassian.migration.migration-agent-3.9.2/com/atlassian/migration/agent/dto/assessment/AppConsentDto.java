/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 */
package com.atlassian.migration.agent.dto.assessment;

import com.atlassian.migration.agent.dto.assessment.ConsentStatus;
import com.atlassian.migration.agent.dto.assessment.DataScopesSerializer;
import com.atlassian.migration.app.AccessScope;
import java.util.List;
import lombok.Generated;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

public class AppConsentDto {
    @JsonProperty
    private final String key;
    @JsonProperty
    private final String cloudKey;
    @JsonProperty
    private final String name;
    @JsonProperty
    private final String vendorName;
    @JsonProperty
    private final ConsentStatus status;
    @JsonProperty
    private final String contactVendorUrl;
    @JsonProperty
    private final String privacyPolicyUrl;
    @JsonProperty
    private final String logoUrl;
    @JsonProperty
    private final boolean isVendorHighlighted;
    @JsonProperty
    @JsonSerialize(using=DataScopesSerializer.class)
    private List<AccessScope> dataScopes;
    @JsonProperty
    private String upgradeAppUrl;

    @Generated
    AppConsentDto(String key, String cloudKey, String name, String vendorName, ConsentStatus status, String contactVendorUrl, String privacyPolicyUrl, String logoUrl, boolean isVendorHighlighted, List<AccessScope> dataScopes, String upgradeAppUrl) {
        this.key = key;
        this.cloudKey = cloudKey;
        this.name = name;
        this.vendorName = vendorName;
        this.status = status;
        this.contactVendorUrl = contactVendorUrl;
        this.privacyPolicyUrl = privacyPolicyUrl;
        this.logoUrl = logoUrl;
        this.isVendorHighlighted = isVendorHighlighted;
        this.dataScopes = dataScopes;
        this.upgradeAppUrl = upgradeAppUrl;
    }

    @Generated
    public static AppConsentDtoBuilder builder() {
        return new AppConsentDtoBuilder();
    }

    @Generated
    public String getKey() {
        return this.key;
    }

    @Generated
    public String getCloudKey() {
        return this.cloudKey;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public String getVendorName() {
        return this.vendorName;
    }

    @Generated
    public ConsentStatus getStatus() {
        return this.status;
    }

    @Generated
    public String getContactVendorUrl() {
        return this.contactVendorUrl;
    }

    @Generated
    public String getPrivacyPolicyUrl() {
        return this.privacyPolicyUrl;
    }

    @Generated
    public String getLogoUrl() {
        return this.logoUrl;
    }

    @Generated
    public boolean isVendorHighlighted() {
        return this.isVendorHighlighted;
    }

    @Generated
    public List<AccessScope> getDataScopes() {
        return this.dataScopes;
    }

    @Generated
    public String getUpgradeAppUrl() {
        return this.upgradeAppUrl;
    }

    @Generated
    public static class AppConsentDtoBuilder {
        @Generated
        private String key;
        @Generated
        private String cloudKey;
        @Generated
        private String name;
        @Generated
        private String vendorName;
        @Generated
        private ConsentStatus status;
        @Generated
        private String contactVendorUrl;
        @Generated
        private String privacyPolicyUrl;
        @Generated
        private String logoUrl;
        @Generated
        private boolean isVendorHighlighted;
        @Generated
        private List<AccessScope> dataScopes;
        @Generated
        private String upgradeAppUrl;

        @Generated
        AppConsentDtoBuilder() {
        }

        @Generated
        public AppConsentDtoBuilder key(String key) {
            this.key = key;
            return this;
        }

        @Generated
        public AppConsentDtoBuilder cloudKey(String cloudKey) {
            this.cloudKey = cloudKey;
            return this;
        }

        @Generated
        public AppConsentDtoBuilder name(String name) {
            this.name = name;
            return this;
        }

        @Generated
        public AppConsentDtoBuilder vendorName(String vendorName) {
            this.vendorName = vendorName;
            return this;
        }

        @Generated
        public AppConsentDtoBuilder status(ConsentStatus status) {
            this.status = status;
            return this;
        }

        @Generated
        public AppConsentDtoBuilder contactVendorUrl(String contactVendorUrl) {
            this.contactVendorUrl = contactVendorUrl;
            return this;
        }

        @Generated
        public AppConsentDtoBuilder privacyPolicyUrl(String privacyPolicyUrl) {
            this.privacyPolicyUrl = privacyPolicyUrl;
            return this;
        }

        @Generated
        public AppConsentDtoBuilder logoUrl(String logoUrl) {
            this.logoUrl = logoUrl;
            return this;
        }

        @Generated
        public AppConsentDtoBuilder isVendorHighlighted(boolean isVendorHighlighted) {
            this.isVendorHighlighted = isVendorHighlighted;
            return this;
        }

        @Generated
        public AppConsentDtoBuilder dataScopes(List<AccessScope> dataScopes) {
            this.dataScopes = dataScopes;
            return this;
        }

        @Generated
        public AppConsentDtoBuilder upgradeAppUrl(String upgradeAppUrl) {
            this.upgradeAppUrl = upgradeAppUrl;
            return this;
        }

        @Generated
        public AppConsentDto build() {
            return new AppConsentDto(this.key, this.cloudKey, this.name, this.vendorName, this.status, this.contactVendorUrl, this.privacyPolicyUrl, this.logoUrl, this.isVendorHighlighted, this.dataScopes, this.upgradeAppUrl);
        }

        @Generated
        public String toString() {
            return "AppConsentDto.AppConsentDtoBuilder(key=" + this.key + ", cloudKey=" + this.cloudKey + ", name=" + this.name + ", vendorName=" + this.vendorName + ", status=" + (Object)((Object)this.status) + ", contactVendorUrl=" + this.contactVendorUrl + ", privacyPolicyUrl=" + this.privacyPolicyUrl + ", logoUrl=" + this.logoUrl + ", isVendorHighlighted=" + this.isVendorHighlighted + ", dataScopes=" + this.dataScopes + ", upgradeAppUrl=" + this.upgradeAppUrl + ")";
        }
    }
}

