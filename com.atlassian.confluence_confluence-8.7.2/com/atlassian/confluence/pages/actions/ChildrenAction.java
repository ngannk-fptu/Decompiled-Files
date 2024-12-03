/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.bean.EntityObject
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.internal.ContentPermissionManagerInternal;
import com.atlassian.confluence.json.JSONAction;
import com.atlassian.confluence.json.json.JsonArray;
import com.atlassian.confluence.json.json.JsonObject;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.ChildPositionComparator;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.actions.AbstractPageAwareAction;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionCheckExemptions;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.actions.SpaceAware;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.core.bean.EntityObject;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChildrenAction
extends AbstractPageAwareAction
implements SpaceAware,
JSONAction {
    private static final Logger log = LoggerFactory.getLogger(ChildrenAction.class);
    static final Comparator<Page> CHILD_PAGE_COMPARATOR = new ChildPositionComparator();
    private String node;
    private Space space;
    protected PageManager pageManager;
    private PermissionCheckExemptions permissionCheckExemptions;
    private ContentPermissionManagerInternal contentPermissionManager;

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public List<Page> getPermittedChildren() {
        List<Page> children = Collections.emptyList();
        if (!this.isPermitted()) {
            return children;
        }
        if ("root".equals(this.node)) {
            children = this.filterBasedOnContentPermissions(this.pageManager.getTopLevelPages(this.getSpace()));
        } else {
            AbstractPage page = this.getPage();
            if (page instanceof Page) {
                children = this.permissionCheckExemptions.isExempt(AuthenticatedUserThreadLocal.get()) ? ((Page)page).getSortedChildren() : this.contentPermissionManager.getPermittedChildren((Page)page, this.getAuthenticatedUser());
            }
        }
        return children;
    }

    public boolean hasPermittedChildren(Page page) {
        if (!this.isPermitted()) {
            return false;
        }
        if (this.permissionCheckExemptions.isExempt(AuthenticatedUserThreadLocal.get())) {
            return page.hasChildren();
        }
        return this.contentPermissionManager.hasPermittedChildrenIgnoreInheritedPermissions(page, this.getAuthenticatedUser());
    }

    public void setPermissionCheckExemptions(PermissionCheckExemptions permissionCheckExemptions) {
        this.permissionCheckExemptions = permissionCheckExemptions;
    }

    private List<Page> filterBasedOnContentPermissions(List<Page> pages) {
        if (this.permissionCheckExemptions.isExempt(AuthenticatedUserThreadLocal.get())) {
            return pages;
        }
        ArrayList<Page> ret = new ArrayList<Page>(pages.size());
        for (Page page : pages) {
            if (!this.contentPermissionManager.hasContentLevelPermission((User)AuthenticatedUserThreadLocal.get(), "View", page)) continue;
            ret.add(page);
        }
        return ret;
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        return "success";
    }

    public void setNode(String node) {
        this.node = node;
    }

    @Override
    public boolean isPageRequired() {
        return false;
    }

    @Override
    public Space getSpace() {
        return this.space == null ? super.getSpace() : this.space;
    }

    @Override
    public void setSpace(Space space) {
        this.space = space;
    }

    @Override
    public boolean isSpaceRequired() {
        return false;
    }

    @Override
    public String getJSONString() {
        String contextPath = this.getBootstrapStatusProvider().getWebAppContextPath();
        JsonArray array = new JsonArray();
        try {
            if (!this.isPermitted()) {
                return array.serialize();
            }
            boolean ignorePermissions = this.permissionCheckExemptions.isExempt(AuthenticatedUserThreadLocal.get());
            boolean isEditingPermittedForTheRoot = ignorePermissions || this.isEditingPermitted();
            ConfluenceUser user = this.getAuthenticatedUser();
            Long homePageId = this.getHomePageId();
            List<Page> pages = this.getPages();
            ArrayList<Page> sortedPages = new ArrayList<Page>(pages);
            sortedPages.sort(CHILD_PAGE_COMPARATOR);
            ArrayList<Page> visiblePages = ignorePermissions ? sortedPages : this.contentPermissionManager.getPermittedPagesIgnoreInheritedPermissions(sortedPages, user, "View");
            Set editablePages = ignorePermissions ? Collections.emptySet() : this.contentPermissionManager.getPermittedPagesIgnoreInheritedPermissions(visiblePages, user, "Edit").stream().map(EntityObject::getId).collect(Collectors.toSet());
            for (Page page : visiblePages) {
                boolean editable;
                boolean isHomePage = homePageId != null && homePageId.equals(page.getId());
                boolean bl = editable = ignorePermissions || editablePages.contains(page.getId());
                boolean hasVisibleChildren = ignorePermissions ? page.getChildren().size() > 0 : this.contentPermissionManager.hasVisibleChildren(page, user);
                this.addPageToJsonArray(array, page, isHomePage, isEditingPermittedForTheRoot && editable, hasVisibleChildren, contextPath);
            }
            return array.serialize();
        }
        catch (Exception e) {
            log.error("Unable to render page tree: " + e.getMessage(), (Throwable)e);
            return new JsonArray().serialize();
        }
    }

    private void addPageToJsonArray(JsonArray jsonArray, Page page, boolean isHomePage, boolean editable, boolean hasVisibleChildren, String contextPath) {
        Object nodeClass;
        String linkClass = isHomePage ? "home-node" : "page-node";
        Object object = nodeClass = hasVisibleChildren ? "closed" : "";
        if (!editable) {
            nodeClass = (String)nodeClass + " undraggable";
        }
        String href = contextPath + page.getUrlPath();
        JsonObject json = new JsonObject().setProperty("text", page.getDisplayTitle()).setProperty("pageId", String.valueOf(page.getId())).setProperty("position", page.getPosition()).setProperty("linkClass", linkClass).setProperty("nodeClass", (String)nodeClass).setProperty("href", href);
        jsonArray.add(json);
    }

    private Long getHomePageId() {
        Page homePage = this.space != null ? this.space.getHomePage() : this.getPage().getSpace().getHomePage();
        return homePage != null ? Long.valueOf(homePage.getId()) : null;
    }

    List<Page> getPages() {
        if ("root".equals(this.node)) {
            return this.pageManager.getTopLevelPages(this.getSpace());
        }
        return ((Page)this.getPage()).getChildren();
    }

    @Override
    public boolean isPermitted() {
        if (this.getPage() == null) {
            return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.VIEW, this.getSpace());
        }
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.VIEW, this.getPage());
    }

    protected boolean isEditingPermitted() {
        ConfluenceUser user = this.getAuthenticatedUser();
        if (this.getPage() == null) {
            return this.spacePermissionManager.hasPermissionNoExemptions("VIEWSPACE", this.getSpace(), user) && this.spacePermissionManager.hasPermissionNoExemptions("EDITSPACE", this.getSpace(), user);
        }
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.EDIT, this.getPage());
    }

    public void setContentPermissionManager(ContentPermissionManagerInternal contentPermissionManager) {
        this.contentPermissionManager = contentPermissionManager;
    }
}

