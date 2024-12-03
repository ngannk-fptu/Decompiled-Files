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
import com.atlassian.confluence.plugins.hipchat.emoticons.marshalling.EmoticonStorageUnmarshaller;
import com.atlassian.confluence.plugins.hipchat.emoticons.marshalling.ImageLinkEmoticonUnmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DelegatingEmoticonStorageUnmarshaller
implements Unmarshaller<HipChatEmoticon>,
InitializingBean {
    private EmoticonStorageUnmarshaller defaultUnmarshaller;
    private ImageLinkEmoticonUnmarshaller imageLinkUnmarshaller;
    private MarshallingRegistry marshallingRegistry;

    @Autowired
    public DelegatingEmoticonStorageUnmarshaller(EmoticonStorageUnmarshaller defaultUnmarshaller, ImageLinkEmoticonUnmarshaller imageLinkUnmarshaller, MarshallingRegistry marshallingRegistry) {
        this.defaultUnmarshaller = defaultUnmarshaller;
        this.imageLinkUnmarshaller = imageLinkUnmarshaller;
        this.marshallingRegistry = marshallingRegistry;
    }

    public void afterPropertiesSet() {
        this.marshallingRegistry.register((Unmarshaller)this, HipChatEmoticon.class, MarshallingType.STORAGE);
    }

    public boolean handles(StartElement startElement, ConversionContext conversionContext) {
        return this.defaultUnmarshaller.handles(startElement, conversionContext) || this.imageLinkUnmarshaller.handles(startElement, conversionContext);
    }

    public HipChatEmoticon unmarshal(XMLEventReader xmlEventReader, FragmentTransformer fragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        try {
            StartElement startElement = xmlEventReader.peek().asStartElement();
            if (this.defaultUnmarshaller.handles(startElement, conversionContext)) {
                return this.defaultUnmarshaller.unmarshal(xmlEventReader, fragmentTransformer, conversionContext);
            }
            if (this.imageLinkUnmarshaller.handles(startElement, conversionContext)) {
                return this.imageLinkUnmarshaller.unmarshal(xmlEventReader, fragmentTransformer, conversionContext);
            }
            throw new XhtmlException("Couldn't find appropriate unmarshaller");
        }
        catch (XMLStreamException e) {
            throw new XhtmlException("XML exception during unmarshalling");
        }
    }
}

