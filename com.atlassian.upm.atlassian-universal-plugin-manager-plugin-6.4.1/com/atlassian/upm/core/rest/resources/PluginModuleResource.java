/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginState
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.PathSegment
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.upm.core.rest.resources;

import com.atlassian.plugin.PluginState;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginEnablementService;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.UpmUriEscaper;
import com.atlassian.upm.core.rest.representations.BasePluginRepresentationFactory;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import java.util.Iterator;
import java.util.Objects;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@Path(value="/{pluginKey}/modules/{moduleKey}")
public class PluginModuleResource {
    private final BasePluginRepresentationFactory representationFactory;
    private final PluginEnablementService enabler;
    private final PluginRetriever pluginRetriever;
    private final PermissionEnforcer permissionEnforcer;

    public PluginModuleResource(BasePluginRepresentationFactory representationFactory, PluginEnablementService enabler, PluginRetriever pluginRetriever, PermissionEnforcer permissionEnforcer) {
        this.representationFactory = Objects.requireNonNull(representationFactory, "representationFactory");
        this.enabler = Objects.requireNonNull(enabler, "enabler");
        this.pluginRetriever = Objects.requireNonNull(pluginRetriever, "pluginRetriever");
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins.plugin.module+json"})
    public Response get(@PathParam(value="pluginKey") PathSegment pluginKeyPath, @PathParam(value="moduleKey") String moduleKey) {
        String pluginKey = UpmUriEscaper.unescape(pluginKeyPath.getPath());
        moduleKey = UpmUriEscaper.unescape(moduleKey);
        for (Plugin plugin : this.pluginRetriever.getPlugin(pluginKey)) {
            Iterator<Plugin.Module> iterator = plugin.getModule(moduleKey).iterator();
            if (!iterator.hasNext()) continue;
            Plugin.Module module = iterator.next();
            this.permissionEnforcer.enforcePermission(Permission.GET_PLUGIN_MODULES, module);
            return Response.ok((Object)this.representationFactory.createPluginModuleRepresentation(module)).build();
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Consumes(value={"application/vnd.atl.plugins.plugin.module+json"})
    public Response updateModuleState(@PathParam(value="pluginKey") PathSegment pluginKeyPath, @PathParam(value="moduleKey") String moduleKey, PluginModuleUpdateRepresentation module) {
        String pluginKey = UpmUriEscaper.unescape(pluginKeyPath.getPath());
        moduleKey = UpmUriEscaper.unescape(moduleKey);
        for (Plugin plugin : this.pluginRetriever.getPlugin(pluginKey)) {
            Iterator<Plugin.Module> iterator = plugin.getModule(moduleKey).iterator();
            if (!iterator.hasNext()) continue;
            Plugin.Module pluginModule = iterator.next();
            this.permissionEnforcer.enforcePermission(Permission.MANAGE_PLUGIN_MODULE_ENABLEMENT, pluginModule);
            if (plugin.isUpmPlugin()) {
                return Response.status((Response.Status)Response.Status.CONFLICT).entity((Object)this.representationFactory.createI18nErrorRepresentation("upm.plugin.error.invalid.upm.plugin.action")).type("application/vnd.atl.plugins.error+json").build();
            }
            if (!pluginModule.hasRecognisableType()) {
                return Response.status((Response.Status)Response.Status.PRECONDITION_FAILED).entity((Object)this.representationFactory.createI18nErrorRepresentation("upm.pluginModule.error.cannot.recognise.type")).type("application/vnd.atl.plugins.error+json").build();
            }
            if (module.isEnabled().booleanValue()) {
                if (!plugin.getPluginState().equals((Object)PluginState.ENABLED)) {
                    return Response.status((Response.Status)Response.Status.CONFLICT).entity((Object)this.representationFactory.createI18nErrorRepresentation("upm.pluginModule.error.failed.to.enable")).type("application/vnd.atl.plugins.error+json").build();
                }
                if (!this.enabler.enablePluginModule(pluginModule.getCompleteKey())) {
                    return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).entity((Object)this.representationFactory.createI18nErrorRepresentation("upm.pluginModule.error.failed.to.enable")).type("application/vnd.atl.plugins.error+json").build();
                }
            } else {
                if (!plugin.getPluginState().equals((Object)PluginState.ENABLED)) {
                    return Response.status((Response.Status)Response.Status.CONFLICT).entity((Object)this.representationFactory.createI18nErrorRepresentation("upm.pluginModule.error.failed.to.disable")).type("application/vnd.atl.plugins.error+json").build();
                }
                if (pluginModule.canNotBeDisabled()) {
                    return Response.status((Response.Status)Response.Status.FORBIDDEN).entity((Object)this.representationFactory.createI18nErrorRepresentation("upm.pluginModule.error.cannot.be.disabled")).type("application/vnd.atl.plugins.error+json").build();
                }
                if (!this.enabler.disablePluginModule(pluginModule.getCompleteKey())) {
                    return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).entity((Object)this.representationFactory.createI18nErrorRepresentation("upm.pluginModule.error.failed.to.disable")).type("application/vnd.atl.plugins.error+json").build();
                }
            }
            return Response.ok((Object)this.representationFactory.createPluginModuleRepresentation(pluginModule)).type("application/vnd.atl.plugins.plugin.module+json").build();
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }

    public static final class PluginModuleUpdateRepresentation {
        @JsonProperty
        private Boolean enabled;

        @JsonCreator
        public PluginModuleUpdateRepresentation(@JsonProperty(value="enabled") Boolean enabled) {
            this.enabled = enabled;
        }

        public Boolean isEnabled() {
            return this.enabled;
        }
    }
}

