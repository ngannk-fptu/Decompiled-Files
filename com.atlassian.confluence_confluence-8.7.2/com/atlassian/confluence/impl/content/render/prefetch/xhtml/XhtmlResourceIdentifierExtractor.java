/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.content.render.prefetch.xhtml;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.core.BodyContent;
import com.atlassian.confluence.core.BodyType;
import com.atlassian.confluence.impl.content.render.prefetch.ResourceIdentifierExtractor;
import com.atlassian.confluence.impl.content.render.prefetch.ResourceIdentifiers;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

public class XhtmlResourceIdentifierExtractor
implements ResourceIdentifierExtractor {
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final Unmarshaller<ResourceIdentifier> resourceIdentifierUnmarshaller;

    public XhtmlResourceIdentifierExtractor(XmlEventReaderFactory xmlEventReaderFactory, Unmarshaller<ResourceIdentifier> resourceIdentifierUnmarshaller) {
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.resourceIdentifierUnmarshaller = resourceIdentifierUnmarshaller;
    }

    @Override
    public boolean handles(BodyType bodyType) {
        return BodyType.XHTML.equals(bodyType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ResourceIdentifiers extractResourceIdentifiers(BodyContent storageFormatContent, ConversionContext conversionContext) throws XMLStreamException, XhtmlException {
        try (XMLEventReader eventReader = this.xmlEventReaderFactory.createStorageXmlEventReader(new StringReader(storageFormatContent.getBody()), false);){
            ResourceIdentifiers resourceIdentifiers = new ResourceIdentifiers(this.extractResourceIdentifiers(eventReader, conversionContext));
            return resourceIdentifiers;
        }
    }

    private Map<Class<? extends ResourceIdentifier>, Set<ResourceIdentifier>> extractResourceIdentifiers(XMLEventReader eventReader, ConversionContext conversionContext) throws XMLStreamException, XhtmlException {
        HashMap<Class<? extends ResourceIdentifier>, Set<ResourceIdentifier>> resourceIdentifiers = new HashMap<Class<? extends ResourceIdentifier>, Set<ResourceIdentifier>>();
        while (eventReader.hasNext()) {
            XMLEvent xmlEvent = eventReader.peek();
            if (xmlEvent.isStartElement() && this.resourceIdentifierUnmarshaller.handles(xmlEvent.asStartElement(), conversionContext)) {
                ResourceIdentifier resourceIdentifier = this.extractResourceIdentifier(eventReader, conversionContext);
                resourceIdentifiers.computeIfAbsent(resourceIdentifier.getClass(), key -> new HashSet()).add(resourceIdentifier);
                continue;
            }
            eventReader.nextEvent();
        }
        return resourceIdentifiers;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ResourceIdentifier extractResourceIdentifier(XMLEventReader eventReader, ConversionContext conversionContext) throws XMLStreamException, XhtmlException {
        try (XMLEventReader resourceIdentifierfragmentReader = this.xmlEventReaderFactory.createXmlFragmentEventReader(eventReader);){
            ResourceIdentifier resourceIdentifier = this.resourceIdentifierUnmarshaller.unmarshal(resourceIdentifierfragmentReader, null, conversionContext);
            return resourceIdentifier;
        }
    }
}

