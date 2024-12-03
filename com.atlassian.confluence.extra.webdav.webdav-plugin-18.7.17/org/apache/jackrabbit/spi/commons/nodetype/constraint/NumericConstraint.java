/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.nodetype.constraint;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.ConstraintViolationException;
import org.apache.jackrabbit.spi.QValue;
import org.apache.jackrabbit.spi.commons.nodetype.InvalidConstraintException;
import org.apache.jackrabbit.spi.commons.nodetype.constraint.ValueConstraint;

class NumericConstraint
extends ValueConstraint {
    private final boolean lowerInclusive;
    private final Double lowerLimit;
    private final boolean upperInclusive;
    private final Double upperLimit;

    public NumericConstraint(String definition) throws InvalidConstraintException {
        block5: {
            super(definition);
            Pattern pattern = Pattern.compile("([\\(\\[]) *(\\-?\\d+\\.?\\d*)? *, *(\\-?\\d+\\.?\\d*)? *([\\)\\]])");
            Matcher matcher = pattern.matcher(definition);
            if (matcher.matches()) {
                try {
                    String s = matcher.group(1);
                    this.lowerInclusive = s.equals("[");
                    s = matcher.group(2);
                    this.lowerLimit = s == null || s.length() == 0 ? null : Double.valueOf(matcher.group(2));
                    s = matcher.group(3);
                    this.upperLimit = s == null || s.length() == 0 ? null : Double.valueOf(matcher.group(3));
                    s = matcher.group(4);
                    this.upperInclusive = s.equals("]");
                    if (this.lowerLimit == null && this.upperLimit == null) {
                        String msg = "'" + definition + "' is not a valid value constraint format for numeric types: neither lower- nor upper-limit specified";
                        log.debug(msg);
                        throw new InvalidConstraintException(msg);
                    }
                    if (this.lowerLimit != null && this.upperLimit != null && this.lowerLimit > this.upperLimit) {
                        String msg = "'" + definition + "' is not a valid value constraint format for numeric types: lower-limit exceeds upper-limit";
                        log.debug(msg);
                        throw new InvalidConstraintException(msg);
                    }
                    break block5;
                }
                catch (NumberFormatException nfe) {
                    String msg = "'" + definition + "' is not a valid value constraint format for numeric types";
                    log.debug(msg);
                    throw new InvalidConstraintException(msg, nfe);
                }
            }
            String msg = "'" + definition + "' is not a valid value constraint format for numeric values";
            log.debug(msg);
            throw new InvalidConstraintException(msg);
        }
    }

    private void check(double number) throws ConstraintViolationException {
        if (this.lowerLimit != null && (this.lowerInclusive ? number < this.lowerLimit : number <= this.lowerLimit)) {
            throw new ConstraintViolationException(number + " does not satisfy the constraint '" + this.getString() + "'");
        }
        if (this.upperLimit != null && (this.upperInclusive ? number > this.upperLimit : number >= this.upperLimit)) {
            throw new ConstraintViolationException(number + " does not satisfy the constraint '" + this.getString() + "'");
        }
    }

    @Override
    public void check(QValue value) throws ConstraintViolationException, RepositoryException {
        if (value == null) {
            throw new ConstraintViolationException("null value does not satisfy the constraint '" + this.getString() + "'");
        }
        switch (value.getType()) {
            case 3: {
                this.check(value.getLong());
                return;
            }
            case 4: {
                this.check(value.getDouble());
                return;
            }
            case 12: {
                this.check(value.getDouble());
                return;
            }
            case 2: {
                long length = value.getLength();
                if (length != -1L) {
                    this.check(length);
                } else {
                    log.warn("failed to determine length of binary value");
                }
                return;
            }
        }
        String msg = "numeric constraint can not be applied to value of type: " + PropertyType.nameFromValue(value.getType());
        log.debug(msg);
        throw new RepositoryException(msg);
    }
}

