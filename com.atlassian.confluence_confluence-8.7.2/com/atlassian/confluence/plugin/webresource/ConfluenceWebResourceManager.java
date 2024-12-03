/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceManager
 */
package com.atlassian.confluence.plugin.webresource;

import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceManager;
import java.io.Writer;
import java.util.Map;

public interface ConfluenceWebResourceManager
extends WebResourceManager {
    @Deprecated
    public void includeResources(Writer var1);

    @Deprecated
    public String getRequiredResources();

    @Deprecated
    public void requireResource(String var1, Writer var2);

    @Deprecated
    public String getResourceTags(String var1);

    @Deprecated
    public String getStaticResourcePrefix();

    @Deprecated
    public String getStaticResourcePrefix(UrlMode var1);

    @Deprecated
    public String getStaticResourcePrefix(String var1);

    @Deprecated
    public String getStaticResourcePrefix(String var1, UrlMode var2);

    @Deprecated
    public String getResources();

    public String getCssResources();

    public String getCssResources(String var1);

    public String getJsResources();

    public String getThemeJsResources(String var1);

    public String getGlobalCssResourcePrefix();

    public String getSpaceCssPrefix(String var1);

    public String getResourceContent(String var1);

    public void requireResourcesForContext(String var1);

    public boolean putMetadata(String var1, String var2);

    public Map<String, String> getMetadata();

    public String getAdminCssResources();

    public String getEditorCssResources(String var1);
}

