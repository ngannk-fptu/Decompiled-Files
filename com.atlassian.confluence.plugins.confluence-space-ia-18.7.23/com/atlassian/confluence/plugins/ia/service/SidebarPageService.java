/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.ia.service;

import com.atlassian.confluence.plugins.ia.model.PagesBean;

public interface SidebarPageService {
    public PagesBean getPageContextualNav(long var1);

    @Deprecated
    public PagesBean getChildren(long var1);
}

