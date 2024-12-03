/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.rest.resources;

import com.atlassian.marketplace.client.MpacException;
import com.atlassian.upm.UpmHostApplicationInformation;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.Sys;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.pac.IncompatiblePluginData;
import com.atlassian.upm.pac.PacClient;
import com.atlassian.upm.rest.representations.UpmRepresentationFactory;
import java.util.Iterator;
import java.util.Objects;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractInstalledMarketplacePluginResource {
    private static final Logger log = LoggerFactory.getLogger(AbstractInstalledMarketplacePluginResource.class);
    private final UpmRepresentationFactory representationFactory;
    private final PluginRetriever pluginRetriever;
    private final PermissionEnforcer permissionEnforcer;
    private final PacClient pacClient;
    private final UpmHostApplicationInformation appInfo;

    protected AbstractInstalledMarketplacePluginResource(UpmRepresentationFactory representationFactory, PluginRetriever pluginRetriever, PermissionEnforcer permissionEnforcer, PacClient pacClient, UpmHostApplicationInformation appInfo) {
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.representationFactory = Objects.requireNonNull(representationFactory, "representationFactory");
        this.pluginRetriever = Objects.requireNonNull(pluginRetriever, "pluginRetriever");
        this.pacClient = Objects.requireNonNull(pacClient, "pacClient");
        this.appInfo = Objects.requireNonNull(appInfo, "appInfo");
    }

    protected Response getInternal(String pluginKey, boolean withUpdate) {
        this.permissionEnforcer.enforcePermission(Permission.GET_INSTALLED_PLUGINS);
        Iterator<Plugin> iterator = this.pluginRetriever.getPlugin(pluginKey).iterator();
        if (iterator.hasNext()) {
            Plugin plugin = iterator.next();
            Option availableUpdate = Option.none();
            Option<IncompatiblePluginData> incompatible = Option.none();
            if (withUpdate) {
                try {
                    availableUpdate = this.pacClient.getUpdate(plugin).map(Option::some).orElseGet(Option::none);
                }
                catch (MpacException e) {
                    log.error("Error looking for available update for " + pluginKey, (Throwable)e);
                }
                if (Sys.isIncompatiblePluginCheckEnabled()) {
                    try {
                        incompatible = this.pacClient.getPluginIncompatibility(plugin);
                    }
                    catch (MpacException e) {
                        log.error("Error checking compatibility for " + pluginKey, (Throwable)e);
                    }
                }
            }
            return Response.ok((Object)this.representationFactory.createInstalledMarketplacePluginRepresentation(plugin, availableUpdate, incompatible)).build();
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }
}

