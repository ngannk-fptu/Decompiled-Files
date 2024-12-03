/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.ImmutableMap
 *  com.google.errorprone.annotations.Immutable
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.actions.ChildrenAction;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.errorprone.annotations.Immutable;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class PageChildrenAction
extends ChildrenAction
implements Beanable {
    private PageManager pageManager;
    private Boolean showChildren;
    private Object bean;

    @Override
    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    @Override
    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        List<Page> pages = super.getPermittedChildren();
        this.bean = Collections2.transform(pages, from -> null == from ? null : new PageWrapper((Page)from, this.pageManager.isPageRecentlyUpdatedForUser((Page)from, currentUser)));
        this.storeUserInterfaceState();
        return "success";
    }

    public String doStoreSettings() {
        this.storeUserInterfaceState();
        this.bean = ImmutableMap.of((Object)"success", (Object)Boolean.TRUE);
        return "success";
    }

    public void setShowChildren(@Nullable Boolean showChildren) {
        this.showChildren = showChildren;
    }

    @Override
    public Object getBean() {
        return this.bean;
    }

    private void storeUserInterfaceState() {
        if (null != this.showChildren) {
            this.getUserInterfaceState().setChildrenShowing(this.showChildren);
        }
    }

    @Immutable
    public static final class PageWrapper {
        private final Page page;
        private final boolean recentlyUpdated;

        PageWrapper(Page page, boolean isRecentlyUpdated) {
            this.page = page;
            this.recentlyUpdated = isRecentlyUpdated;
        }

        public boolean isHomePage() {
            return this.page.isHomePage();
        }

        public String getPageId() {
            return String.valueOf(this.page.getId());
        }

        public String getText() {
            return this.page.getDisplayTitle();
        }

        public boolean isRecentlyUpdated() {
            return this.recentlyUpdated;
        }

        public String getHref() {
            return this.page.getUrlPath();
        }
    }
}

