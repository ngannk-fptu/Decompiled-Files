/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.flyingpdf.impl;

import com.atlassian.confluence.extra.flyingpdf.PdfExportProgressMonitor;
import com.atlassian.confluence.extra.flyingpdf.impl.AbstractPdfExportProgressMonitor;
import java.lang.reflect.Proxy;

public class DefaultProgressMonitor
extends AbstractPdfExportProgressMonitor {
    public final PdfExportProgressMonitor target = (PdfExportProgressMonitor)Proxy.newProxyInstance(PdfExportProgressMonitor.class.getClassLoader(), new Class[]{PdfExportProgressMonitor.class}, (proxy, method, args) -> {
        if (monitor != null) {
            return method.invoke((Object)monitor, args);
        }
        return null;
    });

    public DefaultProgressMonitor(PdfExportProgressMonitor monitor) {
    }

    public DefaultProgressMonitor() {
        this(null);
    }

    @Override
    public void beginCalculationOfContentTree() {
        this.target.beginCalculationOfContentTree();
    }

    @Override
    public void beginHtmlToPdfConversion() {
        this.target.beginHtmlToPdfConversion();
    }

    @Override
    public void completed(String downloadPath) {
        this.target.completed(downloadPath);
    }

    @Override
    public void completedCalculationOfPdfPages(int numberOfPages) {
        super.completedCalculationOfPdfPages(numberOfPages);
        this.target.completedCalculationOfPdfPages(numberOfPages);
    }

    @Override
    public void completedCalculationOfContentTree(int numberOfPages) {
        this.target.completedCalculationOfContentTree(numberOfPages);
    }

    @Override
    public void completedExportedHtmlConversionForPage(String pageId, String pageTitle) {
        super.completedExportedHtmlConversionForPage(pageId, pageTitle);
        this.target.completedExportedHtmlConversionForPage(pageId, pageTitle);
    }

    @Override
    public void performingHtmlToPdfConversionForPage(String pageTitle) {
        this.target.performingHtmlToPdfConversionForPage(pageTitle);
    }

    @Override
    public void errored(String message) {
        super.errored(message);
        this.target.errored(message);
    }

    @Override
    public void started() {
        this.target.started();
    }
}

