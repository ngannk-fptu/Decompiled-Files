/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.util.convert;

import com.twelvemonkeys.util.convert.ConversionException;
import com.twelvemonkeys.util.convert.Converter;
import com.twelvemonkeys.util.convert.MissingTypeException;
import com.twelvemonkeys.util.convert.NoAvailableConverterException;
import com.twelvemonkeys.util.convert.PropertyConverter;

class ConverterImpl
extends Converter {
    ConverterImpl() {
    }

    private PropertyConverter getConverterForType(Class clazz) {
        Class clazz2 = clazz;
        do {
            PropertyConverter propertyConverter;
            if ((propertyConverter = ConverterImpl.getInstance().converters.get(clazz2)) == null) continue;
            return propertyConverter;
        } while ((clazz2 = clazz2.getSuperclass()) != null);
        return null;
    }

    @Override
    public Object toObject(String string, Class clazz, String string2) throws ConversionException {
        if (string == null) {
            return null;
        }
        if (clazz == null) {
            throw new MissingTypeException();
        }
        PropertyConverter propertyConverter = this.getConverterForType(clazz);
        if (propertyConverter == null) {
            throw new NoAvailableConverterException("Cannot convert to object, no converter available for type \"" + clazz.getName() + "\"");
        }
        return propertyConverter.toObject(string, clazz, string2);
    }

    @Override
    public String toString(Object object, String string) throws ConversionException {
        if (object == null) {
            return null;
        }
        PropertyConverter propertyConverter = this.getConverterForType(object.getClass());
        if (propertyConverter == null) {
            throw new NoAvailableConverterException("Cannot object to string, no converter available for type \"" + object.getClass().getName() + "\"");
        }
        return propertyConverter.toString(object, string);
    }
}

