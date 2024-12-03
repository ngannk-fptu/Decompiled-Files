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
 *  com.atlassian.confluence.content.render.xhtml.StorageFormatCleaner
 *  com.atlassian.confluence.core.ApiRestEntityFactory
 *  com.atlassian.confluence.importexport.ImportExportException
 *  com.atlassian.confluence.importexport.ImportExportManager
 *  com.atlassian.confluence.importexport.impl.ExportFileNameGenerator
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.ContentNode
 *  com.atlassian.confluence.pages.ContentTree
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.core.util.ProgressMeter
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  org.apache.commons.io.FileUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.flyingpdf.sandbox;

import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.service.content.SpaceService;
import com.atlassian.confluence.content.render.xhtml.StorageFormatCleaner;
import com.atlassian.confluence.core.ApiRestEntityFactory;
import com.atlassian.confluence.extra.flyingpdf.PdfExportProgressMonitor;
import com.atlassian.confluence.extra.flyingpdf.PdfExporterService;
import com.atlassian.confluence.extra.flyingpdf.analytic.ExportStatus;
import com.atlassian.confluence.extra.flyingpdf.analytic.FailureLocation;
import com.atlassian.confluence.extra.flyingpdf.analytic.PageExportMetrics;
import com.atlassian.confluence.extra.flyingpdf.analytic.SpaceExportMetrics;
import com.atlassian.confluence.extra.flyingpdf.html.DecorationPolicy;
import com.atlassian.confluence.extra.flyingpdf.html.LinkFixer;
import com.atlassian.confluence.extra.flyingpdf.html.LinkRenderingDetails;
import com.atlassian.confluence.extra.flyingpdf.html.TocBuilder;
import com.atlassian.confluence.extra.flyingpdf.html.XhtmlBuilder;
import com.atlassian.confluence.extra.flyingpdf.impl.ExportPermissionChecker;
import com.atlassian.confluence.extra.flyingpdf.impl.SandboxProgressMonitor;
import com.atlassian.confluence.extra.flyingpdf.sandbox.SandboxPdfJoinResponse;
import com.atlassian.confluence.extra.flyingpdf.sandbox.SandboxPdfJoiner;
import com.atlassian.confluence.extra.flyingpdf.sandbox.SandboxXmlToPdfConverter;
import com.atlassian.confluence.extra.flyingpdf.util.ErrorMessages;
import com.atlassian.confluence.extra.flyingpdf.util.ExportedSpaceStructure;
import com.atlassian.confluence.extra.flyingpdf.util.PdfNode;
import com.atlassian.confluence.extra.flyingpdf.util.RenderedPdfFile;
import com.atlassian.confluence.extra.flyingpdf.util.UrlUtils;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.importexport.ImportExportManager;
import com.atlassian.confluence.importexport.impl.ExportFileNameGenerator;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.ContentNode;
import com.atlassian.confluence.pages.ContentTree;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.core.util.ProgressMeter;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

