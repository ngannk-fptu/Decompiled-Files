/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.view.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.editor.macro.PlaceholderUrlFactory;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import java.io.IOException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class ViewUnknownMacroMarshaller
implements Marshaller<MacroDefinition> {
    private final XMLOutputFactory xmlOutputFactory;
    private final PlaceholderUrlFactory placeholderUrlFactory;

    public ViewUnknownMacroMarshaller(XMLOutputFactory xmlOutputFactory, PlaceholderUrlFactory placeholderUrlFactory) {
        this.xmlOutputFactory = xmlOutputFactory;
        this.placeholderUrlFactory = placeholderUrlFactory;
    }

    @Override
    public Streamable marshal(MacroDefinition macroDefinition, ConversionContext conversionContext) throws XhtmlException {
        return out -> {
            try {
                XMLStreamWriter writer = this.xmlOutputFactory.createXMLStreamWriter(out);
                String name = macroDefinition.getName();
                writer.writeStartElement("img");
                writer.writeAttribute("class", "wysiwyg-unknown-macro");
                writer.writeAttribute("src", this.placeholderUrlFactory.getUrlForUnknownMacro(name));
                writer.writeEndElement();
                writer.close();
            }
            catch (XMLStreamException e) {
                throw new IOException(e);
            }
        };
    }
}

