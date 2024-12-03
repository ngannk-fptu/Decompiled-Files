/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.image;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.inline.Emoticon;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class ImageLinkEmoticonUnmarshaller
implements Unmarshaller<Emoticon> {
    private static final QName HTML_IMG_ELEMENT = new QName("http://www.w3.org/1999/xhtml", "img");
    private static final Pattern EMOTICON_ATTRIBUTE_PATTERN = Pattern.compile("(?:^|\\b)emoticon-([a-zA-Z\\-]+)");

    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        return HTML_IMG_ELEMENT.equals(startElementEvent.getName()) && StaxUtils.hasClass(startElementEvent, "emoticon") && this.findEmoticon(startElementEvent) != null && StaxUtils.getAttributeValue(startElementEvent, "data-hipchat-emoticon") == null && StaxUtils.getAttributeValue(startElementEvent, "data-emoji-id") == null;
    }

    @Override
    public Emoticon unmarshal(XMLEventReader xmlEventReader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        try {
            XMLEvent event = xmlEventReader.nextEvent();
            StartElement startElement = event.asStartElement();
            Emoticon emoticon = this.findEmoticon(startElement);
            if (emoticon == null) {
                throw new XhtmlException("Unable to detect emoticon type from img in XHTML format");
            }
            return emoticon;
        }
        catch (XMLStreamException e) {
            throw new XhtmlException(e);
        }
    }

    private Emoticon findEmoticon(StartElement startElement) {
        String emoticonClassValues = StaxUtils.getAttributeValue(startElement, "class");
        Matcher matcher = EMOTICON_ATTRIBUTE_PATTERN.matcher(emoticonClassValues);
        int matches = 0;
        while (matcher.find()) {
            ++matches;
        }
        if (matches != 1) {
            return null;
        }
        matcher.find(0);
        String emoticonType = matcher.group(1);
        return Emoticon.get(emoticonType);
    }
}

