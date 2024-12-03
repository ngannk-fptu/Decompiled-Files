/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh;

import com.opensymphony.module.sitemesh.Page;
import java.io.IOException;
import java.io.Writer;

public interface HTMLPage
extends Page {
    public void writeHead(Writer var1) throws IOException;

    public String getHead();

    public boolean isFrameSet();

    public void setFrameSet(boolean var1);
}

