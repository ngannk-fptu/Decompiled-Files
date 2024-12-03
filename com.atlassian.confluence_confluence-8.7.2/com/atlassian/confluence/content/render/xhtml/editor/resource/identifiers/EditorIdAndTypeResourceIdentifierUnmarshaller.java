/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.editor.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.IdAndTypeResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

public class EditorIdAndTypeResourceIdentifierUnmarshaller
implements Unmarshaller<IdAndTypeResourceIdentifier> {
    @Override
    public IdAndTypeResourceIdentifier unmarshal(XMLEventReader xmlEventReader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        StartElement startElement;
        try {
            startElement = xmlEventReader.peek().asStartElement();
        }
        catch (XMLStreamException e) {
            throw new XhtmlException(e);
        }
        long resourceId = Long.parseLong(StaxUtils.getAttributeValue(startElement, "data-linked-resource-id"));
        String resourceType = StaxUtils.getAttributeValue(startElement, "data-linked-resource-type");
        ContentTypeEnum contentType = ContentTypeEnum.getByRepresentation(resourceType);
        if (contentType == null) {
            throw new XhtmlException("Unsupported resource type '" + resourceType + "' for resource '" + resourceId + "'.");
        }
        return new IdAndTypeResourceIdentifier(resourceId, contentType);
    }

    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        return StaxUtils.hasAttributes(startElementEvent, "data-linked-resource-id", "data-linked-resource-type");
    }
}

