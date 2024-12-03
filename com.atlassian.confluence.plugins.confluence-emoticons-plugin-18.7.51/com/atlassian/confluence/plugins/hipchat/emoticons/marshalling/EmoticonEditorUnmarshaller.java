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
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.marshalling;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.MarshallingRegistry;
import com.atlassian.confluence.content.render.xhtml.MarshallingType;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.plugins.hipchat.emoticons.HipChatEmoticon;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmoticonEditorUnmarshaller
implements Unmarshaller<HipChatEmoticon>,
InitializingBean {
    static final QName EMOTICON_ATTR_ID = new QName("data-hipchat-emoticon");
    private MarshallingRegistry marshallingRegistry;

    @Autowired
    public EmoticonEditorUnmarshaller(@ComponentImport MarshallingRegistry marshallingRegistry) {
        this.marshallingRegistry = marshallingRegistry;
    }

    public void afterPropertiesSet() {
        this.marshallingRegistry.register((Unmarshaller)this, HipChatEmoticon.class, MarshallingType.EDITOR);
        this.marshallingRegistry.register((Unmarshaller)this, HipChatEmoticon.class, MarshallingType.VIEW);
    }

    public boolean handles(StartElement startElement, ConversionContext conversionContext) {
        return startElement.getAttributeByName(EMOTICON_ATTR_ID) != null;
    }

    public HipChatEmoticon unmarshal(XMLEventReader xmlEventReader, FragmentTransformer fragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        try {
            XMLEvent event = xmlEventReader.nextEvent();
            if (!event.isStartElement() || !this.handles(event.asStartElement(), conversionContext)) {
                throw new XhtmlException("Unmarshaller called for wrong element");
            }
            Attribute attribute = event.asStartElement().getAttributeByName(EMOTICON_ATTR_ID);
            if (attribute == null) {
                throw new XhtmlException("Unmarshaller encountered element with missing data-hipchat-emoticon attribute");
            }
            return new HipChatEmoticon(attribute.getValue());
        }
        catch (XMLStreamException e) {
            throw new XhtmlException("XML error during unmarshalling", (Throwable)e);
        }
    }
}

