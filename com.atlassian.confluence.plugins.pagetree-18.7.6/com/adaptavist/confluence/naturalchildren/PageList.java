/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Page
 */
package com.adaptavist.confluence.naturalchildren;

import com.atlassian.confluence.pages.Page;
import java.util.Collections;
import java.util.List;

public class PageList {
    private final List<Page> pageList;
    private final LoadMoreMode loadMoreMode;

    public PageList() {
        this(Collections.emptyList());
    }

    public PageList(List<Page> pageList) {
        this(pageList, LoadMoreMode.NO_LOAD_MORE_BUTTONS);
    }

    public PageList(List<Page> pageList, LoadMoreMode loadMoreMode) {
        this.pageList = pageList;
        this.loadMoreMode = loadMoreMode;
    }

    public List<Page> getPageList() {
        return this.pageList;
    }

    public boolean isHasMoreAfter() {
        return this.loadMoreMode.hasLoadMoreAfter();
    }

    public boolean isHasMoreBefore() {
        return this.loadMoreMode.hasLoadMoreBefore();
    }

    public Long getFirstLoadedId() {
        return this.pageList.size() > 0 ? Long.valueOf(this.pageList.get(0).getId()) : null;
    }

    public Long getLastLoadedId() {
        return this.pageList.size() > 0 ? Long.valueOf(this.pageList.get(this.pageList.size() - 1).getId()) : null;
    }

    public static enum LoadMoreMode {
        NO_LOAD_MORE_BUTTONS,
        LOAD_MORE_BEFORE_ONLY,
        LOAD_MORE_AFTER_ONLY,
        LOAD_MORE_BOTH_BEFORE_AND_AFTER;


        public boolean hasLoadMoreBefore() {
            return LOAD_MORE_BEFORE_ONLY == this || LOAD_MORE_BOTH_BEFORE_AND_AFTER == this;
        }

        public boolean hasLoadMoreAfter() {
            return LOAD_MORE_AFTER_ONLY == this || LOAD_MORE_BOTH_BEFORE_AND_AFTER == this;
        }
    }
}

