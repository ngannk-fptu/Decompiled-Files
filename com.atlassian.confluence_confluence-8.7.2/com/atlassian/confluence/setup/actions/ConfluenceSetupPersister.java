/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.config.setup.SetupPersister
 */
package com.atlassian.confluence.setup.actions;

import com.atlassian.annotations.Internal;
import com.atlassian.config.setup.SetupPersister;

@Internal
public interface ConfluenceSetupPersister
extends SetupPersister {
    public boolean isSetupTypeClustered();

    public boolean isSetupTypeMigration();

    public void convertToClusterMigration();

    public void convertToStandaloneMigration();

    public void resetCancelledMigration();

    public void setMigrationCancelled();

    public void removeClusterSetupEntries();

    public void synchSetupStackWithConfigRecord(String var1);
}

