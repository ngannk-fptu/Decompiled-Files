/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Unmarshaller
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.marshalling;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.plugins.hipchat.emoticons.HipChatEmoticon;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.springframework.stereotype.Component;

@Component
public class EmoticonStorageUnmarshaller
implements Unmarshaller<HipChatEmoticon> {
    static final QName EMOTICON_ELEMENT = new QName("http://atlassian.com/content", "hipchat-emoticon", "ac");
    static final QName SHORTCUT_ATTRIBUTE_ID = new QName("http://atlassian.com/content", "shortcut", "ac");

    public boolean handles(StartElement startElement, ConversionContext conversionContext) {
        return EMOTICON_ELEMENT.equals(startElement.getName());
    }

    public HipChatEmoticon unmarshal(XMLEventReader xmlEventReader, FragmentTransformer fragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        try {
            XMLEvent event = xmlEventReader.nextEvent();
            if (!event.isStartElement() || !this.handles(event.asStartElement(), conversionContext)) {
                throw new XhtmlException("Unmarshaller called for wrong element");
            }
            Attribute shortcutAttribute = event.asStartElement().getAttributeByName(SHORTCUT_ATTRIBUTE_ID);
            if (shortcutAttribute == null) {
                throw new XhtmlException("Unmarshaller encountered element with missing shortcut");
            }
            return new HipChatEmoticon(shortcutAttribute.getValue());
        }
        catch (XMLStreamException e) {
            throw new XhtmlException("XML exception during unmarshalling", (Throwable)e);
        }
    }
}

