/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.converters;

import java.util.List;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.converters.AbstractArrayConverter;

@Deprecated
public final class FloatArrayConverter
extends AbstractArrayConverter {
    private static final float[] MODEL = new float[0];

    public FloatArrayConverter() {
        this.defaultValue = null;
        this.useDefault = false;
    }

    public FloatArrayConverter(Object defaultValue) {
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
                float[] results = new float[values.length];
                for (int i = 0; i < values.length; ++i) {
                    results[i] = Float.parseFloat(values[i]);
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
            float[] results = new float[list.size()];
            for (int i = 0; i < results.length; ++i) {
                results[i] = Float.parseFloat((String)list.get(i));
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

