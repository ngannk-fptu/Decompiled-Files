/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons;

import java.util.Arrays;
import java.util.HashSet;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.nodetype.PropertyDefinition;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.QPropertyDefinition;
import org.apache.jackrabbit.spi.QValue;
import org.apache.jackrabbit.spi.QValueConstraint;
import org.apache.jackrabbit.spi.QValueFactory;
import org.apache.jackrabbit.spi.commons.QItemDefinitionImpl;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.name.NameConstants;
import org.apache.jackrabbit.spi.commons.nodetype.constraint.ValueConstraint;
import org.apache.jackrabbit.spi.commons.value.ValueFormat;

public class QPropertyDefinitionImpl
extends QItemDefinitionImpl
implements QPropertyDefinition {
    private static final long serialVersionUID = 1064686456661663541L;
    private final int requiredType;
    private final QValueConstraint[] valueConstraints;
    private final QValue[] defaultValues;
    private final boolean multiple;
    private final String[] availableQueryOperators;
    private final boolean fullTextSearchable;
    private final boolean queryOrderable;

    public QPropertyDefinitionImpl(QPropertyDefinition propDef) {
        this(propDef.getName(), propDef.getDeclaringNodeType(), propDef.isAutoCreated(), propDef.isMandatory(), propDef.getOnParentVersion(), propDef.isProtected(), propDef.getDefaultValues(), propDef.isMultiple(), propDef.getRequiredType(), propDef.getValueConstraints(), propDef.getAvailableQueryOperators(), propDef.isFullTextSearchable(), propDef.isQueryOrderable());
    }

    public QPropertyDefinitionImpl(Name name, Name declaringNodeType, boolean isAutoCreated, boolean isMandatory, int onParentVersion, boolean isProtected, QValue[] defaultValues, boolean isMultiple, int requiredType, QValueConstraint[] valueConstraints, String[] availableQueryOperators, boolean isFullTextSearchable, boolean isQueryOrderable) {
        super(name, declaringNodeType, isAutoCreated, isMandatory, onParentVersion, isProtected);
        if (valueConstraints == null) {
            throw new NullPointerException("valueConstraints");
        }
        if (availableQueryOperators == null) {
            throw new NullPointerException("availableQueryOperators");
        }
        this.defaultValues = defaultValues;
        this.multiple = isMultiple;
        this.requiredType = requiredType;
        this.valueConstraints = valueConstraints;
        this.availableQueryOperators = availableQueryOperators;
        this.fullTextSearchable = isFullTextSearchable;
        this.queryOrderable = isQueryOrderable;
    }

    public QPropertyDefinitionImpl(PropertyDefinition propDef, NamePathResolver resolver, QValueFactory qValueFactory) throws RepositoryException {
        this(propDef.getName().equals(NameConstants.ANY_NAME.getLocalName()) ? NameConstants.ANY_NAME : resolver.getQName(propDef.getName()), resolver.getQName(propDef.getDeclaringNodeType().getName()), propDef.isAutoCreated(), propDef.isMandatory(), propDef.getOnParentVersion(), propDef.isProtected(), QPropertyDefinitionImpl.convertValues(propDef.getDefaultValues(), resolver, qValueFactory), propDef.isMultiple(), propDef.getRequiredType(), ValueConstraint.create(propDef.getRequiredType(), propDef.getValueConstraints(), resolver), propDef.getAvailableQueryOperators(), propDef.isFullTextSearchable(), propDef.isQueryOrderable());
    }

    @Override
    public int getRequiredType() {
        return this.requiredType;
    }

    @Override
    public QValueConstraint[] getValueConstraints() {
        return this.valueConstraints;
    }

    @Override
    public QValue[] getDefaultValues() {
        return this.defaultValues;
    }

    @Override
    public boolean isMultiple() {
        return this.multiple;
    }

    @Override
    public String[] getAvailableQueryOperators() {
        return this.availableQueryOperators;
    }

    @Override
    public boolean isFullTextSearchable() {
        return this.fullTextSearchable;
    }

    @Override
    public boolean isQueryOrderable() {
        return this.queryOrderable;
    }

    @Override
    public boolean definesNode() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof QPropertyDefinition) {
            QPropertyDefinition other = (QPropertyDefinition)obj;
            return super.equals(obj) && this.requiredType == other.getRequiredType() && this.multiple == other.isMultiple() && this.fullTextSearchable == other.isFullTextSearchable() && this.queryOrderable == other.isQueryOrderable() && (this.valueConstraints == null || other.getValueConstraints() == null ? this.valueConstraints == other.getValueConstraints() : new HashSet<QValueConstraint>(Arrays.asList(this.valueConstraints)).equals(new HashSet<QValueConstraint>(Arrays.asList(other.getValueConstraints())))) && (this.defaultValues == null || other.getDefaultValues() == null ? this.defaultValues == other.getDefaultValues() : new HashSet<QValue>(Arrays.asList(this.defaultValues)).equals(new HashSet<QValue>(Arrays.asList(other.getDefaultValues())))) && new HashSet<String>(Arrays.asList(this.availableQueryOperators)).equals(new HashSet<String>(Arrays.asList(other.getAvailableQueryOperators())));
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            int h = super.hashCode();
            h = 37 * h + this.requiredType;
            h = 37 * h + (this.multiple ? 11 : 43);
            h = 37 * h + (this.queryOrderable ? 11 : 43);
            h = 37 * h + (this.fullTextSearchable ? 11 : 43);
            h = 37 * h + (this.valueConstraints != null ? new HashSet<QValueConstraint>(Arrays.asList(this.valueConstraints)).hashCode() : 0);
            h = 37 * h + (this.defaultValues != null ? new HashSet<QValue>(Arrays.asList(this.defaultValues)).hashCode() : 0);
            this.hashCode = h = 37 * h + new HashSet<String>(Arrays.asList(this.availableQueryOperators)).hashCode();
        }
        return this.hashCode;
    }

    private static QValue[] convertValues(Value[] values, NamePathResolver resolver, QValueFactory factory) throws RepositoryException {
        if (values != null) {
            QValue[] defaultValues = new QValue[values.length];
            for (int i = 0; i < values.length; ++i) {
                defaultValues[i] = ValueFormat.getQValue(values[i], resolver, factory);
            }
            return defaultValues;
        }
        return null;
    }
}

