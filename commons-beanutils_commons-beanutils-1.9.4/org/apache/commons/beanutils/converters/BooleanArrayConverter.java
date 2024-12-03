/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.converters;

import java.util.List;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.converters.AbstractArrayConverter;
import org.apache.commons.beanutils.converters.BooleanConverter;

@Deprecated
public final class BooleanArrayConverter
extends AbstractArrayConverter {
    public static final Class MODEL = new boolean[0].getClass();
    private static final BooleanConverter DEFAULT_CONVERTER = new BooleanConverter();
    protected final BooleanConverter booleanConverter;

    public BooleanArrayConverter() {
        this.booleanConverter = DEFAULT_CONVERTER;
    }

    public BooleanArrayConverter(Object defaultValue) {
        super(defaultValue);
        this.booleanConverter = DEFAULT_CONVERTER;
    }

    public BooleanArrayConverter(BooleanConverter converter, Object defaultValue) {
        super(defaultValue);
        this.booleanConverter = converter;
    }

    @Override
    public Object convert(Class type, Object value) {
        if (value == null) {
            if (this.useDefault) {
                return this.defaultValue;
            }
            throw new ConversionException("No value specified");
        }
        if (MODEL == value.getClass()) {
            return value;
        }
        if (strings.getClass() == value.getClass()) {
            try {
                String[] values = (String[])value;
                boolean[] results = new boolean[values.length];
                for (int i = 0; i < values.length; ++i) {
                    String stringValue = values[i];
                    Boolean result = this.booleanConverter.convert(Boolean.class, stringValue);
                    results[i] = result;
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
        try {
            List list = this.parseElements(value.toString());
            boolean[] results = new boolean[list.size()];
            for (int i = 0; i < results.length; ++i) {
                String stringValue = (String)list.get(i);
                Boolean result = this.booleanConverter.convert(Boolean.class, stringValue);
                results[i] = result;
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

