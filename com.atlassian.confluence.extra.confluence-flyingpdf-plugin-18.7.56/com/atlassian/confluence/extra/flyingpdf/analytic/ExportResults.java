/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.flyingpdf.analytic;

import com.atlassian.confluence.extra.flyingpdf.analytic.ExportStatus;
import com.atlassian.confluence.extra.flyingpdf.analytic.FailureLocation;

public class ExportResults {
    private ExportStatus exportStatus;
    private int pdfPagesTotal = -1;
    private long pdfFileSizeBytes = -1L;
    private FailureLocation failureLocation = FailureLocation.NONE;

    public ExportStatus getExportStatus() {
        return this.exportStatus;
    }

    public void setExportStatus(ExportStatus exportStatus) {
        this.exportStatus = exportStatus;
    }

    int getPdfPagesTotal() {
        return this.pdfPagesTotal;
    }

    public void setPdfPagesTotal(int pdfPagesTotal) {
        this.pdfPagesTotal = pdfPagesTotal;
    }

    long getPdfFileSizeBytes() {
        return this.pdfFileSizeBytes;
    }

    public void setPdfFileSizeBytes(long pdfFileSizeBytes) {
        this.pdfFileSizeBytes = pdfFileSizeBytes;
    }

    FailureLocation getFailureLocation() {
        return this.failureLocation;
    }

    public void setFailureLocation(FailureLocation failureLocation) {
        this.failureLocation = failureLocation;
    }
}

