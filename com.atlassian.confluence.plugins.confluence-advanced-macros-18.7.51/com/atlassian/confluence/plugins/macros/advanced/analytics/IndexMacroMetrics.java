/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.confluence.plugins.macros.advanced.analytics;

import com.atlassian.analytics.api.annotations.EventName;

@EventName(value="confluence.macro.metrics.index")
public class IndexMacroMetrics {
    private final boolean permissionsExempt;
    private final int pagesTotal;
    private final long durationMs;
    private final boolean bulkPermissionsCalled;
    private final boolean bulkPermissionsUpAndRunning;
    private final int macroLimit;
    private final boolean renderingSkipped;

    public IndexMacroMetrics(boolean permissionsExempt, int pagesTotal, long durationMs, boolean bulkPermissionsCalled, boolean bulkPermissionsUpAndRunning, int macroLimit, boolean renderingSkipped) {
        this.permissionsExempt = permissionsExempt;
        this.pagesTotal = pagesTotal;
        this.durationMs = durationMs;
        this.bulkPermissionsCalled = bulkPermissionsCalled;
        this.bulkPermissionsUpAndRunning = bulkPermissionsUpAndRunning;
        this.macroLimit = macroLimit;
        this.renderingSkipped = renderingSkipped;
    }

    public boolean isPermissionsExempt() {
        return this.permissionsExempt;
    }

    public int getPagesTotal() {
        return this.pagesTotal;
    }

    public long getDurationMs() {
        return this.durationMs;
    }

    public boolean isBulkPermissionsCalled() {
        return this.bulkPermissionsCalled;
    }

    public boolean isBulkPermissionsUpAndRunning() {
        return this.bulkPermissionsUpAndRunning;
    }

    public int getMacroLimit() {
        return this.macroLimit;
    }

    public boolean isRenderingSkipped() {
        return this.renderingSkipped;
    }
}

