/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.sal.api.websudo.WebSudoNotRequired
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.PathSegment
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.joda.time.DateTime
 */
package com.atlassian.upm.request.rest.resources;

import com.atlassian.marketplace.client.model.PaymentModel;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.sal.api.websudo.WebSudoNotRequired;
import com.atlassian.upm.Pairs;
import com.atlassian.upm.analytics.event.PluginRequestCompletedAnalyticsEvent;
import com.atlassian.upm.analytics.event.PluginRequestedAnalyticsEvent;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.analytics.AnalyticsLogger;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.rest.UpmUriEscaper;
import com.atlassian.upm.core.rest.resources.permission.PermissionEnforcer;
import com.atlassian.upm.mail.EmailType;
import com.atlassian.upm.mail.ProductUserLists;
import com.atlassian.upm.mail.UpmMailSenderService;
import com.atlassian.upm.notification.PluginRequestNotificationChecker;
import com.atlassian.upm.request.PluginRequest;
import com.atlassian.upm.request.PluginRequestFactory;
import com.atlassian.upm.request.PluginRequestStore;
import com.atlassian.upm.rest.UpmUriBuilder;
import com.atlassian.upm.rest.representations.UpmRepresentationFactory;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;

@Path(value="/requests")
public class PluginRequestCollectionResource {
    private final PermissionEnforcer permissionEnforcer;
    private final PluginRequestFactory pluginRequestFactory;
    private final PluginRequestStore requestStore;
    private final UpmRepresentationFactory representationFactory;
    private final UserManager userManager;
    private final PluginRequestNotificationChecker notificationChecker;
    private final AnalyticsLogger analytics;
    private final UpmMailSenderService mailSenderService;
    private final UpmUriBuilder uriBuilder;
    private final ProductUserLists userLists;

    public PluginRequestCollectionResource(PermissionEnforcer permissionEnforcer, PluginRequestFactory pluginRequestFactory, PluginRequestNotificationChecker notificationChecker, PluginRequestStore requestManager, UpmRepresentationFactory representationFactory, UserManager userManager, AnalyticsLogger analytics, UpmMailSenderService mailSenderService, UpmUriBuilder uriBuilder, ProductUserLists userLists) {
        this.permissionEnforcer = Objects.requireNonNull(permissionEnforcer, "permissionEnforcer");
        this.pluginRequestFactory = Objects.requireNonNull(pluginRequestFactory, "pluginRequestFactory");
        this.notificationChecker = Objects.requireNonNull(notificationChecker, "notificationChecker");
        this.requestStore = Objects.requireNonNull(requestManager, "requestManager");
        this.representationFactory = Objects.requireNonNull(representationFactory, "representationFactory");
        this.userManager = Objects.requireNonNull(userManager, "userManager");
        this.analytics = Objects.requireNonNull(analytics, "analytics");
        this.mailSenderService = Objects.requireNonNull(mailSenderService, "mailSenderService");
        this.uriBuilder = Objects.requireNonNull(uriBuilder, "uriBuilder");
        this.userLists = Objects.requireNonNull(userLists, "userLists");
    }

    @GET
    @Produces(value={"application/vnd.atl.plugins+json"})
    @WebSudoNotRequired
    public Response getRequests(@DefaultValue(value="10") @QueryParam(value="max-results") Integer maxResults, @DefaultValue(value="0") @QueryParam(value="start-index") Integer startIndex, @DefaultValue(value="false") @QueryParam(value="exclude-user-requests") Boolean excludeUserRequests) {
        Map<String, Collection<PluginRequest>> requests;
        if (excludeUserRequests.booleanValue()) {
            UserKey userKey = this.userManager.getRemoteUserKey();
            requests = this.requestStore.getRequestsByPluginExcludingUser(maxResults, startIndex, userKey);
        } else {
            requests = this.requestStore.getRequestsByPlugin(maxResults, startIndex);
        }
        if (this.permissionEnforcer.hasPermission(Permission.GET_PLUGIN_REQUESTS)) {
            return Response.ok((Object)this.representationFactory.createPluginRequestCollectionRepresentation(requests)).build();
        }
        return Response.ok((Object)this.representationFactory.createAnonymousPluginRequestCollectionRepresentation(requests)).build();
    }

