/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.editor.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.editor.macro.CommonMacroAttributeWriter;
import com.atlassian.confluence.content.render.xhtml.editor.macro.MacroMarshaller;
import com.atlassian.confluence.content.render.xhtml.editor.macro.PlaceholderUrlFactory;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import java.io.IOException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.lang3.StringUtils;

public class EditorBodyMacroMarshaller
implements MacroMarshaller {
    private final CommonMacroAttributeWriter commonAttributeWriter;
    private final PlaceholderUrlFactory placeholderUrlFactory;
    private final XMLOutputFactory xmlOutputFactory;

    public EditorBodyMacroMarshaller(CommonMacroAttributeWriter commonAttributeWriter, PlaceholderUrlFactory placeholderUrlFactory, XMLOutputFactory xmlOutputFactory) {
        this.commonAttributeWriter = commonAttributeWriter;
        this.placeholderUrlFactory = placeholderUrlFactory;
        this.xmlOutputFactory = xmlOutputFactory;
    }

    @Override
    public boolean handles(Macro macro) {
        return macro != null && macro.getBodyType() != Macro.BodyType.NONE;
    }

    @Override
    public Streamable marshal(Macro macro, MacroDefinition macroDefinition, ConversionContext conversionContext) throws XhtmlException {
        return out -> {
            try {
                XMLStreamWriter writer = this.xmlOutputFactory.createXMLStreamWriter(out);
                boolean isPlainText = Macro.BodyType.PLAIN_TEXT.equals((Object)macro.getBodyType());
                if (macro.getOutputType() == Macro.OutputType.INLINE && !macroDefinition.getParameters().containsKey("atlassian-macro-output-type")) {
                    macroDefinition.getParameters().put("atlassian-macro-output-type", Macro.OutputType.INLINE.name());
                }
                writer.writeStartElement("table");
                writer.writeAttribute("class", this.getCssClass(macroDefinition));
                this.commonAttributeWriter.writeCommonAttributes(macroDefinition, writer);
                writer.writeAttribute("style", "background-image: url(" + this.getImageSource(macroDefinition) + "); background-repeat: no-repeat;");
                writer.writeAttribute("data-macro-body-type", macro.getBodyType().name());
                writer.writeStartElement("tr");
                writer.writeStartElement("td");
                writer.writeAttribute("class", "wysiwyg-macro-body");
                String macroBody = macroDefinition.getBodyText();
                if (isPlainText) {
                    if (StringUtils.isBlank((CharSequence)macroBody)) {
                        writer.writeEmptyElement("pre");
                    } else {
                        writer.writeStartElement("pre");
                        writer.writeCharacters(macroBody);
                        writer.writeEndElement();
                    }
                } else if (StringUtils.isBlank((CharSequence)macroBody)) {
                    writer.writeEmptyElement("p");
                } else {
                    writer.writeCharacters("");
                    writer.flush();
                    out.append(macroBody);
                }
                writer.writeEndElement();
                writer.writeEndElement();
                writer.writeEndElement();
                writer.close();
            }
            catch (XMLStreamException ex) {
                throw new IOException(ex);
            }
        };
    }

    protected String getCssClass(MacroDefinition definition) {
        return "wysiwyg-macro";
    }

    protected String getImageSource(MacroDefinition definition) {
        return this.placeholderUrlFactory.getUrlForMacroHeading(definition);
    }
}

