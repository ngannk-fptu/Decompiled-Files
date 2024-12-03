/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.storage.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import java.util.List;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

public class DelegatingResourceIdentifierUnmarshaller
implements Unmarshaller<ResourceIdentifier> {
    private final List<Unmarshaller<ResourceIdentifier>> unmarshallers;

    public DelegatingResourceIdentifierUnmarshaller(List<Unmarshaller<ResourceIdentifier>> unmarshallers) {
        this.unmarshallers = unmarshallers;
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
        ResourceIdentifier result = null;
        for (Unmarshaller<ResourceIdentifier> unmarshaller : this.unmarshallers) {
            if (!unmarshaller.handles(startElement, conversionContext)) continue;
            result = unmarshaller.unmarshal(xmlEventReader, mainFragmentTransformer, conversionContext);
            break;
        }
        return result;
    }

    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        for (Unmarshaller<ResourceIdentifier> unmarshaller : this.unmarshallers) {
            if (!unmarshaller.handles(startElementEvent, conversionContext)) continue;
            return true;
        }
        return false;
    }
}

