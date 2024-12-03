/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.Renderer
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.user.User
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.plugins.viewsource.api.v1;

import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.user.User;
import java.util.HashMap;
import java.util.Objects;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path(value="/")
@Produces(value={"application/json"})
public class ViewSourceResource {
    private final ContentEntityManager contentEntityManager;
    private final PermissionManager permissionManager;
    private final Renderer renderer;

    public ViewSourceResource(ContentEntityManager contentEntityManager, PermissionManager permissionManager, Renderer renderer) {
        this.contentEntityManager = Objects.requireNonNull(contentEntityManager);
        this.permissionManager = Objects.requireNonNull(permissionManager);
        this.renderer = Objects.requireNonNull(renderer);
    }

    @GET
    @Path(value="getPageSrc")
    public Response getPageSource(@QueryParam(value="pageId") long pageId) {
        ContentEntityObject content = this.contentEntityManager.getById(pageId);
        if (content == null) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
        }
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)content)) {
            return Response.status((Response.Status)Response.Status.FORBIDDEN).build();
        }
        HashMap<String, String> result = new HashMap<String, String>();
        result.put("content", this.renderer.render(content));
        return Response.ok().entity(result).build();
    }
}

