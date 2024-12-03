/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.SpaceContentEntityObject
 *  com.atlassian.confluence.mail.notification.NotificationManager
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.user.User
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableMap
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.PathParam
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$ResponseBuilder
 *  javax.ws.rs.core.Response$Status
 */
package com.atlassian.confluence.plugins.watch;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.user.User;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="/watchState")
@Produces(value={"application/json"})
public class WatchStateResource {
    private final NotificationManager notificationManager;
    private final PermissionManager permissionManager;
    private final ContentEntityManager contentEntityManager;

    public WatchStateResource(NotificationManager notificationManager, PermissionManager permissionManager, ContentEntityManager contentEntityManager) {
        this.contentEntityManager = contentEntityManager;
        this.notificationManager = Objects.requireNonNull(notificationManager);
        this.permissionManager = Objects.requireNonNull(permissionManager);
    }

    @GET
    @Path(value="/{contentId}")
    public Response getWatchState(@PathParam(value="contentId") long contentId) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        return Optional.ofNullable(this.contentEntityManager.getById(contentId)).filter(content -> content instanceof SpaceContentEntityObject).filter(arg_0 -> this.lambda$getWatchState$1((User)user, arg_0)).map(content -> (SpaceContentEntityObject)content).map(arg_0 -> this.lambda$getWatchState$3((User)user, arg_0)).orElse(Response.status((Response.Status)Response.Status.NOT_FOUND)).build();
    }

    @VisibleForTesting
    Map<String, Boolean> buildWatchState(User user, SpaceContentEntityObject content) {
        Space space = ((SpaceContentEntityObject)content.getLatestVersion()).getSpace();
        return ImmutableMap.of((Object)"watchingPage", (Object)this.notificationManager.isWatchingContent(user, (ContentEntityObject)content), (Object)"watchingSpace", (Object)(this.notificationManager.getNotificationByUserAndSpace(user, space) != null ? 1 : 0), (Object)"watchingBlogs", (Object)(this.notificationManager.getNotificationByUserAndSpaceAndType(user, space, ContentTypeEnum.BLOG) != null ? 1 : 0), (Object)"isAdmin", (Object)this.permissionManager.hasPermission(user, Permission.ADMINISTER, (Object)space), (Object)"isBlogPost", (Object)(content instanceof BlogPost));
    }

    private /* synthetic */ Response.ResponseBuilder lambda$getWatchState$3(User user, SpaceContentEntityObject content) {
        return Response.ok(this.buildWatchState(user, content));
    }

    private /* synthetic */ boolean lambda$getWatchState$1(User user, ContentEntityObject content) {
        return this.permissionManager.hasPermission(user, Permission.VIEW, (Object)content);
    }
}

