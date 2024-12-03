/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.upgrade.AbstractUpgradeTask
 *  com.atlassian.confluence.upgrade.DatabaseUpgradeTask
 *  com.google.common.collect.Iterables
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.core.BatchOperationManager;
import com.atlassian.confluence.schedule.persistence.dao.CachedScheduledJobDao;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.upgrade.AbstractUpgradeTask;
import com.atlassian.confluence.upgrade.DatabaseUpgradeTask;
import com.google.common.collect.Iterables;
import java.util.ArrayList;

public class MigrateScheduledJobCacheUpgradeTask
extends AbstractUpgradeTask
implements DatabaseUpgradeTask {
    private final BandanaManager bandanaManager;
    private final BatchOperationManager batchOperationManager;
    private final BandanaContext context = new ConfluenceBandanaContext(CachedScheduledJobDao.CONFIGURATION_CONTEXT_KEY);

    public MigrateScheduledJobCacheUpgradeTask(BandanaManager bandanaManager, BatchOperationManager batchOperationManager) {
        this.bandanaManager = bandanaManager;
        this.batchOperationManager = batchOperationManager;
    }

    public String getBuildNumber() {
        return "9001";
    }

    public String getShortDescription() {
        return "Update Scheduled Job Configuration Bandana cache to new key format";
    }

    public void doUpgrade() throws Exception {
        Iterable keys = this.bandanaManager.getKeys(this.context);
        ArrayList safeIterateKeys = new ArrayList();
        keys.forEach(safeIterateKeys::add);
        this.batchOperationManager.applyInBatches(safeIterateKeys, Iterables.size(safeIterateKeys), key -> {
            this.bandanaManager.setValue(this.context, MigrateScheduledJobCacheUpgradeTask.serialisedJobKeyToId(key), this.bandanaManager.getValue(this.context, key));
            this.bandanaManager.removeValue(this.context, key);
            return null;
        });
    }

    private static String serialisedJobKeyToId(String serialisedJobKey) {
        return serialisedJobKey.substring(serialisedJobKey.indexOf("#") + 1);
    }

    public boolean runOnSpaceImport() {
        return false;
    }

    public boolean breaksBackwardCompatibility() {
        return true;
    }
}

