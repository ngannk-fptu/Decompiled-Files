/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.oauth2.provider.api.event;

import com.atlassian.analytics.api.annotations.EventName;
import java.util.List;

@EventName(value="plugins.oauth2.provider.statistics")
public class OAuth2ProviderStatisticsEvent {
    private final String product;
    private final List<Integer> numberOfTokensPerIntegration;
    private final boolean applinksFeatureFlagEnabled;
    private final boolean basicAuthenticationTokenEnabled;

    public OAuth2ProviderStatisticsEvent(String product, List<Integer> numberOfTokensPerIntegration, boolean applinksFeatureFlagEnabled, boolean basicAuthenticationTokenEnabled) {
        this.product = product;
        this.numberOfTokensPerIntegration = numberOfTokensPerIntegration;
        this.applinksFeatureFlagEnabled = applinksFeatureFlagEnabled;
        this.basicAuthenticationTokenEnabled = basicAuthenticationTokenEnabled;
    }

    public String getProduct() {
        return this.product;
    }

    public List<Integer> getNumberOfTokensPerIntegration() {
        return this.numberOfTokensPerIntegration;
    }

    public boolean isApplinksFeatureFlagEnabled() {
        return this.applinksFeatureFlagEnabled;
    }

    public boolean isBasicAuthenticationTokenEnabled() {
        return this.basicAuthenticationTokenEnabled;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof OAuth2ProviderStatisticsEvent)) {
            return false;
        }
        OAuth2ProviderStatisticsEvent other = (OAuth2ProviderStatisticsEvent)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.isApplinksFeatureFlagEnabled() != other.isApplinksFeatureFlagEnabled()) {
            return false;
        }
        if (this.isBasicAuthenticationTokenEnabled() != other.isBasicAuthenticationTokenEnabled()) {
            return false;
        }
        String this$product = this.getProduct();
        String other$product = other.getProduct();
        if (this$product == null ? other$product != null : !this$product.equals(other$product)) {
            return false;
        }
        List<Integer> this$numberOfTokensPerIntegration = this.getNumberOfTokensPerIntegration();
        List<Integer> other$numberOfTokensPerIntegration = other.getNumberOfTokensPerIntegration();
        return !(this$numberOfTokensPerIntegration == null ? other$numberOfTokensPerIntegration != null : !((Object)this$numberOfTokensPerIntegration).equals(other$numberOfTokensPerIntegration));
    }

    protected boolean canEqual(Object other) {
        return other instanceof OAuth2ProviderStatisticsEvent;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isApplinksFeatureFlagEnabled() ? 79 : 97);
        result = result * 59 + (this.isBasicAuthenticationTokenEnabled() ? 79 : 97);
        String $product = this.getProduct();
        result = result * 59 + ($product == null ? 43 : $product.hashCode());
        List<Integer> $numberOfTokensPerIntegration = this.getNumberOfTokensPerIntegration();
        result = result * 59 + ($numberOfTokensPerIntegration == null ? 43 : ((Object)$numberOfTokensPerIntegration).hashCode());
        return result;
    }

    public String toString() {
        return "OAuth2ProviderStatisticsEvent(product=" + this.getProduct() + ", numberOfTokensPerIntegration=" + this.getNumberOfTokensPerIntegration() + ", applinksFeatureFlagEnabled=" + this.isApplinksFeatureFlagEnabled() + ", basicAuthenticationTokenEnabled=" + this.isBasicAuthenticationTokenEnabled() + ")";
    }
}

