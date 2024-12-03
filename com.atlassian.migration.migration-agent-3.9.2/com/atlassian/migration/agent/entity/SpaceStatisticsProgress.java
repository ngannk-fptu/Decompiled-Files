/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.entity;

import lombok.Generated;

public class SpaceStatisticsProgress {
    private final int spacesCount;
    private final int spaceStatsCount;
    private boolean isCalculating;

    public double getPercentage() {
        if (this.spacesCount == 0) {
            return 1.0;
        }
        return (double)this.spaceStatsCount / (double)this.spacesCount;
    }

    @Generated
    public int getSpacesCount() {
        return this.spacesCount;
    }

    @Generated
    public int getSpaceStatsCount() {
        return this.spaceStatsCount;
    }

    @Generated
    public boolean isCalculating() {
        return this.isCalculating;
    }

    @Generated
    public SpaceStatisticsProgress(int spacesCount, int spaceStatsCount, boolean isCalculating) {
        this.spacesCount = spacesCount;
        this.spaceStatsCount = spaceStatsCount;
        this.isCalculating = isCalculating;
    }
}

