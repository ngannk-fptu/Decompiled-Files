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
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.SpaceResourceIdentifier;
import org.apache.commons.lang3.StringUtils;

public class StorageSpaceResourceIdentifierMarshaller
implements Marshaller<SpaceResourceIdentifier> {
    private final XmlStreamWriterTemplate xmlStreamWriterTemplate;

    public StorageSpaceResourceIdentifierMarshaller(XmlStreamWriterTemplate xmlStreamWriterTemplate) {
        this.xmlStreamWriterTemplate = xmlStreamWriterTemplate;
    }

    @Override
    public Streamable marshal(SpaceResourceIdentifier spaceResourceIdentifier, ConversionContext conversionContext) throws XhtmlException {
        return Streamables.from(this.xmlStreamWriterTemplate, (xmlStreamWriter, underlyingWriter) -> {
            xmlStreamWriter.writeStartElement("ri", "space", "http://atlassian.com/resource/identifier");
            if (StringUtils.isNotBlank((CharSequence)spaceResourceIdentifier.getSpaceKey())) {
                xmlStreamWriter.writeAttribute("ri", "http://atlassian.com/resource/identifier", "space-key", spaceResourceIdentifier.getSpaceKey());
            }
            xmlStreamWriter.writeEndElement();
        });
    }
}

