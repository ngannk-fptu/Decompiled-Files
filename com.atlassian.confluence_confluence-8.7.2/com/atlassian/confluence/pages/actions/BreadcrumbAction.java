/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.actions.PageAware;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.themes.GlobalHelper;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.breadcrumbs.AttachmentBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.breadcrumbs.BreadcrumbGenerator;
import com.atlassian.confluence.util.breadcrumbs.UserBreadcrumb;
import com.atlassian.confluence.util.breadcrumbs.spaceia.SpaceBreadcrumb;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class BreadcrumbAction
extends ConfluenceActionSupport
implements PageAware,
Beanable {
    private static final String NOT_FOUND = "pagenotfound";
    private SpaceManager spaceManager;
    private List<Breadcrumb> breadcrumbs;
    private String spaceKey;
    private long pageId;
    private String title;
    private AbstractPage page;
    private String fileName;
    private String userName;
    private String breadcrumbType;
    private BreadcrumbGenerator breadcrumbGenerator;
    private int ellipsisIndex = -1;
    private int ellipsisLength = 0;
    private GlobalHelper globalHelper = new GlobalHelper();

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        if (StringUtils.isNotEmpty((CharSequence)this.userName)) {
            return this.handleUserBreadcrumb();
        }
        if (this.fileName != null) {
            return this.handleAttachmentBreadcrumb();
        }
        if (this.page != null || StringUtils.isNotEmpty((CharSequence)this.title) || this.pageId != 0L) {
            return this.page != null ? this.handlePageBreadcrumb() : NOT_FOUND;
        }
        if (StringUtils.isNotEmpty((CharSequence)this.spaceKey) && StringUtils.isEmpty((CharSequence)this.title)) {
            return this.handleSpaceBreadcrumb();
        }
        return NOT_FOUND;
    }

    private String handleUserBreadcrumb() {
        ConfluenceUser targetUser = this.userAccessor.getUserByName(this.userName);
        if (targetUser == null) {
            return NOT_FOUND;
        }
        ConfluenceUser currentUser = this.getAuthenticatedUser();
        if (!this.permissionManager.hasPermission((User)currentUser, Permission.VIEW, targetUser)) {
            return NOT_FOUND;
        }
        this.breadcrumbs = this.getBreadcrumbs(null, new UserBreadcrumb(targetUser));
        this.breadcrumbType = "userinfo";
        return this.breadcrumbs != null ? "success" : NOT_FOUND;
    }

    private String handlePageBreadcrumb() {
        Space space = this.page.getLatestVersion().getSpace();
        Breadcrumb breadcrumb = this.breadcrumbGenerator.getContentBreadcrumb(space, this.page);
        if (breadcrumb == null) {
            return NOT_FOUND;
        }
        this.breadcrumbs = this.getBreadcrumbs(space, breadcrumb);
        List<Breadcrumb> ellipsisCrumbs = this.globalHelper.getEllipsisCrumbs(this.breadcrumbs);
        this.ellipsisIndex = ellipsisCrumbs.size() == 0 ? -1 : this.breadcrumbs.indexOf(ellipsisCrumbs.get(0));
        this.ellipsisLength = ellipsisCrumbs.size();
        this.breadcrumbType = this.page instanceof Page ? "page" : "blogpost";
        return "success";
    }

    private String handleSpaceBreadcrumb() {
        Space space = this.spaceManager.getSpace(this.spaceKey);
        if (space == null) {
            return NOT_FOUND;
        }
        this.breadcrumbs = this.getBreadcrumbs(space, new SpaceBreadcrumb(space));
        this.breadcrumbType = "space";
        return "success";
    }

    private String handleAttachmentBreadcrumb() {
        if (this.page == null) {
            return NOT_FOUND;
        }
        Attachment attachment = this.page.getAttachmentNamed(this.fileName);
        if (attachment == null) {
            return NOT_FOUND;
        }
        this.breadcrumbs = this.getBreadcrumbs(this.page.getLatestVersion().getSpace(), new AttachmentBreadcrumb(attachment));
        this.breadcrumbType = "attachment";
        return "success";
    }

    private List<Breadcrumb> getBreadcrumbs(Space space, Breadcrumb breadcrumb) {
        if (space != null) {
            breadcrumb = new com.atlassian.confluence.util.breadcrumbs.SpaceBreadcrumb(space).concatWith(breadcrumb);
        }
        breadcrumb.setFilterTrailingBreadcrumb(false);
        return this.breadcrumbGenerator.getFilteredBreadcrumbTrail(space, breadcrumb);
    }

    @Override
    public Object getBean() {
        return Map.of("breadcrumbs", this.breadcrumbs, "type", this.breadcrumbType, "ellipsisIndex", this.ellipsisIndex, "ellipsisLength", this.ellipsisLength);
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPageId(long pageId) {
        this.pageId = pageId;
    }

    @Override
    public void setPage(AbstractPage page) {
        this.page = page;
    }

    @Override
    public AbstractPage getPage() {
        return this.page;
    }

    @Override
    public boolean isPageRequired() {
        return false;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    @Override
    public boolean isLatestVersionRequired() {
        return true;
    }

    @Override
    public boolean isViewPermissionRequired() {
        return true;
    }

    public Space getSpace() {
        return this.getPage() == null ? null : this.getPage().getSpace();
    }

    public void setBreadcrumbGenerator(BreadcrumbGenerator breadcrumbGenerator) {
        this.breadcrumbGenerator = breadcrumbGenerator;
    }
}

