/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.internal.diagnostics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.event.api.AsynchronousPreferred;

@AsynchronousPreferred
@EventName(value="confluence.diagnostics.low-disk-space")
public class LowDiskSpaceAnalyticsEvent {
    private final Long freeDiskSpaceInMb;
    private final String folderType;
    private final Long totalDiskSizeInMb;
    private final Long minDiskSizeThresholdInMb;

    public LowDiskSpaceAnalyticsEvent(DiskType folderType, Long freeDiskSpaceInMb, Long totalDiskSizeInMb, Long minDiskSizeThresholdInMb) {
        this.folderType = folderType.name();
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

    public static enum DiskType {
        HOME,
        SHARED;

    }
}

