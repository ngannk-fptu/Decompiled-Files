/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.transformers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import java.util.Iterator;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;

public class Html5VoidElementFragmentTransformer
implements FragmentTransformer {
    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        return StaxUtils.isHTML5VoidElement(startElementEvent.getName());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Streamable transform(XMLEventReader reader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        try {
            StartElement startElement;
            StringBuilder result = new StringBuilder();
            try {
                startElement = reader.nextEvent().asStartElement();
            }
            catch (XMLStreamException e) {
                throw new XhtmlException(e);
            }
            result.append("<").append(startElement.getName().getLocalPart());
            Iterator<Attribute> attributes = startElement.getAttributes();
            while (attributes.hasNext()) {
                Attribute attribute = attributes.next();
                result.append(" ").append(attribute.getName().getLocalPart()).append("=\"").append(attribute.getValue()).append("\"");
            }
            Streamable streamable = Streamables.from(result.append("/>").toString());
            return streamable;
        }
        finally {
            StaxUtils.closeQuietly(reader);
        }
    }
}

