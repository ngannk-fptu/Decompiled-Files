/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal.platform.analytics.directory;

import com.atlassian.diagnostics.internal.platform.analytics.EventFactory;
import com.atlassian.diagnostics.internal.platform.analytics.directory.LowDirectoryDiskSpaceAnalyticsEvent;
import com.atlassian.diagnostics.internal.platform.monitor.operatingsystem.directory.DirectoryType;
import javax.annotation.Nonnull;

public abstract class LowDirectorySpaceEventFactory
implements EventFactory {
    public LowDirectoryDiskSpaceAnalyticsEvent create(@Nonnull DirectoryType folderType, @Nonnull Long freeDiskSpaceInMb, @Nonnull Long totalDiskSizeInMb, @Nonnull Long minDiskSizeThresholdInMb) {
        return new LowDirectoryDiskSpaceAnalyticsEvent(folderType, freeDiskSpaceInMb, totalDiskSizeInMb, minDiskSizeThresholdInMb);
    }

    public static LowDirectorySpaceEventFactory defaultFactory() {
        return new LowDirectorySpaceEventFactory(){

            @Override
            public LowDirectoryDiskSpaceAnalyticsEvent create(@Nonnull DirectoryType folderType, @Nonnull Long freeDiskSpaceInMb, @Nonnull Long totalDiskSizeInMb, @Nonnull Long minDiskSizeThresholdInMb) {
                return super.create(folderType, freeDiskSpaceInMb, totalDiskSizeInMb, minDiskSizeThresholdInMb);
            }
        };
    }
}

