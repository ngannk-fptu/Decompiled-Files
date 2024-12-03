/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentBody
 *  com.atlassian.confluence.api.model.content.ContentRepresentation
 *  com.atlassian.plugin.web.renderer.RendererException
 *  com.atlassian.renderer.v2.RenderUtils
 *  com.google.common.base.Preconditions
 *  io.atlassian.util.concurrent.Timeout
 *  javax.activation.DataSource
 *  org.apache.commons.codec.digest.DigestUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.xhtml.view.excerpt;

import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentBody;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.content.render.xhtml.ContentExcerptUtils;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.HtmlToXmlConverter;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.migration.UrlResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifierResolver;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.CannotResolveResourceIdentifierException;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.storage.MacroDefinitionTransformer;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.content.render.xhtml.view.excerpt.ExcerptConfig;
import com.atlassian.confluence.content.render.xhtml.view.excerpt.ExcerptState;
import com.atlassian.confluence.content.render.xhtml.view.excerpt.Excerpter;
import com.atlassian.confluence.content.render.xhtml.view.excerpt.XMLNodeSkipper;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DataSourceFactory;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.thumbnail.CannotGenerateThumbnailException;
import com.atlassian.confluence.pages.thumbnail.ThumbnailManager;
import com.atlassian.confluence.renderer.ContentIncludeStack;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.ExcerptHelper;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.xhtml.api.EmbeddedImage;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.plugin.web.renderer.RendererException;
import com.atlassian.renderer.v2.RenderUtils;
import com.google.common.base.Preconditions;
import io.atlassian.util.concurrent.Timeout;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.activation.DataSource;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultExcerpter
implements Excerpter {
    private static final Logger log = LoggerFactory.getLogger(DefaultExcerpter.class);
    public static final Set<String> DEFAULT_EXCLUDE_SET = Collections.emptySet();
    public static final int DEFAULT_MAX_BLOCKS_FOR_EXCERPT = 3;
    public static final String EXCERPT_MACRO_NAME = "excerpt";
    private final XMLOutputFactory xmlFragmentOutputFactory;
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final HtmlToXmlConverter htmlToXmlConverter;
    private final SettingsManager settingsManager;
    private final Renderer viewRenderer;
    private final Unmarshaller<EmbeddedImage> embeddedImageUnmarshaller;
    private final FragmentTransformer fragmentTransformer;
    private final AttachmentResourceIdentifierResolver attachmentResolver;
    private final DataSourceFactory datasourceFactory;
    private final ThumbnailManager thumbnailManager;
    private final MacroDefinitionTransformer macroDefinitionTransformer;
    private final ExcerptHelper excerptHelper;
    private final I18NBeanFactory i18NBeanFactory;
    private final LocaleManager localeManager;

    public DefaultExcerpter(XMLOutputFactory xmlFragmentOutputFactory, XmlEventReaderFactory xmlEventReaderFactory, HtmlToXmlConverter htmlToXmlConverter, SettingsManager settingsManager, Renderer viewRenderer, Unmarshaller<EmbeddedImage> embeddedImageUnmarshaller, FragmentTransformer fragmentTransformer, AttachmentResourceIdentifierResolver attachmentResolver, DataSourceFactory datasourceFactory, ThumbnailManager thumbnailManager, MacroDefinitionTransformer macroDefinitionTransformer, ExcerptHelper excerptHelper, I18NBeanFactory i18NBeanFactory, LocaleManager localeManager) {
        this.xmlFragmentOutputFactory = xmlFragmentOutputFactory;
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.htmlToXmlConverter = htmlToXmlConverter;
        this.settingsManager = settingsManager;
        this.viewRenderer = viewRenderer;
        this.embeddedImageUnmarshaller = embeddedImageUnmarshaller;
        this.fragmentTransformer = fragmentTransformer;
        this.attachmentResolver = attachmentResolver;
        this.datasourceFactory = datasourceFactory;
        this.thumbnailManager = thumbnailManager;
        this.macroDefinitionTransformer = macroDefinitionTransformer;
        this.excerptHelper = excerptHelper;
        this.i18NBeanFactory = i18NBeanFactory;
        this.localeManager = localeManager;
    }

    @Override
    public String createExcerpt(Content content) throws Exception {
        Preconditions.checkArgument((content.getBody().containsKey(ContentRepresentation.EXPORT_VIEW) || content.getBody().containsKey(ContentRepresentation.ANONYMOUS_EXPORT_VIEW) ? 1 : 0) != 0, (Object)"Content should contain an expanded export_view, or an anonymous_export_view.");
        ContentBody contentBody = content.getBody().containsKey(ContentRepresentation.ANONYMOUS_EXPORT_VIEW) ? (ContentBody)content.getBody().get(ContentRepresentation.ANONYMOUS_EXPORT_VIEW) : (ContentBody)content.getBody().get(ContentRepresentation.EXPORT_VIEW);
        return this.createExcerpt(contentBody.getValue(), new ExcerptConfig.Builder().excludedLastHtmlElement(DEFAULT_EXCLUDE_SET).maxBlocks(3).build());
    }

    @Override
    public String createExcerpt(ContentEntityObject contentEntity, String outputType) throws Exception {
        return this.createExcerpt(contentEntity, outputType, DEFAULT_EXCLUDE_SET, 3);
    }

    @Deprecated
    public String createExcerpt(ContentEntityObject contentEntity, String outputType, Set<String> excludeSet, int maxBlocksForExcerpt) throws Exception {
        return this.createExcerpt(contentEntity, outputType, new ExcerptConfig.Builder().excludeHtmlElements(excludeSet).maxBlocks(maxBlocksForExcerpt).build());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String createExcerpt(ContentEntityObject contentEntity, String outputType, ExcerptConfig config) throws XMLStreamException {
        if (ContentIncludeStack.contains(contentEntity)) {
            return this.renderRecursiveExcerptError(contentEntity);
        }
        try {
            ContentIncludeStack.push(contentEntity);
            String content = this.renderExcerptContent(contentEntity, outputType, config);
            String string = (String)StringUtils.defaultIfBlank((CharSequence)content, (CharSequence)"");
            return string;
        }
        finally {
            ContentIncludeStack.pop();
        }
    }

    @Deprecated
    public String renderContent(ContentEntityObject contentEntity, String outputType) {
        return this.viewRenderer.render(contentEntity, (ConversionContext)new DefaultConversionContext(this.getPageContext(contentEntity, outputType)));
    }

    private String renderExcerptContent(ContentEntityObject contentEntity, String outputType, ExcerptConfig config) throws XMLStreamException {
        MacroDefinition macroDefinition;
        DefaultConversionContext context = new DefaultConversionContext(this.getPageContext(contentEntity, outputType));
        if (!config.ignoreUserDefinedExcerpt() && (macroDefinition = this.excerptHelper.getMacroDefinition(contentEntity, EXCERPT_MACRO_NAME)) != null) {
            return this.renderExcerptFromMacro(contentEntity, context, config, StringUtils.defaultString((String)macroDefinition.getBodyText()));
        }
        return this.renderExcerptFromContent(contentEntity, context, config);
    }

    private String renderExcerptFromContent(ContentEntityObject contentEntity, ConversionContext context, ExcerptConfig config) throws XMLStreamException {
        String content = this.getContentWithUpdatedMacroDefinitions(contentEntity, context, config, contentEntity.getBodyAsString());
        content = this.viewRenderer.render(content, context);
        return this.createExcerpt(content, config);
    }

    private String renderExcerptFromMacro(ContentEntityObject contentEntity, ConversionContext context, ExcerptConfig config, String excerptMacroContent) {
        context.setProperty("containedRender", true);
        String content = this.getContentWithUpdatedMacroDefinitions(contentEntity, context, config, excerptMacroContent);
        return this.viewRenderer.render(content, context);
    }

    private String renderRecursiveExcerptError(ContentEntityObject contentEntity) {
        I18NBean i18nBean = this.getI18nBean();
        String message = i18nBean.getText("excerptinclude.error.recursive.message");
        String contents = i18nBean.getText("excerptinclude.error.recursive.contents", new String[]{HtmlUtil.htmlEncode(contentEntity.getTitle())});
        return RenderUtils.blockError((String)message, (String)contents);
    }

    private String getContentWithUpdatedMacroDefinitions(ContentEntityObject contentEntity, ConversionContext context, ExcerptConfig config, String content) {
        if (config.getMacroDefinitionUpdater() == null) {
            return content;
        }
        try {
            return this.macroDefinitionTransformer.updateMacroDefinitions(content, context, config.getMacroDefinitionUpdater());
        }
        catch (XhtmlException ex) {
            throw new RendererException("Could not replace macro definitions for contentEntity : " + contentEntity, (Throwable)ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String createExcerpt(String contentHtml, ExcerptConfig config) throws XMLStreamException {
        StringWriter result = new StringWriter();
        XMLEventWriter eventWriter = null;
        XMLEventReader reader = null;
        try {
            eventWriter = this.xmlFragmentOutputFactory.createXMLEventWriter(result);
            String xml = this.htmlToXmlConverter.convert(contentHtml);
            reader = this.xmlEventReaderFactory.createXmlFragmentEventReader(new StringReader(xml));
            this.doCreateExcerpt(reader, eventWriter, config, xml);
        }
        catch (Throwable throwable) {
            StaxUtils.closeQuietly(eventWriter);
            StaxUtils.closeQuietly(reader);
            throw throwable;
        }
        StaxUtils.closeQuietly(eventWriter);
        StaxUtils.closeQuietly(reader);
        return result.toString();
    }

    private ExcerptState doCreateExcerpt(XMLEventReader reader, XMLEventWriter eventWriter, ExcerptConfig config, String debugXml) throws XMLStreamException {
        ExcerptState excerptState = new ExcerptState();
        while (reader.hasNext() && config.canContinue(excerptState)) {
            XMLEvent xmlEvent = reader.peek();
            if (this.isStartElementInSet(xmlEvent, config.getContainerElementSet()) || this.isEndElementInSet(xmlEvent, config.getContainerElementSet())) {
                this.handleContainerElements(reader, eventWriter, excerptState, config);
                continue;
            }
            if (this.isStartElementInSet(xmlEvent, config.getBlockElementSet())) {
                this.handleBlockStartElement(reader, eventWriter, config, excerptState);
                continue;
            }
            if (xmlEvent.isCharacters()) {
                this.handleCharacterElement(reader, eventWriter, config, excerptState);
                continue;
            }
            XMLNodeSkipper.skipCurrentNodeTree(reader);
        }
        if (excerptState.hasContent()) {
            while (reader.hasNext()) {
                XMLEvent event = reader.peek();
                if (this.isEndElementInSet(event, config.getContainerElementSet())) {
                    excerptState.writeEvent(eventWriter, config, reader.nextEvent());
                    continue;
                }
                XMLNodeSkipper.skipCurrentNodeTree(reader);
            }
        }
        return excerptState;
    }

    private void handleBlockStartElement(XMLEventReader reader, XMLEventWriter eventWriter, ExcerptConfig config, ExcerptState excerptState) throws XMLStreamException {
        List<XMLEvent> events = this.filterEmptyAndExcludedEvents(this.xmlEventReaderFactory.createXmlFragmentEventReader(reader), config.getExcludedHtmlElements());
        if (events.size() > 0) {
            excerptState.writeEventList(eventWriter, config, events);
        }
    }

    private void handleCharacterElement(XMLEventReader reader, XMLEventWriter eventWriter, ExcerptConfig config, ExcerptState excerptState) throws XMLStreamException {
        XMLEvent xmlEvent = reader.nextEvent();
        if (!StringUtils.isBlank((CharSequence)xmlEvent.asCharacters().getData())) {
            excerptState.writeEvent(eventWriter, config, xmlEvent);
            while (reader.hasNext() && config.canContinue(excerptState) && !this.isStartElementInSet(xmlEvent = reader.peek(), config.getBlockElementSet()) && !this.isStartElementInSet(xmlEvent, config.getContainerElementSet()) && !this.isStartElementInSet(xmlEvent, config.getExcludedHtmlElements()) && !this.isEndElementInSet(xmlEvent, config.getContainerElementSet())) {
                excerptState.writeEvent(eventWriter, config, reader.nextEvent());
            }
        }
    }

    private void handleContainerElements(XMLEventReader reader, XMLEventWriter eventWriter, ExcerptState excerptState, ExcerptConfig config) throws XMLStreamException {
        XMLEvent nextEvent = reader.nextEvent();
        if (nextEvent.isStartElement()) {
            excerptState.pushContainerElement(nextEvent);
        } else if (excerptState.isContainerStackEmpty()) {
            excerptState.writeEvent(eventWriter, config, nextEvent);
        } else {
            excerptState.popContainerElement();
        }
    }

    private boolean isEndElementInSet(XMLEvent event, Set<String> set) {
        return event.isEndElement() && set.contains(event.asEndElement().getName().getLocalPart().toLowerCase());
    }

    private boolean isStartElementInSet(XMLEvent event, Set<String> set) {
        return event.isStartElement() && set.contains(event.asStartElement().getName().getLocalPart().toLowerCase());
    }

    @Override
    public List<DataSource> extractImageSrc(ContentEntityObject ceo, int maxImages) throws XMLStreamException, XhtmlException {
        return this.extractImageSrc(ceo, maxImages, true);
    }

    @Override
    public List<DataSource> extractImageSrc(ContentEntityObject ceo, int maxImages, boolean alwaysUseThumbnails) throws XMLStreamException, XhtmlException {
        ArrayList<DataSource> result = new ArrayList<DataSource>();
        XMLEventReader reader = this.xmlEventReaderFactory.createStorageXmlEventReader(new StringReader(ceo.getBodyAsString()));
        DefaultConversionContext context = new DefaultConversionContext(ceo.toPageContext());
        while (reader.hasNext() && result.size() < maxImages) {
            XMLEvent xmlEvent = reader.peek();
            if (xmlEvent.isStartElement() && this.embeddedImageUnmarshaller.handles(xmlEvent.asStartElement(), context)) {
                EmbeddedImage image = this.embeddedImageUnmarshaller.unmarshal(this.xmlEventReaderFactory.createXmlFragmentEventReader(reader), this.fragmentTransformer, context);
                ResourceIdentifier ri = image.getResourceIdentifier();
                DataSource ds = null;
                if (ri instanceof AttachmentResourceIdentifier) {
                    try {
                        ds = this.datasourceFactory.getDatasource(this.attachmentResolver.resolve((AttachmentResourceIdentifier)ri, (ConversionContext)context), alwaysUseThumbnails || image.isThumbnail());
                    }
                    catch (IOException io) {
                        log.warn("Could not get datasource for attachment", (Throwable)io);
                    }
                } else if (ri instanceof UrlResourceIdentifier) {
                    UrlResourceIdentifier urlRi = (UrlResourceIdentifier)ri;
                    try {
                        ds = this.datasourceFactory.getURLResource(new URL(urlRi.getUrl()), "attach_" + DigestUtils.md5Hex((String)urlRi.getUrl()));
                    }
                    catch (MalformedURLException ex) {
                        log.warn("Could not create URL Datasource for image : " + urlRi.getUrl(), (Throwable)ex);
                    }
                } else {
                    log.warn("Unknown ri type : " + ri.getClass());
                }
                if (ds == null) continue;
                result.add(ds);
                continue;
            }
            reader.nextEvent();
        }
        return result;
    }

    @Override
    public List<String> extractImageSrc(String contentHtml, int maxImages) throws XMLStreamException {
        ArrayList<String> result = new ArrayList<String>();
        String xml = this.htmlToXmlConverter.convert(contentHtml);
        XMLEventReader reader = this.xmlEventReaderFactory.createXmlFragmentEventReader(new StringReader(xml));
        while (reader.hasNext() && result.size() < maxImages) {
            Attribute att;
            StartElement element;
            XMLEvent xmlEvent = reader.nextEvent();
            if (!xmlEvent.isStartElement() || !xmlEvent.asStartElement().getName().getLocalPart().equals("img") || !(element = xmlEvent.asStartElement()).getName().getLocalPart().equals("img") || (att = element.getAttributeByName(new QName("src"))) == null) continue;
            result.add(att.getValue());
        }
        return result;
    }

    protected List<XMLEvent> filterEmptyAndExcludedEvents(XMLEventReader reader, Set<String> excludeSet) {
        ArrayList<XMLEvent> eventsToWrite = new ArrayList<XMLEvent>();
        try {
            boolean hasCharacters = false;
            while (reader.hasNext()) {
                XMLEvent event = reader.peek();
                if (event.isStartElement()) {
                    boolean isIncluded;
                    boolean bl = isIncluded = !this.isStartElementInSet(event, excludeSet);
                    if (isIncluded) {
                        eventsToWrite.add(reader.nextEvent());
                        continue;
                    }
                    XMLNodeSkipper.skipCurrentNodeTree(reader);
                    continue;
                }
                XMLEvent nextEvent = reader.nextEvent();
                hasCharacters |= nextEvent.isCharacters() && !StringUtils.isBlank((CharSequence)nextEvent.asCharacters().getData());
                eventsToWrite.add(nextEvent);
            }
            if (hasCharacters) {
                return eventsToWrite;
            }
            return Collections.emptyList();
        }
        catch (XMLStreamException e) {
            throw new RuntimeException("Error converting XML event reader to string", e);
        }
    }

    private PageContext getPageContext(ContentEntityObject contentEntity, String outputType) {
        Timeout renderTimeout = Timeout.getMillisTimeout((long)this.settingsManager.getGlobalSettings().getPageTimeout(), (TimeUnit)TimeUnit.SECONDS);
        PageContext pageContext = contentEntity instanceof Comment ? PageContext.newContextWithTimeout(((Comment)contentEntity).getContainer(), renderTimeout) : PageContext.newContextWithTimeout(contentEntity, renderTimeout);
        pageContext.setOutputType(outputType);
        return pageContext;
    }

    @Override
    public List<URI> extractImageThumbnailUris(ContentEntityObject contentEntity, int maxUris) throws XhtmlException {
        LinkedList<URI> result = new LinkedList<URI>();
        try {
            XMLEventReader reader = this.xmlEventReaderFactory.createStorageXmlEventReader(new StringReader(contentEntity.getBodyAsString()));
            DefaultConversionContext context = new DefaultConversionContext(contentEntity.toPageContext());
            while (reader.hasNext() && result.size() < maxUris) {
                XMLEvent xmlEvent = reader.peek();
                if (xmlEvent.isStartElement() && this.embeddedImageUnmarshaller.handles(xmlEvent.asStartElement(), context)) {
                    URI thumbnailUri;
                    EmbeddedImage image = this.embeddedImageUnmarshaller.unmarshal(this.xmlEventReaderFactory.createXmlFragmentEventReader(reader), this.fragmentTransformer, context);
                    ResourceIdentifier ri = image.getResourceIdentifier();
                    if (!(ri instanceof AttachmentResourceIdentifier)) continue;
                    Attachment attachment = null;
                    try {
                        attachment = this.attachmentResolver.resolve((AttachmentResourceIdentifier)ri, (ConversionContext)context);
                    }
                    catch (CannotResolveResourceIdentifierException cannotResolveResourceIdentifierException) {
                        // empty catch block
                    }
                    if ((thumbnailUri = this.getThumbnailUri(attachment)) == null) continue;
                    result.add(thumbnailUri);
                    continue;
                }
                reader.nextEvent();
            }
        }
        catch (XMLStreamException e) {
            throw new XhtmlException(e);
        }
        return result;
    }

    private URI getThumbnailUri(Attachment attachment) {
        if (attachment == null) {
            return null;
        }
        URI result = null;
        if (this.thumbnailManager.isThumbnailable(attachment)) {
            try {
                return new URI(this.thumbnailManager.getThumbnailInfo(attachment).getThumbnailUrlPath());
            }
            catch (CannotGenerateThumbnailException e) {
                log.debug("Error generating thumbnail", (Throwable)e);
            }
            catch (URISyntaxException e) {
                log.debug("URI syntax error", (Throwable)e);
            }
        }
        return result;
    }

    private I18NBean getI18nBean() {
        return this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale(AuthenticatedUserThreadLocal.get()));
    }

    @Override
    public String getExcerpt(ContentEntityObject content) {
        return this.excerptHelper.getExcerpt(content);
    }

    @Override
    public String getExcerptSummary(ContentEntityObject content) {
        return this.excerptHelper.getExcerptSummary(content);
    }

    @Override
    public String getText(String content) {
        return ContentExcerptUtils.extractTextFromXhtmlContent(content);
    }
}

