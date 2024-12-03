/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.app;

import com.atlassian.migration.app.BaseAppCloudMigrationListener;
import com.atlassian.migration.app.MigrationDetailsV1;

public interface AppCloudMigrationListenerV1
extends BaseAppCloudMigrationListener {
    public void onStartAppMigration(String var1, MigrationDetailsV1 var2);
}

