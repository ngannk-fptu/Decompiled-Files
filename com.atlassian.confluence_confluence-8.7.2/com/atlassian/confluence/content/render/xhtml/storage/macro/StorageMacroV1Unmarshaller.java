/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.xhtml.storage.macro;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Unmarshaller;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.definition.MacroBody;
import com.atlassian.confluence.content.render.xhtml.editor.macro.InvalidMacroParameterException;
import com.atlassian.confluence.content.render.xhtml.editor.macro.MacroParameterTypeParser;
import com.atlassian.confluence.content.render.xhtml.storage.macro.PlainTextMacroBodySubParser;
import com.atlassian.confluence.content.render.xhtml.storage.macro.StorageMacroBodyParser;
import com.atlassian.confluence.content.render.xhtml.storage.macro.StorageMacroConstants;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import java.util.HashMap;
import java.util.Map;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageMacroV1Unmarshaller
implements Unmarshaller<MacroDefinition> {
    private static final Logger log = LoggerFactory.getLogger(StorageMacroV1Unmarshaller.class);
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final MacroParameterTypeParser macroParameterTypeParser;
    private final StorageMacroBodyParser storageMacroBodyParser;

    public StorageMacroV1Unmarshaller(XmlEventReaderFactory xmlEventReaderFactory, MacroParameterTypeParser macroParameterTypeParser, StorageMacroBodyParser storageMacroBodyParser) {
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.macroParameterTypeParser = macroParameterTypeParser;
        this.storageMacroBodyParser = storageMacroBodyParser;
    }

    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        return StorageMacroConstants.MACRO_ELEMENT.equals(startElementEvent.getName());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public MacroDefinition unmarshal(XMLEventReader reader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        try {
            StartElement macroElementEvent = (StartElement)reader.nextEvent();
            Attribute macroNameAttribute = macroElementEvent.getAttributeByName(StorageMacroConstants.NAME_ATTRIBUTE);
            String macroName = macroNameAttribute.getValue();
            MacroBody macroBody = null;
            String macroDefaultParameter = null;
            HashMap<String, String> macroParameters = new HashMap<String, String>();
            HashMap<String, Object> typedMacroParameters = new HashMap<String, Object>();
            while (reader.hasNext()) {
                if (reader.peek().isStartElement()) {
                    XMLEventReader bodyReader;
                    StartElement startElement = reader.peek().asStartElement();
                    if (StorageMacroConstants.MACRO_PARAMETER_ELEMENT.equals(startElement.getName())) {
                        Attribute nameAttribute = startElement.getAttributeByName(StorageMacroConstants.NAME_ATTRIBUTE);
                        String parameterName = nameAttribute.getValue();
                        String parameterValue = this.parseV1Parameter(reader, conversionContext, typedMacroParameters, macroName, parameterName);
                        macroParameters.put(parameterName, parameterValue);
                        continue;
                    }
                    if (StorageMacroConstants.DEFAULT_PARAMETER_ELEMENT.equals(startElement.getName())) {
                        macroDefaultParameter = this.parseV1Parameter(reader, conversionContext, typedMacroParameters, macroName, "");
                        continue;
                    }
                    if (StorageMacroConstants.PLAIN_TEXT_BODY_PARAMETER_ELEMENT.equals(startElement.getName())) {
                        bodyReader = this.xmlEventReaderFactory.createXmlFragmentBodyEventReader(reader);
                        try {
                            macroBody = PlainTextMacroBodySubParser.parse(bodyReader);
                            continue;
                        }
                        finally {
                            StaxUtils.closeQuietly(bodyReader);
                            continue;
                        }
                    }
                    if (StorageMacroConstants.RICH_TEXT_BODY_PARAMETER_ELEMENT.equals(startElement.getName())) {
                        bodyReader = this.xmlEventReaderFactory.createXmlFragmentBodyEventReader(reader);
                        try {
                            macroBody = this.parseRichTextMacroBody(mainFragmentTransformer, conversionContext, macroName, bodyReader);
                            continue;
                        }
                        finally {
                            StaxUtils.closeQuietly(bodyReader);
                            continue;
                        }
                    }
                    reader.nextEvent();
                    continue;
                }
                reader.nextEvent();
            }
            MacroDefinition macroDefinition = MacroDefinition.builder(macroName).withMacroBody(macroBody).withParameters(macroParameters).withTypedParameters(typedMacroParameters).withStorageVersion("1").build();
            macroDefinition.setDefaultParameterValue(macroDefaultParameter);
            return macroDefinition;
        }
        catch (XMLStreamException ex) {
            throw new XhtmlException(ex);
        }
    }

    private MacroBody parseRichTextMacroBody(FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext, String macroName, XMLEventReader bodyReader) throws XhtmlException, XMLStreamException {
        return this.storageMacroBodyParser.getMacroBody(macroName, bodyReader, conversionContext, mainFragmentTransformer);
    }

    private String parseV1Parameter(XMLEventReader reader, ConversionContext conversionContext, Map<String, Object> typedMacroParameters, String macroName, String parameterName) throws XMLStreamException, XhtmlException {
        XMLEventReader parameterBodyReader = this.xmlEventReaderFactory.createXmlFragmentBodyEventReader(reader);
        String parameterValue = this.doParseSimpleStringParameter(parameterBodyReader);
        try {
            Object typedValue = this.macroParameterTypeParser.parseMacroParameter(macroName, parameterName, parameterValue, null, conversionContext);
            typedMacroParameters.put(parameterName, typedValue);
        }
        catch (InvalidMacroParameterException e) {
            throw new XhtmlException(e);
        }
        return parameterValue;
    }

    private String doParseSimpleStringParameter(XMLEventReader parameterBodyReader) throws XMLStreamException {
        return StaxUtils.readCharactersAndEntities(parameterBodyReader);
    }
}

