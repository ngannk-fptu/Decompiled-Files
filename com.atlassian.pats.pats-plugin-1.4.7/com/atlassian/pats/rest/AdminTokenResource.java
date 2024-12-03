/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.google.common.collect.Lists
 *  com.sun.jersey.spi.resource.Singleton
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.Path
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
import com.atlassian.pats.rest.PermissionChecker;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.collect.Lists;
import com.sun.jersey.spi.resource.Singleton;
import java.util.Collection;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="/admin/tokens")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Singleton
public class AdminTokenResource {
    private static final Logger logger = LoggerFactory.getLogger(AdminTokenResource.class);
    private final TokenService tokenService;
    private final UserManager userManager;
    private final PermissionChecker permissionChecker;

    public AdminTokenResource(TokenService tokenService, UserManager userManager, PermissionChecker permissionChecker) {
        this.tokenService = tokenService;
        this.userManager = userManager;
        this.permissionChecker = permissionChecker;
    }

    @DELETE
    @PublicApi
    public Response deleteListOfTokens(List<Long> tokenIds) {
        this.permissionChecker.verifyPermissions();
        logger.debug("Deleting tokens with ids {}", tokenIds);
        UserKey remoteUserKey = this.userManager.getRemoteUserKey();
        Lists.partition(tokenIds, (int)100).forEach(batch -> this.tokenService.delete(remoteUserKey, Tables.TOKEN.id.in((Collection<Long>)batch)));
        return Response.status((Response.Status)Response.Status.NO_CONTENT).build();
    }
}

