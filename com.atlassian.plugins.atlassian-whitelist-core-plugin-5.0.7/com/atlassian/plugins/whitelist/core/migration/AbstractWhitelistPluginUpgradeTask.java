/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.upgrade.PluginUpgradeTask
 */
package com.atlassian.plugins.whitelist.core.migration;

import com.atlassian.sal.api.upgrade.PluginUpgradeTask;

public abstract class AbstractWhitelistPluginUpgradeTask
implements PluginUpgradeTask {
    public String getPluginKey() {
        return "com.atlassian.plugins.atlassian-whitelist-core-plugin";
    }
}

