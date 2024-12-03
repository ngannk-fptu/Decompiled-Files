/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ctc.wstx.exc.WstxLazyException
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.text.StrTokenizer
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.pages.templates;

import com.atlassian.confluence.content.render.xhtml.FormatConverter;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.StorageFormatCleaner;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactory;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.pages.templates.TemplateConstants;
import com.atlassian.confluence.pages.templates.TemplateHandler;
import com.atlassian.confluence.pages.templates.TemplateI18nHandler;
import com.atlassian.confluence.pages.templates.XhtmlTemplateUtils;
import com.atlassian.confluence.pages.templates.variables.Variable;
import com.atlassian.confluence.renderer.PageTemplateContext;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.util.i18n.Message;
import com.atlassian.confluence.xml.NoAutoescapeCharacters;
import com.ctc.wstx.exc.WstxLazyException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

public class XhtmlTemplateHandler
implements TemplateHandler {
    private static final Logger log = LoggerFactory.getLogger(XhtmlTemplateHandler.class);
    private static final QName HTML_LINE_BREAK = new QName("br");
    private final XmlOutputFactory xmlFragmentOutputFactory;
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final XMLEventFactory xmlEventFactory;
    private final FormatConverter formatConverter;
    private final TemplateI18nHandler templateI18nHandler;
    private final StorageFormatCleaner storageFormatCleaner;
    private final SpaceManager spaceManager;

    public XhtmlTemplateHandler(@Qualifier(value="xmlFragmentOutputFactory") XmlOutputFactory xmlFragmentOutputFactory, XmlEventReaderFactory xmlEventReaderFactory, FormatConverter formatConverter, XMLEventFactory xmlEventFactory, TemplateI18nHandler templateI18nHandler, StorageFormatCleaner storageFormatCleaner, SpaceManager spaceManager) {
        this.xmlFragmentOutputFactory = xmlFragmentOutputFactory;
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.formatConverter = formatConverter;
        this.xmlEventFactory = xmlEventFactory;
        this.templateI18nHandler = templateI18nHandler;
        this.storageFormatCleaner = storageFormatCleaner;
        this.spaceManager = spaceManager;
    }

    /*
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public List<Variable> getTemplateVariables(PageTemplate template) throws XhtmlException {
        ArrayList<Variable> variables = new ArrayList<Variable>();
        String content = template.getContent();
        XMLEventReader xmlEventReader = null;
        try {
            xmlEventReader = this.xmlEventReaderFactory.createStorageXmlEventReader(new StringReader(content), false);
            while (xmlEventReader.hasNext()) {
                StartElement startEl;
                XMLEvent xmlEvent = xmlEventReader.peek();
                StartElement startElement = startEl = xmlEvent.isStartElement() ? xmlEvent.asStartElement() : null;
                if (startEl != null && this.isVariableDeclarationSection(startEl)) {
                    variables.addAll(this.extractVariables(xmlEventReader));
                    continue;
                }
                xmlEventReader.nextEvent();
            }
        }
        catch (XMLStreamException e) {
            try {
                throw new XhtmlException("Error occurred while reading stream", e);
                catch (Exception e2) {
                    throw new XhtmlException(e2);
                }
            }
            catch (Throwable throwable) {
                StaxUtils.closeQuietly(xmlEventReader);
                throw throwable;
            }
        }
        StaxUtils.closeQuietly(xmlEventReader);
        return variables;
    }

    @Override
    public String insertVariables(Reader templateXml, List<? extends Variable> variables) {
        if (templateXml == null) {
            return "";
        }
        Map<String, Variable> mappedVariables = this.toMap(variables);
        StringWriter result = new StringWriter();
        XMLEventReader xmlEventReader = null;
        XMLEventWriter xmlEventWriter = null;
        try {
            xmlEventReader = this.xmlEventReaderFactory.createStorageXmlEventReader(templateXml, false);
            xmlEventWriter = this.xmlFragmentOutputFactory.createXMLEventWriter(result);
            while (xmlEventReader.hasNext()) {
                StartElement startEl;
                XMLEvent xmlEvent = xmlEventReader.peek();
                boolean isStartEl = xmlEvent.isStartElement();
                StartElement startElement = startEl = isStartEl ? xmlEvent.asStartElement() : null;
                if (isStartEl && this.isVariableDeclarationSection(startEl)) {
                    this.skipVariableDeclarations(xmlEventReader);
                    continue;
                }
                if (isStartEl && XhtmlTemplateHandler.isStorageUsageVariable(startEl)) {
                    this.replaceVariable(xmlEventReader, xmlEventWriter, mappedVariables);
                    continue;
                }
                if (isStartEl && XhtmlTemplateHandler.isStorageUsageI18n(startEl)) {
                    this.replaceI18n(xmlEventReader, xmlEventWriter, mappedVariables);
                    continue;
                }
                xmlEventWriter.add(xmlEventReader.nextEvent());
            }
        }
        catch (XMLStreamException e) {
            try {
                throw new RuntimeException("Error occurred while reading stream", e);
            }
            catch (Throwable throwable) {
                StaxUtils.closeQuietly(xmlEventReader);
                StaxUtils.closeQuietly(xmlEventWriter);
                throw throwable;
            }
        }
        StaxUtils.closeQuietly(xmlEventReader);
        StaxUtils.closeQuietly(xmlEventWriter);
        return result.toString();
    }

    @Override
    public String generateEditorFormat(PageTemplate template, List<? extends Variable> variables, String spaceKey) throws XhtmlException {
        String storageFormat = this.insertVariables(new StringReader(template.getContent()), variables);
        String editorFormat = this.formatConverter.convertToEditorFormat(storageFormat, this.getRenderContext(spaceKey));
        if (log.isDebugEnabled()) {
            log.debug("storageFormat = " + storageFormat);
            log.debug("editorFormat = " + editorFormat);
        }
        return editorFormat;
    }

    private boolean isVariableDeclarationSection(StartElement el) {
        return TemplateConstants.STORAGE_DECLARATION_GROUP_TAG.equals(el.getName());
    }

    private List<Variable> extractVariables(XMLEventReader xmlEventReader) throws XMLStreamException {
        ArrayList<Variable> variables = new ArrayList<Variable>();
        while (xmlEventReader.hasNext()) {
            XMLEvent event = xmlEventReader.peek();
            if (event.isStartElement() && XhtmlTemplateUtils.isVariableDeclaration(event.asStartElement())) {
                variables.add(XhtmlTemplateUtils.extractVariableDeclaration(xmlEventReader, this.xmlEventReaderFactory));
                continue;
            }
            xmlEventReader.next();
        }
        return variables;
    }

    private void skipVariableDeclarations(XMLEventReader xmlEventReader) throws XMLStreamException {
        XMLEventReader fragmentReader = this.xmlEventReaderFactory.createXmlFragmentEventReader(xmlEventReader);
        while (fragmentReader.hasNext()) {
            fragmentReader.next();
        }
    }

    private void replaceVariable(XMLEventReader xmlEventReader, XMLEventWriter xmlEventWriter, Map<String, Variable> variables) throws XMLStreamException {
        int depth = 0;
        do {
            XMLEvent xmlEvent;
            if ((xmlEvent = xmlEventReader.nextEvent()).isStartElement()) {
                ++depth;
                StartElement startElement = xmlEvent.asStartElement();
                if (!XhtmlTemplateHandler.isStorageUsageVariable(startElement)) continue;
                String variableName = startElement.getAttributeByName(TemplateConstants.STORAGE_NAME_ATTR).getValue();
                String variableValue = XhtmlTemplateHandler.stringValue(variables.get(variableName));
                if (XhtmlTemplateHandler.isRawXhtmlVariable(startElement)) {
                    this.echoXmlEventsFromString(this.storageFormatCleaner.cleanQuietly(variableValue), xmlEventWriter);
                    continue;
                }
                this.writeMultiLineStringValue(xmlEventWriter, variableValue);
                continue;
            }
            if (!xmlEvent.isEndElement()) continue;
            --depth;
        } while (depth > 0);
    }

    private void replaceI18n(XMLEventReader xmlEventReader, XMLEventWriter xmlEventWriter, Map<String, Variable> variables) throws XMLStreamException {
        int depth = 0;
        do {
            XMLEvent xmlEvent;
            if ((xmlEvent = xmlEventReader.nextEvent()).isStartElement()) {
                ++depth;
                StartElement startElement = xmlEvent.asStartElement();
                if (!XhtmlTemplateHandler.isStorageUsageI18n(startElement)) continue;
                String i18nKey = startElement.getAttributeByName(TemplateConstants.STORAGE_KEY_ATTR).getValue();
                Attribute noAutoescapeAttribute = startElement.getAttributeByName(TemplateConstants.STORAGE_NOAUTOESCAPE_ATTR);
                boolean noAutoescape = noAutoescapeAttribute != null ? Boolean.valueOf(noAutoescapeAttribute.getValue()) : false;
                Message message = Message.getInstance(i18nKey);
                String variableValue = this.templateI18nHandler.translate(message);
                this.writeMultiLineStringValue(xmlEventWriter, variableValue, noAutoescape);
                continue;
            }
            if (!xmlEvent.isEndElement()) continue;
            --depth;
        } while (depth > 0);
    }

    private void echoXmlEventsFromString(String xmlString, XMLEventWriter xmlEventWriter) throws XMLStreamException {
        if (StringUtils.isBlank((CharSequence)xmlString)) {
            return;
        }
        XMLEventReader xmlEventReader = this.xmlEventReaderFactory.createStorageXmlEventReader(new StringReader(xmlString));
        try {
            xmlEventWriter.add(xmlEventReader);
        }
        catch (RuntimeException e) {
            if (e instanceof WstxLazyException || StaxUtils.isWrappedAnyWstxException(e)) {
                Message message = Message.getInstance("xhtml.transformer.cannot.parse.storage.format", xmlString);
                this.writeMultiLineStringValue(xmlEventWriter, this.templateI18nHandler.translate(message));
            }
            throw e;
        }
    }

    private static boolean isRawXhtmlVariable(StartElement startElement) {
        Attribute rawXhtmlAttribute = startElement.getAttributeByName(TemplateConstants.STORAGE_RAW_XHTML_ATTR);
        return rawXhtmlAttribute != null && Boolean.parseBoolean(rawXhtmlAttribute.getValue());
    }

    private static boolean isStorageUsageVariable(StartElement startElement) {
        return TemplateConstants.STORAGE_USAGE_VARIABLE.equals(startElement.getName());
    }

    private static boolean isStorageUsageI18n(StartElement startElement) {
        return TemplateConstants.STORAGE_USAGE_I18N.equals(startElement.getName());
    }

    private void writeMultiLineStringValue(XMLEventWriter xmlEventWriter, String stringValue) throws XMLStreamException {
        this.writeMultiLineStringValue(xmlEventWriter, stringValue, false);
    }

    private void writeMultiLineStringValue(XMLEventWriter xmlEventWriter, String stringValue, boolean noAutoescape) throws XMLStreamException {
        boolean isFirstLine = true;
        StrTokenizer tokenizer = new StrTokenizer(stringValue, '\n').setIgnoreEmptyTokens(false);
        while (tokenizer.hasNext()) {
            if (!isFirstLine) {
                this.addLineBreak(xmlEventWriter);
            }
            String line = tokenizer.next();
            xmlEventWriter.add(noAutoescape ? new NoAutoescapeCharacters(line) : this.xmlEventFactory.createCharacters(line));
            isFirstLine = false;
        }
    }

    private void addLineBreak(XMLEventWriter xmlEventWriter) throws XMLStreamException {
        xmlEventWriter.add(this.xmlEventFactory.createStartElement(HTML_LINE_BREAK, null, null));
        xmlEventWriter.add(this.xmlEventFactory.createEndElement(HTML_LINE_BREAK, null));
    }

    private static String stringValue(Variable variable) {
        return variable == null ? "" : variable.getValue();
    }

    public PageTemplateContext getRenderContext(String spaceKey) {
        PageTemplate template = new PageTemplate();
        template.setSpace(this.spaceManager.getSpace(spaceKey));
        return new PageTemplateContext(template);
    }

    private Map<String, Variable> toMap(List<? extends Variable> variables) {
        if (variables == null) {
            return Collections.emptyMap();
        }
        HashMap<String, Variable> map = new HashMap<String, Variable>();
        for (Variable variable : variables) {
            map.put(variable.getName(), variable);
        }
        return map;
    }
}

