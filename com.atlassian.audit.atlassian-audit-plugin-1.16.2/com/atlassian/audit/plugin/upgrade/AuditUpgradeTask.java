/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.upgrade.PluginUpgradeTask
 *  javax.annotation.Nonnull
 */
package com.atlassian.audit.plugin.upgrade;

import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import javax.annotation.Nonnull;

public abstract class AuditUpgradeTask
implements PluginUpgradeTask {
    @Nonnull
    public String getPluginKey() {
        return "com.atlassian.audit.atlassian-audit-plugin";
    }
}

