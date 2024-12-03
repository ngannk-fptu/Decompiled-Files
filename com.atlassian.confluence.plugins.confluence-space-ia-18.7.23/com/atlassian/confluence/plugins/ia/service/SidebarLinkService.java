/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.rpc.NotPermittedException
 */
package com.atlassian.confluence.plugins.ia.service;

import com.atlassian.confluence.plugins.ia.SidebarLinkCategory;
import com.atlassian.confluence.plugins.ia.rest.SidebarLinkBean;
import com.atlassian.confluence.rpc.NotPermittedException;
import java.util.Collection;
import java.util.List;

public interface SidebarLinkService {
    public List<SidebarLinkBean> getLinksForSpace(SidebarLinkCategory var1, String var2, boolean var3);

    @Deprecated
    public void move(String var1, Integer var2, Integer var3) throws NotPermittedException;

    public void move(Integer var1, Integer var2) throws NotPermittedException;

    @Deprecated
    public void delete(String var1, Integer var2) throws NotPermittedException;

    public void delete(Integer var1) throws NotPermittedException;

    public SidebarLinkBean create(String var1, Long var2, String var3, String var4) throws NotPermittedException;

    public SidebarLinkBean create(String var1, Long var2, String var3, String var4, String var5) throws NotPermittedException;

    public SidebarLinkBean forceCreate(String var1, Long var2, String var3, String var4, String var5);

    public SidebarLinkBean create(String var1, String var2, Long var3, String var4, String var5) throws NotPermittedException;

    public SidebarLinkBean create(String var1, String var2, Long var3, String var4, String var5, String var6) throws NotPermittedException;

    public SidebarLinkBean forceCreate(String var1, String var2, Long var3, String var4, String var5, String var6);

    @Deprecated
    public void hide(String var1, Integer var2) throws NotPermittedException;

    public void hide(Integer var1) throws NotPermittedException;

    @Deprecated
    public void show(String var1, Integer var2) throws NotPermittedException;

    public void show(Integer var1) throws NotPermittedException;

    public boolean hasQuickLink(String var1, Long var2) throws NotPermittedException;

    public Collection<SidebarLinkBean> getQuickLinksForDestinationPage(String var1, Long var2) throws NotPermittedException;
}

