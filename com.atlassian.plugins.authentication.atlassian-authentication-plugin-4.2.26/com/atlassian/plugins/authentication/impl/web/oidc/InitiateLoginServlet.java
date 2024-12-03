/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.google.common.base.Strings
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  javax.ws.rs.core.Response$Status
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.web.oidc;

import com.atlassian.plugins.authentication.api.config.IdpConfig;
import com.atlassian.plugins.authentication.api.config.IdpConfigService;
import com.atlassian.plugins.authentication.api.config.IdpSearchParameters;
import com.atlassian.plugins.authentication.api.config.SsoType;
import com.atlassian.plugins.authentication.api.config.oidc.OidcConfig;
import com.atlassian.plugins.authentication.impl.util.ApplicationStateValidator;
import com.atlassian.plugins.authentication.impl.web.AuthenticationHandlerProvider;
import com.atlassian.plugins.authentication.impl.web.SessionDataService;
import com.atlassian.plugins.authentication.impl.web.oidc.OidcAuthenticationHandler;
import com.atlassian.plugins.authentication.impl.web.usercontext.AuthenticationFailedException;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.google.common.base.Strings;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitiateLoginServlet
extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(InitiateLoginServlet.class);
    public static final String URL = "/plugins/servlet/oidc/initiate-login";
    public static final String TARGET_LINK_PARAM = "target_link_uri";
    public static final String LOGIN_HINT_PARAM = "login_hint";
    public static final String ISSUER_PARAM = "iss";
    private final AuthenticationHandlerProvider authenticationHandlerProvider;
    private final IdpConfigService idpConfigService;
    private final SessionDataService sessionDataService;
    private final ApplicationStateValidator applicationStateValidator;
    private final ApplicationProperties applicationProperties;

    public InitiateLoginServlet(AuthenticationHandlerProvider authenticationHandlerProvider, IdpConfigService idpConfigService, SessionDataService sessionDataService, ApplicationStateValidator applicationStateValidator, ApplicationProperties applicationProperties) {
        this.authenticationHandlerProvider = authenticationHandlerProvider;
        this.idpConfigService = idpConfigService;
        this.sessionDataService = sessionDataService;
        this.applicationStateValidator = applicationStateValidator;
        this.applicationProperties = applicationProperties;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Optional<OidcConfig> oidcConfig = this.fetchOidcConfigByIssuer(request.getParameter(ISSUER_PARAM));
        if (oidcConfig.isPresent()) {
            this.applicationStateValidator.checkCanProcessAuthenticationRequest(oidcConfig.get());
            log.debug("Login flow has been initiated by: {}", (Object)oidcConfig.get().getIssuer());
            this.sessionDataService.requireNewSession(request);
            OidcAuthenticationHandler.setLoginHint(request, request.getParameter(LOGIN_HINT_PARAM));
            try {
                this.authenticationHandlerProvider.getAuthenticationHandler(oidcConfig.get().getSsoType()).processAuthenticationRequest(request, response, request.getParameter(TARGET_LINK_PARAM), (IdpConfig)oidcConfig.get());
            }
            catch (IllegalArgumentException e) {
                response.sendError(Response.Status.BAD_REQUEST.getStatusCode(), e.getMessage());
            }
        } else {
            response.sendRedirect(this.applicationProperties.getBaseUrl(UrlMode.RELATIVE) + "/plugins/servlet/login");
        }
    }

    private Optional<OidcConfig> fetchOidcConfigByIssuer(String issuerUrl) {
        if (Strings.isNullOrEmpty((String)issuerUrl)) {
            return Optional.empty();
        }
        List oidcConfigs = this.idpConfigService.getIdpConfigs(IdpSearchParameters.allEnabledOfType(SsoType.OIDC)).stream().map(idpConfig -> (OidcConfig)idpConfig).filter(oidcConfig -> Objects.equals(oidcConfig.getIssuer(), issuerUrl)).collect(Collectors.toList());
        if (oidcConfigs.size() != 1) {
            log.warn("IDP initiated OIDC flow: could not retrieve IDP config for issuer {}", (Object)issuerUrl);
            throw new AuthenticationFailedException("Login flow initiated by unknown issuer: " + issuerUrl);
        }
        return Optional.of(oidcConfigs.get(0));
    }
}

