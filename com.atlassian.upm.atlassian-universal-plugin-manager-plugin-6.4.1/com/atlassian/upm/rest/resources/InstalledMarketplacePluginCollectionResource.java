/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.atlassian.sal.api.user.UserManager
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.rest.resources;

import com.atlassian.marketplace.client.MpacException;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.upm.Iterables;
import com.atlassian.upm.UpmInformation;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.Sys;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.resources.RequestContext;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.core.token.TokenManager;
import com.atlassian.upm.pac.AvailableAddonWithVersion;
import com.atlassian.upm.pac.IncompatiblePluginData;
import com.atlassian.upm.pac.PacClient;
import com.atlassian.upm.rest.representations.UpmRepresentationFactory;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="/installed-marketplace")
public class InstalledMarketplacePluginCollectionResource {
    private final UpmRepresentationFactory representationFactory;
    private final PermissionEnforcer permissionEnforcer;
    private final PluginRetriever pluginRetriever;
    private final LocaleResolver localeResolver;
    private final TokenManager tokenManager;
    private final UserManager userManager;
    private final PacClient pacClient;
    private final UpmInformation upm;
    private static final Logger log = LoggerFactory.getLogger(InstalledMarketplacePluginCollectionResource.class);

    public InstalledMarketplacePluginCollectionResource(UpmRepresentationFactory representationFactory, PermissionEnforcer permissionEnforcer, PluginRetriever pluginRetriever, LocaleResolver localeResolver, TokenManager tokenManager, UserManager userManager, PacClient pacClient, UpmInformation upm) {
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.representationFactory = Objects.requireNonNull(representationFactory, "representationFactory");
        this.pluginRetriever = Objects.requireNonNull(pluginRetriever, "pluginRetriever");
        this.localeResolver = Objects.requireNonNull(localeResolver, "localeResolver");
        this.tokenManager = Objects.requireNonNull(tokenManager, "tokenManager");
        this.userManager = Objects.requireNonNull(userManager, "userManager");
        this.pacClient = Objects.requireNonNull(pacClient, "pacClient");
        this.upm = Objects.requireNonNull(upm, "upm");
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins+json"})
    public Response get(@Context HttpServletRequest request, @QueryParam(value="updates") @DefaultValue(value="false") boolean withUpdates) {
        this.permissionEnforcer.enforcePermission(Permission.GET_INSTALLED_PLUGINS);
        boolean pacUnreachable = !this.pacClient.isPacReachable();
        Collection<Object> updates = Collections.emptyList();
        Collection<IncompatiblePluginData> incompatibles = Collections.emptyList();
        String upmUpdateVersion = null;
        if (withUpdates) {
            try {
                updates = this.pacClient.getUpdates();
                block4: for (AvailableAddonWithVersion availableAddonWithVersion : updates) {
                    if (!this.upm.getPluginKey().equals(availableAddonWithVersion.getAddon().getKey())) continue;
                    for (URI binaryUri : availableAddonWithVersion.getVersion().getArtifactUri()) {
                        if (!this.permissionEnforcer.hasInProcessInstallationFromUriPermission(binaryUri)) continue;
                        upmUpdateVersion = (String)availableAddonWithVersion.getVersion().getName().getOrElse((Object)"");
                        continue block4;
                    }
                }
            }
            catch (MpacException e) {
                log.warn("Failed to get plugin updates: " + e.getMessage());
                log.debug(e.getMessage(), (Throwable)e);
                pacUnreachable = true;
            }
            if (!pacUnreachable && Sys.isIncompatiblePluginCheckEnabled()) {
                try {
                    incompatibles = this.pacClient.getIncompatiblePlugins(Collections.emptyList());
                }
                catch (MpacException e) {
                    log.warn("Failed to get incompatible plugins: " + e.getMessage());
                    log.debug(e.getMessage(), (Throwable)e);
                    pacUnreachable = true;
                }
            }
        }
        List<Plugin> plugins = Iterables.toList(this.pluginRetriever.getPlugins(updates.stream().map(AvailableAddonWithVersion.toAddon()).collect(Collectors.toList())));
        return Response.ok((Object)this.representationFactory.createInstalledMarketplacePluginCollectionRepresentation(this.localeResolver.getLocale(request), plugins, updates, incompatibles, new RequestContext(request).pacUnreachable(pacUnreachable), upmUpdateVersion)).header("upm-token", (Object)this.tokenManager.getTokenForUser(this.userManager.getRemoteUserKey())).build();
    }
}

