/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.nodetype.constraint;

import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.ConstraintViolationException;
import org.apache.jackrabbit.spi.QValue;
import org.apache.jackrabbit.spi.commons.nodetype.InvalidConstraintException;
import org.apache.jackrabbit.spi.commons.nodetype.constraint.ValueConstraint;

class BooleanConstraint
extends ValueConstraint {
    private final boolean reqBool;

    public BooleanConstraint(String definition) throws InvalidConstraintException {
        super(definition);
        if (definition.equals("true")) {
            this.reqBool = true;
        } else if (definition.equals("false")) {
            this.reqBool = false;
        } else {
            String msg = "'" + definition + "' is not a valid value constraint format for BOOLEAN values";
            log.debug(msg);
            throw new InvalidConstraintException(msg);
        }
    }

    @Override
    public void check(QValue value) throws ConstraintViolationException, RepositoryException {
        if (value == null) {
            throw new ConstraintViolationException("null value does not satisfy the constraint '" + this.getString() + "'");
        }
        switch (value.getType()) {
            case 6: {
                boolean b = Boolean.valueOf(value.getString());
                if (b != this.reqBool) {
                    throw new ConstraintViolationException("'" + b + "' does not satisfy the constraint '" + this.getString() + "'");
                }
                return;
            }
        }
        String msg = "BOOLEAN constraint can not be applied to value of type: " + PropertyType.nameFromValue(value.getType());
        log.debug(msg);
        throw new RepositoryException(msg);
    }
}

