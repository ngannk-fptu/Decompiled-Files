/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoNotRequired
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 */
package com.atlassian.upm.rest.monitor.resources;

import com.atlassian.sal.api.websudo.WebSudoNotRequired;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.permission.PermissionService;
import com.atlassian.upm.core.permission.UserAttributes;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.rest.monitor.representations.MonitorRepresentationFactory;
import com.atlassian.upm.rest.monitor.representations.PluginStateCollectionRep;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path(value="/monitor/installed")
@WebSudoNotRequired
public class InstalledPluginsStateResource {
    private final MonitorRepresentationFactory representationFactory;
    private final PermissionEnforcer permissionEnforcer;
    private final PermissionService permissionService;
    private final PluginRetriever pluginRetriever;

    public InstalledPluginsStateResource(MonitorRepresentationFactory representationFactory, PermissionEnforcer permissionEnforcer, PermissionService permissionService, PluginRetriever pluginRetriever) {
        this.representationFactory = Objects.requireNonNull(representationFactory, "representationFactory");
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.permissionService = Objects.requireNonNull(permissionService, "permissionService");
        this.pluginRetriever = Objects.requireNonNull(pluginRetriever, "pluginRetriever");
    }

    @GET
    @Produces(value={"application/json"})
    public PluginStateCollectionRep get(@QueryParam(value="onlyProblems") boolean onlyProblems) {
        this.permissionEnforcer.enforcePermission(Permission.MONITOR_PLUGINS_STATE);
        List<Plugin> plugins = StreamSupport.stream(this.pluginRetriever.getPlugins().spliterator(), false).filter(this.filterPlugins(onlyProblems)).collect(Collectors.toList());
        return this.representationFactory.createPluginStateCollectionRep(plugins);
    }

    private Predicate<Plugin> filterPlugins(boolean onlyProblems) {
        return onlyProblems ? this.pluginIsDisabledButAdminCannotDisable() : p -> true;
    }

    private Predicate<Plugin> pluginIsDisabledButAdminCannotDisable() {
        return p -> !p.isEnabled() && this.permissionService.getPermissionError(UserAttributes.ADMIN_USER, Permission.MANAGE_PLUGIN_ENABLEMENT, (Plugin)p).isDefined();
    }
}

