/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.nodetype;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.jackrabbit.commons.query.qom.Operator;
import org.apache.jackrabbit.spi.QPropertyDefinition;
import org.apache.jackrabbit.spi.QValue;
import org.apache.jackrabbit.spi.QValueConstraint;
import org.apache.jackrabbit.spi.commons.QPropertyDefinitionImpl;
import org.apache.jackrabbit.spi.commons.nodetype.QItemDefinitionBuilder;

public class QPropertyDefinitionBuilder
extends QItemDefinitionBuilder {
    private int requiredType = 0;
    private List<QValueConstraint> valueConstraints = new ArrayList<QValueConstraint>();
    private List<QValue> defaultValues;
    private boolean isMultiple = false;
    private boolean fullTextSearchable = true;
    private boolean queryOrderable = true;
    private String[] queryOperators = Operator.getAllQueryOperators();

    public void setRequiredType(int type) {
        this.requiredType = type;
    }

    public int getRequiredType() {
        return this.requiredType;
    }

    public void addValueConstraint(QValueConstraint constraint) {
        this.valueConstraints.add(constraint);
    }

    public void setValueConstraints(QValueConstraint[] constraints) {
        this.valueConstraints.clear();
        this.valueConstraints.addAll(Arrays.asList(constraints));
    }

    public QValueConstraint[] getValueConstraints() {
        return this.valueConstraints.toArray(new QValueConstraint[this.valueConstraints.size()]);
    }

    public void addDefaultValue(QValue value) {
        if (this.defaultValues == null) {
            this.defaultValues = new ArrayList<QValue>();
        }
        this.defaultValues.add(value);
    }

    public void setDefaultValues(QValue[] values) {
        this.defaultValues = values == null ? null : new ArrayList<QValue>(Arrays.asList(values));
    }

    public QValue[] getDefaultValues() {
        if (this.defaultValues == null) {
            return null;
        }
        return this.defaultValues.toArray(new QValue[this.defaultValues.size()]);
    }

    public void setMultiple(boolean isMultiple) {
        this.isMultiple = isMultiple;
    }

    public boolean getMultiple() {
        return this.isMultiple;
    }

    public boolean getFullTextSearchable() {
        return this.fullTextSearchable;
    }

    public void setFullTextSearchable(boolean fullTextSearchable) {
        this.fullTextSearchable = fullTextSearchable;
    }

    public boolean getQueryOrderable() {
        return this.queryOrderable;
    }

    public void setQueryOrderable(boolean queryOrderable) {
        this.queryOrderable = queryOrderable;
    }

    public String[] getAvailableQueryOperators() {
        return this.queryOperators;
    }

    public void setAvailableQueryOperators(String[] queryOperators) {
        this.queryOperators = queryOperators;
    }

    public QPropertyDefinition build() throws IllegalStateException {
        return new QPropertyDefinitionImpl(this.getName(), this.getDeclaringNodeType(), this.getAutoCreated(), this.getMandatory(), this.getOnParentVersion(), this.getProtected(), this.getDefaultValues(), this.getMultiple(), this.getRequiredType(), this.getValueConstraints(), this.getAvailableQueryOperators(), this.getFullTextSearchable(), this.getQueryOrderable());
    }
}

