/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.storage.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifier;
import org.apache.commons.lang3.StringUtils;

public class StoragePageResourceIdentifierMarshaller
implements Marshaller<PageResourceIdentifier> {
    private final XmlStreamWriterTemplate xmlStreamWriterTemplate;

    public StoragePageResourceIdentifierMarshaller(XmlStreamWriterTemplate xmlStreamWriterTemplate) {
        this.xmlStreamWriterTemplate = xmlStreamWriterTemplate;
    }

    @Override
    public Streamable marshal(PageResourceIdentifier pageResourceIdentifier, ConversionContext conversionContext) throws XhtmlException {
        if (!pageResourceIdentifier.isPopulated()) {
            return Streamables.empty();
        }
        return Streamables.from(this.xmlStreamWriterTemplate, (xmlStreamWriter, underlyingWriter) -> {
            xmlStreamWriter.writeStartElement("ri", "page", "http://atlassian.com/resource/identifier");
            if (StringUtils.isNotBlank((CharSequence)pageResourceIdentifier.getSpaceKey())) {
                xmlStreamWriter.writeAttribute("ri", "http://atlassian.com/resource/identifier", "space-key", pageResourceIdentifier.getSpaceKey());
            }
            if (StringUtils.isNotBlank((CharSequence)pageResourceIdentifier.getTitle())) {
                xmlStreamWriter.writeAttribute("ri", "http://atlassian.com/resource/identifier", "content-title", pageResourceIdentifier.getTitle());
            }
            xmlStreamWriter.writeEndElement();
        });
    }
}

