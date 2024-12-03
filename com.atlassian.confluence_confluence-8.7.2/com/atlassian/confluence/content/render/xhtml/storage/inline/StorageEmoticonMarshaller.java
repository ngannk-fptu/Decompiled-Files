/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.storage.inline;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.MarshallingRegistry;
import com.atlassian.confluence.content.render.xhtml.MarshallingType;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.inline.Emoticon;
import java.io.IOException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class StorageEmoticonMarshaller
implements Marshaller<Emoticon> {
    static final String ELEMENT_NAME = "emoticon";
    static final String NAME_ATTRIBUTE_NAME = "name";
    private final XMLOutputFactory xmlOutputFactory;

    public StorageEmoticonMarshaller(XMLOutputFactory xmlOutputFactory, MarshallingRegistry registry) {
        this.xmlOutputFactory = xmlOutputFactory;
        registry.register(this, Emoticon.class, MarshallingType.STORAGE);
    }

    @Override
    public Streamable marshal(Emoticon emoticon, ConversionContext conversionContext) throws XhtmlException {
        return out -> {
            try {
                XMLStreamWriter writer = this.xmlOutputFactory.createXMLStreamWriter(out);
                writer.writeEmptyElement("ac", ELEMENT_NAME, "http://atlassian.com/content");
                writer.writeAttribute("ac", "http://atlassian.com/content", NAME_ATTRIBUTE_NAME, emoticon.getType());
                writer.close();
            }
            catch (XMLStreamException ex) {
                throw new IOException("Error marshalling the emoticon " + emoticon.name() + " to storage.", ex);
            }
        };
    }
}

