/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh;

import com.opensymphony.module.sitemesh.PageParser;

public interface PageParserSelector {
    public boolean shouldParsePage(String var1);

    public PageParser getPageParser(String var1);
}

