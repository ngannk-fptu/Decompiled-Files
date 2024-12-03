/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.plugins.remotepageview.rest;

import com.atlassian.confluence.plugins.remotepageview.api.service.TokenService;
import com.atlassian.confluence.plugins.remotepageview.rest.response.TokenResponse;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Optional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path(value="/token")
@Produces(value={"application/json;charset=UTF-8"})
public class TokenResource {
    private final TokenService tokenService;

    public TokenResource(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GET
    public Response generateToken(@QueryParam(value="pageId") long pageId) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (user == null) {
            return Response.status((Response.Status)Response.Status.UNAUTHORIZED).build();
        }
        Optional<TokenResponse> tokenResponse = this.tokenService.generateLoginTokenForUser(user, pageId);
        if (tokenResponse.isPresent()) {
            return Response.ok((Object)tokenResponse.get()).build();
        }
        return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
    }
}

