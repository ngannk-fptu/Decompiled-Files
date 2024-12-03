/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.editor.macro;

import com.atlassian.confluence.content.render.image.ImageDimensions;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.editor.macro.CommonMacroAttributeWriter;
import com.atlassian.confluence.content.render.xhtml.editor.macro.EditorBodilessMacroMarshaller;
import com.atlassian.confluence.content.render.xhtml.editor.macro.MacroMarshaller;
import com.atlassian.confluence.content.render.xhtml.editor.macro.PlaceholderUrlFactory;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.macro.EditorImagePlaceholder;
import com.atlassian.confluence.macro.ImagePlaceholder;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.util.UrlUtils;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.lang3.StringUtils;

public class CustomImageEditorMacroMarshaller
implements MacroMarshaller {
    private final CommonMacroAttributeWriter commonAttributeWriter;
    private final PlaceholderUrlFactory placeholderUrlFactory;
    private final XMLOutputFactory xmlOutputFactory;
    private final ContextPathHolder contextPathHolder;
    private EditorBodilessMacroMarshaller editorBodilessMacroMarshaller;

    public CustomImageEditorMacroMarshaller(CommonMacroAttributeWriter commonAttributeWriter, ContextPathHolder contextPathHolder, PlaceholderUrlFactory placeholderUrlFactory, XMLOutputFactory xmlOutputFactory) {
        this.commonAttributeWriter = commonAttributeWriter;
        this.contextPathHolder = contextPathHolder;
        this.placeholderUrlFactory = placeholderUrlFactory;
        this.xmlOutputFactory = xmlOutputFactory;
        this.editorBodilessMacroMarshaller = new EditorBodilessMacroMarshaller(commonAttributeWriter, placeholderUrlFactory, xmlOutputFactory);
    }

    @Override
    public boolean handles(Macro macro) {
        return macro != null && macro instanceof EditorImagePlaceholder;
    }

    @Override
    public Streamable marshal(Macro macro, MacroDefinition macroDefinition, ConversionContext conversionContext) throws XhtmlException {
        ImagePlaceholder placeholder = this.getImagePlaceholder((EditorImagePlaceholder)((Object)macro), macroDefinition, conversionContext);
        if (placeholder == null) {
            return this.editorBodilessMacroMarshaller.marshal(macro, macroDefinition, conversionContext);
        }
        return out -> {
            try {
                XMLStreamWriter writer = this.xmlOutputFactory.createXMLStreamWriter(out);
                writer.writeStartElement("img");
                ArrayList<String> cssClasses = new ArrayList<String>();
                cssClasses.add("editor-inline-macro");
                if (placeholder.applyPlaceholderChrome()) {
                    cssClasses.add("with-chrome");
                    writer.writeAttribute("style", "background-image: url(" + this.placeholderUrlFactory.getUrlForMacroHeading(macroDefinition) + "); background-repeat: no-repeat;");
                }
                writer.writeAttribute("class", StringUtils.join((Object[])cssClasses.toArray(), (String)" "));
                ImageDimensions dimensions = placeholder.getImageDimensions();
                if (dimensions != null) {
                    if (dimensions.getHeight() >= 0) {
                        writer.writeAttribute("height", String.valueOf(dimensions.getHeight()));
                    }
                    if (dimensions.getWidth() >= 0) {
                        writer.writeAttribute("width", String.valueOf(dimensions.getWidth()));
                    }
                }
                String imgUrl = UrlUtils.addContextPath(placeholder.getUrl(), this.contextPathHolder);
                writer.writeAttribute("src", imgUrl);
                this.commonAttributeWriter.writeCommonAttributes(macroDefinition, writer);
                writer.writeCharacters("");
                writer.flush();
            }
            catch (XMLStreamException ex) {
                throw new IOException(ex);
            }
        };
    }

    private ImagePlaceholder getImagePlaceholder(EditorImagePlaceholder macro, MacroDefinition macroDefinition, ConversionContext conversionContext) {
        HashMap<String, String> macroParams = new HashMap<String, String>(macroDefinition.getParameters());
        if (macroDefinition.getDefaultParameterValue() != null) {
            macroParams.put("0", macroDefinition.getDefaultParameterValue());
        }
        ImagePlaceholder imagePlaceholder = macro.getImagePlaceholder(macroParams, conversionContext);
        return imagePlaceholder;
    }
}

