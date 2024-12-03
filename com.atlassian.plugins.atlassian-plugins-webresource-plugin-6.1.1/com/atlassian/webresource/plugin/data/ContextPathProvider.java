/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.json.marshal.wrapped.JsonableString
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceIntegration
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 */
package com.atlassian.webresource.plugin.data;

import com.atlassian.json.marshal.Jsonable;
import com.atlassian.json.marshal.wrapped.JsonableString;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceIntegration;
import com.atlassian.webresource.api.data.WebResourceDataProvider;

public class ContextPathProvider
implements WebResourceDataProvider {
    private final WebResourceIntegration webResourceIntegration;

    public ContextPathProvider(WebResourceIntegration webResourceIntegration) {
        this.webResourceIntegration = webResourceIntegration;
    }

    public Jsonable get() {
        return new JsonableString(this.webResourceIntegration.getBaseUrl(UrlMode.RELATIVE));
    }
}

