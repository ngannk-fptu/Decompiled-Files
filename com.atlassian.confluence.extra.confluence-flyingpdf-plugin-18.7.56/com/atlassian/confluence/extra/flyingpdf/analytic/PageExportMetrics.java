/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.flyingpdf.analytic;

import com.atlassian.confluence.extra.flyingpdf.analytic.EnvironmentInfo;
import com.atlassian.confluence.extra.flyingpdf.analytic.ExportResults;

public class PageExportMetrics {
    private long pageId = -1L;
    private long pageRevision = -1L;
    private EnvironmentInfo environmentInfo = new EnvironmentInfo();
    private ExportResults exportResults = new ExportResults();
    private int timeMs = -1;

    public long getPageId() {
        return this.pageId;
    }

    public void setPageId(long pageId) {
        this.pageId = pageId;
    }

    public long getPageRevision() {
        return this.pageRevision;
    }

    public void setPageRevision(long pageRevision) {
        this.pageRevision = pageRevision;
    }

    public EnvironmentInfo getEnvironmentInfo() {
        return this.environmentInfo;
    }

    public void setEnvironmentInfo(EnvironmentInfo environmentInfo) {
        this.environmentInfo = environmentInfo;
    }

    public ExportResults getExportResults() {
        return this.exportResults;
    }

    public void setExportResults(ExportResults exportResults) {
        this.exportResults = exportResults;
    }

    public int getTimeMs() {
        return this.timeMs;
    }

    public void setTimeMs(int timeMs) {
        this.timeMs = timeMs;
    }
}

