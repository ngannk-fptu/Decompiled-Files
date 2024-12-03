/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.ContentPermissionManager;
import com.atlassian.confluence.core.datetime.FriendlyDateFormatterHelper;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.actions.PageAware;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.user.User;

public abstract class AbstractPageAwareAction
extends ConfluenceActionSupport
implements PageAware {
    private AbstractPage page;
    protected SpaceManager spaceManager;
    protected ContentPermissionManager contentPermissionManager;
    private FriendlyDateFormatterHelper friendlyDateFormatterHelper;

    @Override
    public AbstractPage getPage() {
        return this.page;
    }

    public long getPageId() {
        if (this.getPage() != null) {
            return this.getPage().getId();
        }
        return 0L;
    }

    public String getTitle() {
        if (this.getPage() != null) {
            return this.getPage().getTitle();
        }
        return null;
    }

    @Override
    public void setPage(AbstractPage page) {
        this.page = page;
    }

    @Override
    public boolean isPageRequired() {
        return true;
    }

    @Override
    public boolean isLatestVersionRequired() {
        return true;
    }

    @Override
    public boolean isViewPermissionRequired() {
        return true;
    }

    @Override
    public boolean isPermitted() {
        if (!this.spacePermissionManager.hasAllPermissions(this.getPermissionTypes(), this.getSpace(), this.getAuthenticatedUser())) {
            return false;
        }
        if (this.getPage() == null) {
            return true;
        }
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.VIEW, this.getPage());
    }

    public Space getSpace() {
        if (this.getPage() != null) {
            return this.getPage().getLatestVersion().getSpace();
        }
        return null;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public void setContentPermissionManager(ContentPermissionManager contentPermissionManager) {
        this.contentPermissionManager = contentPermissionManager;
    }

    public boolean isCollaborativeContent() {
        return false;
    }

    public String getSpaceKey() {
        if (this.getSpace() != null) {
            return this.getSpace().getKey();
        }
        return null;
    }

    public FriendlyDateFormatterHelper getFriendlyDateFormatterHelper() {
        if (this.friendlyDateFormatterHelper == null) {
            this.friendlyDateFormatterHelper = new FriendlyDateFormatterHelper(this.getFriendlyDateFormatter(), this.i18NBeanFactory, this.getLocaleManager());
        }
        return this.friendlyDateFormatterHelper;
    }

    public String getPageUrl() {
        return this.getPage().getUrlPath();
    }

    public String getCustomPageUrl() {
        return GeneralUtil.customGetPageUrl(this.getPage());
    }
}