    @POST
    @Consumes(value={"application/vnd.atl.plugins+json"})
    @WebSudoNotRequired
    public Response addRequest(CreatePluginRequestRepresentation createRequest) {
        this.permissionEnforcer.enforcePermission(Permission.CREATE_PLUGIN_REQUEST);
        String pluginKey = createRequest.getPluginKey();
        UserKey userKey = this.userManager.getRemoteUserKey();
        Option<String> optionalMessage = Option.option(createRequest.getMessage());
        if (StringUtils.isBlank((CharSequence)createRequest.getMessage())) {
            optionalMessage = Option.none(String.class);
        }
        boolean isPaidViaAtlassian = PaymentModel.PAID_VIA_ATLASSIAN.toString().equals(createRequest.getMarketplaceType());
        boolean newRequest = !this.requestStore.getRequest(pluginKey, userKey).isDefined();
        this.requestStore.addRequest(this.pluginRequestFactory.getPluginRequest(userKey, pluginKey, createRequest.getPluginName(), new DateTime(), optionalMessage));
        Iterator<PluginRequest> iterator = this.requestStore.getRequest(pluginKey, userKey).iterator();
        if (iterator.hasNext()) {
            PluginRequest storedRequest = iterator.next();
            this.notificationChecker.updatePluginRequestNotifications();
            this.analytics.log(new PluginRequestedAnalyticsEvent(pluginKey, newRequest));
            this.sendNotificationEmail(storedRequest, newRequest ? EmailType.ADDON_REQUESTED : EmailType.ADDON_REQUEST_UPDATED, isPaidViaAtlassian);
            return Response.ok((Object)this.representationFactory.createPluginRequestRepresentation(storedRequest)).type("application/vnd.atl.plugins+json").build();
        }
        return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).entity((Object)this.representationFactory.createErrorRepresentation("upm.plugin.error.failed.to.add.request", pluginKey)).type("application/vnd.atl.plugins.error+json").build();
    }

    @DELETE
    @Path(value="{pluginKey}")
    public Response removeRequests(@PathParam(value="pluginKey") PathSegment pluginKeyPath) {
        this.permissionEnforcer.enforcePermission(Permission.MANAGE_PLUGIN_REQUESTS);
        String pluginKey = UpmUriEscaper.unescape(pluginKeyPath.getPath());
        List<PluginRequest> requests = this.requestStore.getRequests(pluginKey);
        this.requestStore.removeRequests(pluginKey);
        if (!this.requestStore.getRequests(pluginKey).isEmpty()) {
            return Response.status((Response.Status)Response.Status.INTERNAL_SERVER_ERROR).entity((Object)this.representationFactory.createI18nErrorRepresentation("upm.messages.request.dismiss.failure")).type("application/vnd.atl.plugins.error+json").build();
        }
        this.notificationChecker.updatePluginRequestNotifications();
        this.analytics.log(new PluginRequestCompletedAnalyticsEvent(pluginKey, false, requests.size()));
        this.sendDismissedRequestsEmail(requests);
        return Response.ok().build();
    }

    private void sendNotificationEmail(PluginRequest request, EmailType emailType, boolean isPaidViaAtlassian) {
        if (this.mailSenderService.canSendEmail()) {
            this.mailSenderService.sendUpmEmail(emailType, Pairs.ImmutablePair.pair(request.getPluginKey(), request.getPluginName()), this.userLists.getSystemAdmins(), this.getSubjectParams(request.getUser(), request), this.getBodyContext(emailType, request, false, Option.some(isPaidViaAtlassian)));
        }
    }

    private void sendDismissedRequestsEmail(Iterable<PluginRequest> requests) {
        if (this.mailSenderService.canSendEmail()) {
            UserProfile profile = this.userManager.getRemoteUser();
            for (PluginRequest request : requests) {
                this.mailSenderService.sendUpmEmail(EmailType.ADDON_REQUEST_DISMISSED, Pairs.ImmutablePair.pair(request.getPluginKey(), request.getPluginName()), Collections.singleton(request.getUser().getUserKey()), this.getSubjectParams(profile, request), this.getBodyContext(EmailType.ADDON_REQUEST_DISMISSED, request, true, Option.none(Boolean.class)));
            }
        }
    }

    private Map<String, Object> getBodyContext(EmailType emailType, PluginRequest request, boolean isDismissed, Option<Boolean> isPaidViaAtlassian) {
        HashMap<String, Object> context = new HashMap<String, Object>();
        context.put("requestMessage", request.getMessage());
        if (!isDismissed) {
            context.put("singlePluginViewLink", this.uriBuilder.emailUri(this.uriBuilder.buildUpmSinglePluginViewUri(request.getPluginKey(), Option.some("most-requested")), emailType));
        }
        for (Boolean paidViaAtlassian : isPaidViaAtlassian) {
            context.put("isPaidViaAtlassian", paidViaAtlassian);
        }
        return Collections.unmodifiableMap(context);
    }

    private List<String> getSubjectParams(UserProfile sender, PluginRequest request) {
        String fullName = sender.getFullName();
        if (StringUtils.isBlank((CharSequence)fullName)) {
            return Arrays.asList(sender.getUsername(), request.getPluginName());
        }
        return Arrays.asList(fullName, request.getPluginName());
    }

    public static final class CreatePluginRequestRepresentation {
        @JsonProperty
        private final String pluginKey;
        @JsonProperty
        private final String pluginName;
        @JsonProperty
        private final String message;
        @JsonProperty
        private final String marketplaceType;

        @JsonCreator
        public CreatePluginRequestRepresentation(@JsonProperty(value="pluginKey") String pluginKey, @JsonProperty(value="pluginName") String pluginName, @JsonProperty(value="message") String message, @JsonProperty(value="marketplaceType") String marketplaceType) {
            this.pluginKey = Objects.requireNonNull(pluginKey, "pluginKey");
            this.pluginName = Objects.requireNonNull(pluginName, "pluginName");
            this.message = message;
            this.marketplaceType = marketplaceType;
        }

        public String getPluginKey() {
            return this.pluginKey;
        }

        public String getPluginName() {
            return this.pluginName;
        }

        public String getMessage() {
            return this.message;
        }

        public String getMarketplaceType() {
            return this.marketplaceType;
        }
    }
}

