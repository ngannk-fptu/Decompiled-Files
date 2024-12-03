/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.labels.LabelManager
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
 */
package com.atlassian.confluence.plugins.rest.resources;

import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.plugins.rest.resources.ContentWatchesResource;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugins.rest.common.security.LicensedOnly;
import com.atlassian.plugins.rest.common.security.RequiresXsrfCheck;
import com.atlassian.plugins.rest.common.security.SystemAdminOnly;
import com.atlassian.user.User;
import java.util.List;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@LicensedOnly
@Path(value="/label/{labelId}/watches")
@Produces(value={"application/json;charset=UTF-8"})
public class LabelWatchesResource {
    private final NotificationManager notificationManager;
    private final LabelManager labelManager;

    private LabelWatchesResource() {
        this.notificationManager = null;
        this.labelManager = null;
    }

    public LabelWatchesResource(NotificationManager notificationManager, LabelManager labelManager) {
        this.notificationManager = notificationManager;
        this.labelManager = labelManager;
    }

    @SystemAdminOnly
    @GET
    @Produces(value={"application/json"})
    public Response getLabelWatches(@PathParam(value="labelId") long labelId) {
        Label label = this.labelManager.getLabel(labelId);
        if (label == null) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).entity((Object)("No label with labelId " + labelId + " found.")).build();
        }
        List notifications = this.notificationManager.getNotificationsByLabel(label);
        return Response.ok(ContentWatchesResource.getResult(notifications)).build();
    }

    @POST
    @Produces(value={"application/json"})
    @RequiresXsrfCheck
    public Response addLabelWatch(@PathParam(value="labelId") long labelId) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        Label label = this.labelManager.getLabel(labelId);
        if (label == null) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).entity((Object)("No label with labelId " + labelId + " found.")).build();
        }
        this.notificationManager.addLabelNotification((User)user, label);
        return Response.ok().build();
    }

    @DELETE
    @Produces(value={"application/json"})
    @RequiresXsrfCheck
    public Response removeLabelWatch(@PathParam(value="labelId") long labelId) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        Label label = this.labelManager.getLabel(labelId);
        if (label == null) {
            return Response.status((Response.Status)Response.Status.NOT_FOUND).entity((Object)("No label with labelId " + labelId + " found.")).build();
        }
        this.notificationManager.removeLabelNotification((User)user, label);
        return Response.ok().build();
    }
}

