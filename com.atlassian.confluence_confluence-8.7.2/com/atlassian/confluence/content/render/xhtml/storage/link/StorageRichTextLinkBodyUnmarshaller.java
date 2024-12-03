/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.storage.link;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.storage.link.StorageLinkConstants;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.xhtml.api.EmbeddedImage;
import com.atlassian.confluence.xhtml.api.EmbeddedImageLinkBody;
import com.atlassian.confluence.xhtml.api.LinkBody;
import com.atlassian.confluence.xhtml.api.RichTextLinkBody;
import java.io.StringWriter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class StorageRichTextLinkBodyUnmarshaller
implements Unmarshaller<LinkBody> {
    private final Unmarshaller<EmbeddedImage> embeddedImageUnmarshaller;
    private final XMLOutputFactory xmlFragmentOutputFactory;
    private final XmlEventReaderFactory xmlEventReaderFactory;

    public StorageRichTextLinkBodyUnmarshaller(Unmarshaller<EmbeddedImage> embeddedImageUnmarshaller, XMLOutputFactory xmlFragmentOutputFactory, XmlEventReaderFactory xmlEventReaderFactory) {
        this.embeddedImageUnmarshaller = embeddedImageUnmarshaller;
        this.xmlFragmentOutputFactory = xmlFragmentOutputFactory;
        this.xmlEventReaderFactory = xmlEventReaderFactory;
    }

    @Override
    public LinkBody unmarshal(XMLEventReader reader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        try {
            StringWriter linkBody = new StringWriter();
            XMLEventWriter linkBodyFragmentWriter = this.xmlFragmentOutputFactory.createXMLEventWriter(linkBody);
            while (reader.hasNext()) {
                XMLEvent event = reader.peek();
                if (event.isStartElement() && this.embeddedImageUnmarshaller.handles(event.asStartElement(), conversionContext)) {
                    EmbeddedImage image = this.embeddedImageUnmarshaller.unmarshal(reader, mainFragmentTransformer, conversionContext);
                    EmbeddedImageLinkBody embeddedImageLinkBody = new EmbeddedImageLinkBody(image);
                    return embeddedImageLinkBody;
                }
                if (event.isStartElement() && "http://atlassian.com/content".equals(event.asStartElement().getName().getNamespaceURI())) {
                    XMLEventReader acFragmentEventReader = this.xmlEventReaderFactory.createXmlFragmentEventReader(reader);
                    while (acFragmentEventReader.hasNext()) {
                        acFragmentEventReader.nextEvent();
                    }
                    continue;
                }
                linkBodyFragmentWriter.add(reader.nextEvent());
            }
            StaxUtils.closeQuietly(linkBodyFragmentWriter);
            RichTextLinkBody richTextLinkBody = new RichTextLinkBody(linkBody.toString());
            return richTextLinkBody;
        }
        catch (XMLStreamException e) {
            throw new XhtmlException(e);
        }
        finally {
            StaxUtils.closeQuietly(reader);
        }
    }

    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        return startElementEvent.getName().equals(StorageLinkConstants.LINK_BODY_ELEMENT_QNAME);
    }
}

