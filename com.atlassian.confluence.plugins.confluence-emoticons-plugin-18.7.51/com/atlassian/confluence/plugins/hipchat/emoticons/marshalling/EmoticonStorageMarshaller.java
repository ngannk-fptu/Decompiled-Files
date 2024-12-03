/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Marshaller
 *  com.atlassian.confluence.content.render.xhtml.MarshallingRegistry
 *  com.atlassian.confluence.content.render.xhtml.MarshallingType
 *  com.atlassian.confluence.content.render.xhtml.Streamable
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.render.xhtml.XmlOutputFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.marshalling;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.MarshallingRegistry;
import com.atlassian.confluence.content.render.xhtml.MarshallingType;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactory;
import com.atlassian.confluence.plugins.hipchat.emoticons.HipChatEmoticon;
import com.atlassian.confluence.plugins.hipchat.emoticons.marshalling.EmoticonStorageUnmarshaller;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmoticonStorageMarshaller
implements Marshaller<HipChatEmoticon>,
InitializingBean {
    private final XmlOutputFactory xmlOutputFactory;
    private MarshallingRegistry marshallingRegistry;

    @Autowired
    public EmoticonStorageMarshaller(@ComponentImport XmlOutputFactory xmlOutputFactory, @ComponentImport MarshallingRegistry marshallingRegistry) {
        this.xmlOutputFactory = xmlOutputFactory;
        this.marshallingRegistry = marshallingRegistry;
    }

    public void afterPropertiesSet() {
        this.marshallingRegistry.register((Marshaller)this, HipChatEmoticon.class, MarshallingType.STORAGE);
    }

    public Streamable marshal(final HipChatEmoticon hipChatEmoticon, ConversionContext conversionContext) throws XhtmlException {
        return new Streamable(){

            public void writeTo(Writer sink) throws IOException {
                try {
                    XMLStreamWriter writer = EmoticonStorageMarshaller.this.xmlOutputFactory.createXMLStreamWriter(sink);
                    writer.writeEmptyElement(EmoticonStorageUnmarshaller.EMOTICON_ELEMENT.getPrefix(), EmoticonStorageUnmarshaller.EMOTICON_ELEMENT.getLocalPart(), EmoticonStorageUnmarshaller.EMOTICON_ELEMENT.getNamespaceURI());
                    writer.writeAttribute(EmoticonStorageUnmarshaller.SHORTCUT_ATTRIBUTE_ID.getPrefix(), EmoticonStorageUnmarshaller.SHORTCUT_ATTRIBUTE_ID.getNamespaceURI(), EmoticonStorageUnmarshaller.SHORTCUT_ATTRIBUTE_ID.getLocalPart(), hipChatEmoticon.getShortcut());
                    writer.close();
                }
                catch (XMLStreamException e) {
                    throw new IOException("Error marshalling " + hipChatEmoticon + " to storage format", e);
                }
            }
        };
    }
}

