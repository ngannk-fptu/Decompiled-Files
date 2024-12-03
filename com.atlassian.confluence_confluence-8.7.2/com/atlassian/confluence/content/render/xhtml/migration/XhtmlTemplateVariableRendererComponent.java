/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.components.AbstractRegexRendererComponent
 *  com.atlassian.renderer.v2.components.phrase.TemplateParamRenderComponent
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.pages.templates.TemplateConstants;
import com.atlassian.confluence.pages.templates.variables.ListVariable;
import com.atlassian.confluence.pages.templates.variables.TextAreaVariable;
import com.atlassian.confluence.pages.templates.variables.Variable;
import com.atlassian.confluence.pages.templates.variables.VariableFactory;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.components.AbstractRegexRendererComponent;
import com.atlassian.renderer.v2.components.phrase.TemplateParamRenderComponent;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;

public class XhtmlTemplateVariableRendererComponent
extends AbstractRegexRendererComponent {
    private final XMLOutputFactory xmlOutputFactory;
    private final XMLEventFactory xmlEventFactory;

    public XhtmlTemplateVariableRendererComponent(XMLOutputFactory xmlOutputFactory, XMLEventFactory xmlEventFactory) {
        this.xmlOutputFactory = xmlOutputFactory;
        this.xmlEventFactory = xmlEventFactory;
    }

    public boolean shouldRender(RenderMode renderMode) {
        return renderMode.renderParagraphs();
    }

    public String render(String wiki, RenderContext renderContext) {
        if (wiki.indexOf("@") == -1) {
            return wiki;
        }
        LinkedHashSet<Variable> variables = new LinkedHashSet<Variable>();
        renderContext.addParam(XhtmlTemplateVariableRendererComponent.class, variables);
        String xhtml = this.regexRender(wiki, renderContext, TemplateParamRenderComponent.VARIABLE_PATTERN);
        return this.renderVariableDeclarations(variables) + xhtml;
    }

    private String renderVariableDeclarations(Set<Variable> variables) {
        if (variables.size() == 0) {
            return "";
        }
        StringWriter stringWriter = new StringWriter();
        try {
            XMLEventWriter eventWriter = this.xmlOutputFactory.createXMLEventWriter(stringWriter);
            eventWriter.add(this.xmlEventFactory.createStartElement(TemplateConstants.STORAGE_DECLARATION_GROUP_TAG, null, null));
            for (Variable variable : variables) {
                QName declareTag;
                ArrayList<Attribute> attribs = new ArrayList<Attribute>();
                attribs.add(this.xmlEventFactory.createAttribute(TemplateConstants.STORAGE_NAME_ATTR, variable.getName()));
                if (variable instanceof TextAreaVariable) {
                    declareTag = TemplateConstants.STORAGE_TEXT_AREA_VAR_TAG;
                    TextAreaVariable textAreaVariable = (TextAreaVariable)variable;
                    attribs.add(this.xmlEventFactory.createAttribute(TemplateConstants.STORAGE_TEXT_AREA_ROWS_ATTR, String.valueOf(textAreaVariable.getRows())));
                    attribs.add(this.xmlEventFactory.createAttribute(TemplateConstants.STORAGE_TEXT_AREA_COLUMNS_ATTR, String.valueOf(textAreaVariable.getColumns())));
                } else {
                    declareTag = variable instanceof ListVariable ? TemplateConstants.STORAGE_LIST_VAR_TAG : TemplateConstants.STORAGE_STRING_VAR_TAG;
                }
                eventWriter.add(this.xmlEventFactory.createStartElement(declareTag, attribs.iterator(), null));
                if (variable instanceof ListVariable) {
                    ListVariable listVariable = (ListVariable)variable;
                    for (String option : listVariable.getOptions()) {
                        ArrayList<Attribute> optionAttribs = new ArrayList<Attribute>();
                        optionAttribs.add(this.xmlEventFactory.createAttribute(TemplateConstants.STORAGE_LIST_OPTION_VALUE_ATTR, option));
                        eventWriter.add(this.xmlEventFactory.createStartElement(TemplateConstants.STORAGE_LIST_OPTION_TAG, optionAttribs.iterator(), null));
                        eventWriter.add(this.xmlEventFactory.createEndElement(TemplateConstants.STORAGE_LIST_OPTION_TAG, null));
                    }
                }
                eventWriter.add(this.xmlEventFactory.createEndElement(declareTag, null));
            }
            eventWriter.add(this.xmlEventFactory.createEndElement(TemplateConstants.STORAGE_DECLARATION_GROUP_TAG, null));
            eventWriter.flush();
        }
        catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return stringWriter.toString();
    }

    public void appendSubstitution(StringBuffer buffer, RenderContext context, Matcher matcher) {
        Set variables = (Set)context.getParam(XhtmlTemplateVariableRendererComponent.class);
        StringWriter stringWriter = new StringWriter();
        try {
            String name = matcher.group(1);
            Variable variable = VariableFactory.wikiNameToVariable(name);
            XMLEventWriter eventWriter = this.xmlOutputFactory.createXMLEventWriter(stringWriter);
            ArrayList<Attribute> attribs = new ArrayList<Attribute>();
            attribs.add(this.xmlEventFactory.createAttribute(TemplateConstants.STORAGE_NAME_ATTR, variable.getName()));
            eventWriter.add(this.xmlEventFactory.createStartElement(TemplateConstants.STORAGE_USAGE_VARIABLE, attribs.iterator(), null));
            eventWriter.add(this.xmlEventFactory.createEndElement(TemplateConstants.STORAGE_USAGE_VARIABLE, null));
            eventWriter.flush();
            variables.add(variable);
        }
        catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        buffer.append(stringWriter.toString());
    }
}

