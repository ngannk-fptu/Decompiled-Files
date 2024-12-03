/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.editor.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.StaxStreamMarshaller;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.StartElement;
import org.apache.commons.lang3.StringUtils;

public class EditorPageResourceIdentifierMarshallerAndUnmarshaller
implements Unmarshaller<ResourceIdentifier>,
StaxStreamMarshaller<PageResourceIdentifier> {
    @Override
    public void marshal(PageResourceIdentifier pageResourceIdentifier, XMLStreamWriter xmlStreamWriter, ConversionContext context) throws XMLStreamException {
        if (StringUtils.isNotBlank((CharSequence)pageResourceIdentifier.getTitle())) {
            xmlStreamWriter.writeAttribute("data-content-title", pageResourceIdentifier.getTitle());
        }
        if (StringUtils.isNotBlank((CharSequence)pageResourceIdentifier.getSpaceKey())) {
            xmlStreamWriter.writeAttribute("data-space-key", pageResourceIdentifier.getSpaceKey());
        }
    }

    @Override
    public ResourceIdentifier unmarshal(XMLEventReader xmlEventReader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        StartElement startElement;
        try {
            startElement = xmlEventReader.peek().asStartElement();
        }
        catch (XMLStreamException e) {
            throw new XhtmlException(e);
        }
        return new PageResourceIdentifier(StaxUtils.getAttributeValue(startElement, "data-space-key"), StaxUtils.getAttributeValue(startElement, "data-content-title"));
    }

    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        return StaxUtils.hasAttributes(startElementEvent, "data-content-title");
    }
}

