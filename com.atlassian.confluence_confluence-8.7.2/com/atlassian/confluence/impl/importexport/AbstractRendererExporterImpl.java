/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.FileUtils
 *  com.atlassian.core.util.ProgressMeter
 *  com.atlassian.renderer.util.UrlUtil
 *  com.atlassian.user.User
 *  com.opensymphony.module.sitemesh.SitemeshBuffer
 *  com.opensymphony.module.sitemesh.html.util.StringSitemeshBuffer
 *  com.opensymphony.util.TextUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.importexport;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.impl.importexport.AbstractExporterImpl;
import com.atlassian.confluence.importexport.DefaultExportContext;
import com.atlassian.confluence.importexport.ExportLinkFormatter;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.importexport.impl.ExportImageDescriptor;
import com.atlassian.confluence.importexport.impl.ExportPathUtils;
import com.atlassian.confluence.importexport.impl.ExportUtils;
import com.atlassian.confluence.importexport.impl.HtmlImageParser;
import com.atlassian.confluence.importexport.impl.ImageProcessingRule;
import com.atlassian.confluence.importexport.resource.DownloadResourceManager;
import com.atlassian.confluence.importexport.resource.DownloadResourceNotFoundException;
import com.atlassian.confluence.importexport.resource.DownloadResourceReader;
import com.atlassian.confluence.importexport.resource.UnauthorizedDownloadResourceException;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.ContentTree;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceDescription;
import com.atlassian.confluence.themes.ThemeManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.UserPreferencesAccessor;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.TemplateSupport;
import com.atlassian.confluence.util.io.ConfluenceFileUtils;
import com.atlassian.core.util.FileUtils;
import com.atlassian.core.util.ProgressMeter;
import com.atlassian.renderer.util.UrlUtil;
import com.atlassian.user.User;
import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.html.util.StringSitemeshBuffer;
import com.opensymphony.util.TextUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Date;
import java.util.Set;
import javax.xml.transform.TransformerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRendererExporterImpl
extends AbstractExporterImpl {
    private static final Logger log = LoggerFactory.getLogger(AbstractRendererExporterImpl.class);
    private TextUtils textUtil = new TextUtils();
    private ExportUtils exportUtils = new ExportUtils();
    private DownloadResourceManager downloadResourceManager;
    private UserPreferencesAccessor userPreferencesAccessor;
    private FormatSettingsManager formatSettingsManager;
    private ThemeManager themeManager;
    private SettingsManager settingsManager;
    private TransformerFactory transformerFactory;
    private LocaleManager localeManager;
    private Renderer xhtmlRenderer;

    protected abstract String getFullExportPath(String var1, ConfluenceEntityObject var2) throws ImportExportException;

    protected abstract void doExportEntity(ConfluenceEntityObject var1, String var2) throws ImportExportException;

    @Override
    protected DefaultExportContext getWorkingExportContext() {
        return super.getWorkingExportContext();
    }

    @Override
    public String doExport(ProgressMeter progress) throws ImportExportException {
        this.checkHaveSomethingToExport();
        String baseExportPath = this.createAndSetExportDirectory();
        for (ConfluenceEntityObject entity : this.getWorkingExportContext().getWorkingEntities()) {
            if (entity instanceof Page) {
                this.exportPage((Page)entity, baseExportPath);
                continue;
            }
            if (entity instanceof Space) {
                this.exportSpace((Space)entity, baseExportPath);
                continue;
            }
            throw new ImportExportException("Data type is not supported to export!");
        }
        return baseExportPath;
    }

    private File getExportFile(String baseExportPath, ConfluenceEntityObject entity) throws ImportExportException {
        File exportFile = new File(this.getFullExportPath(baseExportPath, entity));
        AbstractRendererExporterImpl.verifySubPath(baseExportPath, exportFile);
        this.ensurePathExists(exportFile.getParent());
        return exportFile;
    }

    static void verifySubPath(String baseExportPath, File exportFile) throws ImportExportException {
        try {
            String canonicalBasePath = new File(baseExportPath).getCanonicalPath();
            String canonicalFullExportPath = exportFile.getCanonicalPath();
            if (!canonicalFullExportPath.startsWith(canonicalBasePath)) {
                throw new ImportExportException("Full export path [" + exportFile.getPath() + "] resolves to [" + canonicalFullExportPath + "] which is not a sub-path of [" + canonicalBasePath + "]");
            }
        }
        catch (IOException e) {
            log.warn("Could not canonicalise export path. Could not validate path safety.", (Throwable)e);
        }
    }

    protected void exportSpace(Space space, String baseExportPath) throws ImportExportException {
        File exportFile = this.getExportFile(baseExportPath, space);
        log.debug("trying to export space " + space.getKey());
        this.doExportEntity(space, exportFile.getAbsolutePath());
    }

    protected void exportPage(Page page, String baseExportPath) throws ImportExportException {
        File exportFile = this.getExportFile(baseExportPath, page);
        log.debug("trying to export page " + page.getTitle() + " in space " + page.getSpace().getKey());
        this.doExportEntity(page, exportFile.getAbsolutePath());
    }

    protected void ensurePathExists(String path) throws ImportExportException {
        File file = new File(path);
        if (!file.exists() && !file.mkdirs()) {
            throw new ImportExportException("Can not create the temporary directory for export [" + path + "]!");
        }
    }

    protected abstract ImageProcessingRule getImageProcessingRule(String var1);

    protected abstract ExportLinkFormatter getExportLinkFormatter();

    protected void exportImages(String html, Writer writer, String exportDir) {
        HtmlImageParser parser = new HtmlImageParser();
        try {
            Set<ExportImageDescriptor> exportImages = parser.parse((SitemeshBuffer)new StringSitemeshBuffer(html), writer, this.getImageProcessingRule(exportDir));
            for (ExportImageDescriptor exportImage : exportImages) {
                this.exportResource(exportImage.getImagePath(), exportDir, exportImage.getExportPath());
            }
        }
        catch (IOException e) {
            log.error("Error occurred while parsing the images for the export", (Throwable)e);
        }
    }

    protected void exportResource(String resourceUrl, String exportDir, String exportPath) {
        block13: {
            File destinationFile;
            if (log.isDebugEnabled()) {
                log.debug("Export from resourceUrl = " + resourceUrl);
                log.debug("Export to exportPath = " + exportPath);
            }
            if ((destinationFile = new File(exportDir, exportPath)).exists() || !ConfluenceFileUtils.isChildOf(new File(exportDir), destinationFile)) {
                return;
            }
            String userName = AuthenticatedUserThreadLocal.getUsername();
            String baseUrl = this.settingsManager.getGlobalSettings().getBaseUrl();
            String resourcePath = ExportPathUtils.constructRelativePath(baseUrl, resourceUrl);
            try {
                if (this.downloadResourceManager.matches(resourcePath)) {
                    DownloadResourceReader downloadResourceReader = this.downloadResourceManager.getResourceReader(userName, resourcePath, UrlUtil.getQueryParameters((String)resourceUrl));
                    if (downloadResourceReader == null) break block13;
                    try (InputStream stream = downloadResourceReader.getStreamForReading();){
                        FileUtils.copyFile((InputStream)stream, (File)destinationFile, (boolean)false);
                        break block13;
                    }
                }
                log.warn("There is no download resource manager to export the resource: " + resourcePath);
            }
            catch (UnauthorizedDownloadResourceException e) {
                log.error("User [" + userName + "] is unauthorised to export the resource: " + resourcePath);
            }
            catch (DownloadResourceNotFoundException e) {
                log.error("Resource not found for export: " + resourcePath);
            }
            catch (IOException e) {
                log.error("There was an error exporting the resource: " + resourcePath, (Throwable)e);
            }
        }
    }

    protected TemplateSupport createTemplateSupport(String templateExtension) {
        TemplateSupport templateSupport = new TemplateSupport(this.themeManager, templateExtension);
        templateSupport.putInContext("i18n", this.i18nBeanFactory.getI18NBean());
        templateSupport.putInContext("contentConverter", new ContentConverter(this.getWorkingExportContext().getContentTree(), this.xhtmlRenderer));
        templateSupport.putInContext("generalUtil", GeneralUtil.INSTANCE);
        templateSupport.putInContext("htmlUtil", HtmlUtil.INSTANCE);
        templateSupport.putInContext("textUtil", this.textUtil);
        templateSupport.putInContext("exportUtils", this.exportUtils);
        templateSupport.putInContext("exportDate", new Date());
        templateSupport.putInContext("exportContext", this.context);
        templateSupport.putInContext("dateFormatter", this.getDateFormatter(this.context.getUser()));
        templateSupport.putInContext("baseUrl", this.settingsManager.getGlobalSettings().getBaseUrl());
        return templateSupport;
    }

    private DateFormatter getDateFormatter(User user) {
        return this.userPreferencesAccessor.getConfluenceUserPreferences(user).getDateFormatter(this.formatSettingsManager, this.localeManager);
    }

    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    public void setThemeManager(ThemeManager themeManager) {
        this.themeManager = themeManager;
    }

    public void setDownloadResourceManager(DownloadResourceManager downloadResourceManager) {
        this.downloadResourceManager = downloadResourceManager;
    }

    public void setUserPreferencesAccessor(UserPreferencesAccessor userPreferencesAccessor) {
        this.userPreferencesAccessor = userPreferencesAccessor;
    }

    public void setFormatSettingsManager(FormatSettingsManager formatSettingsManager) {
        this.formatSettingsManager = formatSettingsManager;
    }

    public void setTransformerFactory(TransformerFactory xsltTransformer) {
        this.transformerFactory = xsltTransformer;
    }

    protected TransformerFactory getTransformerFactory() {
        return this.transformerFactory;
    }

    public void setLocaleManager(LocaleManager localeManager) {
        this.localeManager = localeManager;
    }

    public void setXhtmlRenderer(Renderer xhtmlRenderer) {
        this.xhtmlRenderer = xhtmlRenderer;
    }

    public static class ContentConverter {
        private final ContentTree contentTree;
        private final Renderer xhtmlRenderer;

        public ContentConverter(ContentTree contentTree, Renderer xhtmlRenderer) {
            this.contentTree = contentTree;
            this.xhtmlRenderer = xhtmlRenderer;
        }

        public String convertToXHtml(Page page) {
            return this.xhtmlRenderer.render(page, this.createConversionContext(page));
        }

        public String convertToXHtml(SpaceDescription spaceDesc) {
            return this.xhtmlRenderer.render(spaceDesc, this.createConversionContext(spaceDesc));
        }

        public String convertToXHtml(Comment comment) {
            return this.xhtmlRenderer.render(comment, this.createConversionContext(comment));
        }

        private ConversionContext createConversionContext(ContentEntityObject ceo) {
            PageContext pageContext = ceo.toPageContext();
            pageContext.setOutputType(ConversionContextOutputType.HTML_EXPORT.value());
            DefaultConversionContext conversionContext = new DefaultConversionContext(pageContext);
            conversionContext.setContentTree(this.contentTree);
            return conversionContext;
        }
    }
}

