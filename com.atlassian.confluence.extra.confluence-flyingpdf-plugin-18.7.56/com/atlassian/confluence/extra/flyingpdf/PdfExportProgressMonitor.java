/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.flyingpdf;

public interface PdfExportProgressMonitor {
    public void started();

    public void beginCalculationOfContentTree();

    public void completedCalculationOfPdfPages(int var1);

    public void completedCalculationOfContentTree(int var1);

    public void completedExportedHtmlConversionForPage(String var1, String var2);

    public void beginHtmlToPdfConversion();

    public void performingHtmlToPdfConversionForPage(String var1);

    public void completed(String var1);

    public void errored(String var1);
}

