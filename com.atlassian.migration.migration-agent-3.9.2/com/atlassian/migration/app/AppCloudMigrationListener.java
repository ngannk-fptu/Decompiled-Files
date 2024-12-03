/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.app;

import com.atlassian.migration.app.BaseAppCloudMigrationListener;
import com.atlassian.migration.app.MigrationDetails;

@Deprecated
public interface AppCloudMigrationListener
extends BaseAppCloudMigrationListener {
    public void onStartAppMigration(String var1, MigrationDetails var2);
}

