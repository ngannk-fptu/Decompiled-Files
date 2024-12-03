/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.rpc.NotPermittedException
 */
package com.atlassian.confluence.plugins.ia.service;

import com.atlassian.confluence.rpc.NotPermittedException;

public interface SidebarService {
    public static final String NAV_TYPE_OPTION = "nav-type";
    public static final String PAGE_TREE_NAV_TYPE = "page-tree";
    public static final String CHILDREN_NAV_TYPE = "pages";
    public static final String DEFAULT_NAV_TYPE = "page-tree";
    public static final String QUICK_LINKS_STATE_OPTION = "quick-links-state";
    public static final String QUICK_LINKS_SHOW_STATE = "show";
    public static final String QUICK_LINKS_HIDE_STATE = "hide";
    public static final String PAGE_TREE_STATE = "page-tree-state";

    public String getOption(String var1, String var2);

    public void setOption(String var1, String var2, String var3) throws NotPermittedException;

    public void forceSetOption(String var1, String var2, String var3);
}

