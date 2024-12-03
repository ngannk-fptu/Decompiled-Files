/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.content.render.xhtml.storage.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate;
import com.atlassian.confluence.content.render.xhtml.migration.UrlResourceIdentifier;
import com.google.common.base.Preconditions;

public class StorageUrlResourceIdentifierMarshaller
implements Marshaller<UrlResourceIdentifier> {
    private final XmlStreamWriterTemplate xmlStreamWriterTemplate;

    public StorageUrlResourceIdentifierMarshaller(XmlStreamWriterTemplate xmlStreamWriterTemplate) {
        this.xmlStreamWriterTemplate = xmlStreamWriterTemplate;
    }

    @Override
    public Streamable marshal(UrlResourceIdentifier urlResourceIdentifier, ConversionContext conversionContext) throws XhtmlException {
        Preconditions.checkNotNull((Object)urlResourceIdentifier.getUrl(), (Object)"UrlResourceIdentififier contains a null URL; cannot marshal");
        return Streamables.from(this.xmlStreamWriterTemplate, (xmlStreamWriter, underlyingWriter) -> {
            xmlStreamWriter.writeStartElement("ri", "url", "http://atlassian.com/resource/identifier");
            xmlStreamWriter.writeAttribute("ri", "http://atlassian.com/resource/identifier", "value", urlResourceIdentifier.getUrl());
            xmlStreamWriter.writeEndElement();
        });
    }
}

