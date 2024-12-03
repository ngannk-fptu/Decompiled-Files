/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource;

import com.atlassian.plugin.webresource.CssWebResource;
import com.atlassian.plugin.webresource.JavascriptWebResource;
import com.atlassian.plugin.webresource.WebResourceFilter;

public class DefaultWebResourceFilter
implements WebResourceFilter {
    private final JavascriptWebResource javascriptWebResource = new JavascriptWebResource();
    private final CssWebResource cssWebResource = new CssWebResource();

    @Override
    public boolean matches(String resourceName) {
        return this.javascriptWebResource.matches(resourceName) || this.cssWebResource.matches(resourceName);
    }
}

