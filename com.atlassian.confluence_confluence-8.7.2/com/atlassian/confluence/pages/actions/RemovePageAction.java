/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.core.DefaultDeleteContext;
import com.atlassian.confluence.links.LinkManager;
import com.atlassian.confluence.links.OutgoingLink;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.actions.AbstractPageAwareAction;
import com.atlassian.confluence.pages.actions.beans.PageIncomingLinks;
import com.atlassian.confluence.search.ConfluenceIndexer;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.themes.ThemeManager;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.breadcrumbs.spaceia.ContentDetailAction;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.List;

public class RemovePageAction
extends AbstractPageAwareAction
implements ContentDetailAction {
    private ConfluenceIndexer indexer;
    private Page cachedParent;
    private PageManager pageManager;
    private LinkManager linkManager;
    private ThemeManager themeManager;

    public void setIndexer(ConfluenceIndexer indexer) {
        this.indexer = indexer;
    }

    public void setLinkManager(LinkManager linkManager) {
        this.linkManager = linkManager;
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public void setThemeManager(ThemeManager themeManager) {
        this.themeManager = themeManager;
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        return "success";
    }

    public String doRemove() {
        Page oldPage = null;
        if (this.getPage() instanceof Page) {
            oldPage = (Page)this.getPage();
            this.cachedParent = oldPage.getParent();
        }
        if (oldPage != null && this.cachedParent != null) {
            this.pageManager.moveChildrenToNewParent(oldPage, this.cachedParent);
        }
        this.pageManager.trashPage(this.getPage(), DefaultDeleteContext.DEFAULT);
        return "success";
    }

    @Override
    public String getSpaceKey() {
        return super.getSpaceKey();
    }

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.REMOVE, this.getPage());
    }

    public List<OutgoingLink> getIncomingLinks() {
        return new PageIncomingLinks(this.linkManager, this.permissionManager).getIncomingLinks(this.getPage(), this.getAuthenticatedUser());
    }

    public String getRedirectUrl() {
        Page pageToReturn = this.cachedParent;
        Space space = this.getSpace();
        if (pageToReturn == null) {
            pageToReturn = space.getHomePage();
        }
        if (pageToReturn == null) {
            return this.themeManager.getSpaceTheme(space.getKey()).hasSpaceSideBar() ? "/collector/pages.action?key=" + HtmlUtil.urlEncode(space.getKey()) : "/pages/listpages-alphaview.action?key=" + HtmlUtil.urlEncode(this.getSpaceKey());
        }
        return pageToReturn.getUrlPath();
    }

    @Override
    public boolean isLatestVersionRequired() {
        return true;
    }

    @Override
    public boolean isViewPermissionRequired() {
        return true;
    }

    public ConfluenceIndexer getIndexer() {
        return this.indexer;
    }

    public int getPageChildrenCount() {
        AbstractPage abstractPage = this.getPage();
        if (abstractPage instanceof Page) {
            Page page = (Page)abstractPage;
            return page.getChildren().size();
        }
        return 0;
    }

    public int getChildrenCountWithInheritedPermissions() {
        AbstractPage abstractPage = this.getPage();
        if (abstractPage instanceof Page) {
            Page page = (Page)abstractPage;
            if (this.contentPermissionManager.getViewContentPermissions(page).isEmpty()) {
                return 0;
            }
            int childrenWithInheritedPermissionsCount = 0;
            for (Page child : page.getChildren()) {
                if (!this.contentPermissionManager.isPermissionInherited(child)) continue;
                ++childrenWithInheritedPermissionsCount;
            }
            return childrenWithInheritedPermissionsCount;
        }
        return 0;
    }
}

