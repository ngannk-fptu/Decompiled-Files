/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.oauth2.provider.api.settings.ProviderSettingsService
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.sun.jersey.spi.container.ResourceFilters
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.FormParam
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.provider.rest.resource;

import com.atlassian.annotations.PublicApi;
import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.oauth2.provider.api.settings.ProviderSettingsService;
import com.atlassian.oauth2.provider.rest.exception.InvalidGrantException;
import com.atlassian.oauth2.provider.rest.model.TokenRequestFormParams;
import com.atlassian.oauth2.provider.rest.resource.filter.SysadminOnlyResourceFilter;
import com.atlassian.oauth2.provider.rest.service.TokenRestService;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.sun.jersey.spi.container.ResourceFilters;
import com.sun.jersey.spi.resource.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="token")
@Consumes
@Produces(value={"application/json"})
@Singleton
public class TokenResource {
    private static final Logger logger = LoggerFactory.getLogger(TokenResource.class);
    private final TokenRestService tokenRestService;
    private final ProviderSettingsService providerSettingsService;

    public TokenResource(TokenRestService tokenRestService, ProviderSettingsService providerSettingsService) {
        this.tokenRestService = tokenRestService;
        this.providerSettingsService = providerSettingsService;
    }

    @XsrfProtectionExcluded
    @AnonymousAllowed
    @POST
    @PublicApi
    public Response createToken(@FormParam(value="grant_type") String grantType, @FormParam(value="code") String code, @FormParam(value="redirect_uri") String redirectUri, @FormParam(value="client_id") String clientId, @FormParam(value="client_secret") String clientSecret, @FormParam(value="refresh_token") String refreshToken, @FormParam(value="code_verifier") String codeVerifier) throws InvalidGrantException, InterruptedException {
        logger.debug("Creating token for client id [{}]", (Object)clientId);
        TokenRequestFormParams formParams = TokenRequestFormParams.builder().grantType(grantType).code(code).redirectUri(redirectUri).clientId(clientId).clientSecret(clientSecret).refreshToken(refreshToken).codeVerifier(codeVerifier).build();
        return Response.ok((Object)this.tokenRestService.create(formParams)).header("Cache-Control", (Object)"no-store").header("Pragma", (Object)"no-cache").build();
    }

    @XsrfProtectionExcluded
    @ResourceFilters(value={SysadminOnlyResourceFilter.class})
    @POST
    @Path(value="/jwt/reset")
    @PublicApi
    public Response reset() {
        logger.debug("Resetting JWT secret configured in plugin settings");
        this.providerSettingsService.reset();
        return Response.ok().build();
    }
}

