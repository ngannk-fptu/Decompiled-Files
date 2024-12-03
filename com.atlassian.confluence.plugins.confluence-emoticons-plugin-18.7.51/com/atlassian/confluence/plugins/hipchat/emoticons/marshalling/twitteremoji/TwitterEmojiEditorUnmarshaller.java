/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.MarshallingRegistry
 *  com.atlassian.confluence.content.render.xhtml.MarshallingType
 *  com.atlassian.confluence.content.render.xhtml.Unmarshaller
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.marshalling.twitteremoji;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.MarshallingRegistry;
import com.atlassian.confluence.content.render.xhtml.MarshallingType;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.plugins.hipchat.emoticons.TwitterEmoji;
import com.atlassian.confluence.plugins.hipchat.emoticons.rest.AtlaskitEmoticonModel;
import com.atlassian.confluence.plugins.hipchat.emoticons.service.TwitterEmoticonService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TwitterEmojiEditorUnmarshaller
implements Unmarshaller<TwitterEmoji>,
InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(TwitterEmojiEditorUnmarshaller.class);
    static final QName EMOTICON_ATTR_ID = new QName("data-emoji-id");
    private MarshallingRegistry marshallingRegistry;
    private final TwitterEmoticonService twitterEmoticonService;

    @Autowired
    public TwitterEmojiEditorUnmarshaller(@ComponentImport MarshallingRegistry marshallingRegistry, @Qualifier(value="twitterEmoticonService") TwitterEmoticonService twitterEmoticonService) {
        this.marshallingRegistry = marshallingRegistry;
        this.twitterEmoticonService = twitterEmoticonService;
    }

    public TwitterEmoji unmarshal(XMLEventReader xmlEventReader, FragmentTransformer fragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        try {
            XMLEvent event = xmlEventReader.nextEvent();
            if (event.isStartElement() && this.handles(event.asStartElement(), conversionContext)) {
                Attribute emojiIdAttribute = event.asStartElement().getAttributeByName(EMOTICON_ATTR_ID);
                if (emojiIdAttribute == null) {
                    log.error("Twitter emoji with missing " + EMOTICON_ATTR_ID.getLocalPart() + "attributed cannot be unmarshalled.");
                    return null;
                }
                String idIdentifier = emojiIdAttribute.getValue();
                AtlaskitEmoticonModel twitterEmoji = this.twitterEmoticonService.findById(idIdentifier);
                if (twitterEmoji == null) {
                    log.error("No Twitter emoji can be found for the ID" + idIdentifier);
                    return null;
                }
                return new TwitterEmoji(twitterEmoji);
            }
            throw new XhtmlException("The " + TwitterEmojiEditorUnmarshaller.class.getName() + " was called for the wrong XMLEvent.");
        }
        catch (XMLStreamException ex) {
            throw new XhtmlException("Exception while reading the emoticon data.", (Throwable)ex);
        }
    }

    public boolean handles(StartElement startElement, ConversionContext conversionContext) {
        return startElement.getAttributeByName(EMOTICON_ATTR_ID) != null;
    }

    public void afterPropertiesSet() {
        this.marshallingRegistry.register((Unmarshaller)this, TwitterEmoji.class, MarshallingType.EDITOR);
        this.marshallingRegistry.register((Unmarshaller)this, TwitterEmoji.class, MarshallingType.VIEW);
    }
}

