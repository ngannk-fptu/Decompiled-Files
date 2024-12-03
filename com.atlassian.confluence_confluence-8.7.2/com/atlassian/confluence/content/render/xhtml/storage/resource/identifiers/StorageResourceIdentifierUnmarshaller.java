/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.storage.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlConstants;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.migration.UrlResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentContainerResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.BlogPostResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ContentEntityResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ShortcutResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.SpaceResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.storage.resource.identifiers.StorageUserResourceIdentifierUnmarshaller;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.lang3.StringUtils;

public class StorageResourceIdentifierUnmarshaller
implements Unmarshaller<ResourceIdentifier> {
    private static final Set<String> SUPPORTED_RESOURCES = Set.of("page", "blog-post", "attachment", "url", "shortcut", "space", "content-entity");
    private final Collection<? extends Unmarshaller<? extends ResourceIdentifier>> delegateUnmarshallers = Arrays.asList(new StorageUserResourceIdentifierUnmarshaller());

    @Override
    public ResourceIdentifier unmarshal(XMLEventReader xmlEventReader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        try {
            return this.innerUnmarshal(xmlEventReader, mainFragmentTransformer, conversionContext);
        }
        catch (Exception e) {
            throw new XhtmlException(e);
        }
    }

    private ResourceIdentifier innerUnmarshal(XMLEventReader xmlEventReader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException, XMLStreamException {
        StartElement startElement;
        for (Unmarshaller<? extends ResourceIdentifier> unmarshaller : this.delegateUnmarshallers) {
            if (!unmarshaller.handles(xmlEventReader.peek().asStartElement(), conversionContext)) continue;
            return unmarshaller.unmarshal(xmlEventReader, mainFragmentTransformer, conversionContext);
        }
        try {
            startElement = xmlEventReader.nextEvent().asStartElement();
        }
        catch (XMLStreamException xMLStreamException) {
            throw new XhtmlException(xMLStreamException);
        }
        try {
            if ("page".equals(startElement.getName().getLocalPart())) {
                PageResourceIdentifier pageResourceIdentifier = new PageResourceIdentifier(this.getAttributeValue(startElement, "space-key", false), this.getAttributeValue(startElement, "content-title", false));
                return pageResourceIdentifier;
            }
            if ("blog-post".equals(startElement.getName().getLocalPart())) {
                String string = this.getAttributeValue(startElement, "posting-day", false);
                Calendar postingDay = null;
                if (StringUtils.isNotBlank((CharSequence)string)) {
                    postingDay = Calendar.getInstance();
                    try {
                        postingDay.setTime(XhtmlConstants.DATE_FORMATS.getPostingDayFormat().parse(string));
                    }
                    catch (ParseException e) {
                        throw new XhtmlException(e);
                    }
                }
                BlogPostResourceIdentifier blogPostResourceIdentifier = new BlogPostResourceIdentifier(this.getAttributeValue(startElement, "space-key", false), this.getAttributeValue(startElement, "content-title", false), postingDay);
                return blogPostResourceIdentifier;
            }
            if ("attachment".equals(startElement.getName().getLocalPart())) {
                ResourceIdentifier resourceIdentifier = this.unmarshallAttachment(xmlEventReader, mainFragmentTransformer, conversionContext, startElement);
                return resourceIdentifier;
            }
            if ("url".equals(startElement.getName().getLocalPart())) {
                UrlResourceIdentifier urlResourceIdentifier = new UrlResourceIdentifier(this.getAttributeValue(startElement, "value", true));
                return urlResourceIdentifier;
            }
            if ("shortcut".equals(startElement.getName().getLocalPart())) {
                ShortcutResourceIdentifier shortcutResourceIdentifier = new ShortcutResourceIdentifier(this.getAttributeValue(startElement, "key", true), this.getAttributeValue(startElement, "parameter", true));
                return shortcutResourceIdentifier;
            }
            if ("space".equals(startElement.getName().getLocalPart())) {
                SpaceResourceIdentifier spaceResourceIdentifier = new SpaceResourceIdentifier(this.getAttributeValue(startElement, "space-key", true));
                return spaceResourceIdentifier;
            }
            if ("content-entity".equals(startElement.getName().getLocalPart())) {
                try {
                    ContentEntityResourceIdentifier contentEntityResourceIdentifier = new ContentEntityResourceIdentifier(Long.parseLong(this.getAttributeValue(startElement, "content-id", true)));
                    return contentEntityResourceIdentifier;
                }
                catch (NumberFormatException numberFormatException) {
                    throw new XhtmlException(numberFormatException);
                }
            }
            throw new XhtmlException("Unsupported resource identifier: " + startElement.getName().getLocalPart());
        }
        finally {
            xmlEventReader.nextEvent();
        }
    }

    private ResourceIdentifier unmarshallAttachment(XMLEventReader xmlEventReader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext, StartElement riElement) throws XMLStreamException, XhtmlException {
        AttachmentContainerResourceIdentifier attachmentContainerResourceIdentifier = null;
        String filename = this.getAttributeValue(riElement, "filename", true);
        while (!(!xmlEventReader.hasNext() || xmlEventReader.peek().isEndElement() && "attachment".equals(xmlEventReader.peek().asEndElement().getName().getLocalPart()))) {
            XMLEvent nextXmlEvent = xmlEventReader.peek();
            if (nextXmlEvent.isStartElement() && this.handles(nextXmlEvent.asStartElement(), conversionContext)) {
                attachmentContainerResourceIdentifier = (AttachmentContainerResourceIdentifier)this.innerUnmarshal(xmlEventReader, mainFragmentTransformer, conversionContext);
                continue;
            }
            xmlEventReader.next();
        }
        return new AttachmentResourceIdentifier(attachmentContainerResourceIdentifier, filename);
    }

    private String getAttributeValue(StartElement startElement, String attributeName, boolean mandatory) throws XhtmlException {
        QName attributeQName = StorageResourceIdentifierUnmarshaller.getAttributeQName(attributeName);
        Attribute attribute = startElement.getAttributeByName(attributeQName);
        if (attribute == null) {
            if (mandatory) {
                throw new XhtmlException("Missing required attribute: " + attributeQName);
            }
            return null;
        }
        return attribute.getValue();
    }

    private static QName getAttributeQName(String attributeName) {
        return new QName("http://atlassian.com/resource/identifier", attributeName, "ri");
    }

    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        for (Unmarshaller<? extends ResourceIdentifier> unmarshaller : this.delegateUnmarshallers) {
            if (!unmarshaller.handles(startElementEvent, conversionContext)) continue;
            return true;
        }
        QName name = startElementEvent.getName();
        return SUPPORTED_RESOURCES.contains(name.getLocalPart()) && "http://atlassian.com/resource/identifier".equals(name.getNamespaceURI());
    }
}

