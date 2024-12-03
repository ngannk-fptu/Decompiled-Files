/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditEvent$Builder
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.event.directory.DirectoryCreatedEvent
 *  com.atlassian.crowd.event.directory.DirectoryDeletedEvent
 *  com.atlassian.crowd.event.directory.DirectoryUpdatedEvent
 *  com.atlassian.crowd.event.group.GroupCreatedEvent
 *  com.atlassian.crowd.event.group.GroupDeletedEvent
 *  com.atlassian.crowd.event.group.GroupMembershipCreatedEvent
 *  com.atlassian.crowd.event.group.GroupMembershipDeletedEvent
 *  com.atlassian.crowd.event.user.ResetPasswordEvent
 *  com.atlassian.crowd.event.user.UserCreatedEvent
 *  com.atlassian.crowd.event.user.UserDeletedEvent
 *  com.atlassian.crowd.event.user.UserEditedEvent
 *  com.atlassian.event.api.EventListener
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.audit.listener;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import com.atlassian.confluence.audit.AuditingContext;
import com.atlassian.confluence.audit.StandardAuditResourceTypes;
import com.atlassian.confluence.event.events.security.ChangePasswordEvent;
import com.atlassian.confluence.event.events.user.DirectoryUserRenamedEvent;
import com.atlassian.confluence.impl.audit.AuditCategories;
import com.atlassian.confluence.impl.audit.AuditHelper;
import com.atlassian.confluence.impl.audit.handler.AuditAction;
import com.atlassian.confluence.impl.audit.handler.AuditHandlerService;
import com.atlassian.confluence.impl.audit.listener.AbstractAuditListener;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.event.directory.DirectoryCreatedEvent;
import com.atlassian.crowd.event.directory.DirectoryDeletedEvent;
import com.atlassian.crowd.event.directory.DirectoryUpdatedEvent;
import com.atlassian.crowd.event.group.GroupCreatedEvent;
import com.atlassian.crowd.event.group.GroupDeletedEvent;
import com.atlassian.crowd.event.group.GroupMembershipCreatedEvent;
import com.atlassian.crowd.event.group.GroupMembershipDeletedEvent;
import com.atlassian.crowd.event.user.ResetPasswordEvent;
import com.atlassian.crowd.event.user.UserCreatedEvent;
import com.atlassian.crowd.event.user.UserDeletedEvent;
import com.atlassian.crowd.event.user.UserEditedEvent;
import com.atlassian.event.api.EventListener;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.Nullable;

