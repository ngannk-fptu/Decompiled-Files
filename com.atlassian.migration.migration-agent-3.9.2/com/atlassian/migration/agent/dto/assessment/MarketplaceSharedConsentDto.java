/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto.assessment;

import lombok.Generated;
import org.codehaus.jackson.annotate.JsonProperty;

public class MarketplaceSharedConsentDto {
    public static final String MARKETPLACE_PARTNERS_SHARED_CONSENT = "allowAppMigrationsDataShare";
    @JsonProperty
    private final String consentKey = "allowAppMigrationsDataShare";
    @JsonProperty
    private final boolean shouldCollectConsent;

    @Generated
    MarketplaceSharedConsentDto(boolean shouldCollectConsent) {
        this.shouldCollectConsent = shouldCollectConsent;
    }

    @Generated
    public static MarketplaceSharedConsentDtoBuilder builder() {
        return new MarketplaceSharedConsentDtoBuilder();
    }

    @Generated
    public String getConsentKey() {
        return this.consentKey;
    }

    @Generated
    public boolean isShouldCollectConsent() {
        return this.shouldCollectConsent;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof MarketplaceSharedConsentDto)) {
            return false;
        }
        MarketplaceSharedConsentDto other = (MarketplaceSharedConsentDto)o;
        if (!other.canEqual(this)) {
            return false;
        }
        String this$consentKey = this.getConsentKey();
        String other$consentKey = other.getConsentKey();
        if (this$consentKey == null ? other$consentKey != null : !this$consentKey.equals(other$consentKey)) {
            return false;
        }
        return this.isShouldCollectConsent() == other.isShouldCollectConsent();
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof MarketplaceSharedConsentDto;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $consentKey = this.getConsentKey();
        result = result * 59 + ($consentKey == null ? 43 : $consentKey.hashCode());
        result = result * 59 + (this.isShouldCollectConsent() ? 79 : 97);
        return result;
    }

    @Generated
    public static class MarketplaceSharedConsentDtoBuilder {
        @Generated
        private boolean shouldCollectConsent;

        @Generated
        MarketplaceSharedConsentDtoBuilder() {
        }

        @Generated
        public MarketplaceSharedConsentDtoBuilder shouldCollectConsent(boolean shouldCollectConsent) {
            this.shouldCollectConsent = shouldCollectConsent;
            return this;
        }

        @Generated
        public MarketplaceSharedConsentDto build() {
            return new MarketplaceSharedConsentDto(this.shouldCollectConsent);
        }

        @Generated
        public String toString() {
            return "MarketplaceSharedConsentDto.MarketplaceSharedConsentDtoBuilder(shouldCollectConsent=" + this.shouldCollectConsent + ")";
        }
    }
}

