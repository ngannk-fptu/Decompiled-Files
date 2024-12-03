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
 *  com.atlassian.confluence.pages.templates.XhtmlTemplateUtils
 *  com.atlassian.confluence.pages.templates.variables.ListVariable
 *  com.atlassian.confluence.pages.templates.variables.TextAreaVariable
 *  com.atlassian.confluence.pages.templates.variables.Variable
 *  com.atlassian.confluence.util.HtmlUtil
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
import com.atlassian.confluence.pages.templates.XhtmlTemplateUtils;
import com.atlassian.confluence.pages.templates.variables.ListVariable;
import com.atlassian.confluence.pages.templates.variables.TextAreaVariable;
import com.atlassian.confluence.pages.templates.variables.Variable;
import com.atlassian.confluence.plugins.templates.transformer.TransformUtils;
import com.atlassian.confluence.util.HtmlUtil;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

public class StorageToViewTemplateVariablesTransformer
implements Transformer {
    private static final Logger log = LoggerFactory.getLogger(StorageToViewTemplateVariablesTransformer.class);
    private final XmlOutputFactory xmlFragmentOutputFactory;
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final XMLEventFactory xmlEventFactory;

    public StorageToViewTemplateVariablesTransformer(@Qualifier(value="xmlFragmentOutputFactory") XmlOutputFactory xmlFragmentOutputFactory, XmlEventReaderFactory xmlEventReaderFactory, XMLEventFactoryProvider xmlEventFactoryProvider) {
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
        try {
            xmlEventReader = this.xmlEventReaderFactory.createStorageXmlEventReader(input, false);
            xmlEventWriter = this.xmlFragmentOutputFactory.createXMLEventWriter((Writer)result);
            HashMap<String, Variable> variables = new HashMap<String, Variable>();
            while (xmlEventReader.hasNext()) {
                XMLEvent xmlEvent = xmlEventReader.peek();
                if (this.isVariableDeclarationSection(xmlEvent)) {
                    List<Variable> variableList = this.extractVariables(xmlEventReader);
                    for (Variable variable : variableList) {
                        variables.put(variable.getName(), variable);
                    }
                    continue;
                }
                if (this.isVariableInstance(xmlEvent)) {
                    this.renderVariableInput(variables, xmlEventReader, xmlEventWriter, conversionContext);
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
            log.debug("New markup: " + result.toString());
        }
        return result.toString();
    }

    private boolean isVariableDeclarationSection(XMLEvent xmlEvent) {
        if (xmlEvent.isStartElement()) {
            StartElement el = xmlEvent.asStartElement();
            return TemplateConstants.STORAGE_DECLARATION_GROUP_TAG.equals(el.getName());
        }
        return false;
    }

    private List<Variable> extractVariables(XMLEventReader xmlEventReader) throws XMLStreamException {
        XMLEventReader fragmentReader = this.xmlEventReaderFactory.createXmlFragmentEventReader(xmlEventReader);
        ArrayList<Variable> variables = new ArrayList<Variable>();
        while (fragmentReader.hasNext()) {
            XMLEvent event = fragmentReader.peek();
            if (event.isStartElement() && XhtmlTemplateUtils.isVariableDeclaration((StartElement)event.asStartElement())) {
                Variable variable = XhtmlTemplateUtils.extractVariableDeclaration((XMLEventReader)fragmentReader, (XmlEventReaderFactory)this.xmlEventReaderFactory);
                if (variable == null) continue;
                variables.add(variable);
                continue;
            }
            fragmentReader.nextEvent();
        }
        return variables;
    }

    private boolean isVariableInstance(XMLEvent xmlEvent) {
        if (xmlEvent.isStartElement()) {
            StartElement el = xmlEvent.asStartElement();
            return TemplateConstants.STORAGE_USAGE_VARIABLE.equals(el.getName());
        }
        return false;
    }

    private void renderVariableInput(Map<String, Variable> variables, XMLEventReader xmlEventReader, XMLEventWriter xmlEventWriter, ConversionContext conversionContext) throws XMLStreamException {
        XMLEventReader fragmentReader = this.xmlEventReaderFactory.createXmlFragmentEventReader(xmlEventReader);
        String defaultNamespace = fragmentReader.peek().asStartElement().getNamespaceContext().getNamespaceURI("");
        QName spanTag = new QName(defaultNamespace, "span");
        while (fragmentReader.hasNext()) {
            StartElement startElement;
            XMLEvent event = fragmentReader.nextEvent();
            if (!event.isStartElement() || !TemplateConstants.STORAGE_USAGE_VARIABLE.equals((startElement = event.asStartElement()).getName())) continue;
            String name = startElement.getAttributeByName(TemplateConstants.STORAGE_NAME_ATTR).getValue();
            Variable var = variables.get(name);
            String encodedName = HtmlUtil.urlEncode((String)name);
            if (var instanceof TextAreaVariable) {
                this.addTextarea(xmlEventWriter, defaultNamespace, encodedName, (TextAreaVariable)var);
            } else if (var instanceof ListVariable) {
                this.addSelect(xmlEventWriter, defaultNamespace, encodedName, (ListVariable)var);
            } else {
                this.addTextInput(xmlEventWriter, conversionContext, defaultNamespace, encodedName);
            }
            xmlEventWriter.add(this.xmlEventFactory.createSpace(" "));
            ArrayList<Attribute> spanAttribs = new ArrayList<Attribute>();
            spanAttribs.add(this.xmlEventFactory.createAttribute("class", "templateparameter"));
            xmlEventWriter.add(this.xmlEventFactory.createStartElement(spanTag, spanAttribs.iterator(), null));
            xmlEventWriter.add(this.xmlEventFactory.createCharacters("(" + name + ")"));
            xmlEventWriter.add(this.xmlEventFactory.createEndElement(spanTag, null));
        }
    }

    private void addTextarea(XMLEventWriter xmlEventWriter, String defaultNamespace, String name, TextAreaVariable textAreaVariable) throws XMLStreamException {
        QName textAreaTag = new QName(defaultNamespace, "textarea");
        ArrayList<Attribute> inputAttribs = new ArrayList<Attribute>();
        inputAttribs.add(this.xmlEventFactory.createAttribute("name", "variableValues." + name));
        inputAttribs.add(this.xmlEventFactory.createAttribute("class", "page-template-field"));
        inputAttribs.add(this.xmlEventFactory.createAttribute("rows", String.valueOf(textAreaVariable.getRows())));
        inputAttribs.add(this.xmlEventFactory.createAttribute("cols", String.valueOf(textAreaVariable.getColumns())));
        xmlEventWriter.add(this.xmlEventFactory.createStartElement(textAreaTag, inputAttribs.iterator(), null));
        xmlEventWriter.add(this.xmlEventFactory.createEndElement(textAreaTag, null));
    }

    private void addSelect(XMLEventWriter xmlEventWriter, String defaultNamespace, String name, ListVariable listVariable) throws XMLStreamException {
        QName selectTag = new QName(defaultNamespace, "select");
        QName optionTag = new QName(defaultNamespace, "option");
        ArrayList<Attribute> inputAttribs = new ArrayList<Attribute>();
        inputAttribs.add(this.xmlEventFactory.createAttribute("name", "variableValues." + name));
        inputAttribs.add(this.xmlEventFactory.createAttribute("class", "page-template-field"));
        xmlEventWriter.add(this.xmlEventFactory.createStartElement(selectTag, inputAttribs.iterator(), null));
        List options = listVariable.getOptions();
        for (String option : options) {
            ArrayList<Attribute> optionAttribs = new ArrayList<Attribute>();
            optionAttribs.add(this.xmlEventFactory.createAttribute("value", option));
            xmlEventWriter.add(this.xmlEventFactory.createStartElement(optionTag, optionAttribs.iterator(), null));
            xmlEventWriter.add(this.xmlEventFactory.createCharacters(option));
            xmlEventWriter.add(this.xmlEventFactory.createEndElement(optionTag, null));
        }
        if (options.size() == 0) {
            xmlEventWriter.add(this.xmlEventFactory.createCharacters(" "));
        }
        xmlEventWriter.add(this.xmlEventFactory.createEndElement(selectTag, null));
    }

    private void addTextInput(XMLEventWriter xmlEventWriter, ConversionContext conversionContext, String defaultNamespace, String name) throws XMLStreamException {
        QName inputTag = new QName(defaultNamespace, "input");
        ArrayList<Attribute> inputAttribs = new ArrayList<Attribute>();
        inputAttribs.add(this.xmlEventFactory.createAttribute("type", "text"));
        inputAttribs.add(this.xmlEventFactory.createAttribute("name", "variableValues." + name));
        inputAttribs.add(this.xmlEventFactory.createAttribute("size", "12"));
        inputAttribs.add(this.xmlEventFactory.createAttribute("class", "page-template-field"));
        if (conversionContext.getRenderContext().getParam((Object)"com.atlassian.confluence.plugins.templates.input.disable") != null) {
            inputAttribs.add(this.xmlEventFactory.createAttribute("disabled", ""));
        }
        xmlEventWriter.add(this.xmlEventFactory.createStartElement(inputTag, inputAttribs.iterator(), null));
        xmlEventWriter.add(this.xmlEventFactory.createEndElement(inputTag, null));
    }
}

