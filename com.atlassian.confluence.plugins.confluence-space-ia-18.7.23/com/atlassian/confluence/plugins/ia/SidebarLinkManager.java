/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.ia;

import com.atlassian.confluence.plugins.ia.SidebarLink;
import com.atlassian.confluence.plugins.ia.SidebarLinkCategory;
import com.atlassian.confluence.plugins.ia.SidebarLinks;

public interface SidebarLinkManager {
    public SidebarLink createLink(String var1, SidebarLinkCategory var2, SidebarLink.Type var3, String var4, int var5, String var6, String var7, String var8, long var9);

    public void moveLink(SidebarLink var1, int var2, int var3);

    public void deleteLink(SidebarLink var1);

    public void deleteLinks(long var1, SidebarLink.Type var3);

    public void deleteLinksForSpace(String var1);

    public void hideLink(SidebarLink var1);

    public void showLink(SidebarLink var1);

    public SidebarLink findById(int var1);

    public SidebarLinks findBySpace(String var1);
}

