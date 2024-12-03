/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Depth
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.service.content.SpaceService
 *  com.atlassian.confluence.core.ApiRestEntityFactory
 *  com.atlassian.confluence.importexport.ImportExportException
 *  com.atlassian.confluence.importexport.ImportExportManager
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.ContentNode
 *  com.atlassian.confluence.pages.ContentTree
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.core.util.ProgressMeter
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  com.atlassian.util.profiling.UtilTimerStack
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.flyingpdf.impl;

import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.service.content.SpaceService;
import com.atlassian.confluence.core.ApiRestEntityFactory;
import com.atlassian.confluence.extra.flyingpdf.PdfExportProgressMonitor;
import com.atlassian.confluence.extra.flyingpdf.PdfExporterService;
import com.atlassian.confluence.extra.flyingpdf.analytic.ExportStatus;
import com.atlassian.confluence.extra.flyingpdf.analytic.PageExportMetrics;
import com.atlassian.confluence.extra.flyingpdf.analytic.SpaceExportMetrics;
import com.atlassian.confluence.extra.flyingpdf.html.DecorationPolicy;
import com.atlassian.confluence.extra.flyingpdf.html.LinkRenderingDetails;
import com.atlassian.confluence.extra.flyingpdf.html.XhtmlBuilder;
import com.atlassian.confluence.extra.flyingpdf.impl.AbstractPdfExportProgressMonitor;
import com.atlassian.confluence.extra.flyingpdf.impl.DefaultProgressMonitor;
import com.atlassian.confluence.extra.flyingpdf.impl.ExportPermissionChecker;
import com.atlassian.confluence.extra.flyingpdf.impl.FlyingSaucerXmlToPdfConverter;
import com.atlassian.confluence.extra.flyingpdf.impl.ProgressMeterWrappingProgressMonitor;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.importexport.ImportExportManager;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.ContentNode;
import com.atlassian.confluence.pages.ContentTree;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.core.util.ProgressMeter;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import com.atlassian.util.profiling.UtilTimerStack;
import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

