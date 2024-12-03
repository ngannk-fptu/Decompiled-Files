/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.jcr.nodetype;

import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.nodetype.PropertyDefinition;
import org.apache.jackrabbit.webdav.jcr.nodetype.ItemDefinitionImpl;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public final class PropertyDefinitionImpl
extends ItemDefinitionImpl
implements PropertyDefinition {
    private static Logger log = LoggerFactory.getLogger(PropertyDefinitionImpl.class);
    private final int type;
    private final String[] valueConstraints;
    private final Value[] defaultValues;
    private final boolean isMultiple;
    private final String[] availableQueryOperators;
    private final boolean isFullTextSearchable;
    private final boolean isQueryOrderable;

    private PropertyDefinitionImpl(PropertyDefinition definition) {
        super(definition);
        this.type = definition.getRequiredType();
        this.valueConstraints = definition.getValueConstraints();
        this.defaultValues = definition.getDefaultValues();
        this.isMultiple = definition.isMultiple();
        this.availableQueryOperators = definition.getAvailableQueryOperators();
        this.isFullTextSearchable = definition.isFullTextSearchable();
        this.isQueryOrderable = definition.isQueryOrderable();
    }

    public static PropertyDefinitionImpl create(PropertyDefinition definition) {
        if (definition instanceof PropertyDefinitionImpl) {
            return (PropertyDefinitionImpl)definition;
        }
        return new PropertyDefinitionImpl(definition);
    }

    @Override
    public int getRequiredType() {
        return this.type;
    }

    @Override
    public String[] getValueConstraints() {
        return this.valueConstraints;
    }

    @Override
    public Value[] getDefaultValues() {
        return this.defaultValues;
    }

    @Override
    public boolean isMultiple() {
        return this.isMultiple;
    }

    @Override
    public String[] getAvailableQueryOperators() {
        return this.availableQueryOperators;
    }

    @Override
    public boolean isFullTextSearchable() {
        return this.isFullTextSearchable;
    }

    @Override
    public boolean isQueryOrderable() {
        return this.isQueryOrderable;
    }

    @Override
    public Element toXml(Document document) {
        Element elem = super.toXml(document);
        elem.setAttribute("multiple", Boolean.toString(this.isMultiple()));
        elem.setAttribute("requiredType", PropertyType.nameFromValue(this.getRequiredType()));
        elem.setAttribute("fullTextSearchable", Boolean.toString(this.isFullTextSearchable()));
        elem.setAttribute("queryOrderable", Boolean.toString(this.isQueryOrderable()));
        Value[] values = this.getDefaultValues();
        if (values != null) {
            Element dvElement = document.createElement("defaultValues");
            for (Value value : values) {
                try {
                    Element valElem = document.createElement("defaultValue");
                    DomUtil.setText(valElem, value.getString());
                    dvElement.appendChild(valElem);
                }
                catch (RepositoryException e) {
                    log.error(e.getMessage());
                }
            }
            elem.appendChild(dvElement);
        }
        Element constrElem = document.createElement("valueConstraints");
        for (String constraint : this.getValueConstraints()) {
            Element vcElem = document.createElement("valueConstraint");
            DomUtil.setText(vcElem, constraint);
            constrElem.appendChild(vcElem);
        }
        elem.appendChild(constrElem);
        Element qopElem = document.createElement("availableQueryOperators");
        for (String qop : this.getAvailableQueryOperators()) {
            Element opElem = document.createElement("availableQueryOperator");
            DomUtil.setText(opElem, qop);
            qopElem.appendChild(opElem);
        }
        elem.appendChild(qopElem);
        return elem;
    }

    @Override
    String getElementName() {
        return "propertyDefinition";
    }
}

