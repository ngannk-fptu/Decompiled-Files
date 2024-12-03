/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Marshaller
 *  com.atlassian.confluence.content.render.xhtml.MarshallingRegistry
 *  com.atlassian.confluence.content.render.xhtml.MarshallingType
 *  com.atlassian.confluence.content.render.xhtml.Streamable
 *  com.atlassian.confluence.content.render.xhtml.XmlOutputFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.marshalling;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.MarshallingRegistry;
import com.atlassian.confluence.content.render.xhtml.MarshallingType;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactory;
import com.atlassian.confluence.plugins.hipchat.emoticons.HipChatEmoticon;
import com.atlassian.confluence.plugins.hipchat.emoticons.content.entity.CustomEmoticon;
import com.atlassian.confluence.plugins.hipchat.emoticons.marshalling.EmoticonEditorUnmarshaller;
import com.atlassian.confluence.plugins.hipchat.emoticons.service.CustomEmoticonService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.io.IOException;
import java.util.Optional;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class EmoticonEditorMarshaller
implements Marshaller<HipChatEmoticon>,
InitializingBean {
    private final XmlOutputFactory xmlOutputFactory;
    private final CustomEmoticonService customEmoticonService;
    private MarshallingRegistry marshallingRegistry;

    @Autowired
    public EmoticonEditorMarshaller(@ComponentImport XmlOutputFactory xmlOutputFactory, @Qualifier(value="customEmoticonService") CustomEmoticonService customEmoticonService, @ComponentImport MarshallingRegistry marshallingRegistry) {
        this.xmlOutputFactory = xmlOutputFactory;
        this.customEmoticonService = customEmoticonService;
        this.marshallingRegistry = marshallingRegistry;
    }

    public void afterPropertiesSet() {
        this.marshallingRegistry.register((Marshaller)this, HipChatEmoticon.class, MarshallingType.EDITOR);
        this.marshallingRegistry.register((Marshaller)this, HipChatEmoticon.class, MarshallingType.VIEW);
    }

    public Streamable marshal(HipChatEmoticon hipChatEmoticon, ConversionContext conversionContext) {
        return sink -> {
            try {
                XMLStreamWriter writer = this.xmlOutputFactory.createXMLStreamWriter(sink);
                String shortcut = hipChatEmoticon.getShortcut();
                String formattedShortcut = ":" + shortcut + ":";
                Optional<CustomEmoticon> emoticon = this.customEmoticonService.findByShortcut(shortcut).stream().findFirst();
                if (!emoticon.isPresent()) {
                    writer.writeStartElement("span");
                    writer.writeAttribute(EmoticonEditorUnmarshaller.EMOTICON_ATTR_ID.getLocalPart(), shortcut);
                    writer.writeAttribute("title", formattedShortcut);
                    writer.writeAttribute("class", "unknown-hipchat-emoticon");
                    writer.writeAttribute("contenteditable", "false");
                    writer.writeAttribute("data-emoji-short-name", formattedShortcut);
                    writer.writeCharacters(formattedShortcut);
                    writer.writeEndElement();
                    writer.close();
                    return;
                }
                String emoticonName = emoticon.get().getName();
                String title = emoticonName == null || emoticonName.isEmpty() ? formattedShortcut : emoticonName;
                writer.writeEmptyElement("img");
                writer.writeAttribute("alt", title);
                writer.writeAttribute("title", title);
                writer.writeAttribute("border", "0");
                writer.writeAttribute(EmoticonEditorUnmarshaller.EMOTICON_ATTR_ID.getLocalPart(), shortcut);
                writer.writeAttribute("class", "emoticon emoticon-" + shortcut);
                writer.writeAttribute("data-emoji-short-name", formattedShortcut);
                writer.writeAttribute("src", emoticon.get().getURL());
                writer.close();
            }
            catch (XMLStreamException e) {
                throw new IOException("Error marshalling " + hipChatEmoticon + " to editor format", e);
            }
        };
    }
}

