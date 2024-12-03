/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.math.NumberUtils
 */
package com.atlassian.confluence.pages.templates;

import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.pages.templates.TemplateConstants;
import com.atlassian.confluence.pages.templates.variables.ListVariable;
import com.atlassian.confluence.pages.templates.variables.StringVariable;
import com.atlassian.confluence.pages.templates.variables.TextAreaVariable;
import com.atlassian.confluence.pages.templates.variables.Variable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.apache.commons.lang3.math.NumberUtils;

public class XhtmlTemplateUtils {
    public static final int DEFAULT_ROWS = 5;
    public static final int DEFAULT_COLS = 100;

    public static boolean isVariableDeclaration(StartElement startElement) {
        QName name = startElement.getName();
        return TemplateConstants.STORAGE_STRING_VAR_TAG.equals(name) || TemplateConstants.STORAGE_LIST_VAR_TAG.equals(name) || TemplateConstants.STORAGE_TEXT_AREA_VAR_TAG.equals(name);
    }

    public static Variable extractVariableDeclaration(XMLEventReader parentEventReader, XmlEventReaderFactory xmlEventReaderFactory) throws XMLStreamException {
        Variable var;
        XMLEventReader xmlEventReader = xmlEventReaderFactory.createXmlFragmentEventReader(parentEventReader);
        StartElement startElement = xmlEventReader.nextEvent().asStartElement();
        String name = startElement.getAttributeByName(TemplateConstants.STORAGE_NAME_ATTR).getValue();
        if (TemplateConstants.STORAGE_LIST_VAR_TAG.equals(startElement.getName())) {
            List<String> options = XhtmlTemplateUtils.extractListOptions(xmlEventReader, xmlEventReaderFactory);
            var = new ListVariable(name, options);
        } else if (TemplateConstants.STORAGE_TEXT_AREA_VAR_TAG.equals(startElement.getName())) {
            int rows = NumberUtils.toInt((String)startElement.getAttributeByName(TemplateConstants.STORAGE_TEXT_AREA_ROWS_ATTR).getValue(), (int)5);
            int columns = NumberUtils.toInt((String)startElement.getAttributeByName(TemplateConstants.STORAGE_TEXT_AREA_COLUMNS_ATTR).getValue(), (int)100);
            var = new TextAreaVariable(name, rows, columns);
        } else {
            var = new StringVariable(name);
        }
        while (xmlEventReader.hasNext()) {
            xmlEventReader.next();
        }
        return var;
    }

    private static List<String> extractListOptions(XMLEventReader fragmentReader, XmlEventReaderFactory xmlEventReaderFactory) throws XMLStreamException {
        ArrayList<String> options = new ArrayList<String>();
        while (fragmentReader.hasNext()) {
            StartElement startElement;
            Attribute option;
            XMLEvent event = fragmentReader.nextEvent();
            if (!event.isStartElement() || !TemplateConstants.STORAGE_LIST_OPTION_TAG.equals(event.asStartElement().getName()) || (option = (startElement = event.asStartElement()).getAttributeByName(TemplateConstants.STORAGE_LIST_OPTION_VALUE_ATTR)) == null) continue;
            options.add(option.getValue());
        }
        return options;
    }
}

