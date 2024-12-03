/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.plugins.authentication.impl.web.saml.provider;

import com.atlassian.plugins.authentication.api.config.saml.SamlConfig;
import com.atlassian.plugins.authentication.impl.web.saml.provider.InvalidSamlResponse;
import com.atlassian.plugins.authentication.impl.web.saml.provider.SamlRequest;
import com.atlassian.plugins.authentication.impl.web.saml.provider.SamlResponse;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface SamlProvider {
    public SamlRequest createSamlSingleSignOnRequest(@Nonnull HttpServletRequest var1, @Nonnull HttpServletResponse var2, @Nonnull ServiceProviderInfo var3, boolean var4, SamlConfig var5);

    public SamlResponse extractSamlResponse(@Nonnull HttpServletRequest var1, @Nonnull HttpServletResponse var2, @Nonnull ServiceProviderInfo var3, @Nonnull SamlConfig var4, @Nullable SamlRequest var5) throws InvalidSamlResponse;

    public List<String> getIssuers(HttpServletRequest var1);

    public static class ServiceProviderInfo {
        private final String issuerUrl;
        private final String consumerServiceUrl;

        public ServiceProviderInfo(@Nonnull String issuerUrl, @Nonnull String consumerServiceUrl) {
            this.issuerUrl = issuerUrl;
            this.consumerServiceUrl = consumerServiceUrl;
        }

        @Nonnull
        public String getIssuerUrl() {
            return this.issuerUrl;
        }

        @Nonnull
        public String getConsumerServiceUrl() {
            return this.consumerServiceUrl;
        }
    }
}

