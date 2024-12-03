/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.WikiStyleRenderer
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.renderer.WikiStyleRenderer;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import java.util.Date;
import java.util.List;

public class ContentFacade {
    private PageManager pageManager;
    private SettingsManager settingsManager;
    private WikiStyleRenderer wikiStyleRenderer;
    private PermissionManager permissionManager;
    private SpaceManager spaceManager;

    public boolean spaceHasBlogPosts(String spaceKey) {
        return this.getPageManager().spaceHasBlogPosts(spaceKey);
    }

    public List<Page> getRecentlyUpdatedPagesForUserOnSpaceSince(User user, String spaceKey, Date previousLoginDate) {
        List<Page> recentlyUpdatedPages = this.pageManager.getPages(this.spaceManager.getSpace(spaceKey), true);
        return this.permissionManager.getPermittedEntities(user, Permission.VIEW, recentlyUpdatedPages);
    }

    public List getRecentlyUpdatedPagesForUserSince(User user, Date previousLoginDate) {
        List pagesCreatedOrUpdatedSinceLastLogin = this.pageManager.getPagesCreatedOrUpdatedSinceDate(previousLoginDate);
        return this.permissionManager.getPermittedEntities(user, Permission.VIEW, pagesCreatedOrUpdatedSinceLastLogin);
    }

    public PageManager getPageManager() {
        return this.pageManager;
    }

    public SettingsManager getSettingsManager() {
        return this.settingsManager;
    }

    public WikiStyleRenderer getWikiStyleRenderer() {
        if (this.wikiStyleRenderer == null) {
            this.wikiStyleRenderer = (WikiStyleRenderer)ContainerManager.getInstance().getContainerContext().getComponent((Object)"wikiStyleRenderer");
        }
        return this.wikiStyleRenderer;
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    public void setWikiStyleRenderer(WikiStyleRenderer wikiStyleRenderer) {
        this.wikiStyleRenderer = wikiStyleRenderer;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }
}

