/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.util.Base64Encoder
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.xhtml.editor.embed;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.MarshallingRegistry;
import com.atlassian.confluence.content.render.xhtml.MarshallingType;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlConstants;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.editor.embed.EditorImageAttributeParser;
import com.atlassian.confluence.content.render.xhtml.migration.UrlResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.DefaultEmbeddedImage;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.CannotResolveResourceIdentifierException;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.NamedResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.xhtml.api.EmbeddedImage;
import com.atlassian.user.util.Base64Encoder;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EditorEmbeddedImageResourceUnmarshaller
implements Unmarshaller<EmbeddedImage> {
    private static final Logger logger = LoggerFactory.getLogger(EditorEmbeddedImageResourceUnmarshaller.class);
    private final Unmarshaller<ResourceIdentifier> resourceIdentifierUnmarshaller;
    private final Unmarshaller<ResourceIdentifier> storageResourceIdentifierUnmarshaller;
    private final AttachmentManager attachmentManager;
    private final PageManager pageManager;
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private static final Logger log = LoggerFactory.getLogger(EditorEmbeddedImageResourceUnmarshaller.class);

    public EditorEmbeddedImageResourceUnmarshaller(Unmarshaller<ResourceIdentifier> resourceIdentifierUnmarshaller, Unmarshaller<ResourceIdentifier> storageResourceIdentifierUnmarshaller, XmlEventReaderFactory xmlEventReaderFactory, MarshallingRegistry registry, AttachmentManager attachmentManager, PageManager pageManager) {
        this.resourceIdentifierUnmarshaller = resourceIdentifierUnmarshaller;
        this.storageResourceIdentifierUnmarshaller = storageResourceIdentifierUnmarshaller;
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.attachmentManager = attachmentManager;
        this.pageManager = pageManager;
        registry.register(this, EmbeddedImage.class, MarshallingType.EDITOR);
    }

    @Override
    public EmbeddedImage unmarshal(XMLEventReader xmlEventReader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        NamedResourceIdentifier resourceIdentifier = null;
        StartElement imageStartElement = null;
        String src = "";
        try {
            imageStartElement = xmlEventReader.peek().asStartElement();
            Attribute defaultAlias = imageStartElement.getAttributeByName(new QName("data-linked-resource-default-alias"));
            String defaultAliasStr = defaultAlias != null ? defaultAlias.getValue() : null;
            Attribute srcAttribute = imageStartElement.getAttributeByName(new QName("src"));
            if (srcAttribute != null) {
                src = srcAttribute.getValue();
            }
            Attribute classAttribute = imageStartElement.getAttributeByName(new QName("class"));
            Object[] cssClasses = new String[]{};
            if (classAttribute != null && StringUtils.isNotBlank((CharSequence)classAttribute.getValue())) {
                String cssClass = classAttribute.getValue();
                cssClasses = cssClass.split(" ");
            }
            if (ArrayUtils.contains((Object[])cssClasses, (Object)"confluence-external-resource")) {
                logger.debug("Handling Embedded Image to external resource");
                resourceIdentifier = new UrlResourceIdentifier(src);
            } else if (this.resourceIdentifierUnmarshaller.handles(imageStartElement, conversionContext)) {
                logger.debug("Handling Embedded Image with resource identifier");
                Attribute copy = imageStartElement.getAttributeByName(new QName("data-attachment-copy"));
                if (copy != null && copy.isSpecified()) {
                    Attachment attachment;
                    Attribute resourceId = imageStartElement.getAttributeByName(new QName("data-linked-resource-id"));
                    ContentEntityObject attachmentTarget = this.getAttachmentTarget(conversionContext);
                    if (this.attachmentManager.getAttachment(attachmentTarget, (attachment = this.attachmentManager.getAttachment(Long.parseLong(resourceId.getValue()))).getFileName()) == null) {
                        try {
                            this.attachmentManager.copyAttachment(attachment, attachmentTarget);
                            logger.debug("Copied attachment [{}] to target [{}]", (Object)attachment, (Object)attachmentTarget);
                        }
                        catch (IOException e) {
                            log.warn("Could not copy attachment on draft save, attachment may have been deleted");
                        }
                    }
                    resourceIdentifier = new AttachmentResourceIdentifier(attachment.getFileName());
                } else {
                    resourceIdentifier = (NamedResourceIdentifier)this.resourceIdentifierUnmarshaller.unmarshal(xmlEventReader, mainFragmentTransformer, conversionContext);
                    logger.debug("Unmarshal resource identifier [{}] for source  [{}]", (Object)(resourceIdentifier == null ? "null" : resourceIdentifier.getResourceName()), (Object)src);
                }
            } else if (this.isUnresolvedResource(imageStartElement)) {
                resourceIdentifier = this.processUnresolvedResource(conversionContext, imageStartElement);
                logger.debug("Unmarshal with unresolved resource identifier [{}] for source  [{}]", (Object)(resourceIdentifier == null ? "null" : resourceIdentifier.getResourceName()), (Object)src);
            } else if (StringUtils.isNotBlank((CharSequence)src)) {
                resourceIdentifier = new UrlResourceIdentifier(src);
                logger.debug("Unmarshal with URL resource identifier [{}] for source  [{}]", (Object)resourceIdentifier.getResourceName(), (Object)src);
            }
            if (resourceIdentifier == null) {
                throw new CannotResolveResourceIdentifierException(null, "A resource identifier could be determined for the embedded image " + defaultAliasStr);
            }
        }
        catch (RuntimeException | XMLStreamException ex) {
            log.warn("Error unmarshaling editor embedded image", (Throwable)ex);
            resourceIdentifier = new UrlResourceIdentifier(src);
        }
        DefaultEmbeddedImage embeddedImage = new DefaultEmbeddedImage(resourceIdentifier);
        EditorImageAttributeParser parser = new EditorImageAttributeParser(embeddedImage);
        if (imageStartElement != null) {
            parser.readImageAttributes(imageStartElement);
        }
        return parser.getEmbededImage();
    }

    private ContentEntityObject getAttachmentTarget(ConversionContext conversionContext) {
        Long pageId;
        ContentEntityObject content = conversionContext.getEntity();
        if (content instanceof Draft && (pageId = ((Draft)content).getPageIdAsLong()) != 0L) {
            content = this.pageManager.getPage(pageId);
        }
        return (ContentEntityObject)(content != null ? content.getLatestVersion() : null);
    }

    private boolean isUnresolvedResource(StartElement imageStartElement) {
        Attribute unresolvedResourceIdAttribute = imageStartElement.getAttributeByName(new QName("data-resource-id"));
        return unresolvedResourceIdAttribute != null && StringUtils.isNotBlank((CharSequence)unresolvedResourceIdAttribute.getValue());
    }

    private NamedResourceIdentifier processUnresolvedResource(ConversionContext conversionContext, StartElement imageStartElement) throws XhtmlException {
        StringReader stringReader;
        Attribute unresolvedResourceIdAttribute = imageStartElement.getAttributeByName(new QName("data-resource-id"));
        Attribute titleAttribute = imageStartElement.getAttributeByName(new QName("title"));
        String title = titleAttribute != null ? titleAttribute.getValue() : null;
        try {
            byte[] decoded = Base64Encoder.decode((byte[])unresolvedResourceIdAttribute.getValue().getBytes("UTF-8"));
            stringReader = new StringReader(new String(decoded, "UTF-8"));
        }
        catch (UnsupportedEncodingException ex) {
            throw new XhtmlException("The UTF-8 charset is required on the server to decode the unresolved resource named " + title, ex);
        }
        XMLEventReader unresolvedResourceIdEventReader = null;
        NamedResourceIdentifier resourceIdentifier = null;
        try {
            unresolvedResourceIdEventReader = this.xmlEventReaderFactory.createStorageXmlEventReader(stringReader);
            resourceIdentifier = (NamedResourceIdentifier)this.storageResourceIdentifierUnmarshaller.unmarshal(unresolvedResourceIdEventReader, null, conversionContext);
        }
        catch (XMLStreamException e) {
            try {
                throw new XhtmlException("Unable to read the definition of the unresolved resource " + title, e);
            }
            catch (Throwable throwable) {
                StaxUtils.closeQuietly(unresolvedResourceIdEventReader);
                stringReader.close();
                throw throwable;
            }
        }
        StaxUtils.closeQuietly(unresolvedResourceIdEventReader);
        stringReader.close();
        return resourceIdentifier;
    }

    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        Attribute classAttribute = startElementEvent.getAttributeByName(XhtmlConstants.Attribute.CLASS);
        return classAttribute != null && ArrayUtils.contains((Object[])classAttribute.getValue().split(" "), (Object)"confluence-embedded-image");
    }
}

