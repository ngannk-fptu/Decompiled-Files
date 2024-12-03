/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.nodetype.constraint;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import javax.jcr.nodetype.ConstraintViolationException;
import org.apache.jackrabbit.spi.QValue;
import org.apache.jackrabbit.spi.commons.nodetype.InvalidConstraintException;
import org.apache.jackrabbit.spi.commons.nodetype.constraint.ValueConstraint;
import org.apache.jackrabbit.value.DateValue;

class DateConstraint
extends ValueConstraint {
    private final boolean lowerInclusive;
    private final Calendar lowerLimit;
    private final boolean upperInclusive;
    private final Calendar upperLimit;

    public DateConstraint(String definition) throws InvalidConstraintException {
        block6: {
            super(definition);
            Pattern pattern = Pattern.compile("([\\(\\[]) *([0-9TZ\\.\\+-:]*)? *, *([0-9TZ\\.\\+-:]*)? *([\\)\\]])");
            Matcher matcher = pattern.matcher(definition);
            if (matcher.matches()) {
                try {
                    String s = matcher.group(1);
                    this.lowerInclusive = s.equals("[");
                    s = matcher.group(2);
                    this.lowerLimit = s == null || s.length() == 0 ? null : DateValue.valueOf(matcher.group(2)).getDate();
                    s = matcher.group(3);
                    this.upperLimit = s == null || s.length() == 0 ? null : DateValue.valueOf(matcher.group(3)).getDate();
                    s = matcher.group(4);
                    this.upperInclusive = s.equals("]");
                    if (this.lowerLimit == null && this.upperLimit == null) {
                        String msg = "'" + definition + "' is not a valid value constraint format for dates: neither min- nor max-date specified";
                        log.debug(msg);
                        throw new InvalidConstraintException(msg);
                    }
                    if (this.lowerLimit != null && this.upperLimit != null && this.lowerLimit.after(this.upperLimit)) {
                        String msg = "'" + definition + "' is not a valid value constraint format for dates: min-date > max-date";
                        log.debug(msg);
                        throw new InvalidConstraintException(msg);
                    }
                    break block6;
                }
                catch (ValueFormatException vfe) {
                    String msg = "'" + definition + "' is not a valid value constraint format for dates";
                    log.debug(msg);
                    throw new InvalidConstraintException(msg, vfe);
                }
                catch (RepositoryException re) {
                    String msg = "'" + definition + "' is not a valid value constraint format for dates";
                    log.debug(msg);
                    throw new InvalidConstraintException(msg, re);
                }
            }
            String msg = "'" + definition + "' is not a valid value constraint format for dates";
            log.debug(msg);
            throw new InvalidConstraintException(msg);
        }
    }

    private void check(Calendar cal) throws ConstraintViolationException {
        if (cal == null) {
            throw new ConstraintViolationException("null value does not satisfy the constraint '" + this.getString() + "'");
        }
        if (this.lowerLimit != null && (this.lowerInclusive ? cal.getTimeInMillis() < this.lowerLimit.getTimeInMillis() : cal.getTimeInMillis() <= this.lowerLimit.getTimeInMillis())) {
            throw new ConstraintViolationException(cal + " does not satisfy the constraint '" + this.getString() + "'");
        }
        if (this.upperLimit != null && (this.upperInclusive ? cal.getTimeInMillis() > this.upperLimit.getTimeInMillis() : cal.getTimeInMillis() >= this.upperLimit.getTimeInMillis())) {
            throw new ConstraintViolationException(cal + " does not satisfy the constraint '" + this.getString() + "'");
        }
    }

    @Override
    public void check(QValue value) throws ConstraintViolationException, RepositoryException {
        if (value == null) {
            throw new ConstraintViolationException("null value does not satisfy the constraint '" + this.getString() + "'");
        }
        switch (value.getType()) {
            case 5: {
                this.check(value.getCalendar());
                return;
            }
        }
        String msg = "DATE constraint can not be applied to value of type: " + PropertyType.nameFromValue(value.getType());
        log.debug(msg);
        throw new RepositoryException(msg);
    }
}

