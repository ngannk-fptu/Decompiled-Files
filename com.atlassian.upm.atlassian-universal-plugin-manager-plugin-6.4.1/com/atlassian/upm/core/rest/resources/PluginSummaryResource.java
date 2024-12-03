/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.PathSegment
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.upm.core.rest.resources;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.impl.UpmAppManager;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.UpmUriEscaper;
import com.atlassian.upm.core.rest.representations.BasePluginRepresentationFactory;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import java.util.Iterator;
import java.util.Objects;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;

@Path(value="/{pluginKey}/summary")
public class PluginSummaryResource {
    private final BasePluginRepresentationFactory representationFactory;
    private final PluginRetriever pluginRetriever;
    private final PermissionEnforcer permissionEnforcer;

    public PluginSummaryResource(BasePluginRepresentationFactory representationFactory, PluginRetriever pluginRetriever, PermissionEnforcer permissionEnforcer) {
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.representationFactory = Objects.requireNonNull(representationFactory, "representationFactory");
        this.pluginRetriever = Objects.requireNonNull(pluginRetriever, "pluginRetriever");
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins+json"})
    public Response get(@PathParam(value="pluginKey") PathSegment pluginKeyPath) {
        this.permissionEnforcer.enforcePermission(Permission.GET_INSTALLED_PLUGINS);
        String pluginKey = UpmUriEscaper.unescape(pluginKeyPath.getPath());
        Iterator<Plugin> iterator = this.pluginRetriever.getPlugin(pluginKey).iterator();
        if (iterator.hasNext()) {
            Plugin plugin = iterator.next();
            return Response.ok((Object)this.representationFactory.createPluginSummaryRepresentation(plugin, Option.none(UpmAppManager.ApplicationDescriptorModuleInfo.class))).build();
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }
}

