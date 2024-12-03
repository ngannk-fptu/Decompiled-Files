/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.spaces.actions;

import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceDescription;
import com.atlassian.confluence.spaces.SpaceStatus;
import com.atlassian.confluence.spaces.actions.AbstractEditSpaceAction;
import com.atlassian.confluence.util.i18n.I18NBean;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EditSpaceAction
extends AbstractEditSpaceAction {
    private static final Logger log = LoggerFactory.getLogger(EditSpaceAction.class);
    private LocaleManager localeManager;
    private Space originalSpace;

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setHomePageTitle(String homePageTitle) {
        this.homePageTitle = homePageTitle;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public String doEdit() throws Exception {
        Space editSpace = this.getSpace();
        I18NBean systemLocaleI18nBean = this.i18NBeanFactory.getI18NBean(this.localeManager.getSiteDefaultLocale());
        String oldDefaultSpaceHomePageTitle = systemLocaleI18nBean.getText(editSpace.getDefaultHomepageTitle());
        editSpace.setName(this.name);
        if (editSpace.getDescription() == null) {
            editSpace.setDescription(new SpaceDescription(editSpace));
        }
        editSpace.getDescription().setBodyAsString(this.description);
        Page newHomePage = null;
        if (StringUtils.isNotBlank((CharSequence)this.homePageTitle)) {
            Page page = this.pageManager.getPage(editSpace.getKey(), this.homePageTitle);
            if (page != null) {
                newHomePage = page;
                if (page.getTitle().equals(oldDefaultSpaceHomePageTitle)) {
                    String newDefaultSpaceHomePageTitle = systemLocaleI18nBean.getText(editSpace.getDefaultHomepageTitle());
                    if (!page.getTitle().equals(newDefaultSpaceHomePageTitle)) {
                        this.pageManager.renamePageWithoutNotifications(page, newDefaultSpaceHomePageTitle);
                    }
                }
            } else {
                this.addActionError(systemLocaleI18nBean.getText("text.error.homepage.not.found"));
                return "error";
            }
        }
        editSpace.setHomePage(newHomePage);
        editSpace.setSpaceStatus(this.isArchived() ? SpaceStatus.ARCHIVED : SpaceStatus.CURRENT);
        this.spaceManager.saveSpace(editSpace, this.originalSpace);
        return "success";
    }

    @Override
    public void setSpace(Space space) {
        this.originalSpace = null;
        super.setSpace(space);
        if (space != null) {
            try {
                this.originalSpace = (Space)space.clone();
            }
            catch (CloneNotSupportedException e) {
                log.error("Could not clone space?" + e, (Throwable)e);
            }
        }
    }

    public void setSpaceType(String spaceType) {
        this.spaceType = spaceType;
    }

    @Override
    public void setLocaleManager(LocaleManager localeManager) {
        this.localeManager = localeManager;
    }
}

