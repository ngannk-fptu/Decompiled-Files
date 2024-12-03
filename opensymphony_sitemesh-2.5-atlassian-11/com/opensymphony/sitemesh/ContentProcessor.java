/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.sitemesh;

import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.sitemesh.Content;
import com.opensymphony.sitemesh.SiteMeshContext;
import java.io.IOException;

public interface ContentProcessor {
    public boolean handles(SiteMeshContext var1);

    public boolean handles(String var1);

    public Content build(SitemeshBuffer var1, SiteMeshContext var2) throws IOException;
}

