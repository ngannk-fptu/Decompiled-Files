/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.spaces.actions;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceStatus;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAdminAction;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.confluence.user.PersonalInformationManager;
import com.atlassian.user.User;

public abstract class AbstractEditSpaceAction
extends AbstractSpaceAdminAction {
    protected String name;
    protected String description;
    protected PersonalInformationManager personalInformationManager;
    protected PageManager pageManager;
    protected String homePageTitle;
    protected String spaceType;
    protected Boolean archived;

    public void setPersonalInformationManager(PersonalInformationManager personalInformationManager) {
        this.personalInformationManager = personalInformationManager;
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public String getName() {
        if (this.name == null && this.getSpace() != null) {
            this.name = this.getSpace().getName();
        }
        return this.name;
    }

    public String getDescription() {
        if (this.description == null && this.getSpace() != null && this.space.getDescription() != null) {
            this.description = this.getSpace().getDescription().getBodyAsString();
        }
        return this.description;
    }

    public String getHomePageTitle() {
        if (this.homePageTitle == null && this.getSpace() != null && this.getSpace().getHomePage() != null) {
            this.homePageTitle = this.getSpace().getHomePage().getTitle();
        }
        return this.homePageTitle;
    }

    public String getCurrentHomePageTitle() {
        Page homePage = this.getSpace().getHomePage();
        if (homePage != null) {
            return homePage.getTitle();
        }
        return "";
    }

    public String getSpaceType() {
        return this.getText(this.getSpace().getSpaceType().toI18NKey());
    }

    public boolean isArchived() {
        if (this.archived == null && this.getSpace() != null) {
            this.archived = SpaceStatus.ARCHIVED.equals((Object)this.getSpace().getSpaceStatus());
        }
        return this.archived == null ? false : this.archived;
    }

    public boolean isConvertableToPersonalSpace() {
        if (this.getAuthenticatedUser() == null || this.getSpace().isPersonal()) {
            return false;
        }
        if (this.permissionManager.isConfluenceAdministrator(this.getAuthenticatedUser())) {
            return true;
        }
        PersonalInformation info = this.personalInformationManager.getOrCreatePersonalInformation(this.getAuthenticatedUser());
        return info != null && this.permissionManager.hasCreatePermission((User)this.getAuthenticatedUser(), (Object)info, Space.class) && this.spaceManager.getPersonalSpace(this.getAuthenticatedUser()) == null;
    }
}

