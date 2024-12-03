/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.service.network.NetworkService
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.confluence.notifications.RenderContextProviderTemplate
 *  com.atlassian.confluence.plugin.descriptor.mail.NotificationContext
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.fugue.Either
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.notifications.api.medium.NotificationAddress
 *  com.atlassian.plugin.notifications.api.medium.ServerConfiguration
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.notifications.content.context;

import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.service.network.NetworkService;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.RenderContextProviderTemplate;
import com.atlassian.confluence.notifications.content.FollowerPayload;
import com.atlassian.confluence.plugin.descriptor.mail.NotificationContext;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.fugue.Either;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.notifications.api.medium.NotificationAddress;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.sal.api.user.UserKey;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FollowerNotificationRenderContextFactory
extends RenderContextProviderTemplate<FollowerPayload> {
    private static final int MAX_FOLLOWERS_TO_FETCH = 20;
    private static final int MAX_FOLLOWERS_TO_DISPLAY = 9;
    private UserAccessor userAccessor;
    private NetworkService networkService;

    public FollowerNotificationRenderContextFactory(UserAccessor userAccessor, NetworkService networkService) {
        this.userAccessor = userAccessor;
        this.networkService = networkService;
    }

    protected Maybe<Map<String, Object>> checkedCreate(Notification<FollowerPayload> notification, ServerConfiguration serverConfiguration, Maybe<Either<NotificationAddress, RoleRecipient>> roleRecipient) {
        NotificationContext context = new NotificationContext();
        FollowerPayload payload = (FollowerPayload)notification.getPayload();
        ConfluenceUser follower = this.userAccessor.getUserByKey(new UserKey(payload.getFollower()));
        ConfluenceUser userBeingFollowed = this.userAccessor.getExistingUserByKey(new UserKey(payload.getUserBeingFollowed()));
        Set followingUsersSet = this.networkService.getFollowing(follower.getKey(), (PageRequest)new SimplePageRequest(0, 20)).getResults().stream().filter(item -> item.getUserKey().exists(predicate -> !predicate.equals((Object)userBeingFollowed.getKey()))).collect(Collectors.toSet());
        Set followingUsers = followingUsersSet.stream().limit(9L).map(item -> this.userAccessor.getUserByName(item.getUsername())).collect(Collectors.toSet());
        context.put("modifier", (Object)follower);
        context.put("followSubject", (Object)userBeingFollowed);
        context.put("listOfFollowing", followingUsers);
        context.put("notificationKey", (Object)notification.getKey());
        context.put("numberOfPeopleFollowing", (Object)followingUsersSet.size());
        return Option.some((Object)context.getMap());
    }
}

