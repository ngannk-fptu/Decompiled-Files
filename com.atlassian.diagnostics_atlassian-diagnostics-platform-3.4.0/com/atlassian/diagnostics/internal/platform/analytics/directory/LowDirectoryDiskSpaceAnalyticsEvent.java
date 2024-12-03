/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal.platform.analytics.directory;

import com.atlassian.diagnostics.internal.platform.monitor.operatingsystem.directory.DirectoryType;
import javax.annotation.Nonnull;

public class LowDirectoryDiskSpaceAnalyticsEvent {
    private final Long freeDiskSpaceInMb;
    private final String folderType;
    private final Long totalDiskSizeInMb;
    private final Long minDiskSizeThresholdInMb;

    protected LowDirectoryDiskSpaceAnalyticsEvent(@Nonnull DirectoryType folderType, @Nonnull Long freeDiskSpaceInMb, @Nonnull Long totalDiskSizeInMb, @Nonnull Long minDiskSizeThresholdInMb) {
        this.folderType = folderType.name;
        this.totalDiskSizeInMb = totalDiskSizeInMb;
        this.freeDiskSpaceInMb = freeDiskSpaceInMb;
        this.minDiskSizeThresholdInMb = minDiskSizeThresholdInMb;
    }

    public String getFolderType() {
        return this.folderType;
    }

    public Long getTotalDiskSizeInMb() {
        return this.totalDiskSizeInMb;
    }

    public Long getFreeDiskSpaceInMb() {
        return this.freeDiskSpaceInMb;
    }

    public Long getMinDiskSizeThresholdInMb() {
        return this.minDiskSizeThresholdInMb;
    }
}

