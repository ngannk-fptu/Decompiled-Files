/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.StaxUtils
 *  com.atlassian.confluence.content.render.xhtml.XMLEventFactoryProvider
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory
 *  com.atlassian.confluence.content.render.xhtml.XmlOutputFactory
 *  com.atlassian.confluence.content.render.xhtml.transformers.Transformer
 *  com.atlassian.confluence.pages.templates.TemplateConstants
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.templates.transformer;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.XMLEventFactoryProvider;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.XmlOutputFactory;
import com.atlassian.confluence.content.render.xhtml.transformers.Transformer;
import com.atlassian.confluence.pages.templates.TemplateConstants;
import com.atlassian.confluence.plugins.templates.transformer.TransformUtils;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

public class EditorToStorageTemplateVariablesTransformer
implements Transformer {
    private static final Logger log = LoggerFactory.getLogger(EditorToStorageTemplateVariablesTransformer.class);
    private final XmlOutputFactory xmlFragmentOutputFactory;
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final XMLEventFactory xmlEventFactory;

    public EditorToStorageTemplateVariablesTransformer(@Qualifier(value="xmlFragmentOutputFactory") XmlOutputFactory xmlFragmentOutputFactory, XmlEventReaderFactory xmlEventReaderFactory, XMLEventFactoryProvider xmlEventFactoryProvider) {
        this.xmlFragmentOutputFactory = xmlFragmentOutputFactory;
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.xmlEventFactory = xmlEventFactoryProvider.getXmlEventFactory();
    }

    /*
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public String transform(Reader input, ConversionContext conversionContext) throws XhtmlException {
        if (conversionContext.getRenderContext().getParam((Object)"com.atlassian.confluence.plugins.templates") == null) {
            log.debug("Not a template - skip transformation");
            return TransformUtils.unspoilt(input);
        }
        StringWriter result = new StringWriter();
        XMLEventReader xmlEventReader = null;
        XMLEventWriter xmlEventWriter = null;
        HashSet<String> declarationNames = new HashSet<String>();
        HashSet<String> usageNames = new HashSet<String>();
        try {
            xmlEventReader = this.xmlEventReaderFactory.createStorageXmlEventReader(input, false);
            xmlEventWriter = this.xmlFragmentOutputFactory.createXMLEventWriter((Writer)result);
            while (xmlEventReader.hasNext()) {
                XMLEvent xmlEvent = xmlEventReader.peek();
                if (this.isVariableDeclarationSection(xmlEvent)) {
                    declarationNames.addAll(this.handleVariableDeclarationSection(xmlEventReader, xmlEventWriter));
                    continue;
                }
                if (this.isVariableInstance(xmlEvent)) {
                    usageNames.add(this.handleVariablePlaceholder(xmlEventReader, xmlEventWriter));
                    continue;
                }
                xmlEventWriter.add(xmlEventReader.nextEvent());
            }
        }
        catch (XMLStreamException e) {
            try {
                throw new XhtmlException("Error occurred while reading stream", (Throwable)e);
                catch (Exception e2) {
                    throw new XhtmlException((Throwable)e2);
                }
            }
            catch (Throwable throwable) {
                StaxUtils.closeQuietly(xmlEventReader);
                StaxUtils.closeQuietly(xmlEventWriter);
                throw throwable;
            }
        }
        StaxUtils.closeQuietly((XMLEventReader)xmlEventReader);
        StaxUtils.closeQuietly((XMLEventWriter)xmlEventWriter);
        if (log.isDebugEnabled()) {
            log.debug("Declared variables: [" + StringUtils.join(declarationNames, (char)',') + "]");
            log.debug("Used variables: [" + StringUtils.join(usageNames, (char)',') + "]");
            log.debug("New markup: " + result.toString());
        }
        if (!declarationNames.containsAll(usageNames)) {
            TreeSet<String> notDeclared = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
            usageNames.removeAll(declarationNames);
            notDeclared.addAll(usageNames);
            throw new XhtmlException("Some variables used were not declared: [" + StringUtils.join(notDeclared, (char)',') + "]");
        }
        return result.toString();
    }

    private boolean isVariableDeclarationSection(XMLEvent xmlEvent) {
        if (xmlEvent.isStartElement()) {
            StartElement el = xmlEvent.asStartElement();
            String defaultNamespace = el.getNamespaceContext().getNamespaceURI("");
            if (!new QName(defaultNamespace, "ul").equals(el.getName())) {
                return false;
            }
            Attribute attr = el.getAttributeByName(new QName("data-variable-declarations"));
            return attr != null;
        }
        return false;
    }

    private Set<String> handleVariableDeclarationSection(XMLEventReader xmlEventReader, XMLEventWriter xmlEventWriter) throws XMLStreamException {
        XMLEventReader fragmentReader = this.xmlEventReaderFactory.createXmlFragmentEventReader(xmlEventReader);
        HashSet<String> variables = new HashSet<String>();
        String defaultNamespace = fragmentReader.peek().asStartElement().getNamespaceContext().getNamespaceURI("");
        QName li = new QName(defaultNamespace, "li");
        xmlEventWriter.add(this.xmlEventFactory.createStartElement(TemplateConstants.STORAGE_DECLARATION_GROUP_TAG, Collections.emptyIterator(), null));
        while (fragmentReader.hasNext()) {
            XMLEvent nextEvent = fragmentReader.peek();
            if (nextEvent.isStartElement()) {
                StartElement startElement = nextEvent.asStartElement();
                if (li.equals(startElement.getName())) {
                    XMLEventReader variableFragmentReader = this.xmlEventReaderFactory.createXmlFragmentEventReader(xmlEventReader);
                    String variable = this.handleVariableDeclaration(defaultNamespace, variableFragmentReader, xmlEventWriter);
                    if (variable == null) continue;
                    variables.add(variable);
                    continue;
                }
                fragmentReader.nextEvent();
                continue;
            }
            fragmentReader.nextEvent();
        }
        xmlEventWriter.add(this.xmlEventFactory.createEndElement(TemplateConstants.STORAGE_DECLARATION_GROUP_TAG, null));
        return variables;
    }

    private String handleVariableDeclaration(String defaultNamespace, XMLEventReader xmlEventReader, XMLEventWriter xmlEventWriter) throws XMLStreamException {
        QName nameAttr = new QName("data-variable-name");
        QName typeAttr = new QName("data-variable-type");
        StartElement startElement = xmlEventReader.nextEvent().asStartElement();
        Attribute name = startElement.getAttributeByName(nameAttr);
        Attribute type = startElement.getAttributeByName(typeAttr);
        if (name != null) {
            QName tag;
            String typeVal = type != null ? type.getValue() : null;
            ArrayList<Attribute> attribs = new ArrayList<Attribute>();
            attribs.add(this.xmlEventFactory.createAttribute(TemplateConstants.STORAGE_NAME_ATTR, name.getValue()));
            if ("textarea".equals(typeVal)) {
                tag = TemplateConstants.STORAGE_TEXT_AREA_VAR_TAG;
                String rows = startElement.getAttributeByName(new QName("data-variable-rows")).getValue();
                String columns = startElement.getAttributeByName(new QName("data-variable-columns")).getValue();
                attribs.add(this.xmlEventFactory.createAttribute(TemplateConstants.STORAGE_TEXT_AREA_ROWS_ATTR, rows));
                attribs.add(this.xmlEventFactory.createAttribute(TemplateConstants.STORAGE_TEXT_AREA_COLUMNS_ATTR, columns));
            } else {
                tag = "list".equals(typeVal) ? TemplateConstants.STORAGE_LIST_VAR_TAG : TemplateConstants.STORAGE_STRING_VAR_TAG;
            }
            xmlEventWriter.add(this.xmlEventFactory.createStartElement(tag, attribs.iterator(), null));
            if ("list".equals(typeVal)) {
                if (xmlEventReader.peek().isCharacters()) {
                    xmlEventReader.nextEvent();
                }
                XMLEventReader optionsFragmentReader = this.xmlEventReaderFactory.createXmlFragmentEventReader(xmlEventReader);
                this.handleListOptions(defaultNamespace, optionsFragmentReader, xmlEventWriter);
            }
            xmlEventWriter.add(this.xmlEventFactory.createEndElement(tag, null));
            while (xmlEventReader.hasNext()) {
                xmlEventReader.next();
            }
            return name.getValue();
        }
        return null;
    }

    private void handleListOptions(String defaultNamespace, XMLEventReader xmlEventReader, XMLEventWriter xmlEventWriter) throws XMLStreamException {
        QName li = new QName(defaultNamespace, "li");
        QName valueAttr = new QName("data-variable-option");
        while (xmlEventReader.hasNext()) {
            StartElement startElement;
            Attribute option;
            XMLEvent event = xmlEventReader.nextEvent();
            if (!event.isStartElement() || !li.equals(event.asStartElement().getName()) || (option = (startElement = event.asStartElement()).getAttributeByName(valueAttr)) == null) continue;
            String optionValue = option.getValue();
            ArrayList<Attribute> attribs = new ArrayList<Attribute>();
            attribs.add(this.xmlEventFactory.createAttribute(TemplateConstants.STORAGE_LIST_OPTION_VALUE_ATTR, optionValue));
            xmlEventWriter.add(this.xmlEventFactory.createStartElement(TemplateConstants.STORAGE_LIST_OPTION_TAG, attribs.iterator(), null));
            xmlEventWriter.add(this.xmlEventFactory.createEndElement(TemplateConstants.STORAGE_LIST_OPTION_TAG, null));
        }
    }

    private boolean isVariableInstance(XMLEvent xmlEvent) {
        if (xmlEvent.isStartElement()) {
            StartElement el = xmlEvent.asStartElement();
            String defaultNamespace = el.getNamespaceContext().getNamespaceURI("");
            if (!new QName(defaultNamespace, "img").equals(el.getName())) {
                return false;
            }
            Attribute attr = el.getAttributeByName(new QName("data-variable-name"));
            if (attr != null) {
                return StringUtils.isNotEmpty((CharSequence)attr.getValue());
            }
        }
        return false;
    }

    private String handleVariablePlaceholder(XMLEventReader xmlEventReader, XMLEventWriter xmlEventWriter) throws XMLStreamException {
        XMLEventReader fragmentReader = this.xmlEventReaderFactory.createXmlFragmentEventReader(xmlEventReader);
        StartElement start = fragmentReader.nextEvent().asStartElement();
        String varName = start.getAttributeByName(new QName("data-variable-name")).getValue();
        Attribute rawXhtmlAttribute = start.getAttributeByName(new QName("data-variable-raw-xhtml"));
        Attribute nameAttr = this.xmlEventFactory.createAttribute(TemplateConstants.STORAGE_NAME_ATTR, varName);
        if (rawXhtmlAttribute != null) {
            String rawXhtml = rawXhtmlAttribute.getValue();
            Attribute rawXhtmlAttr = this.xmlEventFactory.createAttribute(TemplateConstants.STORAGE_RAW_XHTML_ATTR, rawXhtml);
            xmlEventWriter.add(this.xmlEventFactory.createStartElement(TemplateConstants.STORAGE_USAGE_VARIABLE, Arrays.asList(nameAttr, rawXhtmlAttr).iterator(), null));
        } else {
            xmlEventWriter.add(this.xmlEventFactory.createStartElement(TemplateConstants.STORAGE_USAGE_VARIABLE, Collections.singletonList(nameAttr).iterator(), null));
        }
        while (fragmentReader.hasNext()) {
            fragmentReader.nextEvent();
        }
        xmlEventWriter.add(this.xmlEventFactory.createEndElement(TemplateConstants.STORAGE_USAGE_VARIABLE, null));
        return varName;
    }
}

