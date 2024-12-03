/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.plugin.servlet.DownloadableResource
 */
package com.atlassian.plugin.webresource;

import com.atlassian.annotations.Internal;
import com.atlassian.plugin.servlet.DownloadableResource;
import com.atlassian.plugin.webresource.impl.Globals;
import java.util.Map;

@Deprecated
public interface PluginResourceLocator {
    @Deprecated
    public boolean matches(String var1);

    @Deprecated
    public DownloadableResource getDownloadableResource(String var1, Map<String, String> var2);

    @Deprecated
    @Internal
    public Globals temporaryWayToGetGlobalsDoNotUseIt();
}

