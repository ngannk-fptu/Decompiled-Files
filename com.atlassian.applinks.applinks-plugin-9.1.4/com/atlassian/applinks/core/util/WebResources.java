/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.velocity.htmlsafe.HtmlSafe
 */
package com.atlassian.applinks.core.util;

import com.atlassian.velocity.htmlsafe.HtmlSafe;

public class WebResources {
    private String html;

    public void setIncludedResources(String html) {
        this.html = html;
    }

    @HtmlSafe
    public String getIncludedResources() {
        return this.html;
    }
}

