/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 *  com.google.common.collect.ImmutableMap
 *  javax.inject.Inject
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugins.authentication.data;

import com.atlassian.json.marshal.Jsonable;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.api.config.SsoType;
import com.atlassian.plugins.authentication.impl.basicauth.service.CachingBasicAuthService;
import com.atlassian.plugins.authentication.impl.util.HttpsValidator;
import com.atlassian.plugins.authentication.impl.util.LegacyAuthenticationMethodsDataProvider;
import com.atlassian.plugins.authentication.impl.util.ProductLicenseDataProvider;
import com.atlassian.plugins.authentication.impl.web.AuthenticationHandler;
import com.atlassian.plugins.authentication.impl.web.AuthenticationHandlerProvider;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.webresource.api.data.WebResourceDataProvider;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;

public class ConfigurationConstantsDataProvider
implements WebResourceDataProvider {
    private static final Gson GSON = new Gson();
    private final ApplicationProperties applicationProperties;
    private final AuthenticationHandlerProvider authenticationHandlerProvider;
    private final HttpsValidator httpsValidator;
    private final ProductLicenseDataProvider productLicenseDataProvider;
    private final CachingBasicAuthService cachingBasicAuthService;
    private final LegacyAuthenticationMethodsDataProvider legacyAuthenticationMethodsDataProvider;

    @Inject
    public ConfigurationConstantsDataProvider(@ComponentImport ApplicationProperties applicationProperties, AuthenticationHandlerProvider authenticationHandlerProvider, HttpsValidator httpsValidator, ProductLicenseDataProvider productLicenseDataProvider, CachingBasicAuthService cachingBasicAuthService, LegacyAuthenticationMethodsDataProvider legacyAuthenticationMethodsDataProvider) {
        this.applicationProperties = applicationProperties;
        this.authenticationHandlerProvider = authenticationHandlerProvider;
        this.httpsValidator = httpsValidator;
        this.productLicenseDataProvider = productLicenseDataProvider;
        this.cachingBasicAuthService = cachingBasicAuthService;
        this.legacyAuthenticationMethodsDataProvider = legacyAuthenticationMethodsDataProvider;
    }

    public Jsonable get() {
        AuthenticationHandler samlAuthHandler = this.authenticationHandlerProvider.getAuthenticationHandler(SsoType.SAML);
        AuthenticationHandler oidcAuthHandler = this.authenticationHandlerProvider.getAuthenticationHandler(SsoType.OIDC);
        return writer -> {
            String baseUrl = StringUtils.removeEnd((String)this.applicationProperties.getBaseUrl(UrlMode.CANONICAL), (String)"/");
            GSON.toJson((Object)ImmutableMap.builder().put((Object)"isServiceManagement", (Object)this.productLicenseDataProvider.isServiceManagementProduct()).put((Object)"isDataCenter", (Object)this.productLicenseDataProvider.isDataCenterProduct()).put((Object)"product", (Object)this.applicationProperties.getPlatformId()).put((Object)"isHttps", (Object)this.httpsValidator.isBaseUrlSecure()).put((Object)"isHttpsRequired", (Object)this.httpsValidator.isHttpsRequired()).put((Object)"isBlockingRequests", (Object)this.cachingBasicAuthService.getConfig().isBlockRequests()).put((Object)"externalLoginUrl", (Object)(baseUrl + "/plugins/servlet/external-login")).put((Object)"samlServiceUrl", (Object)samlAuthHandler.getConsumerServletUrl()).put((Object)"samlEntityId", (Object)samlAuthHandler.getIssuerUrl()).put((Object)"oidcServiceUrl", (Object)oidcAuthHandler.getConsumerServletUrl()).put((Object)"oidcInitiateLoginUrl", (Object)(baseUrl + "/plugins/servlet/oidc/initiate-login")).put((Object)"legacyAuthenticationMethodsEnabled", (Object)this.legacyAuthenticationMethodsDataProvider.hasLegacyAuthenticationMethodsConfigured()).build(), (Appendable)writer);
        };
    }
}

