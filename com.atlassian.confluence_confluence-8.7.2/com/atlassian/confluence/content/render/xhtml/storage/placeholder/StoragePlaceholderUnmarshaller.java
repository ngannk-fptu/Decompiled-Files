/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.storage.placeholder;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.storage.placeholder.StoragePlaceholderConstants;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.xhtml.api.Placeholder;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class StoragePlaceholderUnmarshaller
implements Unmarshaller<Placeholder> {
    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        return startElementEvent.getName().equals(StoragePlaceholderConstants.PLACEHOLDER_ELEMENT);
    }

    @Override
    public Placeholder unmarshal(XMLEventReader reader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        try {
            StartElement placeholderElement = (StartElement)reader.nextEvent();
            Attribute placeholderTypeAttribute = placeholderElement.getAttributeByName(StoragePlaceholderConstants.PLACEHOLDER_TYPE_ATTR);
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
            XMLEvent event = reader.nextEvent();
            if (!event.isCharacters()) continue;
            text.append(event.asCharacters().getData());
        }
        return text.toString();
    }
}

