/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.google.common.base.Supplier
 */
package com.atlassian.plugin.webresource;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceFilter;
import com.google.common.base.Supplier;
import java.io.Writer;

@Deprecated
public interface WebResourceManager {
    public void requireResource(String var1);

    public void includeResources(Iterable<String> var1, Writer var2, UrlMode var3);

    public void includeResources(Writer var1, UrlMode var2);

    public void includeResources(Writer var1, UrlMode var2, WebResourceFilter var3);

    public String getRequiredResources(UrlMode var1);

    public String getRequiredResources(UrlMode var1, WebResourceFilter var2);

    public void requireResource(String var1, Writer var2, UrlMode var3);

    public void requireResourcesForContext(String var1);

    public String getResourceTags(String var1, UrlMode var2);

    public <T> T executeInNewContext(Supplier<T> var1);

    @Deprecated
    public String getStaticPluginResource(String var1, String var2, UrlMode var3);

    @Deprecated
    public String getStaticPluginResource(ModuleDescriptor<?> var1, String var2, UrlMode var3);

    @Deprecated
    public String getStaticPluginResource(String var1, String var2);

    @Deprecated
    public String getStaticPluginResource(ModuleDescriptor<?> var1, String var2);

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
    public String getStaticResourcePrefix(String var1);
}

