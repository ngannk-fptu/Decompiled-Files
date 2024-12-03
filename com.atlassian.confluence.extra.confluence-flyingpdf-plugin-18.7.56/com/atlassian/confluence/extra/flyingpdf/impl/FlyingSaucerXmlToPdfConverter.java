/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.importexport.ImportExportException
 *  com.atlassian.confluence.importexport.impl.ExportFileNameGenerator
 *  com.atlassian.confluence.importexport.resource.DownloadResourceManager
 *  com.atlassian.confluence.setup.settings.ConfluenceDirectories
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.webresource.WebResourceIntegration
 *  com.atlassian.plugin.webresource.cdn.CDNStrategy
 *  com.atlassian.util.profiling.UtilTimerStack
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.core.io.FileSystemResource
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.flyingpdf.impl;

import com.atlassian.confluence.extra.flyingpdf.PdfExportProgressMonitor;
import com.atlassian.confluence.extra.flyingpdf.XmlToPdfConverter;
import com.atlassian.confluence.extra.flyingpdf.config.FontManager;
import com.atlassian.confluence.extra.flyingpdf.html.ConfluenceNamespaceHandler;
import com.atlassian.confluence.extra.flyingpdf.impl.ConfluenceExportUserAgent;
import com.atlassian.confluence.extra.flyingpdf.util.ImageFileCacheUtils;
import com.atlassian.confluence.extra.flyingpdf.util.ImageInformationURICacheUtil;
import com.atlassian.confluence.extra.flyingpdf.util.ImageTranscoderCacheUtil;
import com.atlassian.confluence.extra.flyingpdf.util.UrlUtils;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.importexport.impl.ExportFileNameGenerator;
import com.atlassian.confluence.importexport.resource.DownloadResourceManager;
import com.atlassian.confluence.setup.settings.ConfluenceDirectories;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.webresource.WebResourceIntegration;
import com.atlassian.plugin.webresource.cdn.CDNStrategy;
import com.atlassian.util.profiling.UtilTimerStack;
import com.lowagie.text.DocListener;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfDocument;
import com.lowagie.text.pdf.PdfWriter;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.pdf.PDFCreationListener;

