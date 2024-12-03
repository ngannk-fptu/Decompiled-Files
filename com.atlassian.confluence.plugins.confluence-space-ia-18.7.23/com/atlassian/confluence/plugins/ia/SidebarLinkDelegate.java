/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.ia;

import com.atlassian.confluence.plugins.ia.SidebarLink;
import com.atlassian.confluence.plugins.ia.rest.SidebarLinkBean;

public interface SidebarLinkDelegate {
    public SidebarLink createSidebarLink(String var1, Long var2, SidebarLink.Type var3, String var4, String var5, String var6);

    public SidebarLinkBean getSidebarLinkBean(SidebarLink var1);
}

