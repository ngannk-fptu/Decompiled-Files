/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.longtasks.LongTaskSubmission
 *  com.atlassian.confluence.api.model.people.Person
 *  com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.user.EntityException
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user.actions;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.longtasks.LongTaskSubmission;
import com.atlassian.confluence.api.model.people.Person;
import com.atlassian.confluence.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.event.events.analytics.UserRemoveInitiatedAnalyticsEvent;
import com.atlassian.confluence.security.ExternalUserManagementAware;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.actions.AbstractUsersAction;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.user.EntityException;
import java.util.Arrays;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ReadOnlyAccessAllowed
public class RemoveUserAction
extends AbstractUsersAction
implements ExternalUserManagementAware {
    private static final Logger log = LoggerFactory.getLogger(RemoveUserAction.class);
    private SpaceManager spaceManager;
    private List userOwnedSpaces;
    private List userCommentedSpaces;
    private List userEditedSpaces;
    private String taskId;
    private EventPublisher eventPublisher;

    @Override
    public String doDefault() throws Exception {
        this.userKey = this.getUser().getKey();
        this.eventPublisher.publish((Object)new UserRemoveInitiatedAnalyticsEvent(false));
        return super.doDefault();
    }

    public String execute() throws Exception {
        try {
            Person person = (Person)this.getPersonService().find(new Expansion[0]).withUsername(this.getUsername()).fetchOrNull();
            LongTaskSubmission longTaskSubmission = this.getPersonService().delete(person);
            this.taskId = longTaskSubmission.getId().serialise();
            return "success";
        }
        catch (ServiceException e) {
            log.error("Failed to remove user", (Throwable)e);
            this.addActionError("remove.failed", this.getUsername());
            return "error";
        }
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public boolean isUserRemovable() {
        try {
            return this.getUsername() == null || this.getUser() == null || this.userAccessor.isUserRemovable(this.getUser());
        }
        catch (EntityException e) {
            log.error("Error checking whether or not user is removable", (Throwable)e);
            this.addActionError("user.not.removable.check.failed", this.getUsername());
            return false;
        }
    }

    public boolean hasPersonalSpace() {
        return this.spaceManager.getPersonalSpace(this.getUser()) != null;
    }

    public @Nullable String getPersonalSpaceKey() {
        Space personalSpace = this.spaceManager.getPersonalSpace(this.getUser());
        return personalSpace == null ? null : personalSpace.getKey();
    }

    @Deprecated
    public List getUserOwnedSpaces() {
        if (this.userOwnedSpaces == null && this.getUser() != null) {
            this.userOwnedSpaces = this.spaceManager.getAuthoredSpacesByUser(this.getUser().getName());
            this.userOwnedSpaces.remove(this.spaceManager.getPersonalSpace(this.user));
        }
        return this.userOwnedSpaces;
    }

    @Deprecated
    public List getUserEditedSpaces() {
        if (this.userEditedSpaces == null && this.getUser() != null) {
            this.userEditedSpaces = this.spaceManager.getSpacesContainingPagesEditedBy(this.getUser().getName());
            this.userEditedSpaces.remove(this.spaceManager.getPersonalSpace(this.user));
        }
        return this.userEditedSpaces;
    }

    @Deprecated
    public List getUserCommentedSpaces() {
        if (this.userCommentedSpaces == null && this.getUser() != null) {
            this.userCommentedSpaces = this.spaceManager.getSpacesContainingCommentsBy(this.getUser().getName());
            this.userCommentedSpaces.remove(this.spaceManager.getPersonalSpace(this.user));
        }
        return this.userCommentedSpaces;
    }

    public String getTaskId() {
        return this.taskId;
    }

    public List<String> getUserContentCleanupInformation() {
        return Arrays.asList(this.getText("user.delete.consequence.remove.details"), this.getText("user.delete.consequence.replace.username.with.alias"));
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
}

