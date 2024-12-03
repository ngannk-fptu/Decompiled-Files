/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.importexport.ImportExportException
 *  com.atlassian.confluence.importexport.impl.ExportFileNameGenerator
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.util.sandbox.SandboxCrashedException
 *  com.atlassian.confluence.util.sandbox.SandboxException
 *  com.atlassian.confluence.util.sandbox.SandboxTimeoutException
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.webresource.WebResourceIntegration
 *  com.atlassian.plugin.webresource.cdn.CDNStrategy
 *  org.apache.commons.lang3.SerializationUtils
 *  org.springframework.core.io.FileSystemResource
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.flyingpdf.sandbox;

import com.atlassian.confluence.extra.flyingpdf.analytic.ExportStatus;
import com.atlassian.confluence.extra.flyingpdf.analytic.PageExportMetrics;
import com.atlassian.confluence.extra.flyingpdf.config.FontManager;
import com.atlassian.confluence.extra.flyingpdf.sandbox.PdfExportSandbox;
import com.atlassian.confluence.extra.flyingpdf.sandbox.SandboxPdfConversionRequest;
import com.atlassian.confluence.extra.flyingpdf.sandbox.SandboxPdfConversionResponse;
import com.atlassian.confluence.extra.flyingpdf.sandbox.SandboxPdfConversionTask;
import com.atlassian.confluence.extra.flyingpdf.util.ErrorMessages;
import com.atlassian.confluence.extra.flyingpdf.util.RenderedPdfFile;
import com.atlassian.confluence.extra.flyingpdf.util.UrlUtils;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.importexport.impl.ExportFileNameGenerator;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.util.sandbox.SandboxCrashedException;
import com.atlassian.confluence.util.sandbox.SandboxException;
import com.atlassian.confluence.util.sandbox.SandboxTimeoutException;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.webresource.WebResourceIntegration;
import com.atlassian.plugin.webresource.cdn.CDNStrategy;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

@Component
public class SandboxXmlToPdfConverter {
    private final ErrorMessages errorMessages;
    private final SettingsManager settingsManager;
    private final WebResourceIntegration webResourceIntegration;
    private final FontManager pdfExportFontManager;
    private final PdfExportSandbox pdfExportSandbox;
    private final ExportFileNameGenerator pdfExportFileNameGenerator;

    public SandboxXmlToPdfConverter(@ComponentImport SettingsManager settingsManager, @ComponentImport WebResourceIntegration webResourceIntegration, ErrorMessages errorMessages, FontManager pdfExportFontManager, PdfExportSandbox pdfExportSandbox, ExportFileNameGenerator pdfExportFileNameGenerator) {
        this.errorMessages = errorMessages;
        this.webResourceIntegration = webResourceIntegration;
        this.settingsManager = settingsManager;
        this.pdfExportFontManager = pdfExportFontManager;
        this.pdfExportSandbox = pdfExportSandbox;
        this.pdfExportFileNameGenerator = pdfExportFileNameGenerator;
    }

    RenderedPdfFile convertXhtmlToPdf(String filenamePrefix, String pageTitle, Document xhtml, String contextPath, String username, PageExportMetrics pageExportMetrics) throws ImportExportException {
        File exportFile;
        SandboxPdfConversionTask task = new SandboxPdfConversionTask();
        try {
            exportFile = this.pdfExportFileNameGenerator.getExportFile(new String[]{filenamePrefix});
        }
        catch (IOException ex) {
            throw new ImportExportException("Failed to create a location and file for the PDF export.", (Throwable)ex);
        }
        FileSystemResource fontResource = this.pdfExportFontManager.getInstalledFont();
        String fontPath = fontResource == null ? "" : fontResource.getPath();
        String baseUrl = UrlUtils.getFullUrl(this.settingsManager.getGlobalSettings().getBaseUrl(), contextPath);
        CDNStrategy cdnStrategy = this.webResourceIntegration.getCDNStrategy();
        String cdnUrl = cdnStrategy != null ? cdnStrategy.transformRelativeUrl("") : null;
        SandboxPdfConversionRequest request = new SandboxPdfConversionRequest(baseUrl, contextPath, cdnUrl, fontPath, username, exportFile.getAbsolutePath(), SerializationUtils.serialize((Serializable)((Serializable)((Object)xhtml))));
        try {
            SandboxPdfConversionResponse response = this.pdfExportSandbox.execute(task, request);
            return RenderedPdfFile.withKnownSize(response.getResultingPdf(), response.getPdfPageCount());
        }
        catch (SandboxTimeoutException e1) {
            pageExportMetrics.getExportResults().setExportStatus(ExportStatus.SANDBOX_TIMEOUT);
            String message = this.errorMessages.pageTimeoutMessage(pageTitle);
            throw new ImportExportException(message, (Throwable)e1);
        }
        catch (SandboxException e1) {
            if (e1 instanceof SandboxCrashedException) {
                pageExportMetrics.getExportResults().setExportStatus(ExportStatus.SANDBOX_CRASH);
            } else {
                pageExportMetrics.getExportResults().setExportStatus(ExportStatus.FAIL);
            }
            throw new ImportExportException(this.errorMessages.pageErrorMessage(pageTitle), (Throwable)e1);
        }
    }
}

