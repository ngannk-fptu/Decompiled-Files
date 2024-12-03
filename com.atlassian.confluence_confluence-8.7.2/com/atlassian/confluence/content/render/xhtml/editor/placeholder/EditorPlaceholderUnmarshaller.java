/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.editor.placeholder;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.xhtml.api.Placeholder;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class EditorPlaceholderUnmarshaller
implements Unmarshaller<Placeholder> {
    private static final QName BR_ELEMENT = new QName("http://www.w3.org/1999/xhtml", "br");
    private final XmlEventReaderFactory xmlEventReaderFactory;

    public EditorPlaceholderUnmarshaller(XmlEventReaderFactory xmlEventReaderFactory) {
        this.xmlEventReaderFactory = xmlEventReaderFactory;
    }

    @Override
    public boolean handles(StartElement startElement, ConversionContext conversionContext) {
        return StaxUtils.hasClass(startElement, "text-placeholder");
    }

    @Override
    public Placeholder unmarshal(XMLEventReader reader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        try {
            StartElement placeholderElement = (StartElement)reader.nextEvent();
            Attribute placeholderTypeAttribute = placeholderElement.getAttributeByName(new QName("data-placeholder-type"));
            String placeholderType = null;
            String displayText = null;
            if (placeholderTypeAttribute != null) {
                placeholderType = placeholderTypeAttribute.getValue();
            }
            while (reader.hasNext()) {
                XMLEvent nextEvent = reader.peek();
                if (nextEvent.isEndElement()) {
                    reader.nextEvent();
                    continue;
                }
                displayText = this.getBodyText(reader);
            }
            return new Placeholder(placeholderType, displayText);
        }
        catch (XMLStreamException ex) {
            throw new XhtmlException(ex);
        }
    }

    private String getBodyText(XMLEventReader reader) throws XMLStreamException, XhtmlException {
        StringBuilder text = new StringBuilder();
        while (reader.hasNext()) {
            StartElement startElement;
            XMLEvent event = reader.nextEvent();
            if (event.isCharacters()) {
                text.append(event.asCharacters().getData());
                continue;
            }
            if (event.isEntityReference()) {
                EntityReference entity = (EntityReference)event;
                text.append(entity.getDeclaration().getReplacementText());
                continue;
            }
            if (!event.isStartElement() || !BR_ELEMENT.equals((startElement = event.asStartElement()).getName())) continue;
            text.append('\n');
        }
        return text.toString();
    }
}

