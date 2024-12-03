/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.plugins.rest.common.security.AuthenticationContext
 *  com.atlassian.plugins.rest.common.security.CorsAllowed
 *  com.atlassian.plugins.whitelist.InboundWhitelist
 *  com.atlassian.plugins.whitelist.OutboundWhitelist
 *  com.atlassian.plugins.whitelist.WhitelistService
 *  com.atlassian.plugins.whitelist.applinks.ApplicationLinkRestrictions
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.google.common.collect.ImmutableMap
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.HeaderParam
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.plugins.whitelist.ui;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.plugins.rest.common.security.AuthenticationContext;
import com.atlassian.plugins.rest.common.security.CorsAllowed;
import com.atlassian.plugins.whitelist.InboundWhitelist;
import com.atlassian.plugins.whitelist.OutboundWhitelist;
import com.atlassian.plugins.whitelist.WhitelistService;
import com.atlassian.plugins.whitelist.applinks.ApplicationLinkRestrictions;
import com.atlassian.plugins.whitelist.ui.WhitelistBean;
import com.atlassian.plugins.whitelist.ui.WhitelistBeanService;
import com.atlassian.plugins.whitelist.ui.WhitelistListResponseBean;
import com.atlassian.plugins.whitelist.ui.WhitelistSettingsBean;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.google.common.collect.ImmutableMap;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.regex.PatternSyntaxException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@Path(value="/")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
public class WhitelistResource {
    private final ApplicationLinkRestrictions restrictionsService;
    private final I18nResolver i18nResolver;
    private final InboundWhitelist inboundWhitelist;
    private final OutboundWhitelist outboundWhitelist;
    private final UserManager userManager;
    private final WhitelistBeanService whitelistBeanService;
    private final WhitelistService whitelistService;
    private final EventPublisher eventPublisher;

    public WhitelistResource(WhitelistService whitelistService, I18nResolver i18nResolver, OutboundWhitelist outboundWhitelist, InboundWhitelist inboundWhitelist, WhitelistBeanService whitelistBeanService, ApplicationLinkRestrictions restrictionsService, UserManager userManager, EventPublisher eventPublisher) {
        this.whitelistService = whitelistService;
        this.i18nResolver = i18nResolver;
        this.outboundWhitelist = outboundWhitelist;
        this.inboundWhitelist = inboundWhitelist;
        this.whitelistBeanService = whitelistBeanService;
        this.restrictionsService = restrictionsService;
        this.userManager = userManager;
        this.eventPublisher = eventPublisher;
    }

    @GET
    @WebSudoRequired
    public Response listRules() {
        List<WhitelistBean> rules = this.whitelistBeanService.getAll();
        int pages = 0;
        int totalPages = 0;
        if (!rules.isEmpty()) {
            pages = 1;
            totalPages = 1;
        }
        return Response.ok((Object)new WhitelistListResponseBean(rules, pages, totalPages)).build();
    }

    @Path(value="enable")
    @POST
    @WebSudoRequired
    public Response enable() {
        this.whitelistService.enableWhitelist();
        return Response.ok().build();
    }

    @Path(value="disable")
    @POST
    @WebSudoRequired
    public Response disable() {
        this.whitelistService.disableWhitelist();
        return Response.ok().build();
    }

    @Path(value="check")
    @GET
    @CorsAllowed
    @AnonymousAllowed
    public Response isAllowed(@Context AuthenticationContext authenticationContext, @QueryParam(value="url") String uriString, @HeaderParam(value="X-Requested-With") String requestedWith) {
        this.eventPublisher.publish((Object)new RestEndpointUsageAnalyticsEvent(authenticationContext.isAuthenticated(), requestedWith));
        try {
            URI uri = new URI(uriString);
            return Response.ok((Object)ImmutableMap.of((Object)"outbound", (Object)this.outboundWhitelist.isAllowed(uri, null), (Object)"outboundWithAuth", (Object)this.outboundWhitelist.isAllowed(uri, this.userManager.getRemoteUserKey()), (Object)"inbound", (Object)this.inboundWhitelist.isAllowed(uri))).build();
        }
        catch (URISyntaxException e) {
            return WhitelistResource.error(e.getMessage());
        }
    }

