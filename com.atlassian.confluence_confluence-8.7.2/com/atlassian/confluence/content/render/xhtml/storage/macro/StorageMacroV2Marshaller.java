/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.xhtml.storage.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactory;
import com.atlassian.confluence.content.render.xhtml.definition.MacroBody;
import com.atlassian.confluence.content.render.xhtml.definition.PlainTextMacroBody;
import com.atlassian.confluence.content.render.xhtml.definition.RichTextMacroBody;
import com.atlassian.confluence.content.render.xhtml.editor.macro.InvalidMacroParameterException;
import com.atlassian.confluence.content.render.xhtml.editor.macro.MacroIdSupplier;
import com.atlassian.confluence.content.render.xhtml.editor.macro.MacroParameterTypeParser;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.storage.macro.MacroId;
import com.atlassian.confluence.content.render.xhtml.storage.macro.StorageMacroConstants;
import com.atlassian.confluence.impl.macro.schema.MacroSchemaMigrator;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.browser.MacroMetadataManager;
import com.atlassian.confluence.macro.browser.beans.MacroParameter;
import com.atlassian.confluence.macro.browser.beans.MacroParameterType;
import com.atlassian.confluence.macro.xhtml.MacroManager;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageMacroV2Marshaller
implements Marshaller<MacroDefinition> {
    private static final Logger log = LoggerFactory.getLogger(StorageMacroV2Marshaller.class);
    private final XmlOutputFactory xmlOutputFactory;
    private final MacroMetadataManager macroMetadataManager;
    private final Marshaller<ResourceIdentifier> resourceIdentifierMarshaller;
    private final Marshaller<Link> linkMarshaller;
    private final MacroParameterTypeParser macroParameterTypeParser;
    private final MacroIdSupplier macroIdSupplier;
    private final MacroSchemaMigrator macroSchemaMigrator;
    private final MacroManager macroManager;

    public StorageMacroV2Marshaller(XmlOutputFactory xmlOutputFactory, MacroMetadataManager macroMetadataManager, Marshaller<ResourceIdentifier> resourceIdentifierMarshaller, Marshaller<Link> linkMarshaller, MacroParameterTypeParser macroParameterTypeParser, MacroIdSupplier macroIdSupplier, MacroSchemaMigrator macroSchemaMigrator, MacroManager macroManager) {
        this.xmlOutputFactory = xmlOutputFactory;
        this.macroMetadataManager = macroMetadataManager;
        this.resourceIdentifierMarshaller = resourceIdentifierMarshaller;
        this.linkMarshaller = linkMarshaller;
        this.macroParameterTypeParser = macroParameterTypeParser;
        this.macroIdSupplier = macroIdSupplier;
        this.macroSchemaMigrator = macroSchemaMigrator;
        this.macroManager = macroManager;
    }

    @Override
    public Streamable marshal(MacroDefinition originalMacroDefinition, ConversionContext conversionContext) {
        return out -> {
            try {
                this.marshal(originalMacroDefinition, conversionContext, out);
            }
            catch (XhtmlException | InvalidMacroParameterException | XMLStreamException e) {
                throw new IOException(e);
            }
        };
    }

    private void marshal(MacroDefinition originalMacroDefinition, ConversionContext conversionContext, Writer out) throws XhtmlException, XMLStreamException, InvalidMacroParameterException, IOException {
        MacroDefinition migratedMacroDefinition = this.getMigratedDefinition(originalMacroDefinition, conversionContext);
        XMLStreamWriter writer = this.xmlOutputFactory.createXMLStreamWriter(out);
        StaxUtils.writeStartElement(writer, StorageMacroConstants.MACRO_V2_ELEMENT);
        String macroName = migratedMacroDefinition.getName();
        StaxUtils.writeAttribute(writer, StorageMacroConstants.NAME_ATTRIBUTE, macroName);
        StaxUtils.writeAttribute(writer, StorageMacroConstants.MACRO_SCHEMA_VERSION_ATTRIBUTE, Integer.toString(migratedMacroDefinition.getSchemaVersion()));
        this.writeMacroId(migratedMacroDefinition, writer, conversionContext);
        this.writeParameters(macroName, migratedMacroDefinition, conversionContext, out, writer);
        this.writeBody(macroName, migratedMacroDefinition, out, writer);
        writer.writeEndElement();
        writer.close();
    }

    private void writeParameters(String macroName, MacroDefinition migratedMacroDefinition, ConversionContext conversionContext, Writer out, XMLStreamWriter writer) throws XMLStreamException, InvalidMacroParameterException, XhtmlException, IOException {
        Map<String, MacroParameterType> parameterTypes = this.macroMetadataManager.getParameterTypes(macroName);
        Map<String, Object> typedParameters = migratedMacroDefinition.getTypedParameters();
        if (typedParameters == null || typedParameters.isEmpty()) {
            typedParameters = this.macroParameterTypeParser.parseMacroParameters(macroName, migratedMacroDefinition.getParameters(), conversionContext);
        }
        Map<String, MacroParameter> macroParameterMap = this.macroMetadataManager.getParameters(macroName);
        for (Map.Entry<String, Object> parameter : typedParameters.entrySet()) {
            MacroParameter macroParameter = macroParameterMap.get(parameter.getKey());
            if (macroParameter != null && macroParameter.getOptions().get("derived") != null) continue;
            StaxUtils.writeStartElement(writer, StorageMacroConstants.MACRO_PARAMETER_ELEMENT);
            StaxUtils.writeAttribute(writer, StorageMacroConstants.NAME_ATTRIBUTE, parameter.getKey());
            this.writeParameterValue(writer, out, parameterTypes.get(parameter.getKey()), parameter.getValue(), conversionContext, macroName);
            writer.writeEndElement();
        }
    }

    private void writeBody(String macroName, MacroDefinition migratedMacroDefinition, Writer out, XMLStreamWriter writer) throws XMLStreamException, IOException {
        MacroBody macroBody;
        Macro macro = this.macroManager.getMacroByName(macroName);
        Macro.BodyType bodyType = Macro.BodyType.RICH_TEXT;
        if (macro != null) {
            bodyType = macro.getBodyType();
        }
        if ((macroBody = migratedMacroDefinition.getBody()) != null) {
            Streamable bodyStream = macroBody.getBodyStream();
            if (macroBody instanceof PlainTextMacroBody && Macro.BodyType.PLAIN_TEXT.equals((Object)bodyType)) {
                String bodyText = Streamables.writeToString(bodyStream);
                if (StringUtils.isNotBlank((CharSequence)bodyText)) {
                    StaxUtils.writeStartElement(writer, StorageMacroConstants.PLAIN_TEXT_BODY_PARAMETER_ELEMENT);
                    for (String s : StaxUtils.splitCData(bodyText)) {
                        writer.writeCData(s);
                    }
                    writer.writeEndElement();
                }
            } else if (macroBody instanceof RichTextMacroBody && Macro.BodyType.RICH_TEXT.equals((Object)bodyType)) {
                StaxUtils.writeStartElement(writer, StorageMacroConstants.RICH_TEXT_BODY_PARAMETER_ELEMENT);
                StaxUtils.writeRawXML(writer, out, bodyStream);
                writer.writeEndElement();
            }
        }
    }

    private MacroDefinition getMigratedDefinition(MacroDefinition originalMacroDefinition, ConversionContext conversionContext) {
        MacroDefinition migratedMacroDefinition;
        try {
            migratedMacroDefinition = this.macroSchemaMigrator.migrateSchemaIfNecessary(originalMacroDefinition, conversionContext);
        }
        catch (XhtmlException e) {
            log.warn("Unable to migrate macro during Storage marshall", (Throwable)e);
            migratedMacroDefinition = originalMacroDefinition;
        }
        return migratedMacroDefinition;
    }

    private void writeMacroId(MacroDefinition definition, XMLStreamWriter writer, ConversionContext conversionContext) throws XMLStreamException {
        MacroId macroId;
        if (!conversionContext.hasProperty("macroIdList")) {
            conversionContext.setProperty("macroIdList", new HashSet());
        }
        Set uuidList = (Set)conversionContext.getProperty("macroIdList");
        if (definition.getMacroIdentifier().isPresent() && uuidList.contains(definition.getMacroIdentifier().get())) {
            log.debug("detected duplicated macroId [ {} ] ", (Object)definition.getMacroIdentifier().get());
            macroId = this.getUniqueMacroId(uuidList);
        } else if (definition.getMacroIdentifier().isEmpty()) {
            log.debug("detected missing macroId for macro [ {} ]", (Object)definition.getName());
            macroId = this.getUniqueMacroId(uuidList);
        } else {
            macroId = definition.getMacroIdentifier().get();
        }
        StaxUtils.writeAttribute(writer, StorageMacroConstants.MACRO_ID_ATTRIBUTE, macroId.getId());
        uuidList.add(macroId);
    }

    private MacroId getUniqueMacroId(Set<MacroId> uuidList) {
        MacroId macroId;
        int count = 0;
        do {
            macroId = this.macroIdSupplier.get();
        } while (++count < 10 && uuidList.contains(macroId));
        if (uuidList.contains(macroId)) {
            throw new IllegalStateException("macroIdSupplier cannot generate unique macroId");
        }
        return macroId;
    }

    private void writeParameterValue(XMLStreamWriter streamWriter, Writer out, MacroParameterType parameterType, Object value, ConversionContext conversionContext, String macroName) throws IOException, XMLStreamException, XhtmlException {
        if (value instanceof Iterable) {
            for (Object o : (Iterable)value) {
                this.writeParameterValue(streamWriter, out, parameterType, o, conversionContext, macroName);
            }
            return;
        }
        if (parameterType == null) {
            if (!(value instanceof String) && value != null) {
                log.warn("Unhandled parameterType {} for macro {} with value of {}", new Object[]{value.getClass().getName(), macroName, value});
            }
            this.writeSimpleStringParameter(streamWriter, value);
            return;
        }
        switch (parameterType) {
            case ATTACHMENT: 
            case SPACE_KEY: 
            case URL: 
            case USERNAME: 
            case FULL_ATTACHMENT: {
                if (value == null) break;
                StaxUtils.writeRawXML(streamWriter, out, this.resourceIdentifierMarshaller.marshal((ResourceIdentifier)value, conversionContext));
                break;
            }
            case CONFLUENCE_CONTENT: {
                if (value == null) break;
                StaxUtils.writeRawXML(streamWriter, out, this.linkMarshaller.marshal((Link)value, conversionContext));
                break;
            }
            default: {
                this.writeSimpleStringParameter(streamWriter, value);
            }
        }
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

