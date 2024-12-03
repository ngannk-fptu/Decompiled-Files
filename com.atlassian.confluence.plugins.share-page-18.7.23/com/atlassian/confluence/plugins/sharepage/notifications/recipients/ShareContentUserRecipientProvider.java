/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.notifications.ConfluenceUserRole
 *  com.atlassian.confluence.notifications.Notification
 *  com.atlassian.confluence.notifications.RecipientsProviderTemplate
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient
 *  com.atlassian.plugin.notifications.api.medium.recipient.UserKeyRoleRecipient
 *  com.atlassian.plugin.notifications.spi.UserRole
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.Group
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Sets
 *  com.google.common.collect.Sets$SetView
 */
package com.atlassian.confluence.plugins.sharepage.notifications.recipients;

import com.atlassian.confluence.notifications.ConfluenceUserRole;
import com.atlassian.confluence.notifications.Notification;
import com.atlassian.confluence.notifications.RecipientsProviderTemplate;
import com.atlassian.confluence.plugins.sharepage.ShareGroupEmailManager;
import com.atlassian.confluence.plugins.sharepage.notifications.payload.ShareContentPayload;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.fugue.Maybe;
import com.atlassian.plugin.notifications.api.medium.recipient.RoleRecipient;
import com.atlassian.plugin.notifications.api.medium.recipient.UserKeyRoleRecipient;
import com.atlassian.plugin.notifications.spi.UserRole;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.Group;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ShareContentUserRecipientProvider
extends RecipientsProviderTemplate<ShareContentPayload> {
    private static final ConfluenceUserRole SHARE_CONTENT_ROLE = new ConfluenceUserRole("SHARE_CONTENT");
    private static final ImmutableList<UserRole> ROLES = ImmutableList.of((Object)SHARE_CONTENT_ROLE);
    private final UserAccessor userAccessor;
    private final ShareGroupEmailManager shareGroupEmailManager;

    public ShareContentUserRecipientProvider(UserAccessor userAccessor, ShareGroupEmailManager shareGroupEmailManager) {
        this.userAccessor = userAccessor;
        this.shareGroupEmailManager = shareGroupEmailManager;
    }

    protected Iterable<RoleRecipient> computeUserBasedRecipients(Notification<ShareContentPayload> notification) {
        Maybe originator = notification.getOriginator();
        Sets.SetView mappedGroups = Sets.intersection(((ShareContentPayload)notification.getPayload()).getGroups(), this.shareGroupEmailManager.getMappedGroupNames());
        Set groups = ((ShareContentPayload)notification.getPayload()).getGroups().stream().map(arg_0 -> ((UserAccessor)this.userAccessor).getGroup(arg_0)).filter(Objects::nonNull).collect(Collectors.toSet());
        Stream<ConfluenceUser> directUsers = ((ShareContentPayload)notification.getPayload()).getUsers().stream().map(arg_0 -> this.lambda$computeUserBasedRecipients$0((Set)mappedGroups, arg_0)).filter(Objects::nonNull);
        Stream usersFromGroups = groups.stream().map(arg_0 -> this.lambda$computeUserBasedRecipients$2((Set)mappedGroups, arg_0)).flatMap(Collection::stream);
        Set allUsers = Stream.concat(directUsers, usersFromGroups).collect(Collectors.toSet());
        return allUsers.stream().map(confluenceUser -> new UserKeyRoleRecipient((UserRole)SHARE_CONTENT_ROLE, confluenceUser.getKey(), originator.isDefined() && ((UserKey)originator.get()).equals((Object)confluenceUser.getKey()))).collect(Collectors.toSet());
    }

    private ConfluenceUser getConfluenceUser(String userKeyOrName) {
        ConfluenceUser user = this.userAccessor.getUserByKey(new UserKey(userKeyOrName));
        if (user == null) {
            user = this.userAccessor.getUserByName(userKeyOrName);
        }
        return user;
    }

    public Iterable<UserRole> getUserRoles() {
        return ROLES;
    }

    private /* synthetic */ Set lambda$computeUserBasedRecipients$2(Set mappedGroups, Group group) {
        if (mappedGroups.contains(group.getName())) {
            return Collections.emptySet();
        }
        return StreamSupport.stream(this.userAccessor.getMembers(group).spliterator(), false).filter(user -> !this.userAccessor.isDeactivated((User)user)).collect(Collectors.toSet());
    }

    private /* synthetic */ ConfluenceUser lambda$computeUserBasedRecipients$0(Set mappedGroups, String keyOrName) {
        HashSet userGroups;
        ConfluenceUser target = this.getConfluenceUser(keyOrName);
        if (target != null && Sets.intersection((Set)(userGroups = Sets.newHashSet((Iterable)this.userAccessor.getGroupNames((User)target))), (Set)mappedGroups).size() > 0) {
            return null;
        }
        return target;
    }
}

