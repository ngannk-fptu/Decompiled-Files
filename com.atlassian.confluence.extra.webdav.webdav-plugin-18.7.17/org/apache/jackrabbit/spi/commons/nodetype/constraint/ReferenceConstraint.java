/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.nodetype.constraint;

import javax.jcr.NamespaceException;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.ConstraintViolationException;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.QValue;
import org.apache.jackrabbit.spi.commons.conversion.NameException;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.conversion.NameResolver;
import org.apache.jackrabbit.spi.commons.nodetype.InvalidConstraintException;
import org.apache.jackrabbit.spi.commons.nodetype.constraint.ValueConstraint;

class ReferenceConstraint
extends ValueConstraint {
    private final Name ntName;

    static ReferenceConstraint create(String nameString) {
        return new ReferenceConstraint(nameString, NAME_FACTORY.create(nameString));
    }

    static ReferenceConstraint create(String jcrName, NameResolver resolver) throws InvalidConstraintException {
        try {
            Name name = resolver.getQName(jcrName);
            return new ReferenceConstraint(name.toString(), name);
        }
        catch (NameException e) {
            String msg = "Invalid name constraint: " + jcrName;
            log.debug(msg);
            throw new InvalidConstraintException(msg, e);
        }
        catch (NamespaceException e) {
            String msg = "Invalid name constraint: " + jcrName;
            log.debug(msg);
            throw new InvalidConstraintException(msg, e);
        }
    }

    private ReferenceConstraint(String nameString, Name ntName) {
        super(nameString);
        this.ntName = ntName;
    }

    @Override
    public String getDefinition(NamePathResolver resolver) {
        try {
            return resolver.getJCRName(this.ntName);
        }
        catch (NamespaceException e) {
            return this.getString();
        }
    }

    @Override
    public void check(QValue value) throws ConstraintViolationException, RepositoryException {
        if (value == null) {
            throw new ConstraintViolationException("Null value does not satisfy the constraint '" + this.getString() + "'");
        }
        switch (value.getType()) {
            case 9: 
            case 10: {
                log.warn("validation of reference constraint is not yet implemented");
                return;
            }
        }
        String msg = "Reference constraint can not be applied to value of type: " + PropertyType.nameFromValue(value.getType());
        log.debug(msg);
        throw new RepositoryException(msg);
    }
}

