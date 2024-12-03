/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.editor.placeholder;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate;
import com.atlassian.confluence.xhtml.api.Placeholder;
import org.apache.commons.lang3.StringUtils;

public class EditorPlaceholderMarshaller
implements Marshaller<Placeholder> {
    private final XmlStreamWriterTemplate xmlStreamWriterTemplate;

    public EditorPlaceholderMarshaller(XmlStreamWriterTemplate xmlStreamWriterTemplate) {
        this.xmlStreamWriterTemplate = xmlStreamWriterTemplate;
    }

    @Override
    public Streamable marshal(Placeholder placeholder, ConversionContext conversionContext) throws XhtmlException {
        return Streamables.from(this.xmlStreamWriterTemplate, (xmlStreamWriter, underlyingWriter) -> {
            String displayText;
            xmlStreamWriter.writeStartElement("span");
            xmlStreamWriter.writeAttribute("class", "text-placeholder");
            String type = placeholder.getType();
            if (StringUtils.isNotEmpty((CharSequence)type)) {
                xmlStreamWriter.writeAttribute("data-placeholder-type", type);
            }
            if (StringUtils.isNotBlank((CharSequence)(displayText = placeholder.getDisplayText()))) {
                xmlStreamWriter.writeCharacters(displayText);
            }
            xmlStreamWriter.writeEndElement();
        });
    }
}

