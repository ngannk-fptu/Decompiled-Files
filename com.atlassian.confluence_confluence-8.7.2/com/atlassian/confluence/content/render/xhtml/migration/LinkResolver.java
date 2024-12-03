/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.xhtml.api.Link;

public interface LinkResolver {
    public Link resolve(String var1, PageContext var2);
}

