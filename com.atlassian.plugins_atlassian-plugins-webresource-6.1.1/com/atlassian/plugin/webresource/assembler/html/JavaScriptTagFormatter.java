/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.UrlMode
 *  com.atlassian.webresource.api.assembler.resource.PluginUrlResource
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.assembler.html;

import com.atlassian.plugin.webresource.JavascriptWebResource;
import com.atlassian.plugin.webresource.assembler.ResourceUrls;
import com.atlassian.plugin.webresource.assembler.html.HtmlTagFormatter;
import com.atlassian.webresource.api.UrlMode;
import com.atlassian.webresource.api.assembler.resource.PluginUrlResource;
import java.util.LinkedHashMap;
import java.util.Objects;
import javax.annotation.Nonnull;

class JavaScriptTagFormatter
implements HtmlTagFormatter {
    private final JavascriptWebResource formatter = new JavascriptWebResource();
    private final boolean isDeferEnabled;
    private final UrlMode urlMode;

    JavaScriptTagFormatter(@Nonnull UrlMode urlMode, boolean deferEnabled) {
        this.isDeferEnabled = deferEnabled;
        this.urlMode = Objects.requireNonNull(urlMode, "The url mode is mandatory for the creation of JavaScriptTagFormatter.");
    }

    @Override
    @Nonnull
    public String format(@Nonnull ResourceUrls resourceUrls) {
        Objects.requireNonNull(resourceUrls, "The resource urls are mandatory for the creation of the script tag");
        PluginUrlResource<?> urlResource = resourceUrls.getPluginUrlResource();
        LinkedHashMap<String, String> attributes = new LinkedHashMap<String, String>(urlResource.getParams().all());
        attributes.put("data-initially-rendered", "");
        if (this.isDeferEnabled) {
            attributes.put("defer", "");
        }
        return this.formatter.formatResource(urlResource.getStaticUrl(this.urlMode), attributes);
    }

    @Override
    public boolean matches(@Nonnull String resourceName) {
        Objects.requireNonNull(resourceName, "The resource name is mandatory for the comparison.");
        return this.formatter.matches(resourceName);
    }
}

