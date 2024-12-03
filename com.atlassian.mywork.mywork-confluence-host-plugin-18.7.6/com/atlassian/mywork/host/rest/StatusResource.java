/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mywork.model.Notification
 *  com.atlassian.mywork.rest.CacheControl
 *  com.atlassian.mywork.rest.JsonCount
 *  com.atlassian.mywork.service.LocalNotificationService
 *  com.atlassian.mywork.service.TimeoutService
 *  javax.servlet.http.HttpServletRequest
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.Response
 *  org.codehaus.jackson.JsonNode
 */
package com.atlassian.mywork.host.rest;

import com.atlassian.mywork.host.service.UserService;
import com.atlassian.mywork.model.Notification;
import com.atlassian.mywork.rest.CacheControl;
import com.atlassian.mywork.rest.JsonCount;
import com.atlassian.mywork.service.LocalNotificationService;
import com.atlassian.mywork.service.TimeoutService;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.JsonNode;

@Path(value="status")
@Produces(value={"application/json"})
public class StatusResource {
    private final LocalNotificationService notificationService;
    private final UserService userService;
    private final TimeoutService timeoutService;

    public StatusResource(LocalNotificationService notificationService, UserService userService, TimeoutService timeoutService) {
        this.notificationService = notificationService;
        this.userService = userService;
        this.timeoutService = timeoutService;
    }

    @GET
    public Response get(@Context HttpServletRequest request, @QueryParam(value="pageid") Long pageId) {
        return this.getNewNotificationsCountWithTimeout(request, pageId);
    }

    @GET
    @Path(value="notification/new")
    public Response getNewNotificationsCount(@Context HttpServletRequest request) {
        return Response.ok((Object)this.getCount(request)).cacheControl(CacheControl.never()).build();
    }

    private int getCount(HttpServletRequest request) {
        return this.notificationService.getCount(this.userService.getRemoteUsername(request));
    }

    private void markNotificationsWithPageIdAsRead(HttpServletRequest request, Long pageId) {
        String username = this.userService.getRemoteUsername(request);
        ArrayList<Long> notificationIdsForCurrentPage = new ArrayList<Long>();
        Iterable unread = this.notificationService.findAllUnread(username, "", "com.atlassian.mywork.providers.confluence");
        for (Notification notification : unread) {
            Long notificationPageId;
            JsonNode metadata = notification.getMetadata().get("pageId");
            if (metadata == null || !pageId.equals(notificationPageId = Long.valueOf(metadata.getLongValue()))) continue;
            notificationIdsForCurrentPage.add(notification.getId());
        }
        this.notificationService.setRead(username, notificationIdsForCurrentPage);
    }

    @GET
    @Path(value="notification/count")
    public Response getNewNotificationsCountWithTimeout(@Context HttpServletRequest request, @QueryParam(value="pageid") Long pageId) {
        if (pageId != null) {
            this.markNotificationsWithPageIdAsRead(request, pageId);
        }
        return Response.ok((Object)new JsonCount(this.getCount(request), this.timeoutService.getTimeout(), this.timeoutService.getMaxTimeout())).cacheControl(CacheControl.never()).build();
    }
}

