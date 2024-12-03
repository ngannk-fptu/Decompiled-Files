/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Renderer
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.importexport.ImportExportException
 *  com.atlassian.confluence.importexport.impl.ExportFileNameGenerator
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.pages.ContentNode
 *  com.atlassian.confluence.pages.ContentTree
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.util.velocity.VelocityUtils
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.renderer.RenderContext
 *  com.google.common.collect.ImmutableList
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.velocity.VelocityContext
 *  org.apache.velocity.context.Context
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.flyingpdf.html;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.extra.flyingpdf.PdfExportProgressMonitor;
import com.atlassian.confluence.extra.flyingpdf.config.PdfExportSettingsManager;
import com.atlassian.confluence.extra.flyingpdf.html.AutoFontScaleUtils;
import com.atlassian.confluence.extra.flyingpdf.html.BookmarksBuilder;
import com.atlassian.confluence.extra.flyingpdf.html.DecorationPolicy;
import com.atlassian.confluence.extra.flyingpdf.html.ExportHtmlService;
import com.atlassian.confluence.extra.flyingpdf.html.HtmlConverterUtils;
import com.atlassian.confluence.extra.flyingpdf.html.HtmlToDomParser;
import com.atlassian.confluence.extra.flyingpdf.html.LinkFixer;
import com.atlassian.confluence.extra.flyingpdf.html.LinkRenderingDetails;
import com.atlassian.confluence.extra.flyingpdf.html.TocBuilder;
import com.atlassian.confluence.extra.flyingpdf.html.XhtmlBuilder;
import com.atlassian.confluence.extra.flyingpdf.impl.DefaultProgressMonitor;
import com.atlassian.confluence.extra.flyingpdf.impl.PdfResourceManager;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.importexport.impl.ExportFileNameGenerator;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.ContentNode;
import com.atlassian.confluence.pages.ContentTree;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.renderer.RenderContext;
import com.google.common.collect.ImmutableList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Component
public class RenderedXhtmlBuilder
implements XhtmlBuilder {
    static final String MAIN_STYLE_ID = "confluence.flyingpdf.styleId";
    private static final int CHARACTER_PER_LINE = Integer.getInteger("confluence.flyingpdf.default.characters.per.line", 80);
    private static final Pattern HEADING_PATTERN = Pattern.compile("\\</?h(\\d)(\\>|\\s)");
    private static final String PAGE_TEMPLATE_NAME = "/templates/extra/pdfexport/pagehtml.vm";
    private static final String COMPLETE_EXPORT_PAGE_TEMPLATE_NAME = "/templates/extra/pdfexport/completeexport.vm";
    private static final String TOC_TEMPLATE_NAME = "/templates/extra/pdfexport/toc.vm";
    private static final String CONFLUENCE_BASE_STYLES = RenderedXhtmlBuilder.loadResource("master.css");
    private static final Logger LOG = LoggerFactory.getLogger(RenderedXhtmlBuilder.class);
    private final I18NBeanFactory i18NBeanFactory;
    private final Renderer xhtmlRenderer;
    private final SettingsManager settingsManager;
    private final ExportHtmlService exportHtmlService;
    private final PdfExportSettingsManager pdfSettings;
    private final PdfResourceManager pdfResourceManager;
    private final ExportFileNameGenerator htmlExportFileNameGenerator;

    public RenderedXhtmlBuilder(@ComponentImport I18NBeanFactory i18NBeanFactory, @ComponentImport Renderer xhtmlRenderer, @ComponentImport SettingsManager settingsManager, ExportHtmlService exportHtmlService, PdfExportSettingsManager pdfSettings, PdfResourceManager pdfResourceManager, ExportFileNameGenerator htmlExportFileNameGenerator) {
        this.i18NBeanFactory = i18NBeanFactory;
        this.xhtmlRenderer = xhtmlRenderer;
        this.settingsManager = settingsManager;
        this.exportHtmlService = exportHtmlService;
        this.pdfSettings = pdfSettings;
        this.pdfResourceManager = pdfResourceManager;
        this.htmlExportFileNameGenerator = htmlExportFileNameGenerator;
    }

    @Override
    public Document buildHtml(ContentTree contentTree, Space space, LinkRenderingDetails linkRendering, DecorationPolicy decoration) throws ImportExportException {
        return this.buildHtml(contentTree, space, linkRendering, decoration, new DefaultProgressMonitor());
    }

    @Override
    public Document buildHtml(ContentTree contentTree, Space space, LinkRenderingDetails linkRendering, DecorationPolicy decoration, PdfExportProgressMonitor progress) throws ImportExportException {
        TocBuilder tocBuilder = new TocBuilder();
        BookmarksBuilder bookmarksBuilder = new BookmarksBuilder();
        List<String> pageHtml = this.renderContentTreeNodes(contentTree.getRootNodes(), tocBuilder, bookmarksBuilder, 0, contentTree, progress);
        LinkFixer linkFixer = new LinkFixer(space.getKey(), this.settingsManager.getGlobalSettings().getBaseUrl(), linkRendering.getLinkStrategy());
        this.populateLinkFixer(linkFixer, contentTree, linkRendering.getInternalPages());
        return this.buildHtml(pageHtml, space, decoration, tocBuilder, bookmarksBuilder, linkFixer);
    }

    @Override
    public Document buildHtml(BlogPost blogPost) throws ImportExportException {
        ImmutableList pageHtml = ImmutableList.of((Object)this.renderToHtml((AbstractPage)blogPost, null));
        LinkFixer linkFixer = new LinkFixer(blogPost.getSpace().getKey(), this.settingsManager.getGlobalSettings().getBaseUrl(), LinkFixer.InternalPageStrategy.ANCHOR);
        return this.buildHtml((List<String>)pageHtml, blogPost.getSpace(), DecorationPolicy.none(), new TocBuilder(), new BookmarksBuilder(), linkFixer);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Document buildHtml(List<String> pageHtml, Space space, DecorationPolicy decoration, TocBuilder tocBuilder, BookmarksBuilder bookmarksBuilder, LinkFixer linkFixer) throws ImportExportException {
        Reader htmlReader = this.createCompleteExportHtml(pageHtml, tocBuilder, space, decoration);
        try {
            HtmlToDomParser domParser = HtmlConverterUtils.getHtmlToXhtmlParser(linkFixer);
            Document xhtmlDocument = domParser.parse(htmlReader);
            this.addTableLayout(xhtmlDocument);
            this.insertBookmarkElement(xhtmlDocument, bookmarksBuilder);
            AutoFontScaleUtils.applyTableScalingLogic(xhtmlDocument);
            Document document = xhtmlDocument;
            return document;
        }
        finally {
            try {
                htmlReader.close();
            }
            catch (IOException ex) {
                LOG.warn("Exception while closing the intermediate HTML file for reading.");
            }
        }
    }

    private void addTableLayout(Document xhtmlDocument) {
        NodeList tables = xhtmlDocument.getElementsByTagName("table");
        if (tables.getLength() == 0) {
            return;
        }
        for (int tableIndex = 0; tableIndex < tables.getLength(); ++tableIndex) {
            Element table = (Element)tables.item(tableIndex);
            this.fixTableLayout(table);
        }
    }

    private void fixTableLayout(Element table) {
        NodeList headRow = this.getTableHeader(table);
        if (headRow != null && this.rowIsTooLong(headRow)) {
            this.fixTableStyle(table);
            return;
        }
        this.fixTableByBody(table);
    }

    private void fixTableByBody(Element table) {
        Element body = this.getFirstElementByTagName(table, "tbody");
        if (null != body) {
            NodeList children = body.getElementsByTagName("tr");
            for (int index = 0; index < children.getLength(); ++index) {
                Node row = children.item(index);
                if (!this.rowIsTooLong(row.getChildNodes())) continue;
                this.fixTableStyle(table);
                return;
            }
        }
    }

    private Element getFirstElementByTagName(Element element, String tag) {
        NodeList elements = element.getElementsByTagName(tag);
        if (elements.getLength() > 0) {
            return (Element)elements.item(0);
        }
        return null;
    }

    private void fixTableStyle(Element table) {
        table.setAttribute("class", table.getAttribute("class") + " fixedTableLayout");
        Element colGroup = this.getFirstElementByTagName(table, "colgroup");
        if (null != colGroup) {
            NodeList cols = colGroup.getElementsByTagName("col");
            for (int index = 0; index < cols.getLength(); ++index) {
                Element col = (Element)cols.item(index);
                col.removeAttribute("style");
            }
        }
    }

    private boolean rowIsTooLong(NodeList row) {
        int characterCount = 0;
        for (int col = 0; col < row.getLength(); ++col) {
            characterCount += this.getColLength(row.item(col));
        }
        return characterCount > CHARACTER_PER_LINE;
    }

    private int getColLength(Node col) {
        String content = col.getTextContent();
        return content == null ? 0 : content.length();
    }

    private NodeList getTableHeader(Element table) {
        Element header = this.getFirstElementByTagName(table, "thead");
        if (null != header) {
            Element row = this.getFirstElementByTagName(header, "tr");
            return row == null ? null : row.getChildNodes();
        }
        return null;
    }

    private void populateLinkFixer(LinkFixer linkFixer, ContentTree contentTree, Collection<Page> additionalInternalPages) {
        List contentNodes = contentTree.getAllContentNodes();
        for (ContentNode node : contentNodes) {
            Page p2 = node.getPage();
            linkFixer.addPage(p2.getIdAsString(), p2.getTitle());
        }
        additionalInternalPages.forEach(p -> linkFixer.addPage(p.getIdAsString(), p.getTitle()));
    }

    @Override
    public Document generateTableOfContents(String baseUrl, Space space, TocBuilder tocBuilder) throws ImportExportException {
        VelocityContext context = this.createCompleteVelocityContext(Collections.emptyList(), tocBuilder, space, DecorationPolicy.titlePage());
        StringWriter writer = new StringWriter();
        try {
            this.exportHtmlService.renderTemplateWithoutSwallowingErrors(TOC_TEMPLATE_NAME, (Context)context, writer);
        }
        catch (Exception ex) {
            throw new ImportExportException("Failure while rendering the /templates/extra/pdfexport/toc.vm", (Throwable)ex);
        }
        HtmlToDomParser domParser = HtmlConverterUtils.getHtmlToXhtmlParser(new LinkFixer(space.getKey(), baseUrl, LinkFixer.InternalPageStrategy.NORMALISE));
        Document xhtmlDocument = domParser.parse(new StringReader(writer.getBuffer().toString()));
        this.addTableLayout(xhtmlDocument);
        BookmarksBuilder bookmarks = new BookmarksBuilder();
        bookmarks.beginEntry(this.i18NBeanFactory.getI18NBean().getText("com.atlassian.confluence.extra.flyingpdf.toc"));
        bookmarks.endEntry();
        this.insertBookmarkElement(xhtmlDocument, bookmarks);
        AutoFontScaleUtils.applyTableScalingLogic(xhtmlDocument);
        return xhtmlDocument;
    }

    private Reader createCompleteExportHtml(List<String> renderedPages, TocBuilder tocBuilder, Space space, DecorationPolicy decoration) throws ImportExportException {
        Writer writer;
        RenderOutput output = renderedPages.size() > 1 ? new FileRenderOutput(this.htmlExportFileNameGenerator, "export", "intermediate") : new StringRenderOutput();
        VelocityContext context = this.createCompleteVelocityContext(renderedPages, tocBuilder, space, decoration);
        try {
            writer = output.getOutputWriter();
        }
        catch (IOException ex) {
            throw new ImportExportException("Failed to open output writer for the intermediate HTML file.", (Throwable)ex);
        }
        try {
            this.exportHtmlService.renderTemplateWithoutSwallowingErrors(COMPLETE_EXPORT_PAGE_TEMPLATE_NAME, (Context)context, writer);
        }
        catch (Exception ex) {
            throw new ImportExportException("Failure while rendering the /templates/extra/pdfexport/completeexport.vm", (Throwable)ex);
        }
        finally {
            try {
                writer.close();
            }
            catch (IOException ex) {
                LOG.warn("Failed to close the intermediate HTML file during PDF export.", (Throwable)ex);
            }
        }
        try {
            return output.getResultReader();
        }
        catch (IOException ex) {
            throw new ImportExportException("Failed to open the intermediate HTML file for reading.");
        }
    }

    private VelocityContext createCompleteVelocityContext(List<String> renderedPages, TocBuilder tocBuilder, Space currentSpace, DecorationPolicy decoration) {
        String titlePage;
        String footer;
        String header;
        HashMap<String, Object> contextMap = new HashMap<String, Object>(8);
        if (decoration.components().contains((Object)DecorationPolicy.DecorationComponent.HEADER) && !StringUtils.isEmpty((CharSequence)(header = this.getHeader(currentSpace)))) {
            contextMap.put("headerHtml", header);
        }
        if (decoration.components().contains((Object)DecorationPolicy.DecorationComponent.FOOTER) && !StringUtils.isEmpty((CharSequence)(footer = this.getFooter(currentSpace)))) {
            contextMap.put("footerHtml", footer);
        }
        if (decoration.components().contains((Object)DecorationPolicy.DecorationComponent.TITLE_PAGE) && !StringUtils.isEmpty((CharSequence)(titlePage = this.getTitlePage(currentSpace)))) {
            contextMap.put("titleHtml", titlePage);
        }
        if (decoration.components().contains((Object)DecorationPolicy.DecorationComponent.PAGE_NUMBERS)) {
            contextMap.put("pageNumbers", true);
        }
        String customStyles = this.getUserStyles(currentSpace);
        Object userStyle = CONFLUENCE_BASE_STYLES;
        if (customStyles != null) {
            userStyle = (String)userStyle + customStyles;
        }
        if (!StringUtils.isEmpty((CharSequence)userStyle)) {
            contextMap.put("userStyleHtml", userStyle);
        }
        contextMap.put("styleId", MAIN_STYLE_ID);
        contextMap.put("tocEntries", tocBuilder.getEntries());
        contextMap.put("pdfResourceManager", this.pdfResourceManager);
        contextMap.put("pages", renderedPages);
        return new VelocityContext(contextMap);
    }

    private String getUserStyles(Space currentSpace) {
        String customStyles = this.pdfSettings.getStyle((BandanaContext)new ConfluenceBandanaContext(currentSpace));
        if (StringUtils.isEmpty((CharSequence)customStyles)) {
            customStyles = this.pdfSettings.getStyle((BandanaContext)new ConfluenceBandanaContext());
        }
        if (StringUtils.isNotEmpty((CharSequence)customStyles)) {
            return customStyles;
        }
        return "";
    }

    private String getTitlePage(Space currentSpace) {
        String titlePage = this.pdfSettings.getTitlePage((BandanaContext)new ConfluenceBandanaContext(currentSpace));
        if (StringUtils.isEmpty((CharSequence)titlePage)) {
            titlePage = this.pdfSettings.getTitlePage((BandanaContext)new ConfluenceBandanaContext());
        }
        return titlePage;
    }

    private String getFooter(Space currentSpace) {
        String footer = this.pdfSettings.getFooter((BandanaContext)new ConfluenceBandanaContext(currentSpace));
        if (StringUtils.isEmpty((CharSequence)footer)) {
            footer = this.pdfSettings.getFooter((BandanaContext)new ConfluenceBandanaContext());
        }
        return footer;
    }

    private String getHeader(Space currentSpace) {
        String header = this.pdfSettings.getHeader((BandanaContext)new ConfluenceBandanaContext(currentSpace));
        if (StringUtils.isEmpty((CharSequence)header)) {
            header = this.pdfSettings.getHeader((BandanaContext)new ConfluenceBandanaContext());
        }
        return header;
    }

    private List<String> renderContentTreeNodes(List<ContentNode> nodes, TocBuilder tocBuilder, BookmarksBuilder bookmarksBuilder, int level, ContentTree fullContentTree, PdfExportProgressMonitor progress) {
        ArrayList<String> renderedPagesContent = new ArrayList<String>();
        for (ContentNode node : nodes) {
            Page page = node.getPage();
            String renderedHtml = this.renderToHtml((AbstractPage)page, fullContentTree);
            renderedPagesContent.add(renderedHtml);
            progress.completedExportedHtmlConversionForPage(String.valueOf(page.getId()), page.getTitle());
            tocBuilder.addEntry(level, page.getTitle());
            bookmarksBuilder.beginEntry(page.getTitle());
            List children = node.getChildren();
            if (children != null && !children.isEmpty()) {
                renderedPagesContent.addAll(this.renderContentTreeNodes(children, tocBuilder, bookmarksBuilder, level + 1, fullContentTree, progress));
            }
            bookmarksBuilder.endEntry();
        }
        return renderedPagesContent;
    }

    private String renderToHtml(AbstractPage page, ContentTree fullContentTree) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Rendering to exported XHTML page id=" + page.getId() + " (" + page.getTitle() + ")");
        }
        PageContext context = page.toPageContext();
        context.setBaseUrl(this.settingsManager.getGlobalSettings().getBaseUrl());
        context.setOutputType("pdf");
        DefaultConversionContext conversionContext = new DefaultConversionContext((RenderContext)context);
        if (fullContentTree != null) {
            conversionContext.setContentTree(fullContentTree);
        }
        String html = this.xhtmlRenderer.render((ContentEntityObject)page, (ConversionContext)conversionContext);
        return this.renderPageTemplate(page.getTitle(), html);
    }

    private String renderPageTemplate(String title, String content) {
        HashMap<String, String> contextMap = new HashMap<String, String>(2);
        contextMap.put("pageTitle", title);
        contextMap.put("contentHtml", content);
        return VelocityUtils.getRenderedTemplate((String)PAGE_TEMPLATE_NAME, contextMap);
    }

    private void insertBookmarkElement(Document document, BookmarksBuilder builder) {
        List<BookmarksBuilder.BookmarkEntry> topLevelBookmarks = builder.getEntries();
        if (topLevelBookmarks.isEmpty()) {
            return;
        }
        Element bookmarksElement = document.createElement("bookmarks");
        NodeList headList = document.getElementsByTagName("head");
        if (headList.getLength() < 1) {
            return;
        }
        Node headNode = headList.item(0);
        headNode.appendChild(bookmarksElement);
        this.appendBookmarksElement(document, bookmarksElement, topLevelBookmarks);
    }

    private void appendBookmarksElement(Document document, Node parentNode, List<BookmarksBuilder.BookmarkEntry> bookmarkEntries) {
        for (BookmarksBuilder.BookmarkEntry entry : bookmarkEntries) {
            Element bookmarkElement = document.createElement("bookmark");
            bookmarkElement.setAttribute("name", entry.getTitle());
            bookmarkElement.setAttribute("href", "#" + entry.getTitle());
            parentNode.appendChild(bookmarkElement);
            if (!entry.hasChildEntries()) continue;
            this.appendBookmarksElement(document, bookmarkElement, entry.getChildEntries());
        }
    }

    private static String loadResource(String masterCss) {
        try {
            InputStream in = RenderedXhtmlBuilder.class.getResourceAsStream("/templates/extra/pdfexport/" + masterCss);
            String ret = IOUtils.toString((InputStream)in, (String)"ASCII");
            IOUtils.closeQuietly((InputStream)in);
            return ret;
        }
        catch (Throwable t) {
            if (LOG != null) {
                LOG.error("Unable to load the default styles for PDF export", t);
            }
            return "";
        }
    }

    private static class StringRenderOutput
    implements RenderOutput {
        private final StringWriter writer = new StringWriter();

        private StringRenderOutput() {
        }

        @Override
        public Writer getOutputWriter() {
            return this.writer;
        }

        @Override
        public Reader getResultReader() {
            return new StringReader(this.writer.getBuffer().toString());
        }
    }

    private static class FileRenderOutput
    implements RenderOutput {
        private final File outputFile;

        FileRenderOutput(ExportFileNameGenerator fileNameGenerator, String ... distinguishers) throws ImportExportException {
            try {
                this.outputFile = fileNameGenerator.getExportFile(distinguishers);
            }
            catch (IOException ex) {
                throw new ImportExportException("Failed to create output file during PDF export.");
            }
        }

        @Override
        public Writer getOutputWriter() throws IOException {
            return new BufferedWriter(new OutputStreamWriter((OutputStream)new FileOutputStream(this.outputFile), StandardCharsets.UTF_8));
        }

        @Override
        public Reader getResultReader() throws IOException {
            return new BufferedReader(new InputStreamReader((InputStream)new FileInputStream(this.outputFile), StandardCharsets.UTF_8));
        }
    }

    private static interface RenderOutput {
        public Writer getOutputWriter() throws IOException;

        public Reader getResultReader() throws IOException;
    }
}

