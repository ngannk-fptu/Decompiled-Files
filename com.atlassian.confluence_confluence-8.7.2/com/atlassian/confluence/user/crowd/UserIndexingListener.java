/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.DirectoryType
 *  com.atlassian.crowd.event.group.GroupMembershipCreatedEvent
 *  com.atlassian.crowd.event.group.GroupMembershipDeletedEvent
 *  com.atlassian.crowd.event.user.UserCreatedFromDirectorySynchronisationEvent
 *  com.atlassian.crowd.event.user.UserDeletedEvent
 *  com.atlassian.crowd.event.user.UserEditedEvent
 *  com.atlassian.crowd.model.membership.MembershipType
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.user.EntityException
 *  com.atlassian.user.User
 *  com.atlassian.user.impl.DefaultUser
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user.crowd;

import com.atlassian.confluence.event.events.permission.GlobalPermissionChangeEvent;
import com.atlassian.confluence.event.events.user.UserProfilePictureUpdateEvent;
import com.atlassian.confluence.internal.user.UserIndexingManagerInternal;
import com.atlassian.confluence.search.ConfluenceIndexer;
import com.atlassian.confluence.security.SpacePermission;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.user.PersonalInformationManager;
import com.atlassian.confluence.user.actions.ProfilePictureInfo;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.event.group.GroupMembershipCreatedEvent;
import com.atlassian.crowd.event.group.GroupMembershipDeletedEvent;
import com.atlassian.crowd.event.user.UserCreatedFromDirectorySynchronisationEvent;
import com.atlassian.crowd.event.user.UserDeletedEvent;
import com.atlassian.crowd.event.user.UserEditedEvent;
import com.atlassian.crowd.model.membership.MembershipType;
import com.atlassian.event.api.EventListener;
import com.atlassian.user.EntityException;
import com.atlassian.user.User;
import com.atlassian.user.impl.DefaultUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class UserIndexingListener {
    private static final Logger log = LoggerFactory.getLogger(UserIndexingListener.class);
    private final PersonalInformationManager personalInformationManager;
    private final UserIndexingManagerInternal userIndexingManager;
    private final ConfluenceIndexer indexer;

    public UserIndexingListener(PersonalInformationManager personalInformationManager, UserIndexingManagerInternal userIndexingManager, ConfluenceIndexer indexer) {
        this.personalInformationManager = personalInformationManager;
        this.userIndexingManager = userIndexingManager;
        this.indexer = indexer;
    }

    @EventListener
    public void onUserAddEvent(UserCreatedFromDirectorySynchronisationEvent event) {
        if (!this.processEvents()) {
            return;
        }
        com.atlassian.crowd.model.user.User crowdUser = event.getUser();
        log.debug("User added: {}", (Object)crowdUser.getName());
        this.ensurePersonalInformationAndReindex((User)this.convert(crowdUser));
    }

    @EventListener
    public void onMembershipCreatedEvent(GroupMembershipCreatedEvent event) {
        if (!this.processEvents()) {
            return;
        }
        String entityName = event.getEntityName();
        MembershipType membershipType = event.getMembershipType();
        log.debug("{} ({}) added to {}", new Object[]{entityName, membershipType, event.getGroupName()});
        this.reindexEntity(entityName, membershipType);
    }

    @EventListener
    public void onMembershipDeletedEvent(GroupMembershipDeletedEvent event) {
        if (!this.processEvents()) {
            return;
        }
        String entityName = event.getEntityName();
        MembershipType membershipType = event.getMembershipType();
        log.debug("{} ({}) removed from {}", new Object[]{entityName, membershipType, event.getGroupName()});
        this.reindexEntity(entityName, membershipType);
    }

    @EventListener
    public void onGlobalPermissionChangeEvent(GlobalPermissionChangeEvent event) {
        if (!this.processEvents()) {
            return;
        }
        SpacePermission permission = event.getPermission();
        ConfluenceUser userSubject = permission.getUserSubject();
        if (!this.isCanUsePermission(permission)) {
            log.debug("Global permission saved {}", (Object)permission);
        } else if (permission.isGroupPermission()) {
            log.debug("Group permission saved {}", (Object)permission);
            this.indexer.reindexUsersInGroup(permission.getGroup());
        } else if (userSubject != null) {
            log.debug("User permission saved {}", (Object)permission);
            this.ensurePersonalInformationAndReindex(userSubject);
        } else {
            log.debug("Anonymous permission saved {}", (Object)permission);
        }
    }

    private boolean isCanUsePermission(SpacePermission permission) {
        return permission.getSpace() == null && permission.getType().equals("USECONFLUENCE");
    }

    private void reindexEntity(String entityName, MembershipType membershipType) {
        switch (membershipType) {
            case GROUP_GROUP: {
                this.indexer.reindexUsersInGroup(entityName);
                break;
            }
            case GROUP_USER: {
                DefaultUser user = new DefaultUser(entityName);
                this.ensurePersonalInformationAndReindex((User)user);
                break;
            }
            default: {
                throw new IllegalStateException("Unhandled membership type " + membershipType);
            }
        }
    }

    @EventListener
    public void onUserProfilePictureUpdateEvent(UserProfilePictureUpdateEvent event) {
        if (!this.processEvents()) {
            return;
        }
        User user = event.getUser();
        ProfilePictureInfo profilePictureInfo = event.getProfilePictureInfo();
        if (profilePictureInfo != null) {
            log.debug("User profile picture for user: {} updated to: {}", (Object)user.getName(), (Object)profilePictureInfo.getFileName());
        } else {
            log.debug("User profile picture for user: {} deleted", (Object)user.getName());
        }
        this.ensurePersonalInformationAndReindex(user);
    }

    @EventListener
    public void onCrowdUserUpdatedEvent(UserEditedEvent event) throws EntityException {
        if (!this.processEvents()) {
            return;
        }
        com.atlassian.crowd.model.user.User crowdUser = event.getUser();
        log.debug("User Updated: {}", (Object)crowdUser.getName());
        this.ensurePersonalInformationAndReindex((User)this.convert(crowdUser));
    }

    private DefaultUser convert(com.atlassian.crowd.model.user.User user) {
        if (user == null) {
            return null;
        }
        return new DefaultUser(user.getName());
    }

    @EventListener
    public void onUserDeleteEvent(UserDeletedEvent event) {
        if (!this.processEvents()) {
            return;
        }
        if (event.getDirectory().getType() == DirectoryType.INTERNAL) {
            return;
        }
        String username = event.getUsername();
        log.debug("User deleted: {}", (Object)username);
        if (this.personalInformationManager.hasPersonalInformation(username)) {
            DefaultUser user = new DefaultUser(username);
            this.ensurePersonalInformationAndReindex((User)user);
        }
    }

    private void ensurePersonalInformationAndReindex(User user) {
        boolean alreadyExists = this.personalInformationManager.hasPersonalInformation(user.getName());
        PersonalInformation personalInformation = this.personalInformationManager.getOrCreatePersonalInformation(user);
        if (alreadyExists) {
            this.indexer.reIndex(personalInformation);
        }
    }

    private boolean processEvents() {
        return this.userIndexingManager.shouldProcessEvents();
    }
}