@Component
public class InternalPdfExporterService
implements PdfExporterService {
    private final I18NBeanFactory i18NBeanFactory;
    private final ApiRestEntityFactory spaceFactory;
    private final SpaceService apiSpaceService;
    private final XhtmlBuilder intermediateHtmlBuilder;
    private final ImportExportManager importExportManager;
    private final ExportPermissionChecker exportPermissionChecker;
    private final FlyingSaucerXmlToPdfConverter flyingPdfDocumentConverter;

    public InternalPdfExporterService(@ComponentImport I18NBeanFactory i18NBeanFactory, @ComponentImport ApiRestEntityFactory spaceFactory, @ComponentImport SpaceService apiSpaceService, @ComponentImport ImportExportManager importExportManager, XhtmlBuilder intermediateHtmlBuilder, ExportPermissionChecker exportPermissionChecker, FlyingSaucerXmlToPdfConverter flyingPdfDocumentConverter) {
        this.i18NBeanFactory = i18NBeanFactory;
        this.spaceFactory = spaceFactory;
        this.apiSpaceService = apiSpaceService;
        this.intermediateHtmlBuilder = intermediateHtmlBuilder;
        this.importExportManager = importExportManager;
        this.exportPermissionChecker = exportPermissionChecker;
        this.flyingPdfDocumentConverter = flyingPdfDocumentConverter;
    }

    @Override
    public File createPdfForSpace(User user, Space space, ContentTree contentTree, PdfExportProgressMonitor exportProgressMonitor, String contextPath, SpaceExportMetrics spaceExportMetrics, DecorationPolicy decorations) throws ImportExportException {
        this.exportPermissionChecker.checkAuthorization(user, space);
        AbstractPdfExportProgressMonitor progressMonitor = InternalPdfExporterService.progressMonitor(exportProgressMonitor);
        UtilTimerStack.push((String)"intermediateHtmlBuilder.buildHtml");
        Document xhtml = this.intermediateHtmlBuilder.buildHtml(contentTree, space, LinkRenderingDetails.anchors(), DecorationPolicy.space().combine(decorations), progressMonitor);
        UtilTimerStack.pop((String)"intermediateHtmlBuilder.buildHtml");
        contentTree = null;
        String spaceKey = this.getSpaceKeyForExportFileName(space.getKey());
        progressMonitor.beginHtmlToPdfConversion();
        File file = this.flyingPdfDocumentConverter.convertXhtmlToPdf(spaceKey, xhtml, progressMonitor, contextPath);
        spaceExportMetrics.getExportResults().setPdfFileSizeBytes(file.length());
        spaceExportMetrics.getExportResults().setPdfPagesTotal(progressMonitor.numberOfPdfPages);
        return file;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public File createPdfForPage(User user, AbstractPage page, String contextPath, PageExportMetrics pageExportMetrics) throws ImportExportException {
        long startTime = System.currentTimeMillis();
        try {
            pageExportMetrics.setPageId(page.getId());
            pageExportMetrics.setPageRevision(page.getConfluenceRevision().hashCode());
            DefaultProgressMonitor progressMonitor = new DefaultProgressMonitor();
            File result = this.doCreatePdfForPage(user, page, contextPath, progressMonitor);
            pageExportMetrics.getExportResults().setExportStatus(ExportStatus.OK);
            pageExportMetrics.getExportResults().setPdfPagesTotal(progressMonitor.numberOfPdfPages);
            pageExportMetrics.getExportResults().setPdfFileSizeBytes(result.length());
            File file = result;
            return file;
        }
        finally {
            pageExportMetrics.setTimeMs((int)(System.currentTimeMillis() - startTime));
        }
    }

    private static AbstractPdfExportProgressMonitor progressMonitor(PdfExportProgressMonitor monitor) {
        if (monitor instanceof AbstractPdfExportProgressMonitor) {
            return (AbstractPdfExportProgressMonitor)monitor;
        }
        return new DefaultProgressMonitor(monitor);
    }

    private File doCreatePdfForPage(User user, AbstractPage page, String contextPath, AbstractPdfExportProgressMonitor progressMonitor) throws ImportExportException {
        Document xhtml;
        this.exportPermissionChecker.checkAuthorization(user, page);
        if (page instanceof Page) {
            ContentTree tree = this.newContentTree();
            tree.addRootNode(new ContentNode((Page)page));
            xhtml = this.intermediateHtmlBuilder.buildHtml(tree, page.getSpace(), LinkRenderingDetails.anchors(), DecorationPolicy.none(), progressMonitor);
        } else if (page instanceof BlogPost) {
            xhtml = this.intermediateHtmlBuilder.buildHtml((BlogPost)page);
        } else {
            throw new IllegalArgumentException("Only pages and blog post are supported");
        }
        return this.flyingPdfDocumentConverter.convertXhtmlToPdf(this.makePdfFilename(page), xhtml, progressMonitor, contextPath);
    }

    private String makePdfFilename(AbstractPage page) {
        String spaceKey = this.getSpaceKeyForExportFileName(page.getSpaceKey());
        String safeName = spaceKey + "-" + page.getId();
        String filename = spaceKey + "-" + Optional.ofNullable(page.getTitle()).map(title -> title.replaceAll("\\s", "")).orElse(String.valueOf(page.getId()));
        if (!GeneralUtil.isSafeTitleForFilesystem((String)filename)) {
            filename = safeName;
        }
        try {
            Path path = Paths.get(filename, new String[0]);
        }
        catch (InvalidPathException ex) {
            filename = safeName;
        }
        return filename;
    }

    protected ContentTree newContentTree() {
        return new ContentTree();
    }

    @Override
    public ContentTree getContentTree(User user, Space space) {
        return this.importExportManager.getContentTree(user, space);
    }

    @Override
    public boolean isPermitted(User user, AbstractPage page) {
        return this.exportPermissionChecker.isPermitted(user, page);
    }

    @Override
    public boolean isPermitted(User user, Space space) {
        return this.exportPermissionChecker.isPermitted(user, space);
    }

    @Override
    public boolean exportableContentExists(Space space) {
        return space != null && this.apiSpaceService.findContent((com.atlassian.confluence.api.model.content.Space)this.spaceFactory.buildRestEntityFrom(space, Expansions.EMPTY).getDelegate(), new Expansion[0]).withDepth(Depth.ROOT).fetchMany(ContentType.PAGE, (PageRequest)new SimplePageRequest(0, 10)).size() > 0;
    }

    @Override
    public PdfExportProgressMonitor createProgressMonitor(ProgressMeter progressMeter) {
        return new ProgressMeterWrappingProgressMonitor(this.i18NBeanFactory.getI18NBean(), progressMeter);
    }

    private String getSpaceKeyForExportFileName(String spaceKey) {
        if (spaceKey.startsWith("~")) {
            spaceKey = spaceKey.substring(1);
        }
        return spaceKey;
    }
}

