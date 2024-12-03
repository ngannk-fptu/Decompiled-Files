/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Page
 */
package com.atlassian.confluence.extra.flyingpdf.html;

import com.atlassian.confluence.extra.flyingpdf.html.LinkFixer;
import com.atlassian.confluence.pages.Page;
import java.util.Collection;
import java.util.Collections;

public class LinkRenderingDetails {
    private final Collection<Page> internalPages;
    private final LinkFixer.InternalPageStrategy linkStrategy;

    public static LinkRenderingDetails anchors() {
        return new LinkRenderingDetails(Collections.emptyList(), LinkFixer.InternalPageStrategy.ANCHOR);
    }

    public LinkRenderingDetails(Collection<Page> internalPages, LinkFixer.InternalPageStrategy linkStrategy) {
        this.internalPages = internalPages;
        this.linkStrategy = linkStrategy;
    }

    public Collection<Page> getInternalPages() {
        return this.internalPages;
    }

    public LinkFixer.InternalPageStrategy getLinkStrategy() {
        return this.linkStrategy;
    }
}

