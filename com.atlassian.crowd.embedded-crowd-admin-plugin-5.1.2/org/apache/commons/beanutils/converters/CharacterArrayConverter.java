/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.converters;

import java.util.List;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.converters.AbstractArrayConverter;

@Deprecated
public final class CharacterArrayConverter
extends AbstractArrayConverter {
    private static final char[] MODEL = new char[0];

    public CharacterArrayConverter() {
        this.defaultValue = null;
        this.useDefault = false;
    }

    public CharacterArrayConverter(Object defaultValue) {
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
                char[] results = new char[values.length];
                for (int i = 0; i < values.length; ++i) {
                    results[i] = values[i].charAt(0);
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
            char[] results = new char[list.size()];
            for (int i = 0; i < results.length; ++i) {
                results[i] = ((String)list.get(i)).charAt(0);
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

