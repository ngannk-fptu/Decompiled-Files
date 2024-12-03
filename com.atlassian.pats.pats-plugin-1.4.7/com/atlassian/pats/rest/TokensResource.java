/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.sal.api.permission.AuthorisationException
 *  com.atlassian.sal.api.permission.PermissionEnforcer
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.pats.rest;

import com.atlassian.annotations.PublicApi;
import com.atlassian.pats.api.TokenService;
import com.atlassian.pats.db.Tables;
import com.atlassian.pats.db.TokenDTO;
import com.atlassian.pats.db.TokenRepository;
import com.atlassian.pats.rest.RestNewTokenRequest;
import com.atlassian.pats.rest.RestToken;
import com.atlassian.pats.rest.RestValidator;
import com.atlassian.sal.api.permission.AuthorisationException;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.sun.jersey.spi.resource.Singleton;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="/tokens")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Singleton
public class TokensResource {
    private static final Logger logger = LoggerFactory.getLogger(TokensResource.class);
    private final TokenService tokenService;
    private final TokenRepository tokenRepository;
    private final UserManager userManager;
    private final PermissionEnforcer permissionEnforcer;
    private final RestValidator restValidator;

    public TokensResource(TokenService tokenService, TokenRepository tokenRepository, UserManager userManager, PermissionEnforcer permissionEnforcer, RestValidator restValidator) {
        this.tokenService = tokenService;
        this.tokenRepository = tokenRepository;
        this.userManager = userManager;
        this.permissionEnforcer = permissionEnforcer;
        this.restValidator = restValidator;
    }

    @GET
    @PublicApi
    public Response getAllTokensForUser() {
        this.permissionEnforcer.enforceAuthenticated();
        UserKey userKey = this.userManager.getRemoteUserKey();
        logger.debug("Getting tokens for user: [{}]", (Object)userKey);
        List userTokens = this.tokenRepository.findAllByUserKey(userKey.getStringValue()).map(RestToken::valueOf).collect(Collectors.toList());
        return Response.ok(userTokens).build();
    }

    @POST
    @PublicApi
    public Response createToken(RestNewTokenRequest newTokenRequest) {
        this.permissionEnforcer.enforceAuthenticated();
        UserKey userKey = this.userManager.getRemoteUserKey();
        logger.debug("Creating new token for user: [{}]", (Object)userKey);
        this.restValidator.verifyCreateTokenRequest(newTokenRequest, userKey);
        TokenDTO newToken = this.tokenService.create(userKey, newTokenRequest.getName(), newTokenRequest.getExpirationDuration());
        return Response.status((Response.Status)Response.Status.CREATED).entity((Object)RestToken.valueOf(newToken)).build();
    }

    @GET
    @Path(value="/{id}")
    @PublicApi
    public Response getToken(@PathParam(value="id") Long id) {
        this.permissionEnforcer.enforceAuthenticated();
        logger.debug("Getting token with id: [{}]", (Object)id);
        return this.getAccessibleToken(id).map(token -> Response.ok((Object)RestToken.valueOf(token)).build()).orElseGet(() -> Response.status((Response.Status)Response.Status.NOT_FOUND).build());
    }

    private Optional<TokenDTO> getAccessibleToken(Long id) {
        UserKey currentUser = this.userManager.getRemoteUserKey();
        return this.tokenRepository.findById(id).map(token -> this.isTokenAccessibleForUser((TokenDTO)token, currentUser));
    }

    private TokenDTO isTokenAccessibleForUser(TokenDTO token, UserKey currentUser) {
        if (!token.getUserKey().equals(currentUser.getStringValue()) && !this.isAdminUser(currentUser)) {
            throw new AuthorisationException(String.format("User: [%s] has no access to token!", currentUser));
        }
        return token;
    }

    private boolean isAdminUser(UserKey currentUser) {
        return this.userManager.isSystemAdmin(currentUser) || this.userManager.isAdmin(currentUser);
    }

    @DELETE
    @Path(value="/{id}")
    @PublicApi
    public Response deleteToken(@PathParam(value="id") Long id) {
        this.permissionEnforcer.enforceAuthenticated();
        logger.debug("Deleting token with id: [{}]", (Object)id);
        this.getAccessibleToken(id).ifPresent(token -> this.tokenService.delete(this.userManager.getRemoteUserKey(), Tables.TOKEN.id.eq(id)));
        return Response.status((Response.Status)Response.Status.NO_CONTENT).build();
    }
}

