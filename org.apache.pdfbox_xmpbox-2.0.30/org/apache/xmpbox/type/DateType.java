/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.type;

import java.io.IOException;
import java.util.Calendar;
import org.apache.xmpbox.DateConverter;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.type.AbstractSimpleProperty;

public class DateType
extends AbstractSimpleProperty {
    private Calendar dateValue;

    public DateType(XMPMetadata metadata, String namespaceURI, String prefix, String propertyName, Object value) {
        super(metadata, namespaceURI, prefix, propertyName, value);
    }

    private void setValueFromCalendar(Calendar value) {
        this.dateValue = value;
    }

    @Override
    public Calendar getValue() {
        return this.dateValue;
    }

    private boolean isGoodType(Object value) {
        if (value instanceof Calendar) {
            return true;
        }
        if (value instanceof String) {
            try {
                DateConverter.toCalendar((String)value);
                return true;
            }
            catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public void setValue(Object value) {
        if (!this.isGoodType(value)) {
            if (value == null) {
                throw new IllegalArgumentException("Value null is not allowed for the Date type");
            }
            throw new IllegalArgumentException("Value given is not allowed for the Date type: " + value.getClass() + ", value: " + value);
        }
        if (value instanceof String) {
            this.setValueFromString((String)value);
        } else {
            this.setValueFromCalendar((Calendar)value);
        }
    }

    @Override
    public String getStringValue() {
        return DateConverter.toISO8601(this.dateValue);
    }

    private void setValueFromString(String value) {
        try {
            this.setValueFromCalendar(DateConverter.toCalendar(value));
        }
        catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}

