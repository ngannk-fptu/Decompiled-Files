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
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.marshalling.twitteremoji;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.MarshallingRegistry;
import com.atlassian.confluence.content.render.xhtml.MarshallingType;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactory;
import com.atlassian.confluence.plugins.hipchat.emoticons.TwitterEmoji;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.io.IOException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TwitterEmojiEditorMarshaller
implements Marshaller<TwitterEmoji>,
InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(TwitterEmojiEditorMarshaller.class);
    static final String ELEMENT_NAME = "img";
    static final String CSS_CLASS = "emoticon";
    static final String FULL_CSS_CLASS_VALUE = "emoticon emoticon-";
    private final XmlOutputFactory xmlOutputFactory;
    private MarshallingRegistry marshallingRegistry;

    @Autowired
    public TwitterEmojiEditorMarshaller(@ComponentImport XmlOutputFactory xmlOutputFactory, @ComponentImport MarshallingRegistry marshallingRegistry) {
        this.xmlOutputFactory = xmlOutputFactory;
        this.marshallingRegistry = marshallingRegistry;
    }

    public Streamable marshal(TwitterEmoji twitterEmoji, ConversionContext conversionContext) {
        return out -> {
            try {
                if (twitterEmoji == null) {
                    logger.error("Null TwitterEmoji could not marshall into editor mode.");
                    return;
                }
                XMLStreamWriter writer = this.xmlOutputFactory.createXMLStreamWriter(out);
                writer.writeEmptyElement(ELEMENT_NAME);
                writer.writeAttribute("class", FULL_CSS_CLASS_VALUE + twitterEmoji.getId());
                writer.writeAttribute("data-emoji-id", twitterEmoji.getId());
                writer.writeAttribute("border", "0");
                writer.writeAttribute("src", twitterEmoji.getImagePath());
                writer.writeAttribute("alt", twitterEmoji.getName());
                writer.writeAttribute("title", twitterEmoji.getName());
                writer.writeAttribute("data-emoji-short-name", twitterEmoji.getShortcut());
                writer.close();
            }
            catch (XMLStreamException ex) {
                throw new IOException("Exception while writing the emoji " + twitterEmoji.getName() + " for the editor", ex);
            }
        };
    }

    public void afterPropertiesSet() {
        this.marshallingRegistry.register((Marshaller)this, TwitterEmoji.class, MarshallingType.EDITOR);
        this.marshallingRegistry.register((Marshaller)this, TwitterEmoji.class, MarshallingType.VIEW);
    }
}

