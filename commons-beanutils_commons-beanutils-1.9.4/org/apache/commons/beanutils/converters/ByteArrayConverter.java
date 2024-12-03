/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.converters;

import java.util.List;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.converters.AbstractArrayConverter;

@Deprecated
public final class ByteArrayConverter
extends AbstractArrayConverter {
    private static final byte[] MODEL = new byte[0];

    public ByteArrayConverter() {
        this.defaultValue = null;
        this.useDefault = false;
    }

    public ByteArrayConverter(Object defaultValue) {
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
                byte[] results = new byte[values.length];
                for (int i = 0; i < values.length; ++i) {
                    results[i] = Byte.parseByte(values[i]);
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
            byte[] results = new byte[list.size()];
            for (int i = 0; i < results.length; ++i) {
                results[i] = Byte.parseByte((String)list.get(i));
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

