/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.user.UserManager
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.core.PathSegment
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.rest.resources;

import com.atlassian.marketplace.client.MpacException;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.upm.MarketplacePlugins;
import com.atlassian.upm.PluginControlHandlerRegistry;
import com.atlassian.upm.PluginUpdateRequestStore;
import com.atlassian.upm.UpmHostApplicationInformation;
import com.atlassian.upm.analytics.event.PluginUpdateRequestEvent;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.DefaultHostApplicationInformation;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.Sys;
import com.atlassian.upm.core.analytics.AnalyticsLogger;
import com.atlassian.upm.core.analytics.SenFinder;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.UpmUriEscaper;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.pac.AvailableAddonWithVersion;
import com.atlassian.upm.pac.PacClient;
import java.util.Iterator;
import java.util.Objects;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path(value="/{pluginKey}/request-update")
public class RequestPluginUpdateResource {
    private static final Logger log = LoggerFactory.getLogger(RequestPluginUpdateResource.class);
    private final PluginRetriever pluginRetriever;
    private final PermissionEnforcer permissionEnforcer;
    private final ApplicationProperties applicationProperties;
    private final AnalyticsLogger analytics;
    private final PluginUpdateRequestStore pluginUpdateRequestStore;
    private final PluginControlHandlerRegistry pluginControlHandlerRegistry;
    private final UpmHostApplicationInformation appInfo;
    private final PacClient pacClient;
    private final UserManager userManager;
    private final DefaultHostApplicationInformation hostApplicationInformation;
    private final SenFinder senFinder;

    public RequestPluginUpdateResource(PluginRetriever pluginRetriever, PermissionEnforcer permissionEnforcer, ApplicationProperties applicationProperties, AnalyticsLogger analytics, PluginUpdateRequestStore pluginUpdateRequestStore, PluginControlHandlerRegistry pluginControlHandlerRegistry, UpmHostApplicationInformation appInfo, PacClient pacClient, UserManager userManager, DefaultHostApplicationInformation hostApplicationInformation, SenFinder senFinder) {
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.pluginRetriever = Objects.requireNonNull(pluginRetriever, "pluginRetriever");
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
        this.analytics = Objects.requireNonNull(analytics, "analytics");
        this.pluginUpdateRequestStore = Objects.requireNonNull(pluginUpdateRequestStore, "pluginUpdateRequestStore");
        this.pluginControlHandlerRegistry = Objects.requireNonNull(pluginControlHandlerRegistry, "pluginControlHandlerRegistry");
        this.appInfo = Objects.requireNonNull(appInfo, "appInfo");
        this.pacClient = Objects.requireNonNull(pacClient, "pacClient");
        this.userManager = Objects.requireNonNull(userManager, "userManager");
        this.hostApplicationInformation = Objects.requireNonNull(hostApplicationInformation, "hostApplicationInformation");
        this.senFinder = Objects.requireNonNull(senFinder, "senFinder");
    }

    @POST
    @Consumes(value={"application/vnd.atl.plugins+json"})
    public Response requestPluginUpdate(@PathParam(value="pluginKey") PathSegment pluginKeyPath, RequestMessageRepresentation message) {
        this.permissionEnforcer.enforcePermission(Permission.REQUEST_PLUGIN_UPDATE);
        String pluginKey = UpmUriEscaper.unescape(pluginKeyPath.getPath());
        Iterator<Object> iterator = this.pluginRetriever.getPlugin(pluginKey).iterator();
        if (iterator.hasNext()) {
            Plugin installedPlugin = iterator.next();
            PluginUpdateRequestEvent.PluginUpdateRequestEventBuilder builder = PluginUpdateRequestEvent.PluginUpdateRequestEventBuilder.builderForInstalledPlugin(installedPlugin, this.pluginControlHandlerRegistry, this.hostApplicationInformation, this.senFinder).applicationProperties(this.applicationProperties).message(Option.option(message.getMessage())).userInitiated(true).dataCenterIncompatible(MarketplacePlugins.isDataCenterIncompatible(installedPlugin, this.appInfo));
            if (message.isShareDetails()) {
                builder = builder.email(Option.option(this.userManager.getRemoteUser().getEmail())).fullName(Option.option(this.userManager.getRemoteUser().getFullName()));
            }
            this.analytics.log(builder.build());
            this.pluginUpdateRequestStore.requestPluginUpdate(installedPlugin);
            return Response.ok().build();
        }
        try {
            iterator = this.pacClient.getAvailablePlugin(pluginKey).iterator();
            if (iterator.hasNext()) {
                AvailableAddonWithVersion a = (AvailableAddonWithVersion)iterator.next();
                PluginUpdateRequestEvent.PluginUpdateRequestEventBuilder builder = PluginUpdateRequestEvent.PluginUpdateRequestEventBuilder.builderForAvailablePlugin(a.getAddon(), this.hostApplicationInformation, this.senFinder).applicationProperties(this.applicationProperties).message(Option.option(message.getMessage())).userInitiated(true).dataCenterIncompatible(MarketplacePlugins.isDataCenterIncompatible(a.getVersion(), this.appInfo));
                if (message.isShareDetails()) {
                    builder = builder.email(Option.option(this.userManager.getRemoteUser().getEmail())).fullName(Option.option(this.userManager.getRemoteUser().getFullName()));
                }
                this.analytics.log(builder.build());
                this.pluginUpdateRequestStore.requestPluginUpdate(a.getAddon());
                return Response.ok().build();
            }
        }
        catch (MpacException e) {
            log.warn("Failed to get available plugin: " + e.getMessage());
            log.debug(e.getMessage(), (Throwable)e);
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }

    @DELETE
    public Response removePluginUpdateRequest(@PathParam(value="pluginKey") PathSegment pluginKeyPath) {
        this.permissionEnforcer.enforcePermission(Permission.MANAGE_IN_PROCESS_PLUGIN_INSTALL_FROM_URI);
        if (!Sys.isUpmDebugModeEnabled()) {
            return Response.status((Response.Status)Response.Status.PRECONDITION_FAILED).build();
        }
        String pluginKey = UpmUriEscaper.unescape(pluginKeyPath.getPath());
        Iterator<Plugin> iterator = this.pluginRetriever.getPlugin(pluginKey).iterator();
        if (iterator.hasNext()) {
            Plugin plugin = iterator.next();
            this.pluginUpdateRequestStore.resetPluginUpdateRequest(plugin);
            return Response.ok().build();
        }
        return Response.status((Response.Status)Response.Status.NOT_FOUND).build();
    }

    public static final class RequestMessageRepresentation {
        @JsonProperty
        private final String pluginKey;
        @JsonProperty
        private final String message;
        @JsonProperty
        private final boolean shareDetails;

        @JsonCreator
        public RequestMessageRepresentation(@JsonProperty(value="pluginKey") String pluginKey, @JsonProperty(value="message") String message, @JsonProperty(value="shareDetails") boolean shareDetails) {
            this.pluginKey = pluginKey;
            this.message = message;
            this.shareDetails = shareDetails;
        }

        public String getMessage() {
            return this.message;
        }

        public String getPluginKey() {
            return this.pluginKey;
        }

        public boolean isShareDetails() {
            return this.shareDetails;
        }
    }
}

