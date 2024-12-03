/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.version;

import com.atlassian.migration.agent.service.version.PluginVersionInfo;
import java.util.Optional;

public interface PluginVersionManager {
    public static final String UNKNOWN_VERSION = "UNKNOWN";

    public String getPluginVersion();

    public Boolean isTestVersion();

    public Optional<PluginVersionInfo> getPluginVersionInfo(String var1);
}

