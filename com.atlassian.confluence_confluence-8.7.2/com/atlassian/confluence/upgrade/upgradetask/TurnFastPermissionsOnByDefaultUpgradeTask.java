/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.upgrade.AbstractUpgradeTask
 *  com.atlassian.confluence.upgrade.DatabaseUpgradeTask
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.security.denormalisedpermissions.impl.FastPermissionsEnabler;
import com.atlassian.confluence.upgrade.AbstractUpgradeTask;
import com.atlassian.confluence.upgrade.DatabaseUpgradeTask;

public class TurnFastPermissionsOnByDefaultUpgradeTask
extends AbstractUpgradeTask
implements DatabaseUpgradeTask {
    private static final String BUILD_NUMBER = "9101";
    private static final String DISABLE_FAST_PERMISSIONS_ENABLING_PARAMETER_NAME = "confluence.disable-fast-permissions-enabling-on-upgrade";
    private final boolean disableFastPermissionsEnabling;
    private final FastPermissionsEnabler fastPermissionsEnabler;

    public TurnFastPermissionsOnByDefaultUpgradeTask(FastPermissionsEnabler fastPermissionsEnabler) {
        this.fastPermissionsEnabler = fastPermissionsEnabler;
        this.disableFastPermissionsEnabling = Boolean.getBoolean(DISABLE_FAST_PERMISSIONS_ENABLING_PARAMETER_NAME);
    }

    @VisibleForTesting
    public TurnFastPermissionsOnByDefaultUpgradeTask(FastPermissionsEnabler fastPermissionsEnabler, boolean disableFastPermissionsEnabling) {
        this.fastPermissionsEnabler = fastPermissionsEnabler;
        this.disableFastPermissionsEnabling = disableFastPermissionsEnabling;
    }

    public boolean runOnSpaceImport() {
        return false;
    }

    public String getBuildNumber() {
        return BUILD_NUMBER;
    }

    public boolean breaksBackwardCompatibility() {
        return false;
    }

    public void doUpgrade() throws Exception {
        if (this.disableFastPermissionsEnabling) {
            log.warn("Fast permissions won't be enabled on Confluence upgrade because '{}' parameter was set to true", (Object)DISABLE_FAST_PERMISSIONS_ENABLING_PARAMETER_NAME);
            return;
        }
        log.info("Confluence has been upgraded, fast permissions will be turned on to make Confluence more performant.");
        this.fastPermissionsEnabler.turnFastPermissionsOn();
    }
}

