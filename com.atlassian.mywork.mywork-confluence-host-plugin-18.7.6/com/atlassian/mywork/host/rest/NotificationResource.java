/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.mywork.model.Notification
 *  com.atlassian.mywork.model.Status
 *  com.atlassian.mywork.model.Task
 *  com.atlassian.mywork.model.UpdateMetadata
 *  com.atlassian.mywork.rest.CacheControl
 *  com.atlassian.mywork.rest.JsonGroupNotification
 *  com.atlassian.mywork.rest.JsonNotificationGroup
 *  com.atlassian.mywork.service.LocalNotificationService
 *  com.atlassian.plugins.rest.common.security.UnlicensedSiteAccess
 *  com.atlassian.sal.api.user.UserManager
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableListMultimap
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 */
package com.atlassian.mywork.host.rest;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.mywork.host.notification.AggregationUtil;
import com.atlassian.mywork.host.service.LocalClientService;
import com.atlassian.mywork.host.service.NotificationRendererService;
import com.atlassian.mywork.host.service.UserService;
import com.atlassian.mywork.model.Notification;
import com.atlassian.mywork.model.Status;
import com.atlassian.mywork.model.Task;
import com.atlassian.mywork.model.UpdateMetadata;
import com.atlassian.mywork.rest.CacheControl;
import com.atlassian.mywork.rest.JsonGroupNotification;
import com.atlassian.mywork.rest.JsonNotificationGroup;
import com.atlassian.mywork.service.LocalNotificationService;
import com.atlassian.plugins.rest.common.security.UnlicensedSiteAccess;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

@UnlicensedSiteAccess
@Path(value="notification")
@Produces(value={"application/json"})
public class NotificationResource {
    private static final int DEFAULT_LIMIT = 20;
    private final LocalClientService clientService;
    private final LocalNotificationService notificationService;
    private final NotificationRendererService notificationRendererService;
    private final UserManager userManager;
    private final UserService userService;

    public NotificationResource(LocalClientService clientService, LocalNotificationService notificationService, NotificationRendererService notificationRendererService, UserManager userManager, UserService userService) {
        this.clientService = clientService;
        this.notificationService = notificationService;
        this.notificationRendererService = notificationRendererService;
        this.userManager = userManager;
        this.userService = userService;
    }

    @GET
    public Response findByUser(@Context HttpServletRequest request, @QueryParam(value="after") long after, @QueryParam(value="before") long before, @QueryParam(value="limit") int limit, @QueryParam(value="bypass") String bypass) {
        String username = this.userService.getBypassUsername(request, bypass);
        this.clientService.verifyAuth(username);
        return this.notifications(this.findAllAfter(username, after, before, limit));
    }

    private Iterable<Notification> findAllAfter(String username, long after, long before, int limit) {
        return this.notificationService.findAllAfter(username, after, before, limit == 0 ? 20 : limit);
    }

    private Response notifications(Iterable<Notification> notifications) {
        return Response.ok(notifications).cacheControl(CacheControl.never()).build();
    }

    @GET
    @Path(value="nested")
    public Response findByUserNested(@Context HttpServletRequest request, @QueryParam(value="after") long after, @QueryParam(value="before") long before, @QueryParam(value="limit") int limit) {
        String username = this.userManager.getRemoteUsername(request);
        this.clientService.verifyAuth(username);
        Iterable<Notification> notifications = this.findAllAfter(username, after, before, limit);
        return this.aggregateNotifications(this.notificationRendererService.renderDescriptions(notifications));
    }

    private Response aggregateNotifications(Iterable<Notification> allAfter) {
        ImmutableListMultimap<AggregationUtil.AggregateKey, Notification> aggregate = AggregationUtil.aggregate(allAfter);
        ArrayList json = Lists.newArrayList();
        for (final Map.Entry e : aggregate.asMap().entrySet()) {
            Notification notification = (Notification)((Collection)e.getValue()).iterator().next();
            ArrayList notifications = Lists.newArrayList((Iterable)Iterables.transform((Iterable)((Iterable)e.getValue()), (Function)new Function<Notification, JsonGroupNotification>(){

                public JsonGroupNotification apply(Notification from) {
                    return new JsonGroupNotification(((AggregationUtil.AggregateKey)e.getKey()).toString(), from);
                }
            }));
            json.add(new JsonNotificationGroup(notification, (List)notifications, ((AggregationUtil.AggregateKey)e.getKey()).toString()));
        }
        return Response.ok((Object)json).cacheControl(CacheControl.never()).build();
    }

    @GET
    @Path(value="{id}")
    public Response findById(@Context HttpServletRequest request, @PathParam(value="id") long id) {
        String username = this.userManager.getRemoteUsername(request);
        return Response.ok((Object)this.notificationService.find(username, id)).build();
    }

    @POST
    @Consumes(value={"application/json"})
    @XsrfProtectionExcluded
    public Response createOrUpdate(@Context HttpServletRequest request, Notification notification, @QueryParam(value="bypass") String bypass) throws Exception {
        String username = this.userService.getBypassUsername(request, bypass);
        return Response.ok(this.notificationService.createOrUpdate(username, notification).get()).cacheControl(CacheControl.never()).build();
    }

    @DELETE
    @Path(value="{id}")
    @XsrfProtectionExcluded
    public Response delete(@Context HttpServletRequest request, @PathParam(value="id") long id) {
        String username = this.userManager.getRemoteUsername(request);
        this.notificationService.delete(username, id);
        return Response.noContent().build();
    }

    @PUT
    @Path(value="lastreadid")
    @Consumes(value={"application/json"})
    @XsrfProtectionExcluded
    public Response setLastRead(@Context HttpServletRequest request, Long notificationId) {
        String username = this.userManager.getRemoteUsername(request);
        this.notificationService.setLastRead(username, notificationId);
        return Response.ok().build();
    }

    @PUT
    @Path(value="read")
    @Consumes(value={"application/json"})
    @XsrfProtectionExcluded
    public Response setRead(@Context HttpServletRequest request, List<Long> notificationId) {
        String username = this.userManager.getRemoteUsername(request);
        this.notificationService.setRead(username, notificationId);
        return Response.ok().build();
    }

    @PUT
    @Path(value="{id}/status")
    @Consumes(value={"application/json"})
    @XsrfProtectionExcluded
    public Response setStatus(@Context HttpServletRequest request, @PathParam(value="id") long id, Status status) {
        String username = this.userManager.getRemoteUsername(request);
        Task task = this.notificationService.setStatus(username, id, status);
        return Response.ok((Object)task).build();
    }

    @POST
    @Path(value="metadata")
    @Consumes(value={"application/json"})
    @XsrfProtectionExcluded
    public Response updateMetadata(@Context HttpServletRequest request, UpdateMetadata metadata) {
        String username = this.userManager.getRemoteUsername(request);
        this.notificationService.updateMetadata(username, metadata.getGlobalId(), metadata.getCondition(), metadata.getMetadata());
        return Response.noContent().build();
    }
}

