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
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.confluence.pages.templates.TemplateConstants
 *  com.atlassian.confluence.pages.templates.TemplateI18nHandler
 *  com.atlassian.confluence.util.i18n.Message
 *  org.apache.commons.lang3.StringEscapeUtils
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
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.pages.templates.TemplateConstants;
import com.atlassian.confluence.pages.templates.TemplateI18nHandler;
import com.atlassian.confluence.plugins.templates.transformer.TransformUtils;
import com.atlassian.confluence.util.i18n.Message;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.ArrayList;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

public class StorageToEditorTemplateVariablesTransformer
implements Transformer {
    private static final Logger log = LoggerFactory.getLogger(StorageToEditorTemplateVariablesTransformer.class);
    private final XmlOutputFactory xmlFragmentOutputFactory;
    private final XmlEventReaderFactory xmlEventReaderFactory;
    private final XMLEventFactory xmlEventFactory;
    private final ContextPathHolder contextPathHolder;
    private final TemplateI18nHandler templateI18nHandler;

    public StorageToEditorTemplateVariablesTransformer(@Qualifier(value="xmlFragmentOutputFactory") XmlOutputFactory xmlFragmentOutputFactory, XmlEventReaderFactory xmlEventReaderFactory, ContextPathHolder contextPathHolder, TemplateI18nHandler templateI18nHandler, XMLEventFactoryProvider xmlEventFactoryProvider) {
        this.xmlFragmentOutputFactory = xmlFragmentOutputFactory;
        this.xmlEventReaderFactory = xmlEventReaderFactory;
        this.xmlEventFactory = xmlEventFactoryProvider.getXmlEventFactory();
        this.contextPathHolder = contextPathHolder;
        this.templateI18nHandler = templateI18nHandler;
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
            while (xmlEventReader.hasNext()) {
                XMLEvent xmlEvent = xmlEventReader.peek();
                if (this.isVariableDeclarationSection(xmlEvent)) {
                    this.handleVariableDeclarationSection(xmlEventReader, xmlEventWriter);
                    continue;
                }
                if (this.isVariableInstance(xmlEvent)) {
                    this.handleVariablePlaceholder(xmlEventReader, xmlEventWriter);
                    continue;
                }
                if (this.isI18nInstance(xmlEvent)) {
                    this.handleI18nPlaceholder(xmlEventReader, xmlEventWriter);
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

    private void handleVariableDeclarationSection(XMLEventReader xmlEventReader, XMLEventWriter xmlEventWriter) throws XMLStreamException {
        XMLEventReader fragmentReader = this.xmlEventReaderFactory.createXmlFragmentEventReader(xmlEventReader);
        String defaultNamespace = fragmentReader.peek().asStartElement().getNamespaceContext().getNamespaceURI("");
        QName declGroupTag = new QName(defaultNamespace, "ul");
        fragmentReader.nextEvent();
        ArrayList<Attribute> groupAttribs = new ArrayList<Attribute>();
        groupAttribs.add(this.xmlEventFactory.createAttribute("data-variable-declarations", "true"));
        xmlEventWriter.add(this.xmlEventFactory.createStartElement(declGroupTag, groupAttribs.iterator(), null));
        while (fragmentReader.hasNext()) {
            XMLEvent event = fragmentReader.peek();
            if (event.isStartElement()) {
                QName tagName = event.asStartElement().getName();
                String type = null;
                if (TemplateConstants.STORAGE_LIST_VAR_TAG.equals(tagName)) {
                    type = "list";
                } else if (TemplateConstants.STORAGE_TEXT_AREA_VAR_TAG.equals(tagName)) {
                    type = "textarea";
                } else if (TemplateConstants.STORAGE_STRING_VAR_TAG.equals(tagName)) {
                    type = "string";
                }
                if (type != null) {
                    XMLEventReader declareFragmentReader = this.xmlEventReaderFactory.createXmlFragmentEventReader(xmlEventReader);
                    this.generateVariableDeclaration(defaultNamespace, declareFragmentReader, xmlEventWriter, type);
                    continue;
                }
                fragmentReader.next();
                continue;
            }
            fragmentReader.next();
        }
        xmlEventWriter.add(this.xmlEventFactory.createEndElement(TemplateConstants.STORAGE_DECLARATION_GROUP_TAG, null));
    }

    private void generateVariableDeclaration(String defaultNamespace, XMLEventReader xmlEventReader, XMLEventWriter xmlEventWriter, String type) throws XMLStreamException {
        StartElement startElement = xmlEventReader.peek().asStartElement();
        QName declTag = new QName(defaultNamespace, "li");
        String name = startElement.getAttributeByName(TemplateConstants.STORAGE_NAME_ATTR).getValue();
        if (log.isDebugEnabled()) {
            log.debug("Found variable declaration for: " + name + " type = " + type);
        }
        ArrayList<Attribute> declAttribs = new ArrayList<Attribute>();
        declAttribs.add(this.xmlEventFactory.createAttribute("data-variable-name", name));
        declAttribs.add(this.xmlEventFactory.createAttribute("data-variable-type", type));
        if ("textarea".equals(type)) {
            String rows = startElement.getAttributeByName(TemplateConstants.STORAGE_TEXT_AREA_ROWS_ATTR).getValue();
            String columns = startElement.getAttributeByName(TemplateConstants.STORAGE_TEXT_AREA_COLUMNS_ATTR).getValue();
            declAttribs.add(this.xmlEventFactory.createAttribute("data-variable-rows", rows));
            declAttribs.add(this.xmlEventFactory.createAttribute("data-variable-columns", columns));
        }
        xmlEventWriter.add(this.xmlEventFactory.createStartElement(declTag, declAttribs.iterator(), null));
        xmlEventWriter.add(this.xmlEventFactory.createCharacters(StringEscapeUtils.escapeXml((String)name)));
        if ("list".equals(type)) {
            XMLEventReader optionsFragmentReader = this.xmlEventReaderFactory.createXmlFragmentEventReader(xmlEventReader);
            this.generateListOptions(defaultNamespace, optionsFragmentReader, xmlEventWriter);
        }
        xmlEventWriter.add(this.xmlEventFactory.createEndElement(declTag, null));
        while (xmlEventReader.hasNext()) {
            xmlEventReader.nextEvent();
        }
    }

    private void generateListOptions(String defaultNamespace, XMLEventReader xmlEventReader, XMLEventWriter xmlEventWriter) throws XMLStreamException {
        QName optionGroupTag = new QName(defaultNamespace, "ul");
        QName optionTag = new QName(defaultNamespace, "li");
        QName optionValueAttr = new QName(defaultNamespace, "data-variable-option");
        xmlEventWriter.add(this.xmlEventFactory.createStartElement(optionGroupTag, null, null));
        while (xmlEventReader.hasNext()) {
            XMLEvent event = xmlEventReader.nextEvent();
            if (!event.isStartElement() || !TemplateConstants.STORAGE_LIST_OPTION_TAG.equals(event.asStartElement().getName())) continue;
            StartElement option = event.asStartElement();
            String optionValue = option.getAttributeByName(TemplateConstants.STORAGE_LIST_OPTION_VALUE_ATTR).getValue();
            ArrayList<Attribute> attribs = new ArrayList<Attribute>();
            attribs.add(this.xmlEventFactory.createAttribute(optionValueAttr, optionValue));
            xmlEventWriter.add(this.xmlEventFactory.createStartElement(optionTag, attribs.iterator(), null));
            xmlEventWriter.add(this.xmlEventFactory.createCharacters(optionValue));
            xmlEventWriter.add(this.xmlEventFactory.createEndElement(optionTag, null));
        }
        xmlEventWriter.add(this.xmlEventFactory.createEndElement(optionGroupTag, null));
    }

    private boolean isVariableInstance(XMLEvent xmlEvent) {
        if (xmlEvent.isStartElement()) {
            StartElement el = xmlEvent.asStartElement();
            return TemplateConstants.STORAGE_USAGE_VARIABLE.equals(el.getName());
        }
        return false;
    }

    private boolean isI18nInstance(XMLEvent xmlEvent) {
        if (!xmlEvent.isStartElement()) {
            return false;
        }
        StartElement el = xmlEvent.asStartElement();
        return TemplateConstants.STORAGE_USAGE_I18N.equals(el.getName());
    }

    private void handleVariablePlaceholder(XMLEventReader xmlEventReader, XMLEventWriter xmlEventWriter) throws XMLStreamException {
        XMLEventReader fragmentReader = this.xmlEventReaderFactory.createXmlFragmentEventReader(xmlEventReader);
        String defaultNamespace = fragmentReader.peek().asStartElement().getNamespaceContext().getNamespaceURI("");
        QName varTag = new QName(defaultNamespace, "img");
        while (fragmentReader.hasNext()) {
            StartElement startElement;
            XMLEvent event = fragmentReader.nextEvent();
            if (!event.isStartElement() || !TemplateConstants.STORAGE_USAGE_VARIABLE.equals((startElement = event.asStartElement()).getName())) continue;
            String name = startElement.getAttributeByName(TemplateConstants.STORAGE_NAME_ATTR).getValue();
            Attribute rawXhtmlAttr = startElement.getAttributeByName(TemplateConstants.STORAGE_RAW_XHTML_ATTR);
            ArrayList<Attribute> declAttribs = new ArrayList<Attribute>();
            declAttribs.add(this.xmlEventFactory.createAttribute("data-variable-name", name));
            if (rawXhtmlAttr == null) {
                rawXhtmlAttr = startElement.getAttributeByName(TemplateConstants.STORAGE_OLD_RAW_XHTML_ATTR);
            }
            if (rawXhtmlAttr != null) {
                declAttribs.add(this.xmlEventFactory.createAttribute("data-variable-raw-xhtml", rawXhtmlAttr.getValue()));
            }
            declAttribs.add(this.xmlEventFactory.createAttribute("alt", "$" + name));
            declAttribs.add(this.xmlEventFactory.createAttribute("class", "template-variable"));
            declAttribs.add(this.xmlEventFactory.createAttribute("src", this.getPlaceholderImageSrc(name)));
            xmlEventWriter.add(this.xmlEventFactory.createStartElement(varTag, declAttribs.iterator(), null));
            xmlEventWriter.add(this.xmlEventFactory.createEndElement(varTag, null));
        }
    }

    private void handleI18nPlaceholder(XMLEventReader xmlEventReader, XMLEventWriter xmlEventWriter) throws XMLStreamException {
        XMLEventReader fragmentReader = this.xmlEventReaderFactory.createXmlFragmentEventReader(xmlEventReader);
        while (fragmentReader.hasNext()) {
            StartElement startElement;
            XMLEvent event = fragmentReader.nextEvent();
            if (!event.isStartElement() || !TemplateConstants.STORAGE_USAGE_I18N.equals((startElement = event.asStartElement()).getName())) continue;
            String i18nKey = startElement.getAttributeByName(TemplateConstants.STORAGE_KEY_ATTR).getValue();
            String i18nValue = this.templateI18nHandler.translate(Message.getInstance((String)i18nKey));
            xmlEventWriter.add(this.xmlEventFactory.createCharacters(i18nValue));
        }
    }

    private String getPlaceholderImageSrc(String name) {
        try {
            return this.contextPathHolder.getContextPath() + "/plugins/servlet/confluence/placeholder/template-variable?name=" + URLEncoder.encode(name, "utf-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}

