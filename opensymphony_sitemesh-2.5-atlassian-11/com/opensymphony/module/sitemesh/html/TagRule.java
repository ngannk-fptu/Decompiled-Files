/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.html;

import com.opensymphony.module.sitemesh.html.HTMLProcessorContext;
import com.opensymphony.module.sitemesh.html.Tag;

public interface TagRule {
    public void setContext(HTMLProcessorContext var1);

    public boolean shouldProcess(String var1);

    public void process(Tag var1);
}

