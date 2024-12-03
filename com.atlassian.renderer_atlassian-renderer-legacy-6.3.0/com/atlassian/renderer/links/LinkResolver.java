/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.links;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.links.Link;
import java.util.List;

public interface LinkResolver {
    public Link createLink(RenderContext var1, String var2);

    public List extractLinkTextList(String var1);

    public List extractLinks(RenderContext var1, String var2);

    public String removeLinkBrackets(String var1);
}

