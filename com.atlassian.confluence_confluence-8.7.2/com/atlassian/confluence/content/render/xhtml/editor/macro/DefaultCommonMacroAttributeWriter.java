/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.collect.Maps
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.editor.macro;

import com.atlassian.confluence.content.render.xhtml.editor.macro.CommonMacroAttributeWriter;
import com.atlassian.confluence.content.render.xhtml.editor.macro.MacroParameterSerializer;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.lang3.StringUtils;

class DefaultCommonMacroAttributeWriter
implements CommonMacroAttributeWriter {
    private final MacroParameterSerializer macroParameterSerializer;

    public DefaultCommonMacroAttributeWriter(MacroParameterSerializer macroParameterSerializer) {
        this.macroParameterSerializer = macroParameterSerializer;
    }

    @Override
    public void writeCommonAttributes(MacroDefinition macroDefinition, XMLStreamWriter writer) throws XMLStreamException {
        Macro.BodyType bodyType;
        writer.writeAttribute("data-macro-name", macroDefinition.getName());
        if (macroDefinition.getMacroIdentifier().isPresent()) {
            writer.writeAttribute("data-macro-id", macroDefinition.getMacroIdentifier().get().getId());
        }
        if ((bodyType = macroDefinition.getBodyType()).equals((Object)Macro.BodyType.NONE)) {
            writer.writeAttribute("role", "button");
            writer.writeAttribute("tabindex", "0");
            writer.writeAttribute("aria-haspopup", "true");
        }
        Map parameters = Maps.filterKeys(macroDefinition.getParameters(), DefaultCommonMacroAttributeWriter.isNotBlank());
        this.writeAriaLabel(macroDefinition, writer, parameters);
        String encodedParameters = this.macroParameterSerializer.serialize(parameters);
        if (StringUtils.isNotBlank((CharSequence)encodedParameters)) {
            writer.writeAttribute("data-macro-parameters", encodedParameters);
        }
        if (macroDefinition.getDefaultParameterValue() != null) {
            writer.writeAttribute("data-macro-default-parameter", macroDefinition.getDefaultParameterValue());
        }
        if (macroDefinition.getSchemaVersion() > 0) {
            writer.writeAttribute("data-macro-schema-version", Integer.toString(macroDefinition.getSchemaVersion()));
        }
    }

    private void writeAriaLabel(MacroDefinition macroDefinition, XMLStreamWriter writer, Map<String, String> parametersMap) throws XMLStreamException {
        if (macroDefinition.getName().equals("status") && parametersMap.containsKey("title")) {
            writer.writeAttribute("aria-label", HtmlUtil.htmlEncode(parametersMap.get("title")) + " " + macroDefinition.getName() + " macro");
            return;
        }
        writer.writeAttribute("aria-label", macroDefinition.getName() + " macro");
    }

    private static Predicate<String> isNotBlank() {
        return StringUtils::isNotBlank;
    }
}

