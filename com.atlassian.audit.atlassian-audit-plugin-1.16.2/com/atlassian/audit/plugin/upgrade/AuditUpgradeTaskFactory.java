/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.upgrade.spi.UpgradeTask
 *  com.atlassian.upgrade.spi.UpgradeTaskFactory
 *  javax.annotation.Nonnull
 */
package com.atlassian.audit.plugin.upgrade;

import com.atlassian.audit.plugin.upgrade.UpgradeTaskCollector;
import com.atlassian.audit.plugin.upgrade.UtfUpgradeTaskAdaptor;
import com.atlassian.upgrade.spi.UpgradeTask;
import com.atlassian.upgrade.spi.UpgradeTaskFactory;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class AuditUpgradeTaskFactory
implements UpgradeTaskFactory {
    private final UpgradeTaskCollector upgradeTaskCollector;

    public AuditUpgradeTaskFactory(UpgradeTaskCollector upgradeTaskCollector) {
        this.upgradeTaskCollector = Objects.requireNonNull(upgradeTaskCollector);
    }

    public String getProductDisplayName() {
        return "Advanced Auditing";
    }

    public String getProductMinimumVersion() {
        return "1.0.0";
    }

    public int getMinimumBuildNumber() {
        return 0;
    }

    @Nonnull
    public Collection<UpgradeTask> getAllUpgradeTasks() {
        return this.upgradeTaskCollector.findAll().stream().map(UtfUpgradeTaskAdaptor::new).collect(Collectors.toList());
    }
}

