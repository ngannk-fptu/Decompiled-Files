/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.xhtml.editor.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.HtmlToXmlConverter;
import com.atlassian.confluence.content.render.xhtml.MacroBodyType;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlConstants;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEntityExpander;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.definition.MacroBody;
import com.atlassian.confluence.content.render.xhtml.definition.PlainTextMacroBody;
import com.atlassian.confluence.content.render.xhtml.definition.RichTextMacroBody;
import com.atlassian.confluence.content.render.xhtml.editor.macro.MacroBodySubParser;
import com.atlassian.confluence.content.render.xhtml.editor.macro.MacroNameAndParameterSubParser;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.MacroDefinitionBuilder;
import com.google.common.base.Strings;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EditorMacroUnmarshaller
implements Unmarshaller<MacroDefinition> {
    private static final Logger log = LoggerFactory.getLogger(EditorMacroUnmarshaller.class);
    private final MacroNameAndParameterSubParser macroNameAndParameterSubParser;
    private final MacroBodySubParser macroBodySubParser;
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final HtmlToXmlConverter htmlToXmlConverter;

    public EditorMacroUnmarshaller(XMLOutputFactory xmlFragmentOutputFactory, XMLEventFactory xmlEventFactory, MacroNameAndParameterSubParser macroNameAndParameterSubParser, XmlEventReaderFactory xmlEventReaderFactory, XmlEntityExpander xmlEntityExpander, HtmlToXmlConverter htmlToXmlConverter) {
        this.macroNameAndParameterSubParser = macroNameAndParameterSubParser;
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.macroBodySubParser = new MacroBodySubParser(xmlFragmentOutputFactory, xmlEventFactory, xmlEntityExpander);
        this.htmlToXmlConverter = htmlToXmlConverter;
    }

    @Override
    public boolean handles(StartElement startElement, ConversionContext conversionContext) {
        Attribute classAttribute = startElement.getAttributeByName(XhtmlConstants.Attribute.CLASS);
        if (classAttribute != null && classAttribute.getValue() != null) {
            Object[] classes = classAttribute.getValue().split(" ");
            return ArrayUtils.contains((Object[])classes, (Object)"editor-inline-macro") || ArrayUtils.contains((Object[])classes, (Object)"wysiwyg-macro") || ArrayUtils.contains((Object[])classes, (Object)"wysiwyg-unknown-macro");
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public MacroDefinition unmarshal(XMLEventReader reader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        try {
            String macroBodyAttributeValue;
            if (reader.peek().isStartElement() && !this.handles(reader.peek().asStartElement(), null)) {
                throw new XhtmlException("The EditorMacroUnmarshaller has been called for the wrong XMLEvent.");
            }
            MacroDefinitionBuilder builder = MacroDefinition.builder().withStorageVersion("2");
            StartElement macroStartElement = reader.peek().asStartElement();
            this.macroNameAndParameterSubParser.parse(macroStartElement, conversionContext, builder);
            Attribute macroBodyTypeAttribute = macroStartElement.getAttributeByName(new QName("data-macro-body-type"));
            MacroBodyType macroBodyType = MacroBodyType.RICH_TEXT;
            if (macroBodyTypeAttribute != null) {
                try {
                    macroBodyType = Enum.valueOf(MacroBodyType.class, macroBodyTypeAttribute.getValue());
                }
                catch (Exception e) {
                    log.debug("Invalid data-macro-body-type attribute value for macro on " + macroStartElement, (Throwable)e);
                }
            }
            if (StringUtils.isNotBlank((CharSequence)(macroBodyAttributeValue = StaxUtils.getAttributeValue(macroStartElement, "data-macro-body")))) {
                try {
                    macroBodyAttributeValue = URLDecoder.decode(macroBodyAttributeValue, "UTF-8");
                }
                catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                builder.withMacroBody(this.getMacroBody(macroBodyType, macroBodyAttributeValue));
            } else if ("table".equalsIgnoreCase(macroStartElement.getName().getLocalPart())) {
                while (!(!reader.hasNext() || reader.peek().isStartElement() && "td".equalsIgnoreCase(reader.peek().asStartElement().getName().getLocalPart()))) {
                    reader.nextEvent();
                }
                if (reader.peek().isStartElement() && "td".equalsIgnoreCase(reader.peek().asStartElement().getName().getLocalPart())) {
                    XMLEventReader fragmentBodyReader = this.xmlEventReaderFactory.createXmlFragmentBodyEventReader(reader);
                    try {
                        this.macroBodySubParser.parse(fragmentBodyReader, builder, mainFragmentTransformer, conversionContext, macroBodyType);
                    }
                    finally {
                        StaxUtils.closeQuietly(fragmentBodyReader);
                    }
                } else {
                    builder.withMacroBody(this.getMacroBody(macroBodyType, ""));
                }
                String schemaStr = StaxUtils.getAttributeValue(macroStartElement, "data-macro-schema-version");
                if (!Strings.isNullOrEmpty((String)schemaStr)) {
                    try {
                        builder.withSchemaVersion(Integer.parseInt(schemaStr));
                    }
                    catch (NumberFormatException ex) {
                        throw new XhtmlException("Unable to determine schema version for macro on : " + macroStartElement, ex);
                    }
                }
            }
            try {
                return builder.build();
            }
            catch (IllegalArgumentException e) {
                throw new XhtmlException("No valid macro definition could be created from the macro definition fragment.");
            }
        }
        catch (XMLStreamException e) {
            throw new XhtmlException(e);
        }
    }

    private MacroBody getMacroBody(MacroBodyType macroBodyType, String macroBodyAttributeValue) {
        if (macroBodyType == MacroBodyType.PLAIN_TEXT) {
            return new PlainTextMacroBody(macroBodyAttributeValue);
        }
        String xmlEditorFormat = this.htmlToXmlConverter.convert(macroBodyAttributeValue);
        return RichTextMacroBody.withStorageAndTransform(Streamables.from(macroBodyAttributeValue), Streamables.from(xmlEditorFormat));
    }
}

