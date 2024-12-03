/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.troubleshooting.healthcheck.checks.analytic;

import com.atlassian.analytics.api.annotations.EventName;

@EventName(value="healthchecks.check.localhome.diskspace.info")
public class LocalHomeFreeSpaceEvent {
    private final long freeSpaceBytes;
    private final long totalSpaceBytes;
    private final int recommendFreeSpacePercentage;
    private final long recommendFreeSpaceGB;

    public LocalHomeFreeSpaceEvent(long freeSpaceBytes, long totalSpaceBytes, int recommendFreeSpacePercentage, long recommendFreeSpaceGB) {
        this.freeSpaceBytes = freeSpaceBytes;
        this.totalSpaceBytes = totalSpaceBytes;
        this.recommendFreeSpacePercentage = recommendFreeSpacePercentage;
        this.recommendFreeSpaceGB = recommendFreeSpaceGB;
    }

    public long getFreeSpaceBytes() {
        return this.freeSpaceBytes;
    }

    public long getTotalSpaceBytes() {
        return this.totalSpaceBytes;
    }

    public int getRecommendFreeSpacePercentage() {
        return this.recommendFreeSpacePercentage;
    }

    public long getRecommendFreeSpaceGB() {
        return this.recommendFreeSpaceGB;
    }
}

