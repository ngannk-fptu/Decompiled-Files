/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.links;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.links.GenericLinkParser;
import com.atlassian.renderer.links.Link;
import java.text.ParseException;

public interface ContentLinkResolver {
    public Link createContentLink(RenderContext var1, GenericLinkParser var2) throws ParseException;
}

