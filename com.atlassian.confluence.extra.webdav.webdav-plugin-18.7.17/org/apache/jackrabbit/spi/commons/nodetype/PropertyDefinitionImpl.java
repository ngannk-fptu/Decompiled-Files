/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.spi.commons.nodetype;

import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.nodetype.PropertyDefinition;
import org.apache.jackrabbit.spi.QPropertyDefinition;
import org.apache.jackrabbit.spi.QValue;
import org.apache.jackrabbit.spi.QValueConstraint;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.nodetype.AbstractNodeTypeManager;
import org.apache.jackrabbit.spi.commons.nodetype.InvalidConstraintException;
import org.apache.jackrabbit.spi.commons.nodetype.ItemDefinitionImpl;
import org.apache.jackrabbit.spi.commons.nodetype.constraint.ValueConstraint;
import org.apache.jackrabbit.spi.commons.value.ValueFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyDefinitionImpl
extends ItemDefinitionImpl
implements PropertyDefinition {
    private static final Logger log = LoggerFactory.getLogger(PropertyDefinitionImpl.class);
    private final ValueFactory valueFactory;

    public PropertyDefinitionImpl(QPropertyDefinition propDef, NamePathResolver resolver, ValueFactory valueFactory) {
        this(propDef, null, resolver, valueFactory);
    }

    public PropertyDefinitionImpl(QPropertyDefinition propDef, AbstractNodeTypeManager ntMgr, NamePathResolver resolver, ValueFactory valueFactory) {
        super(propDef, ntMgr, resolver);
        this.valueFactory = valueFactory;
    }

    public QPropertyDefinition unwrap() {
        return (QPropertyDefinition)this.itemDef;
    }

    @Override
    public Value[] getDefaultValues() {
        QPropertyDefinition pDef = (QPropertyDefinition)this.itemDef;
        QValue[] defVals = pDef.getDefaultValues();
        if (defVals == null) {
            return null;
        }
        Value[] values = new Value[defVals.length];
        for (int i = 0; i < defVals.length; ++i) {
            try {
                values[i] = ValueFormat.getJCRValue(defVals[i], this.resolver, this.valueFactory);
                continue;
            }
            catch (RepositoryException e) {
                String propName = this.getName() == null ? "[null]" : this.getName();
                log.error("illegal default value specified for property " + propName + " in node type " + this.getDeclaringNodeType(), (Throwable)e);
                return null;
            }
        }
        return values;
    }

    @Override
    public int getRequiredType() {
        return ((QPropertyDefinition)this.itemDef).getRequiredType();
    }

    @Override
    public String[] getValueConstraints() {
        QPropertyDefinition pd = (QPropertyDefinition)this.itemDef;
        QValueConstraint[] constraints = pd.getValueConstraints();
        if (constraints == null || constraints.length == 0) {
            return new String[0];
        }
        String[] vca = new String[constraints.length];
        for (int i = 0; i < constraints.length; ++i) {
            try {
                ValueConstraint vc = ValueConstraint.create(pd.getRequiredType(), constraints[i].getString());
                vca[i] = vc.getDefinition(this.resolver);
                continue;
            }
            catch (InvalidConstraintException e) {
                log.warn("Internal error during conversion of constraint.", (Throwable)e);
                vca[i] = constraints[i].getString();
            }
        }
        return vca;
    }

    @Override
    public boolean isMultiple() {
        return ((QPropertyDefinition)this.itemDef).isMultiple();
    }

    @Override
    public String[] getAvailableQueryOperators() {
        return ((QPropertyDefinition)this.itemDef).getAvailableQueryOperators();
    }

    @Override
    public boolean isFullTextSearchable() {
        return ((QPropertyDefinition)this.itemDef).isFullTextSearchable();
    }

    @Override
    public boolean isQueryOrderable() {
        return ((QPropertyDefinition)this.itemDef).isQueryOrderable();
    }
}

