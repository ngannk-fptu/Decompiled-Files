/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.migration.MigrationDarkFeaturesManager;
import com.atlassian.migration.app.AppMigrationDarkFeatures;

public class AppMigrationDarkFeaturesImpl
implements AppMigrationDarkFeatures {
    private final MigrationDarkFeaturesManager migrationDarkFeaturesManager;

    public AppMigrationDarkFeaturesImpl(MigrationDarkFeaturesManager migrationDarkFeaturesManager) {
        this.migrationDarkFeaturesManager = migrationDarkFeaturesManager;
    }

    @Override
    public boolean isAppVendorCheckMaaFilteringEnabled() {
        return this.migrationDarkFeaturesManager.appVendorChecksFilterEnabled();
    }

    @Override
    public double getAppMigrationUploadChunkSize() {
        return this.migrationDarkFeaturesManager.isTurboChunkSizeEnabled() ? 25.0 : 5.0;
    }

    @Override
    public boolean multiPartUploadParallelModeEnabled() {
        return this.migrationDarkFeaturesManager.isParallelAppDataUploadsEnabled();
    }

    @Override
    public boolean isForgeMigrationPathEnabled() {
        return this.migrationDarkFeaturesManager.isForgeMigrationPathEnabled();
    }

    @Override
    public boolean isFileCachingEnabled() {
        return false;
    }
}

