/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.mail.notification.Notification
 *  com.atlassian.confluence.mail.notification.NotificationManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.plugins.rest.common.security.LicensedOnly
 *  com.atlassian.plugins.rest.common.security.RequiresXsrfCheck
 *  com.atlassian.plugins.rest.common.security.SystemAdminOnly
 *  com.atlassian.user.User
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.rest.resources;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugins.rest.common.security.LicensedOnly;
import com.atlassian.plugins.rest.common.security.RequiresXsrfCheck;
import com.atlassian.plugins.rest.common.security.SystemAdminOnly;
import com.atlassian.user.User;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Qualifier;

@LicensedOnly
@Path(value="/content/{contentId}/watches")
@Produces(value={"application/json;charset=UTF-8"})
public class ContentWatchesResource {
    private final NotificationManager notificationManager;
    private final ContentEntityManager contentEntityManager;

    private ContentWatchesResource() {
        this.notificationManager = null;
        this.contentEntityManager = null;
    }

    public ContentWatchesResource(NotificationManager notificationManager, @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager) {
        this.notificationManager = notificationManager;
        this.contentEntityManager = contentEntityManager;
    }

    @SystemAdminOnly
    @GET
    @Produces(value={"application/json"})
    public Response getContentWatches(@PathParam(value="contentId") long contentId) {
        ContentEntityObject content = this.contentEntityManager.getById(contentId);
        if (content == null) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).entity((Object)("No content with contentId " + contentId + " found.")).build();
        }
        List notifications = this.notificationManager.getNotificationsByContent(content);
        return Response.ok(ContentWatchesResource.getResult(notifications)).build();
    }

    public static Map<String, Object> getResult(List<Notification> notifications) {
        LinkedList<Map<String, String>> data = new LinkedList<Map<String, String>>();
        for (Notification notification : notifications) {
            if (notification.getReceiver() == null) continue;
            data.add(Map.of("key", notification.getReceiver().getKey().getStringValue()));
        }
        return Map.of("count", notifications.size(), "data", data);
    }

    @POST
    @Produces(value={"application/json"})
    @RequiresXsrfCheck
    public Response addContentWatch(@PathParam(value="contentId") long contentId) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        ContentEntityObject content = this.contentEntityManager.getById(contentId);
        if (content == null) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).entity((Object)("No content with contentId " + contentId + " found.")).build();
        }
        this.notificationManager.addContentNotification((User)user, content);
        return Response.ok().build();
    }

    @DELETE
    @Produces(value={"application/json"})
    @RequiresXsrfCheck
    public Response removeContentWatch(@PathParam(value="contentId") long contentId) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        ContentEntityObject content = this.contentEntityManager.getById(contentId);
        if (content == null) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).entity((Object)("No content with contentId " + contentId + " found.")).build();
        }
        this.notificationManager.removeContentNotification((User)user, content);
        return Response.ok().build();
    }
}

