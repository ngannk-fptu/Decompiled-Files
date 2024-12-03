/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.google.common.hash.HashFunction
 *  com.google.common.hash.Hashing
 */
package com.atlassian.confluence.extra.flyingpdf.analytic;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.extra.flyingpdf.analytic.EnvironmentInfo;
import com.atlassian.confluence.extra.flyingpdf.analytic.ExportResults;
import com.atlassian.confluence.extra.flyingpdf.analytic.ExportScope;
import com.atlassian.confluence.extra.flyingpdf.analytic.ExportStatus;
import com.atlassian.confluence.extra.flyingpdf.analytic.FailureLocation;
import com.atlassian.confluence.extra.flyingpdf.analytic.PageExportMetrics;
import com.atlassian.confluence.extra.flyingpdf.analytic.SandboxStatus;
import com.atlassian.confluence.extra.flyingpdf.analytic.SpaceExportMetrics;
import com.atlassian.confluence.extra.flyingpdf.analytic.TimeStatistics;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import java.nio.charset.Charset;
import java.util.Collections;

@EventName(value="confluence.pdf.export.run")
public class PdfExportAnalyticEvent {
    private final EnvironmentInfo environmentInfo;
    private final ExportResults exportResults;
    private final int confluencePagesTotal;
    private final int confluencePagesProcessed;
    private final long failedPageIdHash;
    private final long failedPageRevisionHash;
    private final TimeStatistics timeStatistics;
    private final int totalTime;
    private final int tocBuildTime;
    private final int joinTime;
    private static final HashFunction numbersHash = Hashing.sha256();
    private static final HashFunction stringsHash = Hashing.sha256();

    public PdfExportAnalyticEvent(PageExportMetrics pageExportMetrics) {
        this.environmentInfo = pageExportMetrics.getEnvironmentInfo();
        this.exportResults = pageExportMetrics.getExportResults();
        this.confluencePagesTotal = 1;
        this.confluencePagesProcessed = 1;
        if (ExportStatus.isFail(this.exportResults.getExportStatus())) {
            this.failedPageIdHash = numbersHash.hashLong(pageExportMetrics.getPageId()).asLong();
            this.failedPageRevisionHash = numbersHash.hashLong(pageExportMetrics.getPageRevision()).asLong();
        } else {
            this.failedPageIdHash = -1L;
            this.failedPageRevisionHash = -1L;
        }
        this.timeStatistics = new TimeStatistics(Collections.singleton(pageExportMetrics));
        this.totalTime = this.timeStatistics.getMaxPageTime();
        this.tocBuildTime = -1;
        this.joinTime = -1;
    }

    public PdfExportAnalyticEvent(SpaceExportMetrics spaceExportMetrics) {
        PageExportMetrics failedPage = spaceExportMetrics.getPageExportMetrics().stream().filter(p -> ExportStatus.isFail(p.getExportResults().getExportStatus())).findAny().orElse(null);
        if (failedPage != null) {
            spaceExportMetrics.getExportResults().setExportStatus(failedPage.getExportResults().getExportStatus());
            spaceExportMetrics.getExportResults().setFailureLocation(FailureLocation.PAGE);
            this.failedPageIdHash = numbersHash.hashLong(failedPage.getPageId()).asLong();
            this.failedPageRevisionHash = numbersHash.hashLong(failedPage.getPageRevision()).asLong();
        } else {
            this.failedPageIdHash = -1L;
            this.failedPageRevisionHash = -1L;
        }
        this.environmentInfo = spaceExportMetrics.getEnvironmentInfo();
        this.exportResults = spaceExportMetrics.getExportResults();
        this.confluencePagesTotal = spaceExportMetrics.getConfluencePages();
        this.confluencePagesProcessed = spaceExportMetrics.getEnvironmentInfo().getSandboxStatus() == SandboxStatus.USED ? spaceExportMetrics.getPageExportMetrics().size() : (ExportStatus.isSuccessful(spaceExportMetrics.getExportResults().getExportStatus()) ? spaceExportMetrics.getConfluencePages() : -1);
        this.timeStatistics = new TimeStatistics(spaceExportMetrics.getPageExportMetrics());
        this.totalTime = spaceExportMetrics.getTotalTime();
        this.tocBuildTime = spaceExportMetrics.getTocBuildTime();
        this.joinTime = spaceExportMetrics.getJoinTime();
    }

    public int getDcNodeId() {
        return this.environmentInfo.getDcNodeId();
    }

    public SandboxStatus getSandboxStatus() {
        return this.environmentInfo.getSandboxStatus();
    }

    public ExportScope getExportScope() {
        return this.environmentInfo.getExportScope();
    }

    public long getSpaceKeyHash() {
        return stringsHash.hashString((CharSequence)this.environmentInfo.getSpaceKey(), Charset.defaultCharset()).asLong();
    }

    public int getTotalTime() {
        return this.totalTime;
    }

    public int getMinPageTime() {
        return this.timeStatistics.getMinPageTime();
    }

    public int getMaxPageTime() {
        return this.timeStatistics.getMaxPageTime();
    }

    public int getMeanPageTime() {
        return this.timeStatistics.getMeanPageTime();
    }

    public int getP50PageTime() {
        return this.timeStatistics.getP50PageTime();
    }

    public int getP95PageTime() {
        return this.timeStatistics.getP95PageTime();
    }

    public int getP98PageTime() {
        return this.timeStatistics.getP98PageTime();
    }

    public int getP99PageTime() {
        return this.timeStatistics.getP99PageTime();
    }

    public int getP999PageTime() {
        return this.timeStatistics.getP999PageTime();
    }

    public int getTocBuildTime() {
        return this.tocBuildTime;
    }

    public int getJoinTime() {
        return this.joinTime;
    }

    public ExportStatus getExportStatus() {
        return this.exportResults.getExportStatus();
    }

    public int getConfluencePagesTotal() {
        return this.confluencePagesTotal;
    }

    public int getConfluencePagesProcessed() {
        return this.confluencePagesProcessed;
    }

    public int getPdfPagesCount() {
        return this.exportResults.getPdfPagesTotal();
    }

    public long getPdfFileSizeBytes() {
        return this.exportResults.getPdfFileSizeBytes();
    }

    public FailureLocation getFailureLocation() {
        return this.exportResults.getFailureLocation();
    }

    public long getFailedPageIdHash() {
        return this.failedPageIdHash;
    }

    public long getFailedPageRevisionHash() {
        return this.failedPageRevisionHash;
    }
}

