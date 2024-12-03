/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.version;

import java.time.LocalDate;

public class PluginVersionInfo {
    private Boolean isOutdated;
    private LocalDate upgradeBy;

    public PluginVersionInfo(Boolean isOutdated, LocalDate upgradeBy) {
        this.isOutdated = isOutdated;
        this.upgradeBy = upgradeBy;
    }

    public Boolean isOutdated() {
        return this.isOutdated;
    }

    public LocalDate getUpgradeBy() {
        return this.upgradeBy;
    }
}

