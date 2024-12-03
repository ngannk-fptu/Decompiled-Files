/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.preupgrade;

import com.atlassian.troubleshooting.preupgrade.model.PreUpgradeInfoDto;
import java.util.Optional;

public interface PreUpgradePlanningManager {
    public Optional<PreUpgradeInfoDto> getPreUpgradeInfo(boolean var1);

    public boolean isPreUpgradePageAvailable();
}

