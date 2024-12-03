/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.converters;

import java.util.List;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.converters.AbstractArrayConverter;

@Deprecated
public final class ShortArrayConverter
extends AbstractArrayConverter {
    private static final short[] MODEL = new short[0];

    public ShortArrayConverter() {
        this.defaultValue = null;
        this.useDefault = false;
    }

    public ShortArrayConverter(Object defaultValue) {
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
                short[] results = new short[values.length];
                for (int i = 0; i < values.length; ++i) {
                    results[i] = Short.parseShort(values[i]);
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
            short[] results = new short[list.size()];
            for (int i = 0; i < results.length; ++i) {
                results[i] = Short.parseShort((String)list.get(i));
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

