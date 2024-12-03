/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  javax.annotation.Nullable
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.core.UriBuilder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.web.filter.authentication;

import com.atlassian.plugins.authentication.api.config.IdpConfigService;
import com.atlassian.plugins.authentication.api.config.LoginGatewayType;
import com.atlassian.plugins.authentication.api.config.LoginOptionsService;
import com.atlassian.plugins.authentication.impl.johnson.JohnsonChecker;
import com.atlassian.plugins.authentication.impl.util.TargetUrlNormalizer;
import com.atlassian.plugins.authentication.impl.web.AuthenticationHandlerProvider;
import com.atlassian.plugins.authentication.impl.web.filter.authentication.AuthenticationFilter;
import com.google.common.base.Strings;
import java.net.URI;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.UriBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceManagementAuthenticationFilter
extends AuthenticationFilter {
    private static final Logger log = LoggerFactory.getLogger(ServiceManagementAuthenticationFilter.class);
    public static final String SERVICE_MANAGEMENT_DESTINATION_QUERY_PARAM = "destination";
    public static final String SERVICE_MANAGEMENT_PSEUDO_ABSOLUTE_REDIRECT_QUERY_PARAM = "absolute";
    private final TargetUrlNormalizer targetUrlNormalizer;

    public ServiceManagementAuthenticationFilter(AuthenticationHandlerProvider authenticationHandlerProvider, IdpConfigService idpConfigService, TargetUrlNormalizer targetUrlNormalizer, LoginOptionsService loginOptionsService, JohnsonChecker johnsonChecker) {
        super(authenticationHandlerProvider, idpConfigService, loginOptionsService, johnsonChecker);
        this.targetUrlNormalizer = targetUrlNormalizer;
    }

    @Override
    @Nullable
    protected String extractRequestedUrl(HttpServletRequest req) {
        String destination = Strings.emptyToNull((String)req.getParameter(SERVICE_MANAGEMENT_DESTINATION_QUERY_PARAM));
        if (destination == null) {
            log.trace("No destination query param present, returning empty destination");
            return null;
        }
        URI targetUrl = this.targetUrlNormalizer.getRelativeTargetUrl(destination);
        if (Boolean.parseBoolean(req.getParameter(SERVICE_MANAGEMENT_PSEUDO_ABSOLUTE_REDIRECT_QUERY_PARAM))) {
            String normalizedUrl = this.targetUrlNormalizer.removeContextPathFromUriIfNeeded(targetUrl).toString();
            log.trace("Pseudo absolute redirect present, resolving target url as {}", (Object)normalizedUrl);
            return normalizedUrl;
        }
        String destinationUri = UriBuilder.fromUri((URI)targetUrl).replacePath("/servicedesk/customer").path(targetUrl.getPath()).build(new Object[0]).toString();
        log.trace("Pseudo absolute redirect present, resolving target url as {}", (Object)destinationUri);
        return destinationUri;
    }

    @Override
    protected LoginGatewayType getLoginGatewayType() {
        return LoginGatewayType.JSM_LOGIN_GATEWAY;
    }
}