@Component
@ExportAsService(value={XmlToPdfConverter.class})
public class FlyingSaucerXmlToPdfConverter
implements XmlToPdfConverter {
    public static final String INSTALLED_FONT_FAMILY = "ConfluenceInstalledFont";
    private static final Logger log = LoggerFactory.getLogger(FlyingSaucerXmlToPdfConverter.class);
    private final SettingsManager settingsManager;
    private final WebResourceIntegration webResourceIntegration;
    private final DownloadResourceManager downloadResourceManager;
    private final ConfluenceDirectories confluenceDirectories;
    private final FontManager pdfExportFontManager;
    private final ExportFileNameGenerator pdfExportFileNameGenerator;

    public FlyingSaucerXmlToPdfConverter(@ComponentImport SettingsManager settingsManager, @ComponentImport WebResourceIntegration webResourceIntegration, @ComponentImport DownloadResourceManager downloadResourceManager, @ComponentImport ConfluenceDirectories confluenceDirectories, FontManager pdfExportFontManager, ExportFileNameGenerator pdfExportFileNameGenerator) {
        this.settingsManager = settingsManager;
        this.webResourceIntegration = webResourceIntegration;
        this.downloadResourceManager = downloadResourceManager;
        this.confluenceDirectories = confluenceDirectories;
        this.pdfExportFontManager = pdfExportFontManager;
        this.pdfExportFileNameGenerator = pdfExportFileNameGenerator;
    }

    @Override
    public File convertXhtmlToPdf(String filenamePrefix, Document xhtml, String contextPath) throws ImportExportException {
        return this.convertXhtmlToPdf(filenamePrefix, xhtml, null, contextPath);
    }

    @Override
    public File convertXhtmlToPdf(String filenamePrefix, Document xhtml, PdfExportProgressMonitor progress, String contextPath) throws ImportExportException {
        BufferedOutputStream outstr;
        File exportFile;
        try {
            exportFile = this.pdfExportFileNameGenerator.getExportFile(new String[]{filenamePrefix});
        }
        catch (IOException ex) {
            throw new ImportExportException("Failed to create a location and file for the PDF export.", (Throwable)ex);
        }
        ITextRenderer renderer = this.newITextRenderer();
        this.useCustomFontIfConfigured(renderer.getFontResolver());
        String baseUrl = UrlUtils.getFullUrl(this.settingsManager.getGlobalSettings().getBaseUrl(), contextPath);
        ConfluenceExportUserAgent callback = this.newConfluenceExportUserAgent(renderer, baseUrl);
        renderer.getSharedContext().setUserAgentCallback(callback);
        callback.setSharedContext(renderer.getSharedContext());
        try {
            outstr = new BufferedOutputStream(new FileOutputStream(exportFile));
        }
        catch (FileNotFoundException ex) {
            throw new ImportExportException("Failed to created the output file " + exportFile.getAbsolutePath(), (Throwable)ex);
        }
        ConfluenceNamespaceHandler nsh = new ConfluenceNamespaceHandler(baseUrl);
        try {
            ImageFileCacheUtils.initializeConfluenceTempExportDirectory(this.confluenceDirectories.getTempDirectory());
            ImageInformationURICacheUtil.initializeCache();
            ImageTranscoderCacheUtil.initializeCache();
            renderer.setDocument(xhtml, baseUrl + contextPath + "/");
            renderer.getSharedContext().setNamespaceHandler(nsh);
            UtilTimerStack.push((String)"FlyingSaucerXmlToPdfConverter.renderer.layout");
            this.setPdfCreationListener(progress, renderer);
            renderer.layout();
            renderer.createPDF(outstr);
        }
        catch (Exception ex) {
            String msg = "Exception while rendering the PDF document " + exportFile.getAbsolutePath();
            throw new ImportExportException(msg, (Throwable)ex);
        }
        finally {
            UtilTimerStack.pop((String)"FlyingSaucerXmlToPdfConverter.renderer.layoutAndPaint");
            try {
                ((OutputStream)outstr).close();
                ImageFileCacheUtils.removeTempDirectory();
            }
            catch (IOException ex) {
                throw new ImportExportException("Could not close the export file " + exportFile.getAbsolutePath(), (Throwable)ex);
            }
            ImageInformationURICacheUtil.purgeCache();
            ImageTranscoderCacheUtil.purgeCache();
        }
        return exportFile;
    }

    private void setPdfCreationListener(final PdfExportProgressMonitor progress, ITextRenderer renderer) {
        renderer.setListener(new PDFCreationListener(){

            @Override
            public void preOpen(ITextRenderer iTextRenderer) {
                PdfWriter pdfWriter = iTextRenderer.getWriter();
                FlyingSaucerXmlToPdfConverter.this.setListener(pdfWriter, progress);
            }

            @Override
            public void preWrite(ITextRenderer iTextRenderer, int pageCount) {
                if (progress != null) {
                    progress.completedCalculationOfPdfPages(pageCount);
                }
            }

            @Override
            public void onClose(ITextRenderer renderer) {
            }
        });
    }

    private void setListener(PdfWriter pdfWriter, PdfExportProgressMonitor progress) {
        Optional<PdfDocument> pdfDocument = this.getPdfDocument(pdfWriter);
        pdfDocument.ifPresent(pdfDocument1 -> pdfDocument1.addDocListener(new PageTrackerListener(progress)));
    }

    private Optional<PdfDocument> getPdfDocument(PdfWriter pdfWriter) {
        try {
            Method method = pdfWriter.getClass().getDeclaredMethod("getPdfDocument", new Class[0]);
            method.setAccessible(true);
            return Optional.of((PdfDocument)method.invoke((Object)pdfWriter, new Object[0]));
        }
        catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            log.warn("Failed to invoke the method getPdfDocument() with error: " + e.getMessage());
            return Optional.empty();
        }
    }

    protected ConfluenceExportUserAgent newConfluenceExportUserAgent(ITextRenderer renderer, String baseUrl) {
        CDNStrategy cdnStrategy = this.webResourceIntegration.getCDNStrategy();
        String cdnUrl = cdnStrategy != null ? cdnStrategy.transformRelativeUrl("") : null;
        return new ConfluenceExportUserAgent(renderer.getOutputDevice(), baseUrl, cdnUrl, this.downloadResourceManager);
    }

    protected ITextRenderer newITextRenderer() {
        return new ITextRenderer();
    }

    protected void useCustomFontIfConfigured(ITextFontResolver fontResolver) throws ImportExportException {
        FileSystemResource fontResource = this.pdfExportFontManager.getInstalledFont();
        if (fontResource == null) {
            return;
        }
        try {
            fontResolver.addFont(fontResource.getPath(), INSTALLED_FONT_FAMILY, "Identity-H", true, null);
        }
        catch (DocumentException | IOException ex) {
            throw new ImportExportException((Throwable)ex);
        }
    }

    private static class PageTrackerListener
    implements DocListener {
        private final PdfExportProgressMonitor progress;

        PageTrackerListener(PdfExportProgressMonitor progress) {
            this.progress = progress;
        }

        @Override
        public void open() {
            log.debug("PDF document open");
        }

        @Override
        public void close() {
            log.debug("PDF document closed");
        }

        @Override
        public boolean newPage() {
            if (this.progress != null) {
                this.progress.performingHtmlToPdfConversionForPage("");
            }
            log.debug("PDF document newPage");
            return true;
        }

        @Override
        public boolean setPageSize(Rectangle rectangle) {
            return true;
        }

        @Override
        public boolean setMargins(float v, float v1, float v2, float v3) {
            return false;
        }

        @Override
        public boolean setMarginMirroring(boolean b) {
            return true;
        }

        @Override
        public boolean setMarginMirroringTopBottom(boolean b) {
            return true;
        }

        @Override
        public void setPageCount(int i) {
            if (this.progress != null) {
                this.progress.completedCalculationOfPdfPages(i);
            }
            log.debug("PDF document setPageCount " + i);
        }

        @Override
        public void resetPageCount() {
        }

        @Override
        public void setHeader(HeaderFooter headerFooter) {
        }

        @Override
        public void resetHeader() {
        }

        @Override
        public void setFooter(HeaderFooter headerFooter) {
        }

        @Override
        public void resetFooter() {
        }

        @Override
        public boolean add(Element element) {
            return true;
        }
    }
}

