/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.converters;

import java.util.List;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.converters.AbstractArrayConverter;

@Deprecated
public final class LongArrayConverter
extends AbstractArrayConverter {
    private static final long[] MODEL = new long[0];

    public LongArrayConverter() {
        this.defaultValue = null;
        this.useDefault = false;
    }

    public LongArrayConverter(Object defaultValue) {
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
        if (strings.getClass() == value.getClass()) {
            try {
                String[] values = (String[])value;
                long[] results = new long[values.length];
                for (int i = 0; i < values.length; ++i) {
                    results[i] = Long.parseLong(values[i]);
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
            long[] results = new long[list.size()];
            for (int i = 0; i < results.length; ++i) {
                results[i] = Long.parseLong((String)list.get(i));
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

