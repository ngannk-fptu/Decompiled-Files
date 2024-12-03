/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.converters;

import java.util.List;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.converters.AbstractArrayConverter;

@Deprecated
public final class StringArrayConverter
extends AbstractArrayConverter {
    private static final String[] MODEL = new String[0];
    private static final int[] INT_MODEL = new int[0];

    public StringArrayConverter() {
        this.defaultValue = null;
        this.useDefault = false;
    }

    public StringArrayConverter(Object defaultValue) {
        this.defaultValue = defaultValue;
        this.useDefault = true;
    }

    @Override
    public Object convert(Class type, Object value) {
        if (value == null) {
            if (this.useDefault) {
                return this.defaultValue;
            }
            throw new ConversionException("No value specified");
        }
        if (MODEL.getClass() == value.getClass()) {
            return value;
        }
        if (INT_MODEL.getClass() == value.getClass()) {
            int[] values = (int[])value;
            String[] results = new String[values.length];
            for (int i = 0; i < values.length; ++i) {
                results[i] = Integer.toString(values[i]);
            }
            return results;
        }
        try {
            List list = this.parseElements(value.toString());
            String[] results = new String[list.size()];
            for (int i = 0; i < results.length; ++i) {
                results[i] = (String)list.get(i);
            }
            return results;
        }
        catch (Exception e) {
            if (this.useDefault) {
                return this.defaultValue;
            }
            throw new ConversionException(value.toString(), e);
        }
    }
}

