/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.ContentTooLongException
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.permission.PermissionEnforcer
 *  com.atlassian.webhooks.WebhookScope
 *  com.atlassian.webhooks.internal.rest.RestResponseBuilder
 *  com.atlassian.webhooks.internal.rest.RestWebhook
 *  com.atlassian.webhooks.internal.rest.WebhooksResourceHelper
 *  com.google.common.collect.ImmutableSet
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.UriInfo
 */
package com.atlassian.confluence.plugins.restapi.resources;

import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.ContentTooLongException;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.plugins.restapi.resources.OptionalServiceProvider;
import com.atlassian.confluence.plugins.restapi.resources.WebhooksRestResponseBuilder;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import com.atlassian.webhooks.WebhookScope;
import com.atlassian.webhooks.internal.rest.RestResponseBuilder;
import com.atlassian.webhooks.internal.rest.RestWebhook;
import com.atlassian.webhooks.internal.rest.WebhooksResourceHelper;
import com.google.common.collect.ImmutableSet;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@AnonymousAllowed
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@Path(value="/webhooks")
public class GlobalWebhooksResource {
    private static final int ALLOWED_URL_CONTENT_LENGTH = 2000;
    private static final Set<String> ALLOWED_SCHEMES = ImmutableSet.of((Object)"http", (Object)"https");
    private static final Set<String> BLOCKED_HOSTS_REGEX = ImmutableSet.of((Object)"^0.0.0.0$", (Object)"^169.254.\\d{1,3}\\.\\d{1,3}$", (Object)"^127\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$", (Object)"^0:0:0:0:0:0:[a-z0-9]{1,4}:[a-z0-9]{1,4}");
    private static final boolean WEBHOOKS_ALLOW_ALL_HOSTS = Boolean.getBoolean("confluence.webhooks.allow.all.hosts");
    private final PermissionEnforcer permissionEnforcer;
    private final WebhooksResourceHelper webhooksResourceHelper;
    private final I18nResolver i18nResolver;

    public GlobalWebhooksResource(@ComponentImport I18nResolver i18nResolver, @ComponentImport PermissionEnforcer permissionEnforcer, OptionalServiceProvider optionalServiceProvider) {
        this.permissionEnforcer = permissionEnforcer;
        this.i18nResolver = i18nResolver;
        this.webhooksResourceHelper = new WebhooksResourceHelper(i18nResolver, (RestResponseBuilder)new WebhooksRestResponseBuilder(), () -> optionalServiceProvider.getInvocationHistoryService().orElseThrow(NotFoundException::new), () -> optionalServiceProvider.getWebhookService().orElseThrow(NotFoundException::new), scope -> permissionEnforcer.enforceAdmin());
    }

    @POST
    public Response createWebhook(@Context UriInfo uriInfo, RestWebhook webhook) throws ContentTooLongException {
        this.validateUrl(webhook.getUrl());
        return this.webhooksResourceHelper.createWebhook(uriInfo, WebhookScope.GLOBAL, webhook);
    }

    @DELETE
    @Path(value="/{webhookId}")
    public Response deleteWebhook(@PathParam(value="webhookId") int webhookId) {
        return this.webhooksResourceHelper.deleteWebhook(WebhookScope.GLOBAL, webhookId);
    }

    @GET
    public Response findWebhooks(@Context UriInfo uriInfo, @QueryParam(value="event") List<String> events, @QueryParam(value="statistics") @DefaultValue(value="false") boolean statistics, @QueryParam(value="start") int start, @QueryParam(value="limit") @DefaultValue(value="100") int limit) {
        return this.webhooksResourceHelper.findWebhooks(uriInfo, WebhookScope.GLOBAL, events, statistics, start, limit);
    }

    @GET
    @Path(value="/{webhookId}/latest")
    public Response getLatestInvocation(@PathParam(value="webhookId") int webhookId, @QueryParam(value="event") String eventId, @QueryParam(value="outcome") Set<String> outcomes) {
        return this.webhooksResourceHelper.getLatestInvocation(WebhookScope.GLOBAL, webhookId, eventId, outcomes);
    }

    @GET
    @Path(value="/{webhookId}/statistics")
    public Response getStatistics(@PathParam(value="webhookId") int webhookId, @QueryParam(value="event") String eventId) {
        return this.webhooksResourceHelper.getStatistics(WebhookScope.GLOBAL, webhookId, eventId);
    }

    @GET
    @Path(value="/{webhookId}/statistics/summary")
    public Response getStatisticsSummary(@PathParam(value="webhookId") int webhookId) {
        return this.webhooksResourceHelper.getStatisticsSummary(WebhookScope.GLOBAL, webhookId);
    }

    @GET
    @Path(value="/{webhookId}")
    public Response getWebhook(@PathParam(value="webhookId") int webhookId, @QueryParam(value="statistics") @DefaultValue(value="false") boolean statistics) {
        return this.webhooksResourceHelper.getWebhook(WebhookScope.GLOBAL, webhookId, statistics);
    }

    @POST
    @Path(value="/test")
    public Response testWebhook(@QueryParam(value="url") String url) throws ContentTooLongException {
        this.permissionEnforcer.enforceAdmin();
        this.validateUrl(url);
        return this.webhooksResourceHelper.testWebhook(WebhookScope.GLOBAL, url);
    }

    @PUT
    @Path(value="/{webhookId}")
    public Response updateWebhook(@PathParam(value="webhookId") int webhookId, RestWebhook webhook) throws ContentTooLongException {
        this.validateUrl(webhook.getUrl());
        return this.webhooksResourceHelper.update(WebhookScope.GLOBAL, webhookId, webhook);
    }

    private void validateUrl(String webhookUrl) throws ContentTooLongException {
        URI uri;
        if (webhookUrl.length() > 2000) {
            throw new ContentTooLongException(this.i18nResolver.getText("confluence.webhooks.restapi.url.length.error"));
        }
        try {
            uri = new URI(webhookUrl);
        }
        catch (Exception ex) {
            throw new BadRequestException(this.i18nResolver.getText("confluence.webhooks.restapi.url.format.error"));
        }
        if (!ALLOWED_SCHEMES.contains(uri.getScheme())) {
            throw new BadRequestException(this.i18nResolver.getText("confluence.webhooks.restapi.url.scheme.error"));
        }
        if (!WEBHOOKS_ALLOW_ALL_HOSTS) {
            try {
                String host = uri.getHost();
                InetAddress inetAddress = Inet6Address.getByName(host);
                String hostAddress = inetAddress.getHostAddress();
                for (String blockedHostRegex : BLOCKED_HOSTS_REGEX) {
                    if (!hostAddress.matches(blockedHostRegex)) continue;
                    throw new BadRequestException(this.i18nResolver.getText("confluence.webhooks.restapi.url.host.error"));
                }
            }
            catch (UnknownHostException e) {
                throw new BadRequestException(this.i18nResolver.getText("confluence.webhooks.restapi.url.format.error"));
            }
        }
    }
}

