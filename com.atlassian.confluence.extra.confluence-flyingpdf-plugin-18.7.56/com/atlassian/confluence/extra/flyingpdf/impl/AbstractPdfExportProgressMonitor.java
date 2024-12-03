/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.flyingpdf.impl;

import com.atlassian.confluence.extra.flyingpdf.PdfExportProgressMonitor;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPdfExportProgressMonitor
implements PdfExportProgressMonitor {
    public int numberOfPdfPages = 0;
    public Map<String, String> exportedPages = new HashMap<String, String>();
    public String errorMessage;

    @Override
    public void completedCalculationOfPdfPages(int numberOfPages) {
        this.numberOfPdfPages = numberOfPages;
    }

    @Override
    public void completedExportedHtmlConversionForPage(String pageId, String pageTitle) {
        this.exportedPages.put(pageId, pageTitle);
    }

    @Override
    public void errored(String exceptionMessage) {
        this.errorMessage = exceptionMessage;
    }
}

