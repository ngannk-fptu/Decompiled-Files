/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.ia.SidebarLink
 */
package com.atlassian.confluence.plugin.copyspace.service;

import com.atlassian.confluence.plugins.ia.SidebarLink;
import java.util.Collection;
import java.util.Optional;

public interface RewritableSideBarLinkProvider {
    public Collection<SidebarLink> fetchRewritableLinksWithinSpace(String var1);

    public Optional<SidebarLink> getSidebarLink(String var1, Long var2);
}

