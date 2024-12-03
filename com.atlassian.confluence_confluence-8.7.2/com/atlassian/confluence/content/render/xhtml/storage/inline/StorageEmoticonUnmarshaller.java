/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.xhtml.storage.inline;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.inline.Emoticon;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageEmoticonUnmarshaller
implements Unmarshaller<Emoticon> {
    public static final QName EMOTICON_ELEMENT = new QName("http://atlassian.com/content", "emoticon", "ac");
    private static final QName NAME_ATTRIBUTE_NAME = new QName("http://atlassian.com/content", "name", "ac");
    private static final Logger log = LoggerFactory.getLogger(StorageEmoticonUnmarshaller.class);

    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        Attribute identifierAttribute = startElementEvent.getAttributeByName(NAME_ATTRIBUTE_NAME);
        return EMOTICON_ELEMENT.equals(startElementEvent.getName()) && identifierAttribute != null && Emoticon.get(identifierAttribute.getValue()) != null;
    }

    @Override
    public Emoticon unmarshal(XMLEventReader xmlEventReader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        try {
            XMLEvent event = xmlEventReader.nextEvent();
            if (event.isStartElement() && this.handles(event.asStartElement(), conversionContext)) {
                Attribute nameAttribute = event.asStartElement().getAttributeByName(NAME_ATTRIBUTE_NAME);
                if (nameAttribute == null) {
                    throw new XhtmlException("Emoticon with missing " + NAME_ATTRIBUTE_NAME.getLocalPart() + " attribute cannot be unmarshalled.");
                }
                String emoticonType = nameAttribute.getValue();
                Emoticon emoticon = Emoticon.get(emoticonType);
                if (emoticon == null) {
                    throw new XhtmlException("The type " + emoticonType + " is not a known emoticon.");
                }
                return emoticon;
            }
            throw new XhtmlException("The " + StorageEmoticonUnmarshaller.class.getName() + " was called for the wrong XMLEvent.");
        }
        catch (XMLStreamException ex) {
            throw new XhtmlException("Exception reading data while unmarshalling an emoticon from storage.", ex);
        }
    }
}

