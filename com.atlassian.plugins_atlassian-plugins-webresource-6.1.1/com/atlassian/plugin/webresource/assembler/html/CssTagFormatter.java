/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.UrlMode
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.assembler.html;

import com.atlassian.plugin.webresource.CssWebResource;
import com.atlassian.plugin.webresource.assembler.ResourceUrls;
import com.atlassian.plugin.webresource.assembler.html.HtmlTagFormatter;
import com.atlassian.webresource.api.UrlMode;
import java.util.Objects;
import javax.annotation.Nonnull;

final class CssTagFormatter
implements HtmlTagFormatter {
    private final CssWebResource formatter = new CssWebResource();
    private final UrlMode urlMode;

    CssTagFormatter(@Nonnull UrlMode urlMode) {
        this.urlMode = Objects.requireNonNull(urlMode, "The url mode is mandatory for the creation of CssTagFormatter.");
    }

    @Override
    @Nonnull
    public String format(@Nonnull ResourceUrls resourceUrls) {
        Objects.requireNonNull(resourceUrls, "The resource urls are mandatory for the creation of the script tag");
        return this.formatter.formatResource(resourceUrls.getPluginUrlResource().getStaticUrl(this.urlMode), resourceUrls.getPluginUrlResource().getParams().all());
    }

    @Override
    public boolean matches(@Nonnull String resourceName) {
        Objects.requireNonNull(resourceName, "The resource name is mandatory for the comparison.");
        return this.formatter.matches(resourceName);
    }
}

