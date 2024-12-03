/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  javax.annotation.Nonnull
 *  javax.inject.Inject
 *  javax.inject.Named
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugins.authentication.impl.web.saml;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.plugins.authentication.api.config.saml.SamlConfig;
import com.atlassian.plugins.authentication.impl.util.ApplicationStateValidator;
import com.atlassian.plugins.authentication.impl.util.TargetUrlNormalizer;
import com.atlassian.plugins.authentication.impl.web.AbstractAuthenticationHandler;
import com.atlassian.plugins.authentication.impl.web.AuthenticationRequest;
import com.atlassian.plugins.authentication.impl.web.SessionDataService;
import com.atlassian.plugins.authentication.impl.web.saml.provider.SamlProvider;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

@Named
public class SamlAuthenticationHandler
extends AbstractAuthenticationHandler<SamlConfig> {
    private final SamlProvider samlProvider;

    @Inject
    public SamlAuthenticationHandler(@ComponentImport ApplicationProperties applicationProperties, ApplicationStateValidator applicationStateValidator, SamlProvider samlProvider, SessionDataService sessionDataService, SoyTemplateRenderer soyTemplateRenderer, @ComponentImport WebResourceUrlProvider webResourceUrlProvider, TargetUrlNormalizer targetUrlNormalizer) {
        super(applicationProperties, applicationStateValidator, sessionDataService, targetUrlNormalizer, webResourceUrlProvider, soyTemplateRenderer);
        this.samlProvider = samlProvider;
    }

    @Override
    @Nonnull
    public String getConsumerServletUrl() {
        return StringUtils.removeEnd((String)this.getIssuerUrl(), (String)"/") + "/plugins/servlet/samlconsumer";
    }

    @Override
    protected AuthenticationRequest prepareAuthenticationRequest(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, SamlConfig samlConfig) {
        return this.samlProvider.createSamlSingleSignOnRequest(request, response, this.getServiceProviderInfo(), this.isPermissionViolation(request), samlConfig);
    }

    private SamlProvider.ServiceProviderInfo getServiceProviderInfo() {
        return new SamlProvider.ServiceProviderInfo(this.getIssuerUrl(), this.getConsumerServletUrl());
    }
}

