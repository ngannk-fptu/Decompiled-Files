/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.editor.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.editor.macro.CommonMacroAttributeWriter;
import com.atlassian.confluence.content.render.xhtml.editor.macro.MacroMarshaller;
import com.atlassian.confluence.content.render.xhtml.editor.macro.PlaceholderUrlFactory;
import com.atlassian.confluence.macro.CustomHtmlEditorPlaceholder;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.lang3.StringUtils;

public class CustomPlaceholderEditorMarshaller
implements MacroMarshaller {
    private static final Pattern OBJECT_TAG = Pattern.compile("<object\\s+", 2);
    private static final Pattern IMG_TAG = Pattern.compile("<img\\s+", 2);
    private final CommonMacroAttributeWriter commonAttributeWriter;
    private final PlaceholderUrlFactory placeholderUrlFactory;
    private final XMLOutputFactory xmlOutputFactory;

    public CustomPlaceholderEditorMarshaller(CommonMacroAttributeWriter commonAttributeWriter, PlaceholderUrlFactory placeholderUrlFactory, XMLOutputFactory xmlOutputFactory) {
        this.commonAttributeWriter = commonAttributeWriter;
        this.placeholderUrlFactory = placeholderUrlFactory;
        this.xmlOutputFactory = xmlOutputFactory;
    }

    @Override
    public boolean handles(Macro macro) {
        return macro != null && macro instanceof CustomHtmlEditorPlaceholder;
    }

    @Override
    public Streamable marshal(Macro macro, MacroDefinition macroDefinition, ConversionContext conversionContext) throws XhtmlException {
        String html;
        try {
            html = ((CustomHtmlEditorPlaceholder)((Object)macro)).getCustomPlaceholder(macroDefinition.getParameters(), macroDefinition.getBodyText(), conversionContext);
        }
        catch (CustomHtmlEditorPlaceholder.PlaceholderGenerationException ex) {
            throw new XhtmlException(ex);
        }
        html = StringUtils.trim((String)html);
        String htmlStart = StringUtils.lowerCase((String)StringUtils.substring((String)html, (int)0, (int)8));
        if (StringUtils.isBlank((CharSequence)htmlStart) || !htmlStart.startsWith("<object ") && !htmlStart.startsWith("<img ")) {
            throw new XhtmlException("The custom HTML place holder was not an <img> or <object>");
        }
        return Streamables.from(this.decorateCustomHtml(html, macroDefinition));
    }

    private String decorateCustomHtml(String html, MacroDefinition macroDefinition) {
        String attrs = StringUtils.defaultString((String)this.generateCommonMacroAttributes(macroDefinition)) + " " + StringUtils.defaultString((String)this.getChromeAttributes(macroDefinition)) + " ";
        Matcher matcher = OBJECT_TAG.matcher(html);
        html = matcher.replaceAll("<object " + attrs);
        matcher = IMG_TAG.matcher(html);
        html = matcher.replaceAll("<img " + attrs);
        return html;
    }

    private String generateCommonMacroAttributes(MacroDefinition macroDefinition) {
        StringWriter writer = new StringWriter();
        try {
            XMLStreamWriter xmlWriter = this.xmlOutputFactory.createXMLStreamWriter(writer);
            xmlWriter.writeStartElement("a");
            this.commonAttributeWriter.writeCommonAttributes(macroDefinition, xmlWriter);
            xmlWriter.writeCharacters("");
            xmlWriter.flush();
        }
        catch (XMLStreamException xMLStreamException) {
            // empty catch block
        }
        String aTag = writer.toString();
        return StringUtils.substringBetween((String)aTag, (String)"<a ", (String)">");
    }

    private String getChromeAttributes(MacroDefinition macroDefinition) {
        StringBuilder builder = new StringBuilder();
        builder.append("class=\"").append("editor-inline-macro").append(" ").append("with-chrome").append("\" style=\"background-image: url(").append(this.placeholderUrlFactory.getUrlForMacroHeading(macroDefinition)).append("); background-repeat: no-repeat;\"");
        return builder.toString();
    }
}

