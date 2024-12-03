/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.flyingpdf.analytic;

import com.atlassian.confluence.extra.flyingpdf.analytic.EnvironmentInfo;
import com.atlassian.confluence.extra.flyingpdf.analytic.ExportResults;
import com.atlassian.confluence.extra.flyingpdf.analytic.PageExportMetrics;
import java.util.HashSet;
import java.util.Set;

public class SpaceExportMetrics {
    private EnvironmentInfo environmentInfo = new EnvironmentInfo();
    private Set<PageExportMetrics> pageExportMetrics = new HashSet<PageExportMetrics>();
    private ExportResults exportResults = new ExportResults();
    private int confluencePages = -1;
    private int totalTime = -1;
    private int tocBuildTime = -1;
    private int joinTime = -1;

    public EnvironmentInfo getEnvironmentInfo() {
        return this.environmentInfo;
    }

    public void setEnvironmentInfo(EnvironmentInfo environmentInfo) {
        this.environmentInfo = environmentInfo;
    }

    public Set<PageExportMetrics> getPageExportMetrics() {
        return this.pageExportMetrics;
    }

    public void setPageExportMetrics(Set<PageExportMetrics> pageExportMetrics) {
        this.pageExportMetrics = pageExportMetrics;
    }

    public int getTotalTime() {
        return this.totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public ExportResults getExportResults() {
        return this.exportResults;
    }

    public void setExportResults(ExportResults exportResults) {
        this.exportResults = exportResults;
    }

    public int getConfluencePages() {
        return this.confluencePages;
    }

    public void setConfluencePages(int confluencePages) {
        this.confluencePages = confluencePages;
    }

    public int getTocBuildTime() {
        return this.tocBuildTime;
    }

    public void setTocBuildTime(int tocBuildTime) {
        this.tocBuildTime = tocBuildTime;
    }

    public int getJoinTime() {
        return this.joinTime;
    }

    public void setJoinTime(int joinTime) {
        this.joinTime = joinTime;
    }
}

