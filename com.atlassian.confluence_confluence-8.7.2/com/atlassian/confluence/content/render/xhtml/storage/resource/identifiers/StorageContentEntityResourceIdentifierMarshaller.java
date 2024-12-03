/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.storage.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ContentEntityResourceIdentifier;

public class StorageContentEntityResourceIdentifierMarshaller
implements Marshaller<ContentEntityResourceIdentifier> {
    private final XmlStreamWriterTemplate xmlStreamWriterTemplate;

    public StorageContentEntityResourceIdentifierMarshaller(XmlStreamWriterTemplate xmlStreamWriterTemplate) {
        this.xmlStreamWriterTemplate = xmlStreamWriterTemplate;
    }

    @Override
    public Streamable marshal(ContentEntityResourceIdentifier contentEntityResourceIdentifier, ConversionContext conversionContext) throws XhtmlException {
        return Streamables.from(this.xmlStreamWriterTemplate, (xmlStreamWriter, underlyingWriter) -> {
            xmlStreamWriter.writeStartElement("ri", "content-entity", "http://atlassian.com/resource/identifier");
            xmlStreamWriter.writeAttribute("ri", "http://atlassian.com/resource/identifier", "content-id", String.valueOf(contentEntityResourceIdentifier.getContentId()));
            xmlStreamWriter.writeEndElement();
        });
    }
}

