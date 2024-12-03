/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.provider.rest.resource;

import com.atlassian.annotations.PublicApi;
import com.atlassian.oauth2.provider.rest.model.RestAuthorizationRequest;
import com.atlassian.oauth2.provider.rest.service.AuthorizationRestService;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.sun.jersey.spi.resource.Singleton;
import java.net.URI;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="authorize")
@Singleton
public class AuthorizationResource {
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationResource.class);
    private final AuthorizationRestService authorizationRestService;

    public AuthorizationResource(AuthorizationRestService authorizationRestService) {
        this.authorizationRestService = authorizationRestService;
    }

    @GET
    @AnonymousAllowed
    @PublicApi
    public Response authorize(@Context HttpServletRequest httpServletRequest, @QueryParam(value="client_id") String clientId, @QueryParam(value="redirect_uri") String redirectUri, @QueryParam(value="state") String state, @QueryParam(value="response_type") String responseType, @QueryParam(value="scope") String scope, @QueryParam(value="code_challenge_method") String codeChallengeMethod, @QueryParam(value="code_challenge") String codeChallenge) {
        logger.debug("Authorizing request for client ID [{}]", (Object)clientId);
        RestAuthorizationRequest request = RestAuthorizationRequest.builder().clientId(clientId).redirectUri(redirectUri).state(state).responseType(responseType).scope(StringUtils.defaultString((String)scope)).codeChallengeMethod(codeChallengeMethod).codeChallenge(codeChallenge).build();
        return Response.seeOther((URI)this.authorizationRestService.getRedirectUri(httpServletRequest, request)).header("Cache-Control", (Object)"no-store").header("Pragma", (Object)"no-cache").build();
    }
}

