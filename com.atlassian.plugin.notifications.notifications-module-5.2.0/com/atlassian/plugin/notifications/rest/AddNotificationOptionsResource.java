/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Either
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.google.common.base.Function
 *  com.google.common.collect.Lists
 *  javax.annotation.Nullable
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.plugin.notifications.rest;

import com.atlassian.fugue.Either;
import com.atlassian.plugin.notifications.api.ErrorCollection;
import com.atlassian.plugin.notifications.api.HandleErrorFunction;
import com.atlassian.plugin.notifications.api.event.EventRepresentation;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.ServerManager;
import com.atlassian.plugin.notifications.api.medium.recipient.ParameterConfig;
import com.atlassian.plugin.notifications.api.medium.recipient.RecipientRepresentation;
import com.atlassian.plugin.notifications.api.notification.FilterConfiguration;
import com.atlassian.plugin.notifications.api.notification.NotificationRepresentation;
import com.atlassian.plugin.notifications.api.notification.NotificationSchemeService;
import com.atlassian.plugin.notifications.spi.NotificationEventProvider;
import com.atlassian.plugin.notifications.spi.NotificationFilterProvider;
import com.atlassian.plugin.notifications.spi.NotificationRecipientProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.beans.factory.annotation.Qualifier;

@Path(value="addoptions")
@Produces(value={"application/json"})
@Consumes(value={"application/json"})
@WebSudoRequired
public class AddNotificationOptionsResource {
    private final UserManager userManager;
    private final ServerManager serverManager;
    private final I18nResolver i18n;
    private final NotificationEventProvider eventProvider;
    private final NotificationRecipientProvider recipientProvider;
    private final NotificationFilterProvider filterProvider;
    private final NotificationSchemeService schemeService;

    public AddNotificationOptionsResource(UserManager userManager, ServerManager serverManager, @Qualifier(value="i18nResolver") I18nResolver i18n, NotificationEventProvider eventProvider, NotificationRecipientProvider recipientProvider, NotificationFilterProvider filterProvider, NotificationSchemeService schemeService) {
        this.userManager = userManager;
        this.serverManager = serverManager;
        this.i18n = i18n;
        this.eventProvider = eventProvider;
        this.recipientProvider = recipientProvider;
        this.filterProvider = filterProvider;
        this.schemeService = schemeService;
    }

    @GET
    @Path(value="{notificationId}")
    public Response getEditOptions(@PathParam(value="notificationId") int notificationId) {
        Either<ErrorCollection, NotificationRepresentation> result = this.schemeService.getSchemeNotification(this.userManager.getRemoteUsername(), notificationId);
        return (Response)result.fold((Function)new HandleErrorFunction(), (Function)new Function<NotificationRepresentation, Response>(){

            public Response apply(@Nullable NotificationRepresentation input) {
                if (input != null) {
                    return AddNotificationOptionsResource.this.getOptions(input.getFilterConfiguration());
                }
                return Response.status((Response.Status)Response.Status.NOT_FOUND).cacheControl(HandleErrorFunction.NO_CACHE).build();
            }
        });
    }

    @GET
    public Response getAddOptions() {
        return this.getOptions(new FilterConfiguration(Collections.emptyList()));
    }

    private Response getOptions(FilterConfiguration filterConfig) {
        if (!this.userManager.isSystemAdmin(this.userManager.getRemoteUsername())) {
            return Response.status((Response.Status)Response.Status.UNAUTHORIZED).cacheControl(HandleErrorFunction.NO_CACHE).build();
        }
        ArrayList servers = Lists.newArrayList();
        for (ServerConfiguration config : this.serverManager.getServers()) {
            if (config.getNotificationMedium() == null || !config.getNotificationMedium().isGroupNotificationSupported()) continue;
            RecipientRepresentation recipient = new RecipientRepresentation(0, false, "server_notification_type", config.getFullName(this.i18n), config.getId(), null, null);
            ParameterConfig.Builder builder = new ParameterConfig.Builder();
            if (config.getNotificationMedium().getKey().equals("smtp")) {
                recipient.setParameterConfig(builder.buildHtml("<input type=\"text\" name=\"group-email\" class=\"text\" value=\"\" />"));
            } else {
                recipient.setParameterConfig(builder.buildAjaxSelect("/rest/notifications/1.0/server/" + config.getId() + "/group"));
            }
            servers.add(recipient);
        }
        ArrayList allEvents = Lists.newArrayList(this.eventProvider.getAllEvents());
        ArrayList recipients = Lists.newArrayList(this.recipientProvider.getAllRecipients());
        Collections.sort(recipients);
        Collections.sort(allEvents);
        Collections.sort(servers);
        return Response.ok((Object)new AddOptions(allEvents, recipients, servers, this.filterProvider.getConfigurationHtml(filterConfig))).cacheControl(HandleErrorFunction.NO_CACHE).build();
    }

    public static class AddOptions {
        @JsonProperty
        private final List<EventRepresentation> events;
        @JsonProperty
        private final List<RecipientRepresentation> recipients;
        @JsonProperty
        private final List<RecipientRepresentation> servers;
        @JsonProperty
        private final String filterHtml;

        @JsonCreator
        public AddOptions(@JsonProperty(value="events") List<EventRepresentation> events, @JsonProperty(value="recipients") List<RecipientRepresentation> recipients, @JsonProperty(value="servers") List<RecipientRepresentation> servers, @JsonProperty(value="filterHtml") String filterHtml) {
            this.events = events;
            this.recipients = recipients;
            this.servers = servers;
            this.filterHtml = filterHtml;
        }
    }
}

