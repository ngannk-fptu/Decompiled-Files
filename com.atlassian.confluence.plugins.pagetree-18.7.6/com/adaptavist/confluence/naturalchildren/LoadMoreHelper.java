/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.internal.ContentPermissionManagerInternal
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.adaptavist.confluence.naturalchildren;

import com.adaptavist.confluence.naturalchildren.PageList;
import com.atlassian.confluence.internal.ContentPermissionManagerInternal;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class LoadMoreHelper {
    private final ContentPermissionManagerInternal contentPermissionManager;
    private final ConfluenceUser user;
    private final boolean exempt;

    public LoadMoreHelper(ContentPermissionManagerInternal contentPermissionManager, ConfluenceUser user, boolean exempt) {
        this.contentPermissionManager = contentPermissionManager;
        this.user = user;
        this.exempt = exempt;
    }

    public PageList getSublistWithLoadMoreSupport(List<Page> fullPageList, int limit, Set<Long> mustBeDisplayedItems) {
        ArrayList<Page> pages;
        int startIndex = this.getStartIndex(fullPageList, mustBeDisplayedItems);
        AtomicInteger topIndex = new AtomicInteger(startIndex);
        AtomicInteger bottomIndex = new AtomicInteger(startIndex + 1);
        List<Object> topPages = new ArrayList<Page>(limit + 1);
        ArrayList<Page> bottomPages = new ArrayList<Page>(limit + 1);
        do {
            boolean allElementsAreProcessed;
            Page bottomPage;
            Page topPage;
            if ((topPage = this.getNextVisibleTopPage(fullPageList, topIndex)) != null) {
                topPages.add(topPage);
            }
            if ((bottomPage = this.getNextVisibleBottomPage(fullPageList, bottomIndex)) != null) {
                bottomPages.add(bottomPage);
            }
            boolean bl = allElementsAreProcessed = !(topPage != null && topIndex.get() >= 0 || bottomPage != null && bottomIndex.get() < fullPageList.size());
            if (!allElementsAreProcessed) continue;
            Collections.reverse(topPages);
            topPages.addAll(bottomPages);
            return new PageList(topPages);
        } while (topPages.size() + bottomPages.size() < limit + 2);
        if (topIndex.get() < 0) {
            Collections.reverse(topPages);
            pages = new ArrayList<Page>(topPages);
            pages.addAll(bottomPages.subList(0, bottomPages.size() - 1));
            return new PageList(pages, PageList.LoadMoreMode.LOAD_MORE_AFTER_ONLY);
        }
        if (bottomIndex.get() >= fullPageList.size()) {
            topPages = topPages.subList(0, topPages.size() - 1);
            Collections.reverse(topPages);
            pages = new ArrayList<Page>(topPages);
            pages.addAll(bottomPages);
            return new PageList(pages, PageList.LoadMoreMode.LOAD_MORE_BEFORE_ONLY);
        }
        topPages = topPages.subList(0, topPages.size() - 1);
        Collections.reverse(topPages);
        pages = new ArrayList<Page>(topPages);
        pages.addAll(bottomPages.subList(0, bottomPages.size() - 1));
        return new PageList(pages, PageList.LoadMoreMode.LOAD_MORE_BOTH_BEFORE_AND_AFTER);
    }

    private int getStartIndex(List<Page> fullPageList, Set<Long> mustBeDisplayedItems) {
        int counter = 0;
        for (Page page : fullPageList) {
            if (this.shouldPageBeDisplayed(page.getId(), mustBeDisplayedItems)) {
                return counter;
            }
            ++counter;
        }
        return 0;
    }

    public boolean shouldPageBeDisplayed(Long pageId, Set<Long> mustBeDisplayedItems) {
        return mustBeDisplayedItems.contains(pageId);
    }

    private Page getNextVisibleBottomPage(List<Page> fullPageList, AtomicInteger currentPageIndex) {
        return this.getNextVisibleBottomPage(fullPageList, currentPageIndex, 1);
    }

    private Page getNextVisibleTopPage(List<Page> fullPageList, AtomicInteger currentPageIndex) {
        return this.getNextVisibleBottomPage(fullPageList, currentPageIndex, -1);
    }

    private Page getNextVisibleBottomPage(List<Page> fullPageList, AtomicInteger index, int delta) {
        Page page;
        List permittedPages;
        do {
            int currentIndex;
            if ((currentIndex = index.get()) < 0) {
                return null;
            }
            if (currentIndex >= fullPageList.size()) {
                return null;
            }
            index.addAndGet(delta);
            page = fullPageList.get(currentIndex);
            if (!this.exempt) continue;
            return page;
        } while ((permittedPages = this.contentPermissionManager.getPermittedPagesIgnoreInheritedPermissions(Collections.singletonList(page), this.user, "View")).size() <= 0);
        return page;
    }

    public PageList getAllPermittedElements(List<Page> children) {
        if (this.exempt) {
            return new PageList(children);
        }
        List pages = this.contentPermissionManager.getPermittedPagesIgnoreInheritedPermissions(children, this.user, "View");
        return new PageList(pages);
    }
}

