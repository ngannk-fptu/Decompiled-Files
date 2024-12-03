/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.editor.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.editor.macro.InvalidMacroParameterException;
import com.atlassian.confluence.content.render.xhtml.editor.macro.MacroParameterSerializer;
import com.atlassian.confluence.content.render.xhtml.editor.macro.MacroParameterTypeParser;
import com.atlassian.confluence.content.render.xhtml.storage.macro.MacroId;
import com.atlassian.confluence.xhtml.api.MacroDefinitionBuilder;
import java.util.Collections;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

class MacroNameAndParameterSubParser {
    private final MacroParameterSerializer macroParameterSerializer;
    private final MacroParameterTypeParser macroParameterTypeParser;

    public MacroNameAndParameterSubParser(MacroParameterSerializer macroParameterSerializer, MacroParameterTypeParser macroParameterTypeParser) {
        this.macroParameterSerializer = macroParameterSerializer;
        this.macroParameterTypeParser = macroParameterTypeParser;
    }

    public void parse(XMLEvent event, ConversionContext conversionContext, MacroDefinitionBuilder macroDefinitionBuilder) throws XhtmlException {
        Attribute macroId;
        StartElement startElement = event.asStartElement();
        Attribute macroNameAttribute = startElement.getAttributeByName(new QName("data-macro-name"));
        if (macroNameAttribute == null) {
            throw new XhtmlException("The macro name was not found in the macro definition.");
        }
        String macroName = macroNameAttribute.getValue();
        macroDefinitionBuilder.withName(macroName);
        Map<String, Object> typedParameters = Collections.emptyMap();
        Map<String, String> untypedParameters = Collections.emptyMap();
        Attribute macroParametersAttribute = startElement.getAttributeByName(new QName("data-macro-parameters"));
        if (macroParametersAttribute != null) {
            untypedParameters = this.macroParameterSerializer.deserialize(macroParametersAttribute.getValue());
            try {
                typedParameters = this.macroParameterTypeParser.parseMacroParameters(macroName, untypedParameters, conversionContext);
            }
            catch (InvalidMacroParameterException e) {
                throw new XhtmlException(e);
            }
        }
        macroDefinitionBuilder.withParameters(untypedParameters);
        macroDefinitionBuilder.withTypedParameters(typedParameters);
        Attribute macroDefaultParameterAttribute = startElement.getAttributeByName(new QName("data-macro-default-parameter"));
        if (macroDefaultParameterAttribute != null) {
            macroDefinitionBuilder.withParameter("", macroDefaultParameterAttribute.getValue());
            try {
                Object parsedMacroParameter = this.macroParameterTypeParser.parseMacroParameter(macroName, "", macroDefaultParameterAttribute.getValue(), untypedParameters, conversionContext);
                if (parsedMacroParameter != null) {
                    macroDefinitionBuilder.withTypedParameter("", parsedMacroParameter);
                }
            }
            catch (InvalidMacroParameterException e) {
                throw new XhtmlException(e);
            }
        }
        if ((macroId = event.asStartElement().getAttributeByName(new QName("data-macro-id"))) != null) {
            macroDefinitionBuilder.withMacroIdentifier(MacroId.fromString(macroId.getValue()));
        } else {
            macroDefinitionBuilder.withMacroIdentifier((MacroId)null);
        }
        Attribute macroSchemaVersionAttribute = startElement.getAttributeByName(new QName("data-macro-schema-version"));
        int schemaVersion = 1;
        if (macroSchemaVersionAttribute != null) {
            String value = macroSchemaVersionAttribute.getValue();
            try {
                schemaVersion = Integer.parseInt(value);
            }
            catch (NumberFormatException e) {
                throw new XhtmlException("The macro schema version must be a number, but received: " + value);
            }
        }
        macroDefinitionBuilder.withSchemaVersion(schemaVersion);
    }
}

