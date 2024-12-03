/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.servlet.http.HttpServlet
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.web;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.api.config.IdpConfig;
import com.atlassian.plugins.authentication.api.config.IdpConfigService;
import com.atlassian.plugins.authentication.impl.web.AuthenticationHandlerProvider;
import com.atlassian.sal.api.message.I18nResolver;
import java.io.IOException;
import java.io.Serializable;
import java.util.Optional;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginServlet
extends HttpServlet {
    public static final String URL = "/plugins/servlet/external-login";
    private final Logger log = LoggerFactory.getLogger(LoginServlet.class);
    private final AuthenticationHandlerProvider authenticationHandlerProvider;
    private final IdpConfigService idpConfigService;
    private final I18nResolver i18nResolver;

    public LoginServlet(AuthenticationHandlerProvider authenticationHandlerProvider, IdpConfigService idpConfigService, @ComponentImport I18nResolver i18nResolver) {
        this.authenticationHandlerProvider = authenticationHandlerProvider;
        this.idpConfigService = idpConfigService;
        this.i18nResolver = i18nResolver;
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            long idpId = this.extractIdpIdPathParam(req);
            IdpConfig idpConfig = this.idpConfigService.getIdpConfig(idpId);
            if (idpConfig == null || !idpConfig.isEnabled()) {
                this.log.error("External login request: could not find enabled IDP configuration with ID '{}'", (Object)idpId);
                resp.sendError(404, this.i18nResolver.getText("authentication.config.fail.idp.not.found", new Serializable[]{Long.valueOf(idpId)}));
            } else {
                this.authenticationHandlerProvider.getAuthenticationHandler(idpConfig.getSsoType()).processAuthenticationRequest(req, resp, this.extractDestinationParam(req).orElse(null), idpConfig);
            }
        }
        catch (IllegalArgumentException e) {
            resp.sendError(400, e.getMessage());
        }
    }

    private long extractIdpIdPathParam(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) {
            this.log.error("External login request is missing IDP ID path param");
            throw new IllegalArgumentException("Identity Provider ID was not provided in request path");
        }
        String[] pathParts = pathInfo.split("/");
        if (pathParts.length == 2) {
            return this.parseIdpIdParam(pathParts[1]);
        }
        this.log.error("External login request has unexpected path params: {}", (Object)pathInfo);
        throw new IllegalArgumentException("Unexpected path parameters in request");
    }

    private long parseIdpIdParam(String param) {
        try {
            return Long.parseLong(param);
        }
        catch (NumberFormatException exception) {
            this.log.error("External login request IDP ID param not parsable to Long. Was '{}'", (Object)param);
            throw new IllegalArgumentException("Invalid Identity Provider ID path parameter");
        }
    }

    private Optional<String> extractDestinationParam(HttpServletRequest req) {
        return Optional.ofNullable(req.getParameter("authDest"));
    }
}

