/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.storage.embed;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.model.resource.DefaultEmbeddedImage;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.NamedResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.storage.embed.StorageImageAttributeParser;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.xhtml.api.EmbeddedImage;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class StorageEmbeddedImageUnmarshaller
implements Unmarshaller<EmbeddedImage> {
    public static final QName IMAGE_ELEMENT = new QName("http://atlassian.com/content", "image", "ac");
    private final Unmarshaller<ResourceIdentifier> resourceIdentifierUnmarshaller;
    private final XmlEventReaderFactory xmlEventReaderFactory;

    public StorageEmbeddedImageUnmarshaller(Unmarshaller<ResourceIdentifier> resourceIdentifierUnmarshaller, XmlEventReaderFactory xmlEventReaderFactory) {
        this.resourceIdentifierUnmarshaller = resourceIdentifierUnmarshaller;
        this.xmlEventReaderFactory = xmlEventReaderFactory;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public EmbeddedImage unmarshal(XMLEventReader xmlEventReader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        StartElement imageElement;
        NamedResourceIdentifier resourceIdentifier = null;
        try {
            imageElement = xmlEventReader.nextEvent().asStartElement();
            while (xmlEventReader.hasNext()) {
                XMLEvent xmlEvent = xmlEventReader.peek();
                if (xmlEvent.isStartElement() && this.resourceIdentifierUnmarshaller.handles(xmlEvent.asStartElement(), conversionContext)) {
                    XMLEventReader resourceIdentifierfragmentReader = this.xmlEventReaderFactory.createXmlFragmentEventReader(xmlEventReader);
                    try {
                        resourceIdentifier = (NamedResourceIdentifier)this.resourceIdentifierUnmarshaller.unmarshal(resourceIdentifierfragmentReader, mainFragmentTransformer, conversionContext);
                        continue;
                    }
                    finally {
                        StaxUtils.closeQuietly(resourceIdentifierfragmentReader);
                        continue;
                    }
                }
                xmlEventReader.nextEvent();
            }
        }
        catch (XMLStreamException e) {
            throw new XhtmlException(e);
        }
        if (resourceIdentifier == null) {
            throw new XhtmlException("No resource identifier could be unmarshalled for embedded resource.");
        }
        DefaultEmbeddedImage embeddedImage = new DefaultEmbeddedImage(resourceIdentifier);
        StorageImageAttributeParser parser = new StorageImageAttributeParser(embeddedImage);
        parser.readImageAttributes(imageElement);
        return parser.getEmbededImage();
    }

    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        return IMAGE_ELEMENT.equals(startElementEvent.getName());
    }
}

