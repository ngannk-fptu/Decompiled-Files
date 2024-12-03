/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  com.google.common.base.Throwables
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.web.filter;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.plugins.authentication.impl.config.IdpNotFoundException;
import com.atlassian.plugins.authentication.impl.johnson.JohnsonChecker;
import com.atlassian.plugins.authentication.impl.web.AuthenticationHandlerNotConfiguredException;
import com.atlassian.plugins.authentication.impl.web.InvalidLicenseException;
import com.atlassian.plugins.authentication.impl.web.filter.AbstractJohnsonAwareFilter;
import com.atlassian.plugins.authentication.impl.web.saml.provider.InvalidSamlResponse;
import com.atlassian.plugins.authentication.impl.web.usercontext.AuthenticationFailedException;
import com.atlassian.plugins.authentication.impl.web.usercontext.IdentifiableRuntimeException;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorHandlingFilter
extends AbstractJohnsonAwareFilter {
    private static final Logger log = LoggerFactory.getLogger(ErrorHandlingFilter.class);
    private static final String INCLUDE_STACKTRACE_DARKFEATURE = "atlassian.authentication.include.stacktrace.in.error.messages";
    public static final String TEMPLATE_COMPLETE_KEY = "com.atlassian.plugins.authentication.atlassian-authentication-plugin:templates";
    public static final String INVALID_SAML_RESPONSE_TEMPLATE_NAME_WITH_NAMESPACE = "AuthenticationPlugin.InvalidSamlResponse.display";
    public static final String AUTHENTICATION_FAILED_TEMPLATE_NAME_WITH_NAMESPACE = "AuthenticationPlugin.AuthenticationFailed.display";
    public static final String AUTHENTICATION_NOT_CONFIGURED_TEMPLATE_NAME_WITH_NAMESPACE = "AuthenticationPlugin.AuthenticationNotConfigured.display";
    public static final String IDP_NOT_FOUND_TEMPLATE_NAME_WITH_NAMESPACE = "AuthenticationPlugin.IdpNotFound.display";
    public static final String REFERRER_POLICY_HEADER = "Referrer-Policy";
    public static final String ORIGIN_WHEN_CROSS_ORIGIN = "origin-when-cross-origin";
    private static final String RESPONSE_CONTENT_TYPE = "text/html";
    private final SoyTemplateRenderer templateRenderer;
    private final WebResourceUrlProvider webResourceUrlProvider;
    private final LoginUriProvider loginUriProvider;
    private final ApplicationProperties applicationProperties;
    private final DarkFeatureManager darkFeatureManager;

    public ErrorHandlingFilter(@ComponentImport SoyTemplateRenderer templateRenderer, @ComponentImport WebResourceUrlProvider webResourceUrlProvider, @ComponentImport LoginUriProvider loginUriProvider, @ComponentImport ApplicationProperties applicationProperties, @ComponentImport DarkFeatureManager darkFeatureManager, JohnsonChecker johnsonChecker) {
        super(johnsonChecker);
        this.templateRenderer = templateRenderer;
        this.webResourceUrlProvider = webResourceUrlProvider;
        this.loginUriProvider = loginUriProvider;
        this.applicationProperties = applicationProperties;
        this.darkFeatureManager = darkFeatureManager;
    }

    @Override
    protected void doFilterInternal(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        }
        catch (IdpNotFoundException e) {
            this.logException(e);
            this.renderResponse(response, IDP_NOT_FOUND_TEMPLATE_NAME_WITH_NAMESPACE, 404, (Map<String, Object>)ImmutableMap.of((Object)"idpId", (Object)e.getId()), e);
        }
        catch (AuthenticationHandlerNotConfiguredException | InvalidLicenseException e) {
            this.logException(e);
            this.renderResponse(response, AUTHENTICATION_NOT_CONFIGURED_TEMPLATE_NAME_WITH_NAMESPACE, 400, (Map<String, Object>)ImmutableMap.of(), e);
        }
        catch (InvalidSamlResponse e) {
            this.logException(e);
            ImmutableMap.Builder templateParametersBuilder = ImmutableMap.builder().put((Object)"productName", (Object)this.applicationProperties.getDisplayName());
            String targetUrl = this.prepareTargetUrl(e);
            if (targetUrl != null) {
                templateParametersBuilder.put((Object)"url", (Object)this.prepareTargetUrl(e));
            }
            this.renderResponse(response, INVALID_SAML_RESPONSE_TEMPLATE_NAME_WITH_NAMESPACE, 400, (Map<String, Object>)templateParametersBuilder.build(), e);
        }
        catch (AuthenticationFailedException e) {
            this.logException(e);
            this.renderResponse(response, AUTHENTICATION_FAILED_TEMPLATE_NAME_WITH_NAMESPACE, 400, (Map<String, Object>)ImmutableMap.of(), e);
        }
    }

    private void logException(Exception e) {
        if (e instanceof IdentifiableRuntimeException) {
            log.error(String.format("[UUID: %s] %s", ((IdentifiableRuntimeException)e).getUuid(), e.getMessage()), (Throwable)e);
        } else {
            log.error(e.getMessage(), (Throwable)e);
        }
    }

    private void renderResponse(HttpServletResponse responseToFill, String templateNameWithNamespace, int statusCode, Map<String, Object> customMappings, Throwable error) throws IOException {
        responseToFill.setContentType(RESPONSE_CONTENT_TYPE);
        responseToFill.setStatus(statusCode);
        responseToFill.setHeader(REFERRER_POLICY_HEADER, ORIGIN_WHEN_CROSS_ORIGIN);
        this.templateRenderer.render((Appendable)responseToFill.getWriter(), TEMPLATE_COMPLETE_KEY, templateNameWithNamespace, this.prepareMappings(customMappings, error));
    }

    private Map<String, Object> prepareMappings(Map<String, Object> customMappings, Throwable error) {
        ImmutableMap.Builder mapBuilder = ImmutableMap.builder();
        if (error instanceof IdentifiableRuntimeException) {
            mapBuilder.put((Object)"errorUuid", (Object)((IdentifiableRuntimeException)error).getUuid().toString());
        }
        if (this.shouldIncludeStackTrace()) {
            mapBuilder.put((Object)"stackTrace", (Object)Throwables.getStackTraceAsString((Throwable)error));
        }
        mapBuilder.put((Object)"errorImgUrl", (Object)this.webResourceUrlProvider.getStaticPluginResourceUrl(TEMPLATE_COMPLETE_KEY, "error", UrlMode.RELATIVE)).putAll(customMappings);
        return mapBuilder.build();
    }

    public String prepareTargetUrl(InvalidSamlResponse invalidSamlResponse) {
        try {
            if (invalidSamlResponse.getTargetUrl() != null) {
                return this.loginUriProvider.getLoginUri(new URI(invalidSamlResponse.getTargetUrl())).toString();
            }
        }
        catch (URISyntaxException uRISyntaxException) {
            // empty catch block
        }
        if (invalidSamlResponse.getIdpConfigId() != null) {
            return this.applicationProperties.getBaseUrl(com.atlassian.sal.api.UrlMode.RELATIVE) + "/plugins/servlet/external-login" + "/" + invalidSamlResponse.getIdpConfigId();
        }
        return null;
    }

    private boolean shouldIncludeStackTrace() {
        return this.darkFeatureManager.isFeatureEnabledForAllUsers(INCLUDE_STACKTRACE_DARKFEATURE);
    }
}

