/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ConfigurationException
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.upgrade;

import com.atlassian.config.ConfigurationException;
import com.atlassian.confluence.upgrade.UpgradeException;
import com.atlassian.confluence.upgrade.UpgradeTask;
import java.util.Optional;
import javax.annotation.Nullable;

public interface UpgradeFinalizationManager {
    public void finalizeIfNeeded() throws UpgradeException;

    public boolean isPendingDatabaseFinalization();

    public boolean isPendingLocalFinalization();

    public Optional<Run> getLastRun();

    public void markAsFullyFinalized(boolean var1) throws ConfigurationException;

    public static interface Run {
        public long getRequestTimestamp();

        @Nullable
        public Long completedTimestamp();

        public boolean isDatabaseUpgrade();

        @Nullable
        public UpgradeException getException();

        @Nullable
        public UpgradeTask getLastTask();
    }
}

