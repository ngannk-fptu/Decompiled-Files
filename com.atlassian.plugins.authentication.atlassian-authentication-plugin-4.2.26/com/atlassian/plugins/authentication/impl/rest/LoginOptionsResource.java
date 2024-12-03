/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.sal.api.ApplicationProperties
 *  javax.inject.Inject
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.HeaderParam
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.web.util.UriComponents
 *  org.springframework.web.util.UriComponentsBuilder
 */
package com.atlassian.plugins.authentication.impl.rest;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.api.config.LoginGatewayType;
import com.atlassian.plugins.authentication.api.config.LoginOptionsService;
import com.atlassian.plugins.authentication.impl.rest.model.LoginOptionEntity;
import com.atlassian.plugins.authentication.impl.rest.model.RestPage;
import com.atlassian.plugins.authentication.impl.rest.model.SimpleRestPageRequest;
import com.atlassian.plugins.authentication.impl.util.JsmUrlChecker;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.ApplicationProperties;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Path(value="/login-options")
@Produces(value={"application/json"})
@Consumes(value={"application/json"})
@AnonymousAllowed
public class LoginOptionsResource {
    private static final Logger log = LoggerFactory.getLogger(LoginOptionsResource.class);
    private final LoginOptionsService loginOptionsService;
    private final JsmUrlChecker jsmUrlChecker;
    private final ApplicationProperties applicationProperties;

    @Inject
    public LoginOptionsResource(LoginOptionsService loginOptionsService, JsmUrlChecker jsmUrlChecker, @ComponentImport ApplicationProperties applicationProperties) {
        this.loginOptionsService = loginOptionsService;
        this.jsmUrlChecker = jsmUrlChecker;
        this.applicationProperties = applicationProperties;
    }

    @GET
    public RestPage<LoginOptionEntity> getLoginOptions(@HeaderParam(value="Referer") String referer) {
        Optional<UriComponents> refererUrl = this.extractRefererUrl(referer);
        List loginOptions = this.loginOptionsService.getLoginOptions(this.isAuthFallbackQueryParamPresent(refererUrl), this.resolveLoginGatewayType(refererUrl)).stream().map(LoginOptionEntity::new).collect(Collectors.toList());
        return RestPage.fromListPlusOne(loginOptions, SimpleRestPageRequest.ALL_RESULTS_REQUEST);
    }

    private Optional<UriComponents> extractRefererUrl(String referer) {
        try {
            return Optional.of(UriComponentsBuilder.fromHttpUrl((String)referer).build());
        }
        catch (Exception e) {
            log.debug("Could not extract fallback query param from the Referer header", (Throwable)e);
            return Optional.empty();
        }
    }

    private LoginGatewayType resolveLoginGatewayType(Optional<UriComponents> referer) {
        boolean isJsmRequest = referer.map(UriComponents::getPath).map(this.jsmUrlChecker::isJsmRequest).orElse(false);
        return isJsmRequest ? LoginGatewayType.JSM_LOGIN_GATEWAY : LoginGatewayType.GLOBAL_LOGIN_GATEWAY;
    }

    private boolean isAuthFallbackQueryParamPresent(Optional<UriComponents> url) {
        return url.map(u -> u.getQueryParams().containsKey((Object)"auth_fallback")).orElse(false);
    }
}

