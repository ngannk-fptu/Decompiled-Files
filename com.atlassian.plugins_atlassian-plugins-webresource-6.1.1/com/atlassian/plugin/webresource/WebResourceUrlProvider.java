/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 */
package com.atlassian.plugin.webresource;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.webresource.UrlMode;

public interface WebResourceUrlProvider {
    public String getStaticResourcePrefix(UrlMode var1);

    public String getStaticResourcePrefix(String var1, UrlMode var2);

    public String getStaticResourcePrefix(String var1, String var2, UrlMode var3);

    public String getStaticPluginResourceUrl(String var1, String var2, UrlMode var3);

    public String getStaticPluginResourceUrl(ModuleDescriptor<?> var1, String var2, UrlMode var3);

    public String getResourceUrl(String var1, String var2);

    public String getBaseUrl();

    public String getBaseUrl(UrlMode var1);
}

