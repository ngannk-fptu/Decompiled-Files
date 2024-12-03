/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth2.client.api.ClientConfiguration
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  javax.annotation.Nonnull
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.core.UriBuilder
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.slf4j.helpers.MessageFormatter
 */
package com.atlassian.oauth2.client.lib.web;

import com.atlassian.oauth2.client.RedirectUriSuffixGenerator;
import com.atlassian.oauth2.client.api.ClientConfiguration;
import com.atlassian.oauth2.client.util.ClientHttpsValidator;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import java.net.URI;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.UriBuilder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

public class AuthorizationCodeFlowUrlsProvider {
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationCodeFlowUrlsProvider.class);
    static final String REDIRECT_URL = "/plugins/servlet/oauth2/client/callback";
    static final String START_FLOW_ID = "startFlow";
    private final ApplicationProperties applicationProperties;
    private final ClientHttpsValidator clientHttpsValidator;
    private final RedirectUriSuffixGenerator redirectUriSuffixGenerator;

    public AuthorizationCodeFlowUrlsProvider(ApplicationProperties applicationProperties, ClientHttpsValidator clientHttpsValidator, RedirectUriSuffixGenerator redirectUriSuffixGenerator) {
        this.applicationProperties = applicationProperties;
        this.clientHttpsValidator = clientHttpsValidator;
        this.redirectUriSuffixGenerator = redirectUriSuffixGenerator;
    }

    @Nonnull
    public URI getRedirectUri(ClientConfiguration clientConfiguration) {
        return this.getRedirectUri(this.redirectUriSuffixGenerator.generateRedirectUriSuffix(clientConfiguration.getAuthorizationEndpoint()));
    }

    @Nonnull
    public URI getRedirectUri(String suffix) {
        return this.buildUri(uri -> uri.path(suffix));
    }

    public URI getInitFlowUrl(@Nonnull String flowRequestId) {
        return this.buildUri(uri -> uri.queryParam(START_FLOW_ID, new Object[]{flowRequestId}));
    }

    @Nonnull
    public URI getProductBaseUrl() {
        return URI.create(this.applicationProperties.getBaseUrl(UrlMode.CANONICAL));
    }

    private URI buildUri(Consumer<UriBuilder> consumer) {
        UriBuilder uriBuilder = UriBuilder.fromUri((String)this.applicationProperties.getBaseUrl(UrlMode.CANONICAL)).path(REDIRECT_URL);
        consumer.accept(uriBuilder);
        URI uri = uriBuilder.build(new Object[0]).normalize();
        this.clientHttpsValidator.enforceSecureBaseUrl(uri);
        return uri;
    }

    public void validateRedirectUri(ClientConfiguration clientConfiguration, HttpServletRequest request) throws IllegalArgumentException {
        String actualPath;
        String actualSuffix;
        String expectedSuffix = this.redirectUriSuffixGenerator.generateRedirectUriSuffix(clientConfiguration.getAuthorizationEndpoint());
        if (!Objects.equals(expectedSuffix, actualSuffix = StringUtils.substringAfterLast((String)(actualPath = StringUtils.removeEnd((String)request.getRequestURI(), (String)"/")), (String)"/"))) {
            String message = MessageFormatter.format((String)"Expected provider {} but got {}.", (Object)expectedSuffix, (Object)actualSuffix).getMessage();
            logger.error(message);
            throw new IllegalArgumentException(message);
        }
    }
}

