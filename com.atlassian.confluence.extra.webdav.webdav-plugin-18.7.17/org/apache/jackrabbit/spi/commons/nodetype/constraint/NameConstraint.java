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

class NameConstraint
extends ValueConstraint {
    private final Name name;

    static NameConstraint create(String nameString) {
        return new NameConstraint(nameString, NAME_FACTORY.create(nameString));
    }

    static NameConstraint create(String jcrName, NameResolver resolver) throws InvalidConstraintException {
        try {
            Name name = resolver.getQName(jcrName);
            return new NameConstraint(name.toString(), name);
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

    private NameConstraint(String nameString, Name name) {
        super(nameString);
        this.name = name;
    }

    @Override
    public String getDefinition(NamePathResolver resolver) {
        try {
            return resolver.getJCRName(this.name);
        }
        catch (NamespaceException e) {
            return this.getString();
        }
    }

    @Override
    public void check(QValue value) throws ConstraintViolationException, RepositoryException {
        if (value == null) {
            throw new ConstraintViolationException("null value does not satisfy the constraint '" + this.getString() + "'");
        }
        switch (value.getType()) {
            case 7: {
                Name n = value.getName();
                if (!this.name.equals(n)) {
                    throw new ConstraintViolationException(n + " does not satisfy the constraint '" + this.getString() + "'");
                }
                return;
            }
        }
        String msg = "NAME constraint can not be applied to value of type: " + PropertyType.nameFromValue(value.getType());
        log.debug(msg);
        throw new RepositoryException(msg);
    }
}