    @POST
    @WebSudoRequired
    public Response create(WhitelistBean whitelistBean) {
        if (whitelistBean.getExpression() == null) {
            return WhitelistResource.fieldError("expression", this.i18nResolver.getText("whitelist.ui.error.noexpression"));
        }
        try {
            WhitelistBean result = this.whitelistBeanService.add(whitelistBean);
            return Response.ok((Object)result).build();
        }
        catch (PatternSyntaxException e) {
            return WhitelistResource.fieldError("expression", e.getMessage());
        }
        catch (IllegalArgumentException e) {
            return WhitelistResource.error(e.getMessage());
        }
    }

    @PUT
    @Path(value="{id}")
    @WebSudoRequired
    public Response update(@PathParam(value="id") int id, WhitelistBean whitelistBean) {
        try {
            WhitelistBean result = this.whitelistBeanService.update(id, whitelistBean);
            return Response.ok((Object)result).build();
        }
        catch (NullPointerException e) {
            return WhitelistResource.error(this.i18nResolver.getText("whitelist.ui.error.deleted"));
        }
        catch (IllegalArgumentException e) {
            return WhitelistResource.error(e.getMessage());
        }
    }

    @DELETE
    @Path(value="{id}")
    @WebSudoRequired
    public Response delete(@PathParam(value="id") int id) {
        try {
            this.whitelistService.remove(id);
        }
        catch (NullPointerException nullPointerException) {
        }
        catch (IllegalArgumentException e) {
            return WhitelistResource.error(e.getMessage());
        }
        return Response.ok().build();
    }

    @GET
    @Path(value="settings")
    @WebSudoRequired
    public Response getSettings() {
        return Response.ok((Object)this.getSettingsBean()).build();
    }

    @PUT
    @Path(value="settings")
    @WebSudoRequired
    public Response putSettings(WhitelistSettingsBean settingsBean) {
        if (settingsBean.getApplicationLinkRestrictiveness() == null) {
            return WhitelistResource.fieldError("applicationLinkRestrictiveness", this.i18nResolver.getText("whitelist.ui.error.noapplinkrestrictiveness"));
        }
        this.restrictionsService.setRestrictiveness(settingsBean.getApplicationLinkRestrictiveness());
        return Response.ok((Object)this.getSettingsBean()).build();
    }

    private WhitelistSettingsBean getSettingsBean() {
        return new WhitelistSettingsBean(this.restrictionsService.getRestrictiveness());
    }

    private static Response fieldError(String field, String message) {
        return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)ImmutableMap.of((Object)"errors", (Object)ImmutableMap.of((Object)field, (Object)message))).build();
    }

    private static Response error(String message) {
        return Response.status((Response.Status)Response.Status.BAD_REQUEST).entity((Object)ImmutableMap.of((Object)"message", (Object)message)).build();
    }

    @EventName(value="jira.rest.anon.endpoint.usage")
    static class RestEndpointUsageAnalyticsEvent {
        private static final String AJAX_ATTRIBUTE = "XMLHttpRequest";
        private final boolean authenticatedAccess;
        private final boolean ajaxRequest;

        RestEndpointUsageAnalyticsEvent(boolean authenticatedAccess, boolean ajaxRequest) {
            this.authenticatedAccess = authenticatedAccess;
            this.ajaxRequest = ajaxRequest;
        }

        RestEndpointUsageAnalyticsEvent(boolean authenticatedAccess, String requestedWith) {
            this(authenticatedAccess, AJAX_ATTRIBUTE.equals(requestedWith));
        }

        public AnalyzedEndpoint getEndpoint() {
            return AnalyzedEndpoint.WHITELIST_CHECK;
        }

        public boolean isAuthenticatedAccess() {
            return this.authenticatedAccess;
        }

        public boolean isAjaxRequest() {
            return this.ajaxRequest;
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || this.getClass() != other.getClass()) {
                return false;
            }
            RestEndpointUsageAnalyticsEvent that = (RestEndpointUsageAnalyticsEvent)other;
            return this.authenticatedAccess == that.authenticatedAccess && this.ajaxRequest == that.ajaxRequest;
        }

        public int hashCode() {
            return Objects.hash(this.authenticatedAccess, this.ajaxRequest);
        }

        static enum AnalyzedEndpoint {
            WHITELIST_CHECK;

        }
    }
}

