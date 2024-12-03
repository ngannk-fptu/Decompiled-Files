/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.storage.placeholder;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.storage.placeholder.StoragePlaceholderConstants;
import com.atlassian.confluence.xhtml.api.Placeholder;
import java.io.IOException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.lang3.StringUtils;

public class StoragePlaceholderMarshaller
implements Marshaller<Placeholder> {
    private final XMLOutputFactory xmlOutputFactory;

    public StoragePlaceholderMarshaller(XMLOutputFactory xmlOutputFactory) {
        this.xmlOutputFactory = xmlOutputFactory;
    }

    @Override
    public Streamable marshal(Placeholder placeholder, ConversionContext conversionContext) throws XhtmlException {
        return out -> {
            try {
                String displayText;
                XMLStreamWriter writer = this.xmlOutputFactory.createXMLStreamWriter(out);
                writer.writeStartElement(StoragePlaceholderConstants.PLACEHOLDER_ELEMENT.getPrefix(), StoragePlaceholderConstants.PLACEHOLDER_ELEMENT.getLocalPart(), StoragePlaceholderConstants.PLACEHOLDER_ELEMENT.getNamespaceURI());
                String placeholderType = placeholder.getType();
                if (StringUtils.isNotEmpty((CharSequence)placeholderType)) {
                    writer.writeAttribute(StoragePlaceholderConstants.PLACEHOLDER_TYPE_ATTR.getPrefix(), StoragePlaceholderConstants.PLACEHOLDER_TYPE_ATTR.getNamespaceURI(), StoragePlaceholderConstants.PLACEHOLDER_TYPE_ATTR.getLocalPart(), placeholderType);
                }
                if (StringUtils.isNotBlank((CharSequence)(displayText = placeholder.getDisplayText()))) {
                    writer.writeCharacters(displayText);
                }
                writer.writeEndElement();
                writer.flush();
            }
            catch (XMLStreamException e) {
                throw new IOException(e);
            }
        };
    }
}

