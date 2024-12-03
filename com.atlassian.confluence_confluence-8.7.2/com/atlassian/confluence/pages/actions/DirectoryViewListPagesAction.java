/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.security.access.annotations.RequiresAnyConfluenceAccess;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import com.atlassian.confluence.spaces.actions.SpaceAware;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiresAnyConfluenceAccess
public class DirectoryViewListPagesAction
extends AbstractSpaceAction
implements SpaceAware {
    private static final String PLUGIN_KEY = "list-content-tree";
    private long openPageId = -1L;
    private List<Long> openedNodes = Collections.emptyList();
    private PageManager pageManager;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        GeneralUtil.setCookie("confluence.list.pages.cookie", PLUGIN_KEY);
        Page openPage = this.pageManager.getPage(this.openPageId);
        if (this.isOpenNode() && openPage != null) {
            List<Page> ancestors = openPage.getAncestors();
            ArrayList<Long> newOpenedNodes = new ArrayList<Long>();
            for (int i = ancestors.size() - 1; i >= 0; --i) {
                Page page = ancestors.get(i);
                if (page == null) continue;
                newOpenedNodes.add(page.getId());
            }
            this.openedNodes = ImmutableList.copyOf(newOpenedNodes);
        }
        return "success";
    }

    public void setOpenId(long openPageId) {
        this.openPageId = openPageId;
    }

    public boolean isOpenNode() {
        return this.openPageId != -1L;
    }

    public List getOpenedNodes() {
        return this.openedNodes;
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public long getOpenId() {
        return this.openPageId;
    }

    @Override
    public boolean isSpaceRequired() {
        return true;
    }

    @Override
    public boolean isViewPermissionRequired() {
        return true;
    }
}

