/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.activeobjects.spi;

public interface PluginInformation {
    public boolean isAvailable();

    public String getPluginName();

    public String getPluginKey();

    public String getPluginVersion();

    public String getVendorName();

    public String getVendorUrl();
}

