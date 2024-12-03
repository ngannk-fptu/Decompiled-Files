/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableCollection
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMultimap
 *  com.google.common.collect.Multimaps
 *  javax.annotation.concurrent.ThreadSafe
 */
package com.atlassian.confluence.plugins.ia;

import com.atlassian.confluence.plugins.ia.SidebarLink;
import com.atlassian.confluence.plugins.ia.SidebarLinkCategory;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimaps;
import java.io.Serializable;
import java.util.Collection;
import java.util.stream.Collectors;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class SidebarLinks
implements Serializable {
    private final ImmutableCollection<SidebarLink> links;
    private final ImmutableMultimap<SidebarLinkCategory, SidebarLink> linksByCategory;

    public SidebarLinks(Iterable<SidebarLink> links) {
        this.linksByCategory = Multimaps.index(links, SidebarLink::getCategory);
        this.links = ImmutableList.copyOf(links);
    }

    public Collection<SidebarLink> getLinks(SidebarLinkCategory category) {
        return this.linksByCategory.get((Object)category);
    }

    public Collection<SidebarLink> getAllLinks() {
        return this.links;
    }

    public Collection<SidebarLink> getLinksByDestPage(SidebarLinkCategory category, long destPageId) {
        return this.getLinks(category).stream().filter(link -> link.getDestPageId() == destPageId).collect(Collectors.toList());
    }
}

