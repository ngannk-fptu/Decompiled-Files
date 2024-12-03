/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 */
package com.atlassian.upm.core;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Plugin;
import java.io.InputStream;
import java.net.URI;

public interface PluginMetadataAccessor {
    public boolean isUserInstalled(Plugin var1);

    public boolean isUserInstalled(com.atlassian.plugin.Plugin var1);

    public boolean isOptional(Plugin var1);

    public boolean isOptional(Plugin.Module var1);

    public Option<URI> getConfigureUrl(Plugin var1);

    public Option<URI> getPostInstallUri(Plugin var1);

    public Option<URI> getPostUpdateUri(Plugin var1);

    public Option<InputStream> getPluginIconInputStream(Plugin var1);

    public Option<InputStream> getPluginLogoInputStream(Plugin var1);

    public Option<InputStream> getPluginBannerInputStream(Plugin var1);

    public Option<InputStream> getVendorIconInputStream(Plugin var1);

    public Option<InputStream> getVendorLogoInputStream(Plugin var1);
}

