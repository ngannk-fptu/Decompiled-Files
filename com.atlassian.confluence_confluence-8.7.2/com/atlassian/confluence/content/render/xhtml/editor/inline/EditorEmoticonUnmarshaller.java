/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.editor.inline;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.MarshallingRegistry;
import com.atlassian.confluence.content.render.xhtml.MarshallingType;
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

public class EditorEmoticonUnmarshaller
implements Unmarshaller<Emoticon> {
    private static final QName IDENTIFYING_ATTRIBUTE_QNAME = new QName("data-emoticon-name");

    public EditorEmoticonUnmarshaller(MarshallingRegistry registry) {
        registry.register(this, Emoticon.class, MarshallingType.EDITOR);
    }

    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        Attribute identifierAttribute = startElementEvent.getAttributeByName(IDENTIFYING_ATTRIBUTE_QNAME);
        return "img".equals(startElementEvent.getName().getLocalPart()) && identifierAttribute != null && Emoticon.get(identifierAttribute.getValue()) != null;
    }

    @Override
    public Emoticon unmarshal(XMLEventReader xmlEventReader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        try {
            XMLEvent event = xmlEventReader.nextEvent();
            if (event.isStartElement() && this.handles(event.asStartElement(), conversionContext)) {
                String identifier = event.asStartElement().getAttributeByName(IDENTIFYING_ATTRIBUTE_QNAME).getValue();
                Emoticon emoticon = Emoticon.get(identifier);
                if (emoticon == null) {
                    throw new XhtmlException("No emoticon can be found for the identifier " + identifier);
                }
                return emoticon;
            }
            throw new XhtmlException("The " + EditorEmoticonUnmarshaller.class.getName() + " was called for the wrong XMLEvent.");
        }
        catch (XMLStreamException ex) {
            throw new XhtmlException("Exception while reading the emoticon data.", ex);
        }
    }
}

