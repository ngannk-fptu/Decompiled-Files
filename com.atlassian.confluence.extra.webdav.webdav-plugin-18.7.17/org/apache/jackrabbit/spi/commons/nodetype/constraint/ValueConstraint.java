/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.spi.commons.nodetype.constraint;

import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.ConstraintViolationException;
import org.apache.jackrabbit.spi.NameFactory;
import org.apache.jackrabbit.spi.QPropertyDefinition;
import org.apache.jackrabbit.spi.QValue;
import org.apache.jackrabbit.spi.QValueConstraint;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.name.NameFactoryImpl;
import org.apache.jackrabbit.spi.commons.nodetype.InvalidConstraintException;
import org.apache.jackrabbit.spi.commons.nodetype.constraint.BooleanConstraint;
import org.apache.jackrabbit.spi.commons.nodetype.constraint.DateConstraint;
import org.apache.jackrabbit.spi.commons.nodetype.constraint.NameConstraint;
import org.apache.jackrabbit.spi.commons.nodetype.constraint.NumericConstraint;
import org.apache.jackrabbit.spi.commons.nodetype.constraint.PathConstraint;
import org.apache.jackrabbit.spi.commons.nodetype.constraint.ReferenceConstraint;
import org.apache.jackrabbit.spi.commons.nodetype.constraint.StringConstraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ValueConstraint
implements QValueConstraint {
    protected static Logger log = LoggerFactory.getLogger(ValueConstraint.class);
    public static final ValueConstraint[] EMPTY_ARRAY = new ValueConstraint[0];
    static final NameFactory NAME_FACTORY = NameFactoryImpl.getInstance();
    private final String definition;

    protected ValueConstraint(String definition) {
        this.definition = definition;
    }

    public String getDefinition(NamePathResolver resolver) {
        return this.definition;
    }

    @Override
    public String getString() {
        return this.definition;
    }

    public String toString() {
        return this.getString();
    }

    public boolean equals(Object other) {
        return other == this || other instanceof ValueConstraint && this.definition.equals(((ValueConstraint)other).definition);
    }

    public int hashCode() {
        return this.definition.hashCode();
    }

    public static ValueConstraint create(int type, String definition) throws InvalidConstraintException {
        if (definition == null) {
            throw new IllegalArgumentException("illegal definition (null)");
        }
        switch (type) {
            case 1: 
            case 11: {
                return new StringConstraint(definition);
            }
            case 6: {
                return new BooleanConstraint(definition);
            }
            case 2: {
                return new NumericConstraint(definition);
            }
            case 5: {
                return new DateConstraint(definition);
            }
            case 3: 
            case 4: 
            case 12: {
                return new NumericConstraint(definition);
            }
            case 7: {
                return NameConstraint.create(definition);
            }
            case 8: {
                return PathConstraint.create(definition);
            }
            case 9: 
            case 10: {
                return ReferenceConstraint.create(definition);
            }
        }
        throw new IllegalArgumentException("unknown/unsupported target type for constraint: " + PropertyType.nameFromValue(type));
    }

    public static ValueConstraint[] create(int type, String[] definition) throws InvalidConstraintException {
        if (definition == null || definition.length == 0) {
            return EMPTY_ARRAY;
        }
        ValueConstraint[] ret = new ValueConstraint[definition.length];
        for (int i = 0; i < ret.length; ++i) {
            ret[i] = ValueConstraint.create(type, definition[i]);
        }
        return ret;
    }

    public static ValueConstraint[] create(int type, String[] jcrDefinition, NamePathResolver resolver) throws InvalidConstraintException {
        if (jcrDefinition == null || jcrDefinition.length == 0) {
            return EMPTY_ARRAY;
        }
        ValueConstraint[] ret = new ValueConstraint[jcrDefinition.length];
        for (int i = 0; i < ret.length; ++i) {
            ret[i] = ValueConstraint.create(type, jcrDefinition[i], resolver);
        }
        return ret;
    }

    public static ValueConstraint create(int type, String jcrDefinition, NamePathResolver resolver) throws InvalidConstraintException {
        if (jcrDefinition == null) {
            throw new IllegalArgumentException("Illegal definition (null) for ValueConstraint.");
        }
        switch (type) {
            case 1: 
            case 11: {
                return new StringConstraint(jcrDefinition);
            }
            case 6: {
                return new BooleanConstraint(jcrDefinition);
            }
            case 2: {
                return new NumericConstraint(jcrDefinition);
            }
            case 5: {
                return new DateConstraint(jcrDefinition);
            }
            case 3: 
            case 4: 
            case 12: {
                return new NumericConstraint(jcrDefinition);
            }
            case 7: {
                return NameConstraint.create(jcrDefinition, resolver);
            }
            case 8: {
                return PathConstraint.create(jcrDefinition, resolver);
            }
            case 9: 
            case 10: {
                return ReferenceConstraint.create(jcrDefinition, resolver);
            }
        }
        throw new IllegalArgumentException("Unknown/unsupported target type for constraint: " + PropertyType.nameFromValue(type));
    }

    public static void checkValueConstraints(QPropertyDefinition pd, QValue[] values) throws ConstraintViolationException, RepositoryException {
        if (!pd.isMultiple() && values != null && values.length > 1) {
            throw new ConstraintViolationException("the property is not multi-valued");
        }
        QValueConstraint[] constraints = pd.getValueConstraints();
        if (constraints == null || constraints.length == 0) {
            return;
        }
        if (values != null && values.length > 0) {
            for (QValue value : values) {
                boolean satisfied = false;
                ConstraintViolationException cve = null;
                for (int j = 0; j < constraints.length && !satisfied; ++j) {
                    try {
                        constraints[j].check(value);
                        satisfied = true;
                        continue;
                    }
                    catch (ConstraintViolationException e) {
                        cve = e;
                        continue;
                    }
                    catch (InvalidConstraintException e) {
                        cve = new ConstraintViolationException(e.getMessage(), e);
                    }
                }
                if (satisfied) continue;
                throw cve;
            }
        }
    }
}

