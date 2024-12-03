/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.ical4j.property;

import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyFactory;
import net.fortuna.ical4j.validate.ValidationException;
import org.apache.commons.lang.BooleanUtils;

public abstract class BooleanProperty
extends Property {
    private boolean value;

    protected BooleanProperty(String aName, PropertyFactory factory) {
        super(aName, factory);
    }

    @Override
    public void setValue(String aValue) {
        this.value = BooleanUtils.toBooleanObject(aValue);
    }

    @Override
    public void validate() throws ValidationException {
    }

    @Override
    public String getValue() {
        return String.valueOf(this.value);
    }
}

