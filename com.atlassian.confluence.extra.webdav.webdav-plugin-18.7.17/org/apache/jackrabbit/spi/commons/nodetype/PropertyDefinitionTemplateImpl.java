/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.nodetype;

import javax.jcr.PropertyType;
import javax.jcr.Value;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.PropertyDefinition;
import javax.jcr.nodetype.PropertyDefinitionTemplate;
import org.apache.jackrabbit.commons.query.qom.Operator;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.nodetype.AbstractItemDefinitionTemplate;

class PropertyDefinitionTemplateImpl
extends AbstractItemDefinitionTemplate
implements PropertyDefinitionTemplate {
    private int type;
    private String[] constraints;
    private Value[] defaultValues;
    private boolean multiple;
    private boolean fullTextSearchable;
    private boolean queryOrderable;
    private String[] queryOperators;

    PropertyDefinitionTemplateImpl(NamePathResolver resolver) {
        super(resolver);
        this.type = 1;
        this.fullTextSearchable = true;
        this.queryOrderable = true;
        this.queryOperators = Operator.getAllQueryOperators();
    }

    PropertyDefinitionTemplateImpl(PropertyDefinition def, NamePathResolver resolver) throws ConstraintViolationException {
        super(def, resolver);
        this.type = def.getRequiredType();
        this.defaultValues = def.getDefaultValues();
        this.multiple = def.isMultiple();
        this.fullTextSearchable = def.isFullTextSearchable();
        this.queryOrderable = def.isQueryOrderable();
        this.queryOperators = def.getAvailableQueryOperators();
        this.setValueConstraints(def.getValueConstraints());
    }

    @Override
    public void setRequiredType(int type) {
        PropertyType.nameFromValue(type);
        this.type = type;
    }

    @Override
    public void setValueConstraints(String[] constraints) {
        this.constraints = constraints;
    }

    @Override
    public void setDefaultValues(Value[] defaultValues) {
        this.defaultValues = defaultValues;
    }

    @Override
    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    @Override
    public void setAvailableQueryOperators(String[] operators) {
        this.queryOperators = operators;
    }

    @Override
    public void setFullTextSearchable(boolean searchable) {
        this.fullTextSearchable = searchable;
    }

    @Override
    public void setQueryOrderable(boolean orderable) {
        this.queryOrderable = orderable;
    }

    @Override
    public int getRequiredType() {
        return this.type;
    }

    @Override
    public String[] getValueConstraints() {
        return this.constraints;
    }

    @Override
    public Value[] getDefaultValues() {
        return this.defaultValues;
    }

    @Override
    public boolean isMultiple() {
        return this.multiple;
    }

    @Override
    public String[] getAvailableQueryOperators() {
        return this.queryOperators;
    }

    @Override
    public boolean isFullTextSearchable() {
        return this.fullTextSearchable;
    }

    @Override
    public boolean isQueryOrderable() {
        return this.queryOrderable;
    }
}

