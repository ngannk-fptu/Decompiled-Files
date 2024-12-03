/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.links;

import com.atlassian.confluence.content.render.xhtml.links.LinksUpdateException;

public interface AttachmentLinksUpdater {
    public String updateLinksInContent(String var1, String var2, String var3) throws LinksUpdateException;
}

