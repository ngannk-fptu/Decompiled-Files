/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 */
package com.atlassian.confluence.userstatus;

import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;

@Deprecated
public interface StatusTextRenderer {
    @HtmlSafe
    public String render(String var1);
}

