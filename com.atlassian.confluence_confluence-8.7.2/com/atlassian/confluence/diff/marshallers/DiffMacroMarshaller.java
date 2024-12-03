/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Maps
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.diff.marshallers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.xhtml.MacroManager;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.lang3.StringUtils;

public class DiffMacroMarshaller
implements Marshaller<MacroDefinition> {
    private final MacroManager macroManager;
    private final XMLOutputFactory xmlOutputFactory;
    public static final String MACRO_CLASS = "diff-macro";
    public static final String BODYLESS_MACRO_CLASS = "bodyless";
    private static final Predicate<String> notMacroOutputType = s -> !"atlassian-macro-output-type".equals(s);

    public DiffMacroMarshaller(MacroManager macroManager, XMLOutputFactory xmlOutputFactory) {
        this.macroManager = macroManager;
        this.xmlOutputFactory = xmlOutputFactory;
    }

    @Override
    public Streamable marshal(MacroDefinition macroDefinition, ConversionContext conversionContext) throws XhtmlException {
        return out -> {
            try {
                XMLStreamWriter writer = this.xmlOutputFactory.createXMLStreamWriter(out);
                Macro macro = this.macroManager.getMacroByName(macroDefinition.getName());
                if (macro != null) {
                    writer.writeStartElement("table");
                    Object macroClasses = MACRO_CLASS;
                    if (macro.getBodyType() == Macro.BodyType.NONE) {
                        macroClasses = (String)macroClasses + " bodyless";
                    }
                    writer.writeAttribute("class", (String)macroClasses);
                    this.writeTitle(macroDefinition, writer);
                    this.writeMacroAttributes(macroDefinition, writer);
                    if (macro.getBodyType() != Macro.BodyType.NONE) {
                        boolean isPlainText = Macro.BodyType.PLAIN_TEXT.equals((Object)macro.getBodyType());
                        writer.writeStartElement("tbody");
                        writer.writeStartElement("tr");
                        writer.writeStartElement("td");
                        writer.writeAttribute("class", "diff-macro-body");
                        writer.writeCharacters("");
                        writer.flush();
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
                    }
                    writer.writeEndElement();
                }
                writer.flush();
                writer.close();
            }
            catch (XMLStreamException e) {
                throw new IOException(e);
            }
        };
    }

    private void writeTitle(MacroDefinition macroDefinition, XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement("thead");
        writer.writeStartElement("tr");
        writer.writeStartElement("th");
        writer.writeAttribute("class", "diff-macro-title");
        writer.writeCharacters(macroDefinition.getName());
        writer.writeEndElement();
        writer.writeEndElement();
        writer.writeEndElement();
    }

    private void writeMacroAttributes(MacroDefinition macroDefinition, XMLStreamWriter writer) throws XMLStreamException {
        Map<String, String> parameters = macroDefinition.getParameters();
        if ((parameters = this.filterOutHiddenParameters(parameters)) == null) {
            parameters = Collections.emptyMap();
        }
        if (StringUtils.isNotBlank((CharSequence)macroDefinition.getDefaultParameterValue()) || !parameters.isEmpty()) {
            writer.writeStartElement("tbody");
            writer.writeStartElement("tr");
            writer.writeStartElement("td");
            writer.writeAttribute("class", "diff-macro-properties");
            writer.writeStartElement("table");
            this.writeDefaultParameter(macroDefinition, writer);
            for (Map.Entry<String, String> parameter : parameters.entrySet()) {
                this.writeParameter(parameter.getKey(), parameter.getValue(), writer);
            }
            writer.writeEndElement();
            writer.writeEndElement();
            writer.writeEndElement();
            writer.writeEndElement();
        }
    }

    Map<String, String> filterOutHiddenParameters(Map<String, String> parameters) {
        return parameters == null ? null : Maps.filterKeys(parameters, notMacroOutputType);
    }

    void writeParameter(String key, String value, XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement("tr");
        writer.writeStartElement("th");
        writer.writeCharacters(key);
        writer.writeEndElement();
        writer.writeStartElement("td");
        writer.writeCharacters(value);
        writer.writeEndElement();
        writer.writeEndElement();
    }

    void writeDefaultParameter(MacroDefinition macroDefinition, XMLStreamWriter writer) throws XMLStreamException {
        String defaultParameterValue = macroDefinition.getDefaultParameterValue();
        if (StringUtils.isNotBlank((CharSequence)defaultParameterValue)) {
            this.writeParameter("", defaultParameterValue, writer);
        }
    }
}

