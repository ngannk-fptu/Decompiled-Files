/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.xhtml.storage.link;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.model.links.DefaultLink;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierContextUtility;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.xhtml.api.EmbeddedImageLinkBody;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.confluence.xhtml.api.LinkBody;
import com.atlassian.confluence.xhtml.api.PlainTextLinkBody;
import com.atlassian.confluence.xhtml.api.RichTextLinkBody;
import java.util.List;
import java.util.Optional;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageLinkUnmarshaller
implements Unmarshaller<Link> {
    private static Logger log = LoggerFactory.getLogger(StorageLinkUnmarshaller.class);
    private final Unmarshaller<ResourceIdentifier> resourceIdentifierUnmarshaller;
    private final List<Unmarshaller<LinkBody>> linkBodyUnmarshallers;
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final ResourceIdentifierContextUtility resourceIdentifierContextUtility;

    public StorageLinkUnmarshaller(Unmarshaller<ResourceIdentifier> resourceIdentifierUnmarshaller, List<Unmarshaller<LinkBody>> linkBodyUnmarshallers, XmlEventReaderFactory xmlEventReaderFactory, ResourceIdentifierContextUtility resourceIdentifierContextUtility) {
        this.resourceIdentifierUnmarshaller = resourceIdentifierUnmarshaller;
        this.linkBodyUnmarshallers = linkBodyUnmarshallers;
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.resourceIdentifierContextUtility = resourceIdentifierContextUtility;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Link unmarshal(XMLEventReader linkReader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        try {
            StartElement linkElement = linkReader.peek().asStartElement();
            String tooltip = this.getAttributeValue(linkElement, "tooltip");
            String anchor = this.getAttributeValue(linkElement, "anchor");
            Optional<String> target = Optional.ofNullable(this.getAttributeValue(linkElement, "target"));
            ResourceIdentifier resourceIdentifier = null;
            LinkBody body = null;
            XMLEventReader reader = this.xmlEventReaderFactory.createXmlFragmentBodyEventReader(linkReader);
            while (reader.hasNext()) {
                XMLEvent nextXmlEvent = reader.peek();
                if (nextXmlEvent.isStartElement()) {
                    StartElement startElement = nextXmlEvent.asStartElement();
                    if (this.resourceIdentifierUnmarshaller.handles(startElement, conversionContext)) {
                        XMLEventReader resourceIdentifierFragmentReader = this.xmlEventReaderFactory.createXmlFragmentEventReader(reader);
                        try {
                            ResourceIdentifier candidateRi = this.resourceIdentifierUnmarshaller.unmarshal(resourceIdentifierFragmentReader, mainFragmentTransformer, conversionContext);
                            if (resourceIdentifier == null) {
                                resourceIdentifier = candidateRi;
                                continue;
                            }
                            log.debug("Multiple resource identifiers encountered within the link. Ignoring {}", (Object)candidateRi);
                            continue;
                        }
                        finally {
                            StaxUtils.closeQuietly(resourceIdentifierFragmentReader);
                            continue;
                        }
                    }
                    Unmarshaller<LinkBody> bodyUnmarshaller = this.getLinkBodyUnmarshaller(startElement, conversionContext);
                    if (bodyUnmarshaller != null) {
                        XMLEventReader linkBodyFragmentReader = this.xmlEventReaderFactory.createXmlFragmentBodyEventReader(reader);
                        try {
                            if (!linkBodyFragmentReader.hasNext()) continue;
                            body = bodyUnmarshaller.unmarshal(linkBodyFragmentReader, mainFragmentTransformer, conversionContext);
                            continue;
                        }
                        finally {
                            StaxUtils.closeQuietly(linkBodyFragmentReader);
                            continue;
                        }
                    }
                    reader.nextEvent();
                    continue;
                }
                reader.nextEvent();
            }
            StaxUtils.closeQuietly(reader);
            if (body == null || body instanceof EmbeddedImageLinkBody || body instanceof PlainTextLinkBody || body instanceof RichTextLinkBody) {
                if (conversionContext != null) {
                    resourceIdentifier = this.resourceIdentifierContextUtility.convertToRelative(resourceIdentifier, conversionContext.getEntity());
                }
                return DefaultLink.builder().withDestinationResourceIdentifier(resourceIdentifier).withBody(body).withTooltip(tooltip).withAnchor(anchor).withTarget(target).build();
            }
            throw new UnsupportedOperationException("Unsupported body type: " + body);
        }
        catch (IllegalStateException | XMLStreamException e) {
            throw new XhtmlException(e);
        }
    }

    private String getAttributeValue(StartElement startElement, String attributeName) {
        Attribute attribute = startElement.getAttributeByName(new QName("http://atlassian.com/content", attributeName, "ac"));
        return attribute != null ? attribute.getValue() : null;
    }

    private Unmarshaller<LinkBody> getLinkBodyUnmarshaller(StartElement element, ConversionContext context) {
        for (Unmarshaller<LinkBody> bodyUnmarshaller : this.linkBodyUnmarshallers) {
            if (!bodyUnmarshaller.handles(element, context)) continue;
            return bodyUnmarshaller;
        }
        return null;
    }

    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        QName qName = startElementEvent.getName();
        return "link".equals(qName.getLocalPart()) && "http://atlassian.com/content".equals(qName.getNamespaceURI());
    }
}

