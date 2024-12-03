/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.FormParam
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.provider.rest.resource;

import com.atlassian.annotations.PublicApi;
import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.oauth2.provider.rest.exception.BadRequestException;
import com.atlassian.oauth2.provider.rest.exception.InvalidClientException;
import com.atlassian.oauth2.provider.rest.model.RevokeRequestFormParams;
import com.atlassian.oauth2.provider.rest.service.RevokeTokenService;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.sun.jersey.spi.resource.Singleton;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="revoke")
@Produces(value={"application/json"})
@Singleton
public class RevokeResource {
    private static final Logger logger = LoggerFactory.getLogger(RevokeResource.class);
    private final RevokeTokenService revokeTokenService;

    public RevokeResource(RevokeTokenService revokeTokenService) {
        this.revokeTokenService = revokeTokenService;
    }

    @POST
    @XsrfProtectionExcluded
    @AnonymousAllowed
    @PublicApi
    public Response revokeToken(@FormParam(value="token") String token, @FormParam(value="token_type_hint") String tokenTypeHint, @FormParam(value="client_id") String clientId, @FormParam(value="client_secret") String clientSecret) throws BadRequestException, InvalidClientException {
        logger.debug("Revoking token associated with client id [{}]", (Object)clientId);
        RevokeRequestFormParams formParams = RevokeRequestFormParams.builder().token(token).clientId(clientId).clientSecret(clientSecret).tokenTypeHint(tokenTypeHint).build();
        this.revokeTokenService.revokeToken(formParams);
        return Response.ok().build();
    }

    @POST
    @XsrfProtectionExcluded
    @Path(value="/{id}")
    public Response revokeTokenWithId(@PathParam(value="id") String tokenId) throws BadRequestException {
        logger.debug("Revoking token associated with token id [{}]", (Object)tokenId);
        this.revokeTokenService.revokeToken(tokenId);
        return Response.ok().build();
    }
}

