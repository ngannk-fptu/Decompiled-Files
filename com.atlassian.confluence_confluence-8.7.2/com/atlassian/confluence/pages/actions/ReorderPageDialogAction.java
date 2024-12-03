/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.ChildPositionComparator;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.actions.AbstractPageAwareAction;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.actions.SpaceAware;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ReorderPageDialogAction
extends AbstractPageAwareAction
implements SpaceAware {
    private static final Comparator<Page> PAGE_ORDERING_COMPARATOR = new ChildPositionComparator();
    private String panelName;
    private Space space;
    private String movedPageId;
    private String pageTitle;
    private PageManager pageManager;

    @Override
    @XsrfProtectionExcluded
    public String doDefault() {
        if ("reorder".equalsIgnoreCase(this.panelName)) {
            return "reorderpanel";
        }
        return "input";
    }

    public List<Page> getChildren() {
        List<Page> children = null;
        if (this.getPage() == null) {
            children = this.getPermittedEntitiesOf(this.pageManager.getTopLevelPages(this.getSpace()));
        } else {
            AbstractPage page = this.getPage();
            if (page instanceof Page) {
                children = this.getPermittedEntitiesOf(((Page)page).getSortedChildren());
            }
        }
        if (children == null) {
            throw new IllegalStateException("Failed to determine children of page");
        }
        return this.addMovedPageToChildren(children);
    }

    private List<Page> addMovedPageToChildren(List<Page> children) {
        long currentPageId = Long.parseLong(this.movedPageId);
        for (Page childPage : children) {
            if (childPage.getId() != currentPageId) continue;
            return children;
        }
        Page currentPage = new Page();
        currentPage.setTitle(this.pageTitle);
        currentPage.setId(Long.parseLong(this.movedPageId));
        children.add(currentPage);
        Collections.sort(children, PAGE_ORDERING_COMPARATOR);
        return children;
    }

    public void setPanelName(String panelName) {
        this.panelName = panelName;
    }

    @Override
    public boolean isPageRequired() {
        return false;
    }

    @Override
    public boolean isSpaceRequired() {
        return true;
    }

    @Override
    public void setSpace(Space space) {
        this.space = space;
    }

    @Override
    public Space getSpace() {
        return this.space == null ? super.getSpace() : this.space;
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public String getPageTitle() {
        return this.pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getMovedPageId() {
        return this.movedPageId;
    }

    public void setMovedPageId(String movedPageId) {
        this.movedPageId = movedPageId;
    }
}

