/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh;

import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.SitemeshBuffer;
import java.io.IOException;

public interface PageParser {
    public Page parse(SitemeshBuffer var1) throws IOException;

    @Deprecated
    public Page parse(char[] var1) throws IOException;
}

