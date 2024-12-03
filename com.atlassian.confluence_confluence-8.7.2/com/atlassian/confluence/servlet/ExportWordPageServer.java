/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.core.util.FileUtils
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.servlet.DownloadException
 *  com.atlassian.plugin.servlet.DownloadableResource
 *  com.atlassian.plugin.webresource.PluginResourceLocator
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.links.Link
 *  com.atlassian.renderer.links.LinkRenderer
 *  com.atlassian.renderer.links.UnpermittedLink
 *  com.atlassian.renderer.links.UnresolvedLink
 *  com.atlassian.renderer.util.UrlUtil
 *  com.atlassian.renderer.v2.components.HtmlEscaper
 *  com.atlassian.user.User
 *  com.atlassian.velocity.htmlsafe.HtmlFragment
 *  javax.activation.DataHandler
 *  javax.activation.DataSource
 *  javax.activation.FileDataSource
 *  javax.activation.MimetypesFileTypeMap
 *  javax.activation.URLDataSource
 *  javax.mail.BodyPart
 *  javax.mail.MessagingException
 *  javax.mail.Multipart
 *  javax.mail.Session
 *  javax.mail.internet.MimeBodyPart
 *  javax.mail.internet.MimeMessage
 *  javax.mail.internet.MimeMultipart
 *  javax.servlet.ServletConfig
 *  javax.servlet.ServletException
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.codec.DecoderException
 *  org.apache.commons.codec.binary.Base64
 *  org.apache.commons.codec.binary.StringUtils
 *  org.apache.commons.codec.digest.DigestUtils
 *  org.apache.commons.codec.net.URLCodec
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 *  org.jsoup.Jsoup
 *  org.jsoup.nodes.Document
 *  org.jsoup.nodes.Element
 *  org.jsoup.nodes.Node
 *  org.jsoup.nodes.TextNode
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.servlet;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.content.render.xhtml.view.excerpt.Excerpter;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.core.DefaultContextPathHolder;
import com.atlassian.confluence.event.events.content.ContentExportedToWordEvent;
import com.atlassian.confluence.importexport.resource.DownloadResourceManager;
import com.atlassian.confluence.importexport.resource.DownloadResourceNotFoundException;
import com.atlassian.confluence.importexport.resource.DownloadResourceReader;
import com.atlassian.confluence.importexport.resource.UnauthorizedDownloadResourceException;
import com.atlassian.confluence.links.linktypes.PageCreateLink;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugin.services.DefaultVelocityHelperService;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.servlet.ServletManager;
import com.atlassian.confluence.servlet.SpringManagedServlet;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.SeraphUtils;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.servlet.DownloadException;
import com.atlassian.plugin.servlet.DownloadableResource;
import com.atlassian.plugin.webresource.PluginResourceLocator;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.links.Link;
import com.atlassian.renderer.links.LinkRenderer;
import com.atlassian.renderer.links.UnpermittedLink;
import com.atlassian.renderer.links.UnresolvedLink;
import com.atlassian.renderer.util.UrlUtil;
import com.atlassian.renderer.v2.components.HtmlEscaper;
import com.atlassian.user.User;
import com.atlassian.velocity.htmlsafe.HtmlFragment;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MimetypesFileTypeMap;
import javax.activation.URLDataSource;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public class ExportWordPageServer
implements ServletManager {
    private static final Logger log = LoggerFactory.getLogger(ExportWordPageServer.class);
    private static final int PIXELS_PER_INCH = 72;
    private static final double LETTER_SIZE_WIDTH_INCHES = 8.5;
    private static final String FILE_PREFIX = "file:";
    private static final String DATA_PREFIX = "data:";
    private static final Pattern RESOURCE_PATH_PATTERN = Pattern.compile("/s/(.*)/_/");
    private static final Integer MAX_EMBEDDED_IMAGES = Integer.getInteger("atlassian.confluence.export.word.max.embedded.images", 1000);
    private final ContextPathHolder contextPathHolder;
    private final GlobalSettingsManager settingsManager;
    private final PageManager pageManager;
    private final PermissionManager permissionManager;
    private final Renderer viewBodyTypeAwareRenderer;
    private final Excerpter excerpter;
    private final PluginResourceLocator pluginResourceLocator;
    private final DownloadResourceManager downloadResourceManager;
    private final VelocityHelperService velocityHelperService;
    private final EventPublisher eventPublisher;
    private final Settings globalSettings = this.getGlobalSettings();

    ExportWordPageServer(ContextPathHolder contextPathHolder, GlobalSettingsManager settingsManager, PageManager pageManager, PermissionManager permissionManager, Renderer viewBodyTypeAwareRenderer, Excerpter excerpter, PluginResourceLocator pluginResourceLocator, DownloadResourceManager downloadResourceManager, EventPublisher eventPublisher, VelocityHelperService velocityHelperService) {
        this.contextPathHolder = contextPathHolder;
        this.settingsManager = settingsManager;
        this.pageManager = pageManager;
        this.permissionManager = permissionManager;
        this.viewBodyTypeAwareRenderer = viewBodyTypeAwareRenderer;
        this.excerpter = excerpter;
        this.pluginResourceLocator = pluginResourceLocator;
        this.downloadResourceManager = downloadResourceManager;
        this.velocityHelperService = velocityHelperService;
        this.eventPublisher = eventPublisher;
    }

    @Deprecated(forRemoval=true)
    public ExportWordPageServer(BootstrapManager bootstrapManager, SettingsManager settingsManager, PageManager pageManager, PermissionManager permissionManager, Renderer viewBodyTypeAwareRenderer, Excerpter excerpter, PluginResourceLocator pluginResourceLocator, DownloadResourceManager downloadResourceManager, EventPublisher eventPublisher, MimetypesFileTypeMap ignored) {
        this(new DefaultContextPathHolder(bootstrapManager), (GlobalSettingsManager)settingsManager, pageManager, permissionManager, viewBodyTypeAwareRenderer, excerpter, pluginResourceLocator, downloadResourceManager, eventPublisher, new DefaultVelocityHelperService());
    }

    public Settings getGlobalSettings() {
        if (this.settingsManager == null) {
            return new Settings();
        }
        return this.settingsManager.getGlobalSettings();
    }

    @Override
    public void servletInitialised(SpringManagedServlet springManagedServlet, ServletConfig servletConfig) throws ServletException {
    }

    @Override
    public void servletDestroyed(SpringManagedServlet springManagedServlet) {
    }

    @Override
    @Transactional
    public void service(SpringManagedServlet springManagedServlet, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pageIdParameter = request.getParameter("pageId");
        Long pageId = null;
        if (pageIdParameter != null) {
            try {
                pageId = Long.parseLong(pageIdParameter);
            }
            catch (NumberFormatException e) {
                response.sendError(404, "Page not found");
            }
        } else {
            response.sendError(404, "A valid page id was not specified");
        }
        if (pageId != null) {
            AbstractPage page = this.pageManager.getAbstractPage(pageId);
            if (this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, page)) {
                if (page == null || !page.isCurrent()) {
                    response.sendError(404);
                } else {
                    this.outputWordDocument(page, request, response);
                    this.eventPublisher.publish((Object)new ContentExportedToWordEvent(page));
                }
            } else if (request.getRemoteUser() == null) {
                request.setAttribute("atlassian.core.seraph.original.url", (Object)GeneralUtil.getOriginalUrl(request));
                response.sendRedirect(SeraphUtils.getLoginURL(request));
            } else {
                response.sendError(404);
            }
        }
    }

    private void outputWordDocument(AbstractPage page, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userAgentHeader = request.getHeader("User-Agent");
        if (userAgentHeader != null && (ExportWordPageServer.isMSIE8OrLess(userAgentHeader) || ExportWordPageServer.isSafari(userAgentHeader))) {
            response.addHeader("Content-Disposition", "attachment;filename=" + HtmlUtil.urlEncode(page.getTitle()) + ".doc");
        } else {
            response.addHeader("Content-Disposition", "attachment;filename*=" + this.settingsManager.getGlobalSettings().getDefaultEncoding().toLowerCase() + "''" + HtmlUtil.urlEncode(page.getTitle()) + ".doc;");
        }
        response.setHeader("Cache-Control", "max-age=5");
        response.setHeader("Pragma", "");
        response.setDateHeader("Expires", System.currentTimeMillis() + 300L);
        response.setContentType("application/vnd.ms-word;charset=" + this.settingsManager.getGlobalSettings().getDefaultEncoding());
        PageContext context = page.toPageContext();
        context.setBaseUrl(this.settingsManager.getGlobalSettings().getBaseUrl());
        context.setSiteRoot(this.contextPathHolder.getContextPath());
        context.setOutputType(ConversionContextOutputType.WORD.value());
        context.setLinkRenderer(new WordDocumentLinkRenderer(context));
        File tempDir = new File(BootstrapUtils.getBootstrapManager().getFilePathProperty("struts.multipart.saveDir"), "page-to-doc" + page.getIdAsString() + "-" + UUID.randomUUID());
        try {
            ServletActionContext.setRequest((HttpServletRequest)request);
            ServletActionContext.setResponse((HttpServletResponse)response);
            String renderedContent = this.viewBodyTypeAwareRenderer.render(page, (ConversionContext)new DefaultConversionContext(context));
            renderedContent = this.replaceEmojisWithShortcutText(renderedContent);
            Map<String, DataSource> imagesToDatasourceMap = this.extractImagesFromPage(renderedContent, tempDir);
            renderedContent = this.transformRenderedContent(imagesToDatasourceMap, renderedContent);
            HashMap<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("baseUrl", this.globalSettings.getBaseUrl());
            paramMap.put("page", page);
            paramMap.put("pixelsPerInch", 72);
            paramMap.put("renderedPageContent", new HtmlFragment((Object)renderedContent));
            String renderedTemplate = this.velocityHelperService.getRenderedTemplate("/pages/exportword.vm", paramMap);
            MimeMessage mhtmlOutput = this.constructMimeMessage(renderedTemplate, imagesToDatasourceMap.values());
            mhtmlOutput.writeTo((OutputStream)response.getOutputStream());
        }
        catch (MessagingException | XMLStreamException e) {
            throw new IOException(e);
        }
        finally {
            com.atlassian.core.util.FileUtils.deleteDir((File)tempDir);
            ServletActionContext.setRequest(null);
            ServletActionContext.setResponse(null);
        }
    }

    private String transformRenderedContent(Map<String, DataSource> imagesToDatasourceMap, String renderedContent) throws IOException {
        Document doc = Jsoup.parseBodyFragment((String)renderedContent);
        for (Map.Entry<String, DataSource> dataSourceEntry : imagesToDatasourceMap.entrySet()) {
            for (Element imgTag : doc.select("img[src=" + dataSourceEntry.getKey() + "]")) {
                imgTag.attr("src", dataSourceEntry.getValue().getName());
                this.fixAspectRatio(imgTag, dataSourceEntry.getKey(), dataSourceEntry.getValue().getInputStream());
            }
        }
        doc.outputSettings().indentAmount(0);
        Element body = doc.body();
        return body.html();
    }

    private String replaceEmojisWithShortcutText(String renderedContent) {
        Document doc = Jsoup.parseBodyFragment((String)renderedContent);
        for (Element imgTag : doc.select("img.emoticon")) {
            String shortcut = imgTag.attr("data-emoji-short-name");
            imgTag.replaceWith((Node)new TextNode(shortcut));
        }
        doc.outputSettings().indentAmount(0);
        Element body = doc.body();
        return body.html();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void fixAspectRatio(Element imgTag, String imgSrc, InputStream dataSourceInputStream) {
        InputStream is = dataSourceInputStream;
        ImageReader imgReader = null;
        try {
            ImageInputStream imageInputStream;
            Iterator<ImageReader> imgReaders;
            if (is == null) {
                is = new URLDataSource(new URL(imgSrc)).getInputStream();
            }
            if ((imgReaders = ImageIO.getImageReaders(imageInputStream = ImageIO.createImageInputStream(is))).hasNext()) {
                int desiredWidth;
                imgReader = imgReaders.next();
                imgReader.setInput(imageInputStream);
                int imgWidth = imgReader.getWidth(0);
                int imgHeight = imgReader.getHeight(0);
                int margin = 2;
                int maxWidth = 468;
                int n = desiredWidth = imgTag.hasAttr("width") ? Integer.parseInt(imgTag.attr("width"), 10) : imgWidth;
                if (desiredWidth > 468) {
                    desiredWidth = 468;
                }
                double imageAspectRatio = (double)imgWidth / (double)imgHeight;
                int desiredHeight = (int)((double)desiredWidth / imageAspectRatio);
                imgTag.attr("width", "" + desiredWidth);
                imgTag.attr("height", "" + desiredHeight);
            }
        }
        catch (IOException e) {
            log.warn("Unable to read from url {}. Image may appear warped : {}", (Object)imgSrc, (Object)e.getMessage());
            if (log.isDebugEnabled()) {
                log.warn("Unable to read from url " + imgSrc + ". Image may appear warped:", (Throwable)e);
            }
        }
        finally {
            if (imgReader != null) {
                imgReader.dispose();
            }
            try {
                if (is != null) {
                    is.close();
                }
            }
            catch (IOException ioe) {
                log.warn("Failed to close input stream", (Throwable)ioe);
            }
        }
    }

    private Map<String, DataSource> extractImagesFromPage(String renderedHtml, File tempDir) throws XMLStreamException {
        HashMap<String, DataSource> imagesToDatasourceMap = new HashMap<String, DataSource>();
        for (String imgSrc : this.excerpter.extractImageSrc(renderedHtml, (int)MAX_EMBEDDED_IMAGES)) {
            try {
                if (imagesToDatasourceMap.containsKey(imgSrc)) continue;
                InputStream inputStream = this.createInputStreamFromRelativeUrl(imgSrc);
                try {
                    if (inputStream != null) {
                        File tempFile = this.storeImageAsTempFile(inputStream, tempDir, DigestUtils.md5Hex((String)imgSrc));
                        FileDataSource datasource = new FileDataSource(tempFile);
                        imagesToDatasourceMap.put(imgSrc, (DataSource)datasource);
                        continue;
                    }
                    log.warn("Skipping datasource creation for non-relative url: " + imgSrc);
                }
                finally {
                    if (inputStream == null) continue;
                    inputStream.close();
                }
            }
            catch (IOException e) {
                log.warn("Unable to get datasource for uri [{}] : {}", (Object)imgSrc, (Object)e.getMessage());
                if (!log.isDebugEnabled()) continue;
                log.debug("Unable to get datasource for uri [" + imgSrc + "] ", (Throwable)e);
            }
        }
        return imagesToDatasourceMap;
    }

    private File storeImageAsTempFile(InputStream imageInputStream, File tempDir, String fileName) throws IOException {
        File outputFile = new File(tempDir, fileName);
        FileUtils.copyInputStreamToFile((InputStream)imageInputStream, (File)outputFile);
        return outputFile;
    }

    private InputStream createInputStreamFromRelativeUrl(String uri) {
        String relativeUri = uri;
        if (relativeUri.startsWith(FILE_PREFIX)) {
            return null;
        }
        Matcher matcher = RESOURCE_PATH_PATTERN.matcher(relativeUri);
        String decodedUri = relativeUri = matcher.replaceFirst("/");
        try {
            decodedUri = URLDecoder.decode(relativeUri, "UTF8");
        }
        catch (UnsupportedEncodingException e) {
            log.error("Can't decode uri " + uri, (Throwable)e);
        }
        if (this.pluginResourceLocator.matches(decodedUri)) {
            Map queryParams = UrlUtil.getQueryParameters((String)decodedUri);
            decodedUri = this.stripQueryString(decodedUri);
            DownloadableResource resource = this.pluginResourceLocator.getDownloadableResource(decodedUri, queryParams);
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                resource.streamResource((OutputStream)outputStream);
                return new ByteArrayInputStream(outputStream.toByteArray());
            }
            catch (DownloadException ex) {
                log.error("Unable to serve plugin resource to word export : uri " + uri, (Throwable)ex);
            }
        } else if (this.downloadResourceManager.matches(decodedUri)) {
            String strippedUri;
            String userName = AuthenticatedUserThreadLocal.getUsername();
            DownloadResourceReader downloadResourceReader = this.getResourceReader(decodedUri, userName, strippedUri = this.stripQueryString(decodedUri));
            if (downloadResourceReader == null) {
                strippedUri = this.stripQueryString(relativeUri);
                downloadResourceReader = this.getResourceReader(relativeUri, userName, strippedUri);
            }
            if (downloadResourceReader != null) {
                try {
                    return downloadResourceReader.getStreamForReading();
                }
                catch (Exception e) {
                    log.warn("Could not retrieve image resource {} during Confluence word export :{}", (Object)decodedUri, (Object)e.getMessage());
                    if (log.isDebugEnabled()) {
                        log.warn("Could not retrieve image resource " + decodedUri + " during Confluence word export :" + e.getMessage(), (Throwable)e);
                    }
                }
            }
        } else if (uri.startsWith(DATA_PREFIX)) {
            return this.streamDataUrl(uri);
        }
        return null;
    }

    private InputStream streamDataUrl(String dataUrl) {
        byte[] bytes;
        int dataIndex = dataUrl.indexOf(44);
        String data = dataUrl.substring(dataIndex + 1);
        if (dataUrl.substring(0, dataIndex).endsWith(";base64")) {
            bytes = Base64.decodeBase64((String)data);
        } else {
            try {
                bytes = URLCodec.decodeUrl((byte[])org.apache.commons.codec.binary.StringUtils.getBytesUsAscii((String)data));
            }
            catch (DecoderException e) {
                throw new IllegalArgumentException("Invalid data URL: \"" + dataUrl + "\".", e);
            }
        }
        return new ByteArrayInputStream(bytes);
    }

    private DownloadResourceReader getResourceReader(String uri, String userName, String strippedUri) {
        DownloadResourceReader downloadResourceReader = null;
        try {
            downloadResourceReader = this.downloadResourceManager.getResourceReader(userName, strippedUri, UrlUtil.getQueryParameters((String)uri));
        }
        catch (UnauthorizedDownloadResourceException ex) {
            log.debug("Not authorized to download resource " + uri, (Throwable)ex);
        }
        catch (DownloadResourceNotFoundException ex) {
            log.debug("No resource found for url " + uri, (Throwable)ex);
        }
        return downloadResourceReader;
    }

    private String stripQueryString(String uri) {
        int queryIndex = uri.indexOf(63);
        if (queryIndex > 0) {
            uri = uri.substring(0, queryIndex);
        }
        return uri;
    }

    private MimeMessage constructMimeMessage(String renderedTemplate, Iterable<DataSource> images) throws MessagingException {
        Session session = Session.getInstance((Properties)new Properties(), null);
        MimeMessage message = new MimeMessage(session);
        MimeMultipart mpart = new MimeMultipart("related");
        MimeBodyPart bodypart = this.createMainPart(renderedTemplate);
        mpart.addBodyPart((BodyPart)bodypart);
        for (DataSource image : images) {
            mpart.addBodyPart(ExportWordPageServer.createAttachmentPart(image));
        }
        message.setContent((Multipart)mpart);
        message.setSubject("Exported From Confluence");
        return message;
    }

    private MimeBodyPart createMainPart(String renderedTemplate) throws MessagingException {
        MimeBodyPart bodypart = new MimeBodyPart();
        bodypart.setHeader("Content-Type", "text/html; charset=" + this.settingsManager.getGlobalSettings().getDefaultEncoding());
        bodypart.setContent((Object)renderedTemplate, "text/html; charset=" + this.settingsManager.getGlobalSettings().getDefaultEncoding());
        bodypart.setHeader("Content-Transfer-Encoding", "quoted-printable");
        bodypart.addHeader("Content-Location", "file:///C:/exported.html");
        return bodypart;
    }

    private static BodyPart createAttachmentPart(DataSource ds) throws MessagingException {
        MimeBodyPart body = new MimeBodyPart();
        DataHandler dh = new DataHandler((DataSource)new DelegatingDataSource(ds));
        body.setDataHandler(dh);
        body.addHeader("Content-Location", dh.getName());
        return body;
    }

    private static boolean isMSIE8OrLess(String userAgentHeader) {
        int index = userAgentHeader.indexOf("MSIE");
        if (index != -1) {
            String version;
            return Integer.parseInt(version = userAgentHeader.substring(index += "MSIE".length() + 1, userAgentHeader.indexOf(46, index))) <= 8;
        }
        return false;
    }

    private static boolean isSafari(String userAgentHeader) {
        return userAgentHeader.contains("Safari") && !userAgentHeader.contains("Chrome");
    }

    private static final class WordDocumentLinkRenderer
    implements LinkRenderer {
        private final PageContext context;

        public WordDocumentLinkRenderer(PageContext context) {
            this.context = context;
        }

        public String renderLink(Link link, RenderContext renderContext) {
            StringBuilder buffer = new StringBuilder();
            if (link instanceof UnresolvedLink || link instanceof UnpermittedLink || link instanceof PageCreateLink) {
                buffer.append(link.getLinkBody());
                return buffer.toString();
            }
            buffer.append("<a href=\"");
            if (link.isRelativeUrl()) {
                buffer.append(this.context.getBaseUrl());
            }
            buffer.append(link.getUrl());
            buffer.append("\"");
            if (StringUtils.isNotBlank((CharSequence)link.getTitle())) {
                buffer.append(" title=\"").append(link.getTitle()).append("\"");
            }
            buffer.append(">");
            buffer.append(UrlUtil.escapeUrlFirstCharacter((String)HtmlEscaper.escapeAmpersands((String)link.getLinkBody(), (boolean)true)));
            buffer.append("</a>");
            return buffer.toString();
        }
    }

    private static final class DelegatingDataSource
    implements DataSource {
        private final DataSource ds;

        public DelegatingDataSource(DataSource ds) {
            this.ds = ds;
        }

        public String getContentType() {
            return this.ds.getContentType();
        }

        public InputStream getInputStream() throws IOException {
            return this.ds.getInputStream();
        }

        public String getName() {
            return "file:///C:/" + this.ds.getName();
        }

        public OutputStream getOutputStream() throws IOException {
            return this.ds.getOutputStream();
        }
    }
}

