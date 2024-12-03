/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.PathSegment
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.upm.request.rest.resources;

import com.atlassian.sal.api.user.UserKey;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.UpmUriEscaper;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.request.PluginRequest;
import com.atlassian.upm.request.PluginRequestStore;
import com.atlassian.upm.rest.representations.UpmRepresentationFactory;
import java.util.Iterator;
import java.util.Objects;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;

@Path(value="/requests/{pluginKey}/{userKey}")
public class PluginRequestResource {
    private final PermissionEnforcer permissionEnforcer;
    private final PluginRequestStore requestStore;
    private final UpmRepresentationFactory representationFactory;

    public PluginRequestResource(PermissionEnforcer permissionEnforcer, PluginRequestStore requestManager, UpmRepresentationFactory representationFactory) {
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.requestStore = Objects.requireNonNull(requestManager, "requestManager");
        this.representationFactory = Objects.requireNonNull(representationFactory, "representationFactory");
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins+json"})
    public Response getRequest(@PathParam(value="pluginKey") PathSegment pluginKeyPath, @PathParam(value="userKey") String userKey) {
        this.permissionEnforcer.enforcePermission(Permission.GET_PLUGIN_REQUESTS);
        String pluginKey = UpmUriEscaper.unescape(pluginKeyPath.getPath());
        Iterator<PluginRequest> iterator = this.requestStore.getRequest(pluginKey, new UserKey(userKey)).iterator();
        if (iterator.hasNext()) {
            PluginRequest request = iterator.next();
            return Response.ok((Object)this.representationFactory.createPluginRequestRepresentation(request)).build();
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }
}