public class UserManagementAuditListener
extends AbstractAuditListener {
    public static final String USER_CREATED_SUMMARY = AuditHelper.buildSummaryTextKey("user.created");
    public static final String USER_REMOVED_SUMMARY = AuditHelper.buildSummaryTextKey("user.deleted");
    public static final String USER_RENAMED_SUMMARY = AuditHelper.buildSummaryTextKey("user.renamed");
    public static final String USER_INFORMATION_UPDATE_SUMMARY = AuditHelper.buildSummaryTextKey("user.updated");
    public static final String GROUP_CREATED_SUMMARY = AuditHelper.buildSummaryTextKey("group.created");
    public static final String GROUP_REMOVED_SUMMARY = AuditHelper.buildSummaryTextKey("group.deleted");
    public static final String GROUP_MEMBERSHIP_ADDED_SUMMARY = AuditHelper.buildSummaryTextKey("group.membership.added");
    public static final String GROUP_MEMBERSHIP_REMOVED_SUMMARY = AuditHelper.buildSummaryTextKey("group.membership.removed");
    public static final String PASSWORD_RESET_SUMMARY = AuditHelper.buildSummaryTextKey("user.password.reset");
    public static final String PASSWORD_CHANGE_SUMMARY = AuditHelper.buildSummaryTextKey("user.password.changed");
    public static final String USER_DIRECTORY_CREATED = AuditHelper.buildSummaryTextKey("directory.added");
    public static final String USER_DIRECTORY_DELETED = AuditHelper.buildSummaryTextKey("directory.deleted");
    public static final String USER_DIRECTORY_UPDATED = AuditHelper.buildSummaryTextKey("directory.updated");

    public UserManagementAuditListener(AuditHandlerService auditHandlerService, AuditService service, AuditHelper auditHelper, StandardAuditResourceTypes resourceTypes, AuditingContext auditingContext) {
        super(auditHandlerService, service, auditHelper, resourceTypes, auditingContext);
    }

    @EventListener
    public void userCreatedFromCrowdEvent(UserCreatedEvent event) {
        this.save(() -> this.getBaseUserBuilder(this.auditHelper.fetchUserKey(event.getUser()), event.getUser().getDisplayName(), USER_CREATED_SUMMARY).changedValues(this.getAuditHandlerService().handle(event.getUser(), AuditAction.ADD)).build());
    }

    @EventListener
    public void userDeletedFromCrowdEvent(UserDeletedEvent event) {
        this.save(() -> this.getDeletedUserBuilder(event.getUsername()).build());
    }

    @EventListener
    public void directoryUserRenamedEvent(DirectoryUserRenamedEvent event) {
        this.save(() -> this.getBaseUserBuilder(this.auditHelper.fetchUserKey(event.getUser()), event.getUser().getDisplayName(), USER_RENAMED_SUMMARY).changedValue(this.newChangedValue("username", event.getOldUsername(), event.getUser().getName())).build());
    }

    @EventListener
    public void userEditedEvent(UserEditedEvent event) {
        this.save(() -> this.getBaseUserBuilder(this.auditHelper.fetchUserKey(event.getUser()), event.getUser().getDisplayName(), USER_INFORMATION_UPDATE_SUMMARY).changedValues(this.getAuditHandlerService().handle(event.getOriginalUser(), event.getUser())).build());
    }

    @EventListener
    public void groupCreateEvent(GroupCreatedEvent event) {
        this.save(() -> this.getBaseGroupBuilder(event.getGroup().getName(), GROUP_CREATED_SUMMARY).build());
    }

    @EventListener
    public void groupDeleteEvent(GroupDeletedEvent event) {
        this.save(() -> this.getBaseGroupBuilder(event.getGroupName(), GROUP_REMOVED_SUMMARY).build());
    }

    @EventListener
    public void groupMembershipCreatedEvent(GroupMembershipCreatedEvent event) {
        this.save(() -> this.getBaseGroupBuilder(event.getGroupName(), GROUP_MEMBERSHIP_ADDED_SUMMARY).affectedObject(this.buildResource(event.getEntityName(), this.resourceTypes.user(), this.auditHelper.fetchUserKey(event.getEntityName()))).build());
    }

    @EventListener
    public void groupMembershipDeletedEvent(GroupMembershipDeletedEvent event) {
        this.save(() -> this.getBaseGroupBuilder(event.getGroupName(), GROUP_MEMBERSHIP_REMOVED_SUMMARY).affectedObject(this.buildResource(event.getEntityName(), this.resourceTypes.user(), this.auditHelper.fetchUserKey(event.getEntityName()))).build());
    }

    @EventListener
    public void resetPasswordEvent(ResetPasswordEvent event) {
        this.save(() -> AuditEvent.fromI18nKeys((String)AuditCategories.USER_MANAGEMENT, (String)PASSWORD_RESET_SUMMARY, (CoverageLevel)CoverageLevel.BASE, (CoverageArea)CoverageArea.USER_MANAGEMENT).affectedObject(this.buildResource(event.getUser().getName(), this.resourceTypes.user(), this.auditHelper.fetchUserKey(event.getUser()))).build());
    }

    @EventListener
    public void changePasswordEvent(ChangePasswordEvent event) {
        this.save(() -> AuditEvent.fromI18nKeys((String)AuditCategories.AUTH, (String)PASSWORD_CHANGE_SUMMARY, (CoverageLevel)CoverageLevel.ADVANCED, (CoverageArea)CoverageArea.SECURITY).affectedObject(this.buildResource(event.getUser().getFullName(), this.resourceTypes.user(), this.auditHelper.fetchUserKey(event.getUser().getName()))).build());
    }

    @EventListener
    public void directoryCreatedEvent(DirectoryCreatedEvent event) {
        this.save(() -> this.buildDirectoryEvent(event.getDirectory(), USER_DIRECTORY_CREATED, AuditAction.ADD));
    }

    @EventListener
    public void directoryDeletedEvent(DirectoryDeletedEvent event) {
        this.saveIfPresent(() -> event.getSource() != null ? Optional.of(this.buildDirectoryEvent(event.getDirectory(), USER_DIRECTORY_DELETED, AuditAction.REMOVE)) : Optional.empty());
    }

    @EventListener
    public void directoryUpdatedEvent(DirectoryUpdatedEvent event) {
        this.saveIfPresent(() -> event.getSource() != null ? Optional.of(this.buildDirectoryEvent(event.getDirectory(), USER_DIRECTORY_UPDATED, AuditAction.ADD)) : Optional.empty());
    }

    private AuditEvent buildDirectoryEvent(Directory directory, String summary, AuditAction action) {
        return this.getBaseBuilderWithoutResourceId(directory.getName(), summary, this.resourceTypes.directory()).changedValues(this.getAuditHandlerService().handle(directory, action)).build();
    }

    private AuditEvent.Builder getDeletedUserBuilder(String fullName) {
        return this.getBaseBuilder(fullName, USER_REMOVED_SUMMARY, this.resourceTypes.user(), null);
    }

    private AuditEvent.Builder getBaseUserBuilder(@Nullable String userKey, String fullName, String summary) {
        return this.getBaseBuilder(fullName, summary, this.resourceTypes.user(), userKey);
    }

    private AuditEvent.Builder getBaseGroupBuilder(String groupName, String summary) {
        return this.getBaseBuilder(groupName, summary, this.resourceTypes.group(), groupName);
    }

    private AuditEvent.Builder getBaseBuilderWithoutResourceId(String objectName, String actionKey, String objectType) {
        return this.getBaseBuilder(objectName, actionKey, objectType, null);
    }

    private AuditEvent.Builder getBaseBuilder(String objectName, String actionKey, String objectType, @Nullable String objectId) {
        return AuditEvent.fromI18nKeys((String)AuditCategories.USER_MANAGEMENT, (String)actionKey, (CoverageLevel)CoverageLevel.BASE, (CoverageArea)CoverageArea.USER_MANAGEMENT).affectedObject(this.buildResource(objectName, objectType, objectId));
    }
}

