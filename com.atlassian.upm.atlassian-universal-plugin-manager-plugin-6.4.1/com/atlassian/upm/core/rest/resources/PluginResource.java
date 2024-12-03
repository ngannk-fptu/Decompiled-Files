/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
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

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginEnablementService;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.permission.PermissionService;
import com.atlassian.upm.core.rest.PluginRestUninstaller;
import com.atlassian.upm.core.rest.UpmUriEscaper;
import com.atlassian.upm.core.rest.representations.BasePluginRepresentationFactory;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Objects;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

@Path(value="/{pluginKey}")
public class PluginResource {
    private final BasePluginRepresentationFactory representationFactory;
    private final I18nResolver i18nResolver;
    private final PluginEnablementService enabler;
    private final PluginRetriever pluginRetriever;
    private final PermissionEnforcer permissionEnforcer;
    private final PluginRestUninstaller restUninstaller;

    public PluginResource(BasePluginRepresentationFactory representationFactory, I18nResolver i18nResolver, PluginEnablementService enabler, PluginRetriever pluginRetriever, PermissionEnforcer permissionEnforcer, PluginRestUninstaller restUninstaller) {
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.representationFactory = Objects.requireNonNull(representationFactory, "representationFactory");
        this.i18nResolver = Objects.requireNonNull(i18nResolver, "i18nResolver");
        this.enabler = Objects.requireNonNull(enabler, "enabler");
        this.pluginRetriever = Objects.requireNonNull(pluginRetriever, "pluginRetriever");
        this.restUninstaller = Objects.requireNonNull(restUninstaller, "restUninstaller");
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins.plugin+json"})
    public Response get(@PathParam(value="pluginKey") PathSegment pluginKeyPath) {
        this.permissionEnforcer.enforcePermission(Permission.GET_INSTALLED_PLUGINS);
        String pluginKey = UpmUriEscaper.unescape(pluginKeyPath.getPath());
        Iterator<Plugin> iterator = this.pluginRetriever.getPlugin(pluginKey).iterator();
        if (iterator.hasNext()) {
            Plugin plugin = iterator.next();
            return Response.ok((Object)this.representationFactory.createPluginRepresentation(plugin)).build();
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }

    @PUT
    @Consumes(value={"application/vnd.atl.plugins.plugin+json"})
    public Response put(@PathParam(value="pluginKey") PathSegment pluginKeyPath, PluginUpdateRepresentation updateRepresentation) {
        String pluginKey = UpmUriEscaper.unescape(pluginKeyPath.getPath());
        for (Plugin plugin : this.pluginRetriever.getPlugin(pluginKey)) {
            Iterator<Plugin> iterator;
            this.permissionEnforcer.enforcePermission(Permission.MANAGE_PLUGIN_ENABLEMENT, plugin);
            if (plugin.isUpmPlugin()) {
                return Response.status((Response.Status)Response.Status.CONFLICT).entity((Object)this.representationFactory.createI18nErrorRepresentation("upm.plugin.error.invalid.upm.plugin.action")).type("application/vnd.atl.plugins.error+json").build();
            }
            if (this.pluginRetriever.isPluginEnabled(pluginKey) != updateRepresentation.isEnabled().booleanValue()) {
                if (updateRepresentation.isEnabled().booleanValue()) {
                    if (!this.enabler.enablePlugin(pluginKey)) {
                        return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).entity((Object)this.representationFactory.createI18nErrorRepresentation("upm.plugin.error.failed.to.enable")).type("application/vnd.atl.plugins.error+json").build();
                    }
                } else if (!this.enabler.disablePlugin(pluginKey)) {
                    return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).entity((Object)this.representationFactory.createI18nErrorRepresentation("upm.plugin.error.failed.to.disable")).type("application/vnd.atl.plugins.error+json").build();
                }
            }
            if (!(iterator = this.pluginRetriever.getPlugin(pluginKey).iterator()).hasNext()) continue;
            Plugin updatedPlugin = iterator.next();
            return Response.ok((Object)this.representationFactory.createPluginRepresentation(updatedPlugin)).type("application/vnd.atl.plugins.plugin+json").build();
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }

    @DELETE
    public Response uninstallPlugin(@PathParam(value="pluginKey") PathSegment pluginKeyPath) {
        String pluginKey = UpmUriEscaper.unescape(pluginKeyPath.getPath());
        Option<Plugin> maybePlugin = this.pluginRetriever.getPlugin(pluginKey);
        Iterator<Plugin> iterator = maybePlugin.iterator();
        if (iterator.hasNext()) {
            Plugin plugin = iterator.next();
            Iterator<PluginRestUninstaller.UninstallError> iterator2 = this.restUninstaller.uninstall(plugin).iterator();
            if (iterator2.hasNext()) {
                Response.Status status;
                PluginRestUninstaller.UninstallError error = iterator2.next();
                for (PermissionService.PermissionError permError : error.getPermissionError()) {
                    this.permissionEnforcer.handleError(permError);
                }
                if (error.getType() == PluginRestUninstaller.UninstallError.Type.REQUIRES_RESTART) {
                    return Response.status((Response.Status)Response.Status.ACCEPTED).entity((Object)this.representationFactory.createPluginRepresentation(error.getPlugin())).type("application/vnd.atl.plugins.plugin+json").build();
                }
                String code = error.getType().getErrorCode();
                switch (error.getType()) {
                    case PLUGIN_IS_UPM: {
                        status = Response.Status.fromStatusCode((int)405);
                        break;
                    }
                    case SAFE_MODE: {
                        status = Response.Status.CONFLICT;
                        break;
                    }
                    default: {
                        status = Response.Status.FORBIDDEN;
                    }
                }
                return Response.status((Response.Status)status).type("application/vnd.atl.plugins.error+json").entity((Object)this.representationFactory.createErrorRepresentation(this.i18nResolver.getText(code, new Serializable[]{plugin.getName()}), code)).build();
            }
            return Response.noContent().build();
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }

    public static final class PluginUpdateRepresentation {
        @JsonProperty
        private Boolean enabled;

        @JsonCreator
        public PluginUpdateRepresentation(@JsonProperty(value="enabled") Boolean enabled) {
            this.enabled = enabled;
        }

        public Boolean isEnabled() {
            return this.enabled;
        }
    }
}

