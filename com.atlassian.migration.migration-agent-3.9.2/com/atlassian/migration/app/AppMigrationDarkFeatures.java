/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 */
package com.atlassian.migration.app;

import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 7, 1}, k=1, xi=48, d1={"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0005\bf\u0018\u00002\u00020\u0001J\b\u0010\n\u001a\u00020\u0007H\u0016J\b\u0010\u000b\u001a\u00020\u0007H\u0016R\u0012\u0010\u0002\u001a\u00020\u0003X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0004\u0010\u0005R\u0012\u0010\u0006\u001a\u00020\u0007X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\u0006\u0010\bR\u0012\u0010\t\u001a\u00020\u0007X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\t\u0010\b\u00a8\u0006\f"}, d2={"Lcom/atlassian/migration/app/AppMigrationDarkFeatures;", "", "appMigrationUploadChunkSize", "", "getAppMigrationUploadChunkSize", "()D", "isAppVendorCheckMaaFilteringEnabled", "", "()Z", "isForgeMigrationPathEnabled", "isFileCachingEnabled", "multiPartUploadParallelModeEnabled", "app-migration-assistant"})
public interface AppMigrationDarkFeatures {
    public boolean isAppVendorCheckMaaFilteringEnabled();

    public double getAppMigrationUploadChunkSize();

    public boolean multiPartUploadParallelModeEnabled();

    public boolean isFileCachingEnabled();

    public boolean isForgeMigrationPathEnabled();

    @Metadata(mv={1, 7, 1}, k=3, xi=48)
    public static final class DefaultImpls {
        public static boolean multiPartUploadParallelModeEnabled(@NotNull AppMigrationDarkFeatures $this) {
            return false;
        }

        public static boolean isFileCachingEnabled(@NotNull AppMigrationDarkFeatures $this) {
            return false;
        }
    }
}

