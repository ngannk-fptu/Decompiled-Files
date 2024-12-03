/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.html.tokenizer;

import com.opensymphony.module.sitemesh.html.Tag;
import com.opensymphony.module.sitemesh.html.Text;

public interface TokenHandler {
    public boolean shouldProcessTag(String var1);

    public void tag(Tag var1);

    public void text(Text var1);

    public void warning(String var1, int var2, int var3);
}

