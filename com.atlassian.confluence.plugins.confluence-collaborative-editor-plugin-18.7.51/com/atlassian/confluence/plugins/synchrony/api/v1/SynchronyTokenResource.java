/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.user.User
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.synchrony.api.v1;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.synchrony.api.v1.model.SynchronyWebToken;
import com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager;
import com.atlassian.confluence.plugins.synchrony.model.SynchronyError;
import com.atlassian.confluence.plugins.synchrony.service.SynchronyJsonWebTokenGenerator;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.user.User;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="/token")
@Produces(value={"application/json"})
public class SynchronyTokenResource {
    private static final Logger log = LoggerFactory.getLogger(SynchronyTokenResource.class);
    private final SynchronyConfigurationManager synchronyConfigurationManager;
    private final SynchronyJsonWebTokenGenerator synchronyJsonWebTokenGenerator;
    private final PageManager pageManager;
    private final PermissionManager permissionManager;

    public SynchronyTokenResource(SynchronyConfigurationManager synchronyConfigurationManager, SynchronyJsonWebTokenGenerator synchronyJsonWebTokenGenerator, @ComponentImport PageManager pageManager, @ComponentImport PermissionManager permissionManager) {
        this.synchronyConfigurationManager = synchronyConfigurationManager;
        this.synchronyJsonWebTokenGenerator = synchronyJsonWebTokenGenerator;
        this.pageManager = pageManager;
        this.permissionManager = permissionManager;
    }

    @GET
    @Path(value="/{contentId}/generate")
    @AnonymousAllowed
    @Produces(value={"application/json"})
    public Response generateToken(@PathParam(value="contentId") Long contentId, @QueryParam(value="errorType") String errorType) {
        SynchronyWebToken response;
        boolean success;
        AbstractPage page = this.pageManager.getAbstractPage(contentId.longValue());
        if (page == null) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (!this.permissionManager.hasPermission((User)user, Permission.EDIT, (Object)page)) {
            return Response.status((Response.Status)Response.Status.UNAUTHORIZED).build();
        }
        if (SynchronyError.Code.JWT_DECRYPTION_FAILED.getSynchronyValue().equals(errorType) && !(success = this.synchronyConfigurationManager.retrievePublicKey())) {
            return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        try {
            response = new SynchronyWebToken(this.synchronyJsonWebTokenGenerator.create(contentId, user), Long.toString(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + SynchronyJsonWebTokenGenerator.TOKEN_EXPIRY_TIME - SynchronyJsonWebTokenGenerator.TOKEN_EXPIRY_LEEWAY), contentId);
        }
        catch (Exception e) {
            log.error("{}", (Object)e.getMessage());
            return Response.serverError().build();
        }
        return Response.ok().entity((Object)response).build();
    }
}

