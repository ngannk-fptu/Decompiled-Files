/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.editor.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.MacroBodyType;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.definition.PlainTextMacroBody;
import com.atlassian.confluence.content.render.xhtml.editor.macro.CommonMacroAttributeWriter;
import com.atlassian.confluence.content.render.xhtml.editor.macro.MacroMarshaller;
import com.atlassian.confluence.content.render.xhtml.editor.macro.PlaceholderUrlFactory;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class EditorBodilessMacroMarshaller
implements MacroMarshaller {
    protected final CommonMacroAttributeWriter commonAttributeWriter;
    protected final PlaceholderUrlFactory placeholderUrlFactory;
    protected final XMLOutputFactory xmlOutputFactory;

    public EditorBodilessMacroMarshaller(CommonMacroAttributeWriter commonAttributeWriter, PlaceholderUrlFactory placeholderUrlFactory, XMLOutputFactory xmlOutputFactory) {
        this.commonAttributeWriter = commonAttributeWriter;
        this.placeholderUrlFactory = placeholderUrlFactory;
        this.xmlOutputFactory = xmlOutputFactory;
    }

    @Override
    public boolean handles(Macro macro) {
        return macro != null && macro.getBodyType() == Macro.BodyType.NONE;
    }

    @Override
    public Streamable marshal(Macro macro, MacroDefinition macroDefinition, ConversionContext conversionContext) throws XhtmlException {
        return out -> {
            try {
                XMLStreamWriter writer = this.xmlOutputFactory.createXMLStreamWriter(out);
                writer.writeStartElement("img");
                writer.writeAttribute("class", this.getCssClass(macroDefinition));
                writer.writeAttribute("src", this.getImageSource(macroDefinition));
                this.commonAttributeWriter.writeCommonAttributes(macroDefinition, writer);
                if (macroDefinition.hasBody()) {
                    try {
                        writer.writeAttribute("data-macro-body", URLEncoder.encode(macroDefinition.getStorageBodyText(), "UTF-8"));
                    }
                    catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                    if (macroDefinition.getBody() instanceof PlainTextMacroBody) {
                        writer.writeAttribute("data-macro-body-type", MacroBodyType.PLAIN_TEXT.name());
                    }
                }
                writer.writeCharacters("");
                writer.flush();
            }
            catch (XMLStreamException ex) {
                throw new IOException(ex);
            }
        };
    }

    protected String getCssClass(MacroDefinition definition) {
        return "editor-inline-macro";
    }

    protected String getImageSource(MacroDefinition definition) {
        return this.placeholderUrlFactory.getUrlForMacro(definition);
    }
}

