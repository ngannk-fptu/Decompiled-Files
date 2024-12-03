/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.importexport.ImportExportException
 *  com.atlassian.confluence.license.LicenseService
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.ContentTree
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.core.util.ProgressMeter
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.flyingpdf.impl;

import com.atlassian.confluence.extra.flyingpdf.PdfExportProgressMonitor;
import com.atlassian.confluence.extra.flyingpdf.PdfExporterService;
import com.atlassian.confluence.extra.flyingpdf.analytic.PageExportMetrics;
import com.atlassian.confluence.extra.flyingpdf.analytic.SpaceExportMetrics;
import com.atlassian.confluence.extra.flyingpdf.html.DecorationPolicy;
import com.atlassian.confluence.extra.flyingpdf.impl.InternalPdfExporterService;
import com.atlassian.confluence.extra.flyingpdf.sandbox.SandboxPdfExporterService;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.ContentTree;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.core.util.ProgressMeter;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.io.File;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={PdfExporterService.class})
public class DelegatingPdfExporterService
implements PdfExporterService {
    private final LicenseService licenseService;
    private final SandboxPdfExporterService sandboxPdfExporterService;
    private final InternalPdfExporterService internalPdfExporterService;

    public DelegatingPdfExporterService(@ComponentImport LicenseService licenseService, SandboxPdfExporterService sandboxPdfExporterService, InternalPdfExporterService internalPdfExporterService) {
        this.licenseService = licenseService;
        this.sandboxPdfExporterService = sandboxPdfExporterService;
        this.internalPdfExporterService = internalPdfExporterService;
    }

    private static boolean sandboxExplicitlyDisabled() {
        return Boolean.getBoolean("pdf.export.sandbox.disable");
    }

    @Override
    public File createPdfForSpace(User user, Space space, ContentTree contentTree, PdfExportProgressMonitor progress, String contextPath, SpaceExportMetrics spaceExportMetrics, DecorationPolicy decorations) throws ImportExportException {
        return this.getService().createPdfForSpace(user, space, contentTree, progress, contextPath, spaceExportMetrics, decorations);
    }

    @Override
    public File createPdfForPage(User user, AbstractPage page, String contextPath, PageExportMetrics pageExportMetrics) throws ImportExportException {
        return this.getService().createPdfForPage(user, page, contextPath, pageExportMetrics);
    }

    @Override
    public ContentTree getContentTree(User user, Space space) {
        return this.getService().getContentTree(user, space);
    }

    @Override
    public boolean isPermitted(User user, AbstractPage page) {
        return this.getService().isPermitted(user, page);
    }

    @Override
    public boolean isPermitted(User user, Space space) {
        return this.getService().isPermitted(user, space);
    }

    @Override
    public boolean exportableContentExists(Space space) {
        return this.getService().exportableContentExists(space);
    }

    @Override
    public PdfExportProgressMonitor createProgressMonitor(ProgressMeter progressMeter) {
        return this.getService().createProgressMonitor(progressMeter);
    }

    private PdfExporterService getService() {
        if (this.sandboxIsUsed()) {
            return this.sandboxPdfExporterService;
        }
        return this.internalPdfExporterService;
    }

    public boolean sandboxIsUsed() {
        return !DelegatingPdfExporterService.sandboxExplicitlyDisabled() && this.isDc();
    }

    private boolean isDc() {
        return this.licenseService.isLicensedForDataCenterOrExempt();
    }
}

