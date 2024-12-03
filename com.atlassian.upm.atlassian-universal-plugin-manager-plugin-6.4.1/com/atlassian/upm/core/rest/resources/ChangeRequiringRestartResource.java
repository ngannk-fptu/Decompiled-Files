/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.core.PathSegment
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.upm.core.rest.resources;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Change;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginRestartRequiredService;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.Plugins;
import com.atlassian.upm.core.rest.UpmUriEscaper;
import com.atlassian.upm.core.rest.representations.BaseRepresentationFactory;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import java.util.Objects;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;

@Path(value="/requires-restart/{plugin-key}")
public class ChangeRequiringRestartResource {
    private final PermissionEnforcer permissionEnforcer;
    private final PluginRetriever pluginRetriever;
    private final PluginRestartRequiredService restartRequiredService;
    private final BaseRepresentationFactory representationFactory;

    public ChangeRequiringRestartResource(PermissionEnforcer permissionEnforcer, PluginRetriever pluginRetriever, PluginRestartRequiredService restartRequiredService, BaseRepresentationFactory representationFactory) {
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.pluginRetriever = Objects.requireNonNull(pluginRetriever, "pluginRetriever");
        this.restartRequiredService = Objects.requireNonNull(restartRequiredService, "restartRequiredService");
        this.representationFactory = Objects.requireNonNull(representationFactory, "representationFactory");
    }

    @DELETE
    public Response delete(@PathParam(value="plugin-key") PathSegment keyPath) {
        String key = UpmUriEscaper.unescape(keyPath.getPath());
        try {
            Option<Plugin> plugin = this.pluginRetriever.getPlugin(key);
            if (!((Boolean)plugin.map(Plugins::hasRestartRequiredChange).getOrElse(true)).booleanValue()) {
                return Response.status((Response.Status)Response.Status.NOT_FOUND).entity((Object)this.representationFactory.createI18nErrorRepresentation("upm.messages.requiresRestart.no.such.plugin")).type("application/vnd.atl.plugins.error+json").build();
            }
            for (Plugin p : plugin) {
                for (Change change : this.restartRequiredService.getRestartRequiredChange(p)) {
                    this.permissionEnforcer.enforcePermission(change.getRequiredPermission(), p);
                    this.restartRequiredService.revertRestartRequiredChange(p);
                }
            }
        }
        catch (Exception e) {
            return Response.serverError().entity((Object)this.representationFactory.createErrorRepresentation(e.getMessage())).type("application/vnd.atl.plugins.error+json").build();
        }
        return Response.noContent().build();
    }
}

