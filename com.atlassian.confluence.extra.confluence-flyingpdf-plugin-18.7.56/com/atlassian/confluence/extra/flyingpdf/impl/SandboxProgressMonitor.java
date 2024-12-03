/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.core.util.ProgressMeter
 */
package com.atlassian.confluence.extra.flyingpdf.impl;

import com.atlassian.confluence.extra.flyingpdf.impl.AbstractPdfExportProgressMonitor;
import com.atlassian.confluence.extra.flyingpdf.util.ErrorMessages;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.core.util.ProgressMeter;

public class SandboxProgressMonitor
extends AbstractPdfExportProgressMonitor {
    private static final int CONTENT_TREE_CALCULATION_PERCENTAGE = 4;
    private static final int HTML_CONVERSION_PERCENTAGE = 4;
    private final I18NBean i18NBean;
    private final ErrorMessages errorMessages;
    private final ProgressMeter progressMeter;
    private int convertedPageCount = 0;

    public SandboxProgressMonitor(I18NBean bean, ErrorMessages errorMessages, ProgressMeter progressMeter) {
        this.i18NBean = bean;
        this.errorMessages = errorMessages;
        this.progressMeter = progressMeter;
    }

    @Override
    public void started() {
        this.progressMeter.setPercentage(0);
        this.progressMeter.setStatus(this.i18NBean.getText("com.atlassian.confluence.extra.flyingpdf.progress.started"));
    }

    @Override
    public void beginCalculationOfContentTree() {
        this.progressMeter.setStatus(this.i18NBean.getText("com.atlassian.confluence.extra.flyingpdf.progress.calculatecontenttree"));
    }

    @Override
    public void completedCalculationOfPdfPages(int numberOfPages) {
        super.completedCalculationOfPdfPages(numberOfPages);
        this.convertedPageCount = 0;
    }

    @Override
    public void completedCalculationOfContentTree(int numberOfPages) {
        super.completedCalculationOfPdfPages(numberOfPages);
        this.progressMeter.setStatus(this.i18NBean.getText("com.atlassian.confluence.extra.flyingpdf.progress.contenttreecalculated"));
        this.progressMeter.setPercentage(4);
    }

    private static double getPercentage(double progress, double total) {
        return progress * 100.0 / total;
    }

    private static double scalePercentage(double originalPercentage, double scalingFactor) {
        return scalingFactor / 100.0 * originalPercentage;
    }

    @Override
    public void completedExportedHtmlConversionForPage(String pageId, String pageTitle) {
        super.completedExportedHtmlConversionForPage(pageId, pageTitle);
        ++this.convertedPageCount;
        double percentageComplete = SandboxProgressMonitor.getPercentage(this.convertedPageCount, this.numberOfPdfPages);
        double weightedPercentageComplete = SandboxProgressMonitor.scalePercentage(percentageComplete, 4.0) + 4.0;
        this.progressMeter.setPercentage((int)weightedPercentageComplete);
        String message = this.i18NBean.getText("com.atlassian.confluence.extra.flyingpdf.progress.pageConvertedToHtml", (Object[])new String[]{String.valueOf(this.convertedPageCount), String.valueOf(this.numberOfPdfPages)});
        this.progressMeter.setStatus(message);
    }

    @Override
    public void beginHtmlToPdfConversion() {
        this.convertedPageCount = 0;
        this.progressMeter.setPercentage(8);
        this.progressMeter.setStatus(this.i18NBean.getText("com.atlassian.confluence.extra.flyingpdf.progress.beginhtmltopdf"));
    }

    @Override
    public void performingHtmlToPdfConversionForPage(String pageTitle) {
        int pdfConversionPercentage;
        ++this.convertedPageCount;
        double percentageComplete = SandboxProgressMonitor.getPercentage(this.convertedPageCount, this.numberOfPdfPages);
        double weightedPercentageComplete = SandboxProgressMonitor.scalePercentage(percentageComplete, pdfConversionPercentage = 92) + 4.0 + 4.0;
        int calculatedPercentage = (int)weightedPercentageComplete;
        if (calculatedPercentage > 99) {
            calculatedPercentage = 99;
        }
        this.progressMeter.setPercentage(calculatedPercentage);
        String message = this.i18NBean.getText("com.atlassian.confluence.extra.flyingpdf.progress.pageConvertedToPdf", (Object[])new String[]{String.valueOf(this.convertedPageCount), String.valueOf(this.numberOfPdfPages)});
        this.progressMeter.setStatus(message);
    }

    @Override
    public void completed(String downloadPath) {
        this.progressMeter.setCompletedSuccessfully(true);
        this.progressMeter.setStatus(this.i18NBean.getText("com.atlassian.confluence.extra.flyingpdf.progress.completed", (Object[])new String[]{downloadPath}));
        this.progressMeter.setPercentage(100);
    }

    @Override
    public void errored(String exceptionMessage) {
        super.errored(exceptionMessage);
        this.progressMeter.setCompletedSuccessfully(false);
        this.progressMeter.setStatus(exceptionMessage);
    }
}

