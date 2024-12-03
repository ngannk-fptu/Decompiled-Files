/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.velocity.htmlsafe.HtmlSafe
 */
package com.atlassian.confluence.search.summary;

import com.atlassian.velocity.htmlsafe.HtmlSafe;

public interface HitHighlighter {
    @HtmlSafe
    public String getSummary(String var1);

    @HtmlSafe
    public String highlightText(String var1);
}

