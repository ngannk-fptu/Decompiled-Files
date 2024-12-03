/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.confluence.spaces.actions;

import com.atlassian.confluence.search.IndexManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.actions.AbstractCreateSpaceAction;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.user.PersonalInformationManager;
import com.atlassian.user.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;

public class CreatePersonalSpaceAction
extends AbstractCreateSpaceAction {
    private String spacePermission;
    private PersonalInformationManager personalInformationManager;

    @Override
    public void validate() {
        ConfluenceUser remoteUser = this.getAuthenticatedUser();
        if (remoteUser != null && StringUtils.isBlank((CharSequence)remoteUser.getFullName())) {
            this.addActionError("personal.space.error.fullname.blank", this.getContextPath());
        }
        if (this.spaceManager.getPersonalSpace(this.getAuthenticatedUser()) != null) {
            this.addActionError("personal.space.error.already.exist", this.getContextPath(), this.getUsername());
        }
    }

    public String execute() throws Exception {
        ConfluenceUser creator = this.getAuthenticatedUser();
        String fullName = creator.getFullName();
        this.space = "private".equals(this.spacePermission) ? this.spaceManager.createPrivatePersonalSpace(fullName, null, creator) : this.spaceManager.createPersonalSpace(fullName, null, creator);
        this.notificationManager.addSpaceNotification(creator, this.space);
        this.indexManager.flushQueue(IndexManager.IndexQueueFlushMode.ONLY_FIRST_BATCH);
        return super.execute();
    }

    @Override
    public boolean isPermitted() {
        return this.getAuthenticatedUser() != null && this.permissionManager.hasCreatePermission((User)this.getAuthenticatedUser(), (Object)this.getPersonalInformation(this.getAuthenticatedUser()), Space.class);
    }

    public String getUsername() {
        return this.getAuthenticatedUser().getName();
    }

    public PersonalInformationManager getPersonalInformationManager() {
        return this.personalInformationManager;
    }

    public void setPersonalInformationManager(PersonalInformationManager personalInformationManager) {
        this.personalInformationManager = personalInformationManager;
    }

    private PersonalInformation getPersonalInformation(User remoteUser) {
        return this.personalInformationManager.getOrCreatePersonalInformation(remoteUser);
    }

    private String getContextPath() {
        return ServletActionContext.getRequest().getContextPath();
    }

    public String getSpacePermission() {
        return this.spacePermission;
    }

    public void setSpacePermission(String spacePermission) {
        this.spacePermission = spacePermission;
    }

    public String getPersonalSpaceUrlPath() {
        return this.space.getUrlPath();
    }
}

