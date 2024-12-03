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
import com.atlassian.confluence.internal.user.UserAccessorInternal;
import com.atlassian.confluence.security.ExternalUserManagementAware;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.actions.AbstractUsersAction;
import com.atlassian.event.api.EventPublisher;
import java.util.Arrays;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ReadOnlyAccessAllowed
public class RemoveUnsyncedUserAction
extends AbstractUsersAction
implements ExternalUserManagementAware {
    private SpaceManager spaceManager;
    private String taskId;
    private static final Logger logger = LoggerFactory.getLogger(RemoveUnsyncedUserAction.class);
    private EventPublisher eventPublisher;

    @Override
    public String doDefault() throws Exception {
        this.eventPublisher.publish((Object)new UserRemoveInitiatedAnalyticsEvent(true));
        return super.doDefault();
    }

    @Override
    public void validate() {
        if (this.getUser() == null) {
            this.addActionError(this.getText("user.doesnt.exist"));
        } else if (this.isDeletedUser()) {
            this.addActionError(this.getText("user.already.deleted"));
        } else if (!this.isUnsyncedUser()) {
            this.addActionError(this.getText("user.not.unsynced"));
        }
        super.validate();
    }

    private boolean isUnsyncedUser() {
        return ((UserAccessorInternal)this.getUserAccessor()).isUnsyncedUser(this.getUser());
    }

    private boolean isDeletedUser() {
        return ((UserAccessorInternal)this.getUserAccessor()).isDeletedUser(this.getUser());
    }

    @Override
    public ConfluenceUser getUser() {
        if (this.user == null) {
            this.user = this.userAccessor.getUserByKey(this.userKey);
        }
        return this.user;
    }

    public String execute() throws Exception {
        try {
            Person person = (Person)this.getPersonService().find(new Expansion[0]).withUserKey(this.getUserKey()).fetchOrNull();
            LongTaskSubmission longTaskSubmission = this.getPersonService().delete(person);
            this.taskId = longTaskSubmission.getId().serialise();
            return "success";
        }
        catch (ServiceException e) {
            logger.error("Failed to remove unsynced user with key " + this.userKey, (Throwable)e);
            this.addActionError("remove.failed", this.getUser() != null ? this.getUser().getName() : null);
            return "error";
        }
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public boolean hasPersonalSpace() {
        return this.spaceManager.getPersonalSpace(this.getUser()) != null;
    }

    public @Nullable String getPersonalSpaceKey() {
        Space personalSpace = this.spaceManager.getPersonalSpace(this.getUser());
        return personalSpace == null ? null : personalSpace.getKey();
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

