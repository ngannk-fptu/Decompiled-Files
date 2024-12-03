/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.audit.plugin.upgrade;

import com.atlassian.audit.plugin.upgrade.AuditUpgradeTask;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class UpgradeTaskCollector {
    private final Collection<AuditUpgradeTask> upgradeTasks;

    public UpgradeTaskCollector(Collection<AuditUpgradeTask> upgradeTasks) {
        this.upgradeTasks = new ArrayList<AuditUpgradeTask>(Objects.requireNonNull(upgradeTasks));
    }

    public Collection<AuditUpgradeTask> findAll() {
        return Collections.unmodifiableCollection(this.upgradeTasks);
    }
}

