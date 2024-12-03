/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.util.migration;

import com.atlassian.user.EntityException;
import com.atlassian.user.util.migration.MigrationProgressListener;
import com.atlassian.user.util.migration.MigratorConfiguration;

public interface EntityMigrator {
    public void migrate(MigratorConfiguration var1, MigrationProgressListener var2) throws EntityException;

    public boolean hasExistingUsers();
}

