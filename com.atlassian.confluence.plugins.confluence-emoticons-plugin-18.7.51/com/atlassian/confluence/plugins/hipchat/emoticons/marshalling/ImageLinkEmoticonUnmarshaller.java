/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.StaxUtils
 *  com.atlassian.confluence.content.render.xhtml.Unmarshaller
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.render.xhtml.model.inline.Emoticon
 *  com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.marshalling;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.inline.Emoticon;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.plugins.hipchat.emoticons.HipChatEmoticon;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class ImageLinkEmoticonUnmarshaller
implements Unmarshaller<HipChatEmoticon> {
    private static final QName HTML_IMG_ELEMENT = new QName("http://www.w3.org/1999/xhtml", "img");
    private static final Pattern EMOTICON_ATTRIBUTE_PATTERN = Pattern.compile("(?:^|\\b)emoticon-([a-zA-Z\\-]+)");

    public boolean handles(StartElement startElement, ConversionContext conversionContext) {
        return HTML_IMG_ELEMENT.equals(startElement.getName()) && StaxUtils.hasClass((StartElement)startElement, (String)"emoticon") && this.isNotDefaultEmoticon(startElement) && this.isNotTwitterEmoji(startElement);
    }

    public HipChatEmoticon unmarshal(XMLEventReader xmlEventReader, FragmentTransformer fragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        try {
            XMLEvent event = xmlEventReader.nextEvent();
            if (!event.isStartElement() || !this.handles(event.asStartElement(), conversionContext)) {
                throw new XhtmlException("Unmarshaller called for wrong element");
            }
            String emoticonName = this.findEmoticonName(event.asStartElement());
            if (emoticonName == null) {
                throw new XhtmlException("Unmarshaller encountered element with missing shortcut");
            }
            return new HipChatEmoticon(emoticonName);
        }
        catch (XMLStreamException e) {
            throw new XhtmlException("XML exception during unmarshalling", (Throwable)e);
        }
    }

    private String findEmoticonName(StartElement startElement) {
        String emoticonClassValues = StaxUtils.getAttributeValue((StartElement)startElement, (String)"class");
        Matcher matcher = EMOTICON_ATTRIBUTE_PATTERN.matcher(emoticonClassValues);
        int matches = 0;
        while (matcher.find()) {
            ++matches;
        }
        if (matches != 1) {
            return null;
        }
        matcher.find(0);
        return matcher.group(1);
    }

    private boolean isNotDefaultEmoticon(StartElement startElement) {
        String emoticonName = this.findEmoticonName(startElement);
        return StringUtils.isNotBlank((CharSequence)emoticonName) && Emoticon.get((String)emoticonName) == null;
    }

    private boolean isNotTwitterEmoji(StartElement startElement) {
        return StaxUtils.getAttributeValue((StartElement)startElement, (String)"data-emoji-id") == null;
    }
}