@Component
public class SandboxPdfExporterService
implements PdfExporterService {
    private static final Logger log = LoggerFactory.getLogger(SandboxPdfExporterService.class);
    private final I18NBeanFactory i18NBeanFactory;
    private final ApiRestEntityFactory apiRestEntityFactory;
    private final ErrorMessages errorMessages;
    private final SpaceService apiSpaceService;
    private final SettingsManager settingsManager;
    private final SandboxPdfJoiner sandboxPdfJoiner;
    private final XhtmlBuilder intermediateHtmlBuilder;
    private final ImportExportManager importExportManager;
    private final ExportPermissionChecker exportPermissionChecker;
    private final SandboxXmlToPdfConverter sandboxXmlToPdfConverter;
    private final ExportFileNameGenerator pdfExportFileNameGenerator;
    private final StorageFormatCleaner storageFormatCleaner;

    public SandboxPdfExporterService(@ComponentImport I18NBeanFactory i18NBeanFactory, @ComponentImport ApiRestEntityFactory apiRestEntityFactory, @ComponentImport SpaceService apiSpaceService, @ComponentImport SettingsManager settingsManager, @ComponentImport ImportExportManager importExportManager, @ComponentImport StorageFormatCleaner storageFormatCleaner, ErrorMessages errorMessages, SandboxPdfJoiner sandboxPdfJoiner, XhtmlBuilder intermediateHtmlBuilder, ExportPermissionChecker exportPermissionChecker, SandboxXmlToPdfConverter sandboxXmlToPdfConverter, ExportFileNameGenerator pdfExportFileNameGenerator) {
        this.i18NBeanFactory = i18NBeanFactory;
        this.apiRestEntityFactory = apiRestEntityFactory;
        this.errorMessages = errorMessages;
        this.apiSpaceService = apiSpaceService;
        this.settingsManager = settingsManager;
        this.sandboxPdfJoiner = sandboxPdfJoiner;
        this.intermediateHtmlBuilder = intermediateHtmlBuilder;
        this.importExportManager = importExportManager;
        this.storageFormatCleaner = storageFormatCleaner;
        this.exportPermissionChecker = exportPermissionChecker;
        this.sandboxXmlToPdfConverter = sandboxXmlToPdfConverter;
        this.pdfExportFileNameGenerator = pdfExportFileNameGenerator;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public File createPdfForSpace(User user, Space space, ContentTree contentTree, PdfExportProgressMonitor progress, String contextPath, SpaceExportMetrics spaceExportMetrics, DecorationPolicy decorations) throws ImportExportException {
        ExportedSpaceStructure pdfStructure;
        this.exportPermissionChecker.checkAuthorization(user, space);
        if (contentTree.size() == 1) {
            Page page = (Page)Iterables.getOnlyElement((Iterable)contentTree.getPages());
            page.setTitle(this.storageFormatCleaner.cleanQuietly(page.getTitle()));
            String tmpFilePrefix = this.getTmpFilePrefix((AbstractPage)page);
            File outputFile = this.createPdf((AbstractPage)page, contextPath, LinkRenderingDetails.anchors(), tmpFilePrefix, new PageExportMetrics(), DecorationPolicy.space().combine(decorations)).getFile();
            progress.completed(outputFile.getAbsolutePath());
            return outputFile;
        }
        progress.completedCalculationOfPdfPages(contentTree.size());
        progress.beginHtmlToPdfConversion();
        ArrayList<PdfNode> pagesForest = new ArrayList<PdfNode>();
        if (contentTree.getPages() != null) {
            for (ContentNode topLevel : contentTree.getRootNodes()) {
                topLevel.getPage().setTitle(this.storageFormatCleaner.cleanQuietly(topLevel.getPage().getTitle()));
                PageExportMetrics rootPageInfo = new PageExportMetrics();
                spaceExportMetrics.getPageExportMetrics().add(rootPageInfo);
                pagesForest.add(this.createPdfTreeNode(topLevel, contextPath, progress, contentTree.getPages(), spaceExportMetrics, rootPageInfo));
            }
        }
        PageExportMetrics initialTocInfo = new PageExportMetrics();
        PageExportMetrics finalTocInfo = new PageExportMetrics();
        long tocStartTime = System.currentTimeMillis();
        try {
            PdfNode initialToc = this.generateTableOfContents(contextPath, space, pagesForest, Collections.emptyMap(), initialTocInfo);
            pdfStructure = new ExportedSpaceStructure(initialToc, pagesForest);
            PdfNode finalToc = this.generateTableOfContents(contextPath, space, pagesForest, pdfStructure.locationByTitleMap(), finalTocInfo);
            pdfStructure.replaceToc(finalToc);
            this.removeParentDirectory(initialToc.getRenderedPdfFile().getFile());
        }
        catch (Exception e) {
            for (PageExportMetrics tocInfo : ImmutableList.of((Object)initialTocInfo, (Object)finalTocInfo)) {
                if (!ExportStatus.isFail(tocInfo.getExportResults().getExportStatus())) continue;
                spaceExportMetrics.getExportResults().setExportStatus(tocInfo.getExportResults().getExportStatus());
                spaceExportMetrics.getExportResults().setFailureLocation(FailureLocation.TOC);
            }
            throw e;
        }
        finally {
            spaceExportMetrics.setTocBuildTime((int)(System.currentTimeMillis() - tocStartTime));
        }
        String exportFileName = this.getSpaceExportFileName(space.getKey());
        long joinStartTime = System.currentTimeMillis();
        try {
            SandboxPdfJoinResponse result = this.sandboxPdfJoiner.join(spaceExportMetrics, space.getKey(), pdfStructure, exportFileName, this.settingsManager.getGlobalSettings().getBaseUrl(), decorations);
            spaceExportMetrics.getExportResults().setPdfFileSizeBytes(result.getPdf().length());
            spaceExportMetrics.getExportResults().setPdfPagesTotal(result.getNumberOfPages());
        }
        finally {
            spaceExportMetrics.setJoinTime((int)(System.currentTimeMillis() - joinStartTime));
            this.cleanAllFiles(pdfStructure);
        }
        return new File(exportFileName);
    }

    private void cleanAllFiles(ExportedSpaceStructure structure) {
        this.removeParentDirectory(structure.getTableOfContents().getRenderedPdfFile().getFile());
        for (PdfNode root : structure.getConfluencePages()) {
            this.cleanupSubtree(root);
        }
    }

    private void cleanupSubtree(PdfNode root) {
        this.removeParentDirectory(root.getRenderedPdfFile().getFile());
        for (PdfNode child : root.getChildren()) {
            this.cleanupSubtree(child);
        }
    }

    private void removeParentDirectory(File file) {
        FileUtils.deleteQuietly((File)file.getParentFile());
    }

    private PdfNode generateTableOfContents(String contextPath, Space space, List<PdfNode> pages, Map<String, Integer> locations, PageExportMetrics pageExportMetrics) throws ImportExportException {
        Document xhtml = this.renderToc(contextPath, space, pages, locations);
        String tocString = this.i18NBeanFactory.getI18NBean().getText("com.atlassian.confluence.extra.flyingpdf.toc");
        return new PdfNode(tocString, this.renderPdf(contextPath, tocString, xhtml, "toc", pageExportMetrics));
    }

    private Document renderToc(String contextPath, Space space, List<PdfNode> pages, Map<String, Integer> locations) throws ImportExportException {
        TocBuilder tocBuilder = new TocBuilder(contextPath, space.getKey());
        pages.forEach(p -> this.populateTocBuilder(tocBuilder, (PdfNode)p, 0, locations));
        String baseUrl = UrlUtils.getFullUrl(this.settingsManager.getGlobalSettings().getBaseUrl(), contextPath);
        return this.intermediateHtmlBuilder.generateTableOfContents(baseUrl, space, tocBuilder);
    }

    private void populateTocBuilder(TocBuilder tocBuilder, PdfNode node, int level, Map<String, Integer> locations) {
        String title = node.getPageTitle();
        if (locations.containsKey(title)) {
            tocBuilder.addEntry(level, title, locations.get(node.getPageTitle()));
        } else {
            tocBuilder.addEntry(level, title);
        }
        node.getChildren().forEach(n -> this.populateTocBuilder(tocBuilder, (PdfNode)n, level + 1, locations));
    }

    @Override
    public File createPdfForPage(User user, AbstractPage page, String contextPath, PageExportMetrics pageExportMetrics) throws ImportExportException {
        this.exportPermissionChecker.checkAuthorization(user, page);
        String tmpFilePrefix = this.getTmpFilePrefix(page);
        return this.createPdf(page, contextPath, LinkRenderingDetails.anchors(), tmpFilePrefix, pageExportMetrics, DecorationPolicy.none()).getFile();
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
        return space != null && this.apiSpaceService.findContent((com.atlassian.confluence.api.model.content.Space)this.apiRestEntityFactory.buildRestEntityFrom(space, Expansions.EMPTY).getDelegate(), new Expansion[0]).withDepth(Depth.ROOT).fetchMany(ContentType.PAGE, (PageRequest)new SimplePageRequest(0, 10)).size() > 0;
    }

    @Override
    public PdfExportProgressMonitor createProgressMonitor(ProgressMeter progressMeter) {
        return new SandboxProgressMonitor(this.i18NBeanFactory.getI18NBean(), this.errorMessages, progressMeter);
    }

    private PdfNode createPdfTreeNode(ContentNode contentNode, String contextPath, PdfExportProgressMonitor progress, Collection<Page> internalPages, SpaceExportMetrics spaceExportMetrics, PageExportMetrics pageExportMetrics) throws ImportExportException {
        spaceExportMetrics.getPageExportMetrics().add(pageExportMetrics);
        Page page = contentNode.getPage();
        progress.performingHtmlToPdfConversionForPage(page.getTitle());
        String tmpFilePrefix = this.getTmpFilePrefix((AbstractPage)page);
        RenderedPdfFile file = this.createPdf((AbstractPage)page, contextPath, new LinkRenderingDetails(internalPages, LinkFixer.InternalPageStrategy.NORMALISE), tmpFilePrefix, pageExportMetrics, DecorationPolicy.headerAndFooter());
        PdfNode node = new PdfNode(page.getTitle(), file);
        for (ContentNode child : contentNode.getChildren()) {
            PageExportMetrics childInfo = new PageExportMetrics();
            node.addChild(this.createPdfTreeNode(child, contextPath, progress, internalPages, spaceExportMetrics, childInfo));
        }
        return node;
    }

    private String getTmpFilePrefix(AbstractPage page) {
        String tmpFilePrefix = page.getTitle();
        if (!GeneralUtil.isSafeTitleForFilesystem((String)tmpFilePrefix)) {
            tmpFilePrefix = page.getIdAsString();
        }
        return tmpFilePrefix;
    }

    private String getSpaceExportFileName(String spaceKey) {
        String differentiators = spaceKey.startsWith("~") ? spaceKey.substring(1) : spaceKey;
        try {
            return this.pdfExportFileNameGenerator.getExportFile(new String[]{differentiators}).getAbsolutePath();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Document createDocument(Page page, LinkRenderingDetails linkDetails, DecorationPolicy decoration) {
        Document xhtml;
        ContentTree tree = new ContentTree();
        tree.addRootNode(new ContentNode(page));
        try {
            xhtml = this.intermediateHtmlBuilder.buildHtml(tree, page.getSpace(), linkDetails, decoration);
        }
        catch (ImportExportException e) {
            log.error("error build xml dom", (Throwable)e);
            throw new RuntimeException(e);
        }
        return xhtml;
    }

    private Document createDocument(BlogPost blogPost) {
        Document xhtml;
        try {
            xhtml = this.intermediateHtmlBuilder.buildHtml(blogPost);
        }
        catch (ImportExportException e) {
            throw new RuntimeException(e);
        }
        return xhtml;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private RenderedPdfFile createPdf(AbstractPage page, String contextPath, LinkRenderingDetails linkDetails, String filePrefix, PageExportMetrics pageExportMetrics, DecorationPolicy decoration) throws ImportExportException {
        log.debug("Exporting page {}", (Object)page.getId());
        Document xhtml = page instanceof Page ? this.createDocument((Page)page, linkDetails, decoration) : this.createDocument((BlogPost)page);
        long startTime = System.currentTimeMillis();
        try {
            pageExportMetrics.setPageId(page.getId());
            pageExportMetrics.setPageRevision(page.getConfluenceRevision().hashCode());
            RenderedPdfFile result = this.renderPdf(contextPath, page.getTitle(), xhtml, filePrefix, pageExportMetrics);
            pageExportMetrics.getExportResults().setExportStatus(ExportStatus.OK);
            pageExportMetrics.getExportResults().setPdfPagesTotal(result.getNumPages());
            pageExportMetrics.getExportResults().setPdfFileSizeBytes(result.getFile().length());
            RenderedPdfFile renderedPdfFile = result;
            return renderedPdfFile;
        }
        finally {
            pageExportMetrics.setTimeMs((int)(System.currentTimeMillis() - startTime));
        }
    }

    private RenderedPdfFile renderPdf(String contextPath, String pageTitle, Document xhtml, String filePrefix, PageExportMetrics pageExportMetrics) throws ImportExportException {
        String filename = SandboxPdfExporterService.uniqueFilename(filePrefix);
        return this.sandboxXmlToPdfConverter.convertXhtmlToPdf(filename, pageTitle, xhtml, contextPath, SandboxPdfExporterService.getUsername(), pageExportMetrics);
    }

    private static String uniqueFilename(String filePrefix) {
        String trimmedPrefix = filePrefix.substring(0, Math.min(filePrefix.length(), 32));
        return trimmedPrefix + "_" + UUID.randomUUID().toString().replaceAll("-", "");
    }

    private static String getUsername() {
        return AuthenticatedUserThreadLocal.getUsername();
    }
}

