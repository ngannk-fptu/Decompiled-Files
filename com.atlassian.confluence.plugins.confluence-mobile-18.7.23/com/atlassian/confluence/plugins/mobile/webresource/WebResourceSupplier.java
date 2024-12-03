/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.velocity.htmlsafe.HtmlSafe
 */
package com.atlassian.confluence.plugins.mobile.webresource;

import com.atlassian.velocity.htmlsafe.HtmlSafe;

public interface WebResourceSupplier {
    @HtmlSafe
    public String getCssResourcesHtml();

    @HtmlSafe
    public String getJsResourcesHtml();
}

