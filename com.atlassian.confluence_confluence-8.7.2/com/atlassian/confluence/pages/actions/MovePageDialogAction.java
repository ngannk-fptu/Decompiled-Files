/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.actions.AbstractPageAwareAction;
import com.atlassian.confluence.pages.actions.beans.AvailableSpaces;
import com.atlassian.confluence.spaces.Space;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class MovePageDialogAction
extends AbstractPageAwareAction {
    private PageManager pageManager;
    private Page parentPage;
    private String parentPageTitle;
    private String parentPageString;
    private String dialogMode;
    private Space space;
    private String spaceKey;
    private String panelName;

    @Override
    @XsrfProtectionExcluded
    public String doDefault() {
        if ("browse".equalsIgnoreCase(this.panelName)) {
            return "browsepanel";
        }
        return "input";
    }

    public List<Space> getAvailableSpaces() {
        return new AvailableSpaces(this.spaceManager).getAvailableSpaces(this.getSpace(), this.getAuthenticatedUser());
    }

    public void setDialogMode(String dialogMode) {
        this.dialogMode = dialogMode;
    }

    public String getDialogMode() {
        return this.dialogMode;
    }

    @Override
    public String getSpaceKey() {
        if (this.getSpace() != null) {
            return this.getSpace().getKey();
        }
        return null;
    }

    public String getNewSpaceKey() {
        return this.getSpaceKey();
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
        this.space = null;
    }

    @Override
    public Space getSpace() {
        if (this.space == null) {
            this.space = super.getSpace();
        }
        if (this.space == null && StringUtils.isNotBlank((CharSequence)this.spaceKey)) {
            this.space = this.spaceManager.getSpace(this.spaceKey);
        }
        return this.space;
    }

    public Page getParentPage() {
        if (this.parentPage == null && StringUtils.isNotEmpty((CharSequence)this.getParentPageTitle())) {
            this.parentPage = this.pageManager.getPage(this.getSpaceKey(), this.getParentPageTitle());
        }
        return this.parentPage;
    }

    public String getParentPageTitle() {
        if (StringUtils.isEmpty((CharSequence)this.parentPageTitle)) {
            this.parentPageTitle = this.getParentPageString();
        }
        return this.parentPageTitle;
    }

    @Override
    public String getTitle() {
        String title = super.getTitle();
        if (StringUtils.isBlank((CharSequence)title)) {
            title = this.getText("untitled.page.title");
        }
        return title;
    }

    public void setParentPageString(String parentPageString) {
        this.parentPageString = parentPageString;
    }

    public String getParentPageString() {
        return this.parentPageString;
    }

    public void setPanelName(String panelName) {
        this.panelName = panelName;
    }

    @Override
    public boolean isPageRequired() {
        return false;
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }
}

