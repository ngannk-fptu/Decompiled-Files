/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.XhtmlConstants;
import com.atlassian.confluence.content.render.xhtml.XmlEntityExpander;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import java.io.StringReader;
import java.util.Collections;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.lang3.StringUtils;

public class DefaultXmlEntityExpander
implements XmlEntityExpander {
    private final XmlEventReaderFactory xmlEventReaderFactory;

    public DefaultXmlEntityExpander(XmlEventReaderFactory xmlEventReaderFactory) {
        this.xmlEventReaderFactory = xmlEventReaderFactory;
    }

    @Override
    public String expandEntities(String text) {
        if (StringUtils.isBlank((CharSequence)text)) {
            return text;
        }
        StringBuilder result = new StringBuilder();
        try {
            XMLEventReader reader = this.xmlEventReaderFactory.createXMLEventReader(new StringReader(text), Collections.singletonList(XhtmlConstants.XHTML_NAMESPACE), true);
            while (reader.hasNext()) {
                XMLEvent xmlEvent = reader.nextEvent();
                if (!xmlEvent.isCharacters() || xmlEvent.asCharacters().isCData()) {
                    throw new IllegalArgumentException("XML detected. This method should only be used on XML text.");
                }
                result.append(xmlEvent.asCharacters().getData());
            }
        }
        catch (XMLStreamException e) {
            throw new IllegalArgumentException("XML detected. This method should only be used on XML text.", e);
        }
        return result.toString();
    }
}

