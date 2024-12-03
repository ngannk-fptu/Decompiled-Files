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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TwitterEmojiStorageUnmarshaller
implements Unmarshaller<TwitterEmoji>,
InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(TwitterEmojiStorageUnmarshaller.class);
    private static final QName EMOTICON_ELEMENT = new QName("http://atlassian.com/content", "emoticon", "ac");
    private static final QName EMOJI_ID_ATTRIBUTE_NAME = new QName("http://atlassian.com/content", "emoji-id", "ac");
    private MarshallingRegistry marshallingRegistry;
    private final TwitterEmoticonService twitterEmoticonService;

    public TwitterEmojiStorageUnmarshaller(@ComponentImport MarshallingRegistry marshallingRegistry, @Qualifier(value="twitterEmoticonService") TwitterEmoticonService twitterEmoticonService) {
        this.marshallingRegistry = marshallingRegistry;
        this.twitterEmoticonService = twitterEmoticonService;
    }

    public TwitterEmoji unmarshal(XMLEventReader xmlEventReader, FragmentTransformer fragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        try {
            XMLEvent event = xmlEventReader.nextEvent();
            if (event.isStartElement() && this.handles(event.asStartElement(), conversionContext)) {
                Attribute emojiIdAttribute = event.asStartElement().getAttributeByName(EMOJI_ID_ATTRIBUTE_NAME);
                if (emojiIdAttribute == null) {
                    log.error("Twitter emoji with missing " + EMOJI_ID_ATTRIBUTE_NAME.getLocalPart() + "attributed cannot be unmarshalled.");
                    return null;
                }
                AtlaskitEmoticonModel twitterEmoji = this.twitterEmoticonService.findById(emojiIdAttribute.getValue());
                if (twitterEmoji == null) {
                    log.error("Twitter emoji with ID " + emojiIdAttribute.getValue() + "is not a known emoji.");
                    return null;
                }
                return new TwitterEmoji(twitterEmoji);
            }
            throw new XhtmlException("The " + TwitterEmojiStorageUnmarshaller.class.getName() + " was called for the wrong XMLEvent.");
        }
        catch (XMLStreamException ex) {
            throw new XhtmlException("Exception reading data while unmarshalling an emoticon from storage.", (Throwable)ex);
        }
    }

    public boolean handles(StartElement startElement, ConversionContext conversionContext) {
        return EMOTICON_ELEMENT.equals(startElement.getName()) && startElement.getAttributeByName(EMOJI_ID_ATTRIBUTE_NAME) != null;
    }

    public void afterPropertiesSet() {
        this.marshallingRegistry.register((Unmarshaller)this, TwitterEmoji.class, MarshallingType.STORAGE);
    }
}

