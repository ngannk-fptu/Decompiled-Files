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
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ShortcutResourceIdentifier;
import org.apache.commons.lang3.StringUtils;

public class StorageShortcutResourceIdentifierMarshaller
implements Marshaller<ShortcutResourceIdentifier> {
    private final XmlStreamWriterTemplate xmlStreamWriterTemplate;

    public StorageShortcutResourceIdentifierMarshaller(XmlStreamWriterTemplate xmlStreamWriterTemplate) {
        this.xmlStreamWriterTemplate = xmlStreamWriterTemplate;
    }

    @Override
    public Streamable marshal(ShortcutResourceIdentifier shortcutResourceIdentifier, ConversionContext conversionContext) throws XhtmlException {
        return Streamables.from(this.xmlStreamWriterTemplate, (xmlStreamWriter, underlyingWriter) -> {
            xmlStreamWriter.writeStartElement("ri", "shortcut", "http://atlassian.com/resource/identifier");
            xmlStreamWriter.writeAttribute("ri", "http://atlassian.com/resource/identifier", "key", shortcutResourceIdentifier.getShortcutKey());
            xmlStreamWriter.writeAttribute("ri", "http://atlassian.com/resource/identifier", "parameter", StringUtils.defaultString((String)shortcutResourceIdentifier.getShortcutParameter()));
            xmlStreamWriter.writeEndElement();
        });
    }
}

