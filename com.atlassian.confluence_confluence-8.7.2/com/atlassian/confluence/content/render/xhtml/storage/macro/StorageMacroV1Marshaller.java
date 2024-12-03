/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.storage.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactory;
import com.atlassian.confluence.content.render.xhtml.definition.MacroBody;
import com.atlassian.confluence.content.render.xhtml.definition.PlainTextMacroBody;
import com.atlassian.confluence.content.render.xhtml.storage.macro.StorageMacroConstants;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.lang3.StringUtils;

public class StorageMacroV1Marshaller
implements Marshaller<MacroDefinition> {
    private final XmlOutputFactory xmlOutputFactory;

    public StorageMacroV1Marshaller(XmlOutputFactory xmlOutputFactory) {
        this.xmlOutputFactory = xmlOutputFactory;
    }

    @Override
    public Streamable marshal(MacroDefinition macroDefinition, ConversionContext conversionContext) throws XhtmlException {
        return out -> {
            try {
                this.marshal(macroDefinition, out);
            }
            catch (XhtmlException | XMLStreamException e) {
                throw new IOException(e);
            }
        };
    }

    private void marshal(MacroDefinition macroDefinition, Writer out) throws XMLStreamException, XhtmlException, IOException {
        XMLStreamWriter writer = this.xmlOutputFactory.createXMLStreamWriter(out);
        this.writeStartElement(writer, StorageMacroConstants.MACRO_ELEMENT);
        this.writeAttribute(writer, StorageMacroConstants.NAME_ATTRIBUTE, macroDefinition.getName());
        this.writeParameters(macroDefinition, writer);
        this.writeBody(macroDefinition, writer, out);
        writer.writeEndElement();
        writer.close();
    }

    private void writeParameters(MacroDefinition macroDefinition, XMLStreamWriter writer) throws XMLStreamException {
        if (macroDefinition.getTypedParameters() != null) {
            for (Map.Entry<String, Object> parameter : macroDefinition.getTypedParameters().entrySet()) {
                if (parameter.getKey().isEmpty()) continue;
                this.writeStartElement(writer, StorageMacroConstants.MACRO_PARAMETER_ELEMENT);
                this.writeAttribute(writer, StorageMacroConstants.NAME_ATTRIBUTE, parameter.getKey());
                this.writeParameterValue(writer, macroDefinition.getParameter(parameter.getKey()));
                writer.writeEndElement();
            }
        }
        if (macroDefinition.getDefaultParameterValue() != null) {
            this.writeStartElement(writer, StorageMacroConstants.DEFAULT_PARAMETER_ELEMENT);
            this.writeParameterValue(writer, macroDefinition.getDefaultParameterValue());
            writer.writeEndElement();
        }
    }

    private void writeBody(MacroDefinition macroDefinition, XMLStreamWriter writer, Writer out) throws XMLStreamException, IOException {
        if (macroDefinition.getBody() != null && !macroDefinition.getBodyText().isEmpty()) {
            MacroBody macroBody = macroDefinition.getBody();
            if (macroBody instanceof PlainTextMacroBody) {
                this.writeStartElement(writer, StorageMacroConstants.PLAIN_TEXT_BODY_PARAMETER_ELEMENT);
                for (String s : StaxUtils.splitCData(StringUtils.defaultString((String)macroDefinition.getBodyText()))) {
                    writer.writeCData(s);
                }
            } else {
                this.writeStartElement(writer, StorageMacroConstants.RICH_TEXT_BODY_PARAMETER_ELEMENT);
                this.writeRawXML(writer, out, macroBody.getBodyStream());
            }
            writer.writeEndElement();
        }
    }

    private void writeParameterValue(XMLStreamWriter streamWriter, String rawValue) throws XMLStreamException {
        this.writeSimpleStringParameter(streamWriter, rawValue);
    }

    private void writeStartElement(XMLStreamWriter writer, QName element) throws XMLStreamException {
        writer.writeStartElement(element.getPrefix(), element.getLocalPart(), element.getNamespaceURI());
    }

    private void writeAttribute(XMLStreamWriter writer, QName attribute, String value) throws XMLStreamException {
        writer.writeAttribute(attribute.getPrefix(), attribute.getNamespaceURI(), attribute.getLocalPart(), value);
    }

    private void writeRawXML(XMLStreamWriter writer, Writer out, Streamable streamable) throws IOException, XMLStreamException {
        writer.writeCharacters("");
        writer.flush();
        streamable.writeTo(out);
    }

    private void writeSimpleStringParameter(XMLStreamWriter writer, Object value) throws XMLStreamException {
        if (value == null) {
            return;
        }
        String s = value.toString();
        if (StringUtils.isNotBlank((CharSequence)s)) {
            writer.writeCharacters(s);
        }
    }
}

