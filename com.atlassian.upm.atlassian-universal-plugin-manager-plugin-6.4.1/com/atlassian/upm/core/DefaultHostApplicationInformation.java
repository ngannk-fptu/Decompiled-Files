/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core;

import com.atlassian.upm.core.HostingType;

public interface DefaultHostApplicationInformation {
    public boolean canInstallLegacyPlugins();

    public boolean canInstallXmlPlugins();

    public String getServerId();

    public HostingType getHostingType();
}

