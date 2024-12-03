/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.nodetype.constraint;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.ConstraintViolationException;
import org.apache.jackrabbit.spi.QValue;
import org.apache.jackrabbit.spi.commons.nodetype.InvalidConstraintException;
import org.apache.jackrabbit.spi.commons.nodetype.constraint.ValueConstraint;

class StringConstraint
extends ValueConstraint {
    private final Pattern pattern;

    public StringConstraint(String definition) throws InvalidConstraintException {
        super(definition);
        try {
            this.pattern = Pattern.compile(definition);
        }
        catch (PatternSyntaxException pse) {
            String msg = "'" + definition + "' is not valid regular expression syntax";
            log.debug(msg);
            throw new InvalidConstraintException(msg, pse);
        }
    }

    @Override
    public void check(QValue value) throws ConstraintViolationException, RepositoryException {
        if (value == null) {
            throw new ConstraintViolationException("null value does not satisfy the constraint '" + this.getString() + "'");
        }
        switch (value.getType()) {
            case 1: 
            case 11: {
                String text = value.getString();
                Matcher matcher = this.pattern.matcher(text);
                if (!matcher.matches()) {
                    throw new ConstraintViolationException("'" + text + "' does not satisfy the constraint '" + this.getString() + "'");
                }
                return;
            }
        }
        String msg = "String constraint can not be applied to value of type: " + PropertyType.nameFromValue(value.getType());
        log.debug(msg);
        throw new RepositoryException(msg);
    }
}

