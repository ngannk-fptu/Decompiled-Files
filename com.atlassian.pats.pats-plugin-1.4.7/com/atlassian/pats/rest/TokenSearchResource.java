/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.google.common.annotations.VisibleForTesting
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 */
package com.atlassian.pats.rest;

import com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.pats.api.TokenService;
import com.atlassian.pats.db.TokenDTO;
import com.atlassian.pats.rest.PermissionChecker;
import com.atlassian.pats.rest.RestPage;
import com.atlassian.pats.rest.RestToken;
import com.atlassian.pats.rest.RestTokenSearchRequest;
import com.atlassian.pats.rest.RestUserProfile;
import com.atlassian.pats.rest.RestValidator;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.google.common.annotations.VisibleForTesting;
import com.sun.jersey.spi.resource.Singleton;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.springframework.data.domain.Page;

@Path(value="/tokens/search")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Singleton
public class TokenSearchResource {
    private final TokenService tokenService;
    private final RestValidator restValidator;
    private final UserManager userManager;
    private final PermissionChecker permissionChecker;

    public TokenSearchResource(TokenService tokenService, UserManager userManager, RestValidator restValidator, PermissionChecker permissionChecker) {
        this.tokenService = tokenService;
        this.userManager = userManager;
        this.restValidator = restValidator;
        this.permissionChecker = permissionChecker;
    }

    @POST
    @ReadOnlyAccessAllowed
    public Response search(RestTokenSearchRequest tokenSearchRequest) {
        this.permissionChecker.verifyPermissions();
        this.restValidator.verifyTokenSearchRequest(tokenSearchRequest);
        Page<TokenDTO> page = this.tokenService.search(tokenSearchRequest);
        List<RestToken> restTokens = this.toRestTokens(page.getContent());
        RestPage<RestToken> restTokenRestPage = new RestPage<RestToken>(page, restTokens);
        return Response.ok(restTokenRestPage).build();
    }

    @VisibleForTesting
    List<RestToken> toRestTokens(List<TokenDTO> tokens) {
        HashMap userProfiles = new HashMap();
        return tokens.stream().map(token -> {
            UserKey key = new UserKey(token.getUserKey());
            return RestToken.valueOf(token, userProfiles.computeIfAbsent(key, userKey -> this.userManager.getUserProfile(key)));
        }).collect(Collectors.toList());
    }

    @GET
    @Path(value="/user")
    @ReadOnlyAccessAllowed
    public Response searchForUsers(@QueryParam(value="contains") String nameExpression, @QueryParam(value="limit") @DefaultValue(value="20") int limit) {
        this.permissionChecker.verifyPermissions();
        List<UserProfile> tokenNamesForSearchField = this.tokenService.searchForUsers(nameExpression, limit);
        return Response.ok(tokenNamesForSearchField.stream().map(RestUserProfile::valueOf).collect(Collectors.toList())).build();
    }
}

