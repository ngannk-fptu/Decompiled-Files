/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.converters;

import org.apache.commons.beanutils.converters.AbstractConverter;

public final class CharacterConverter
extends AbstractConverter {
    public CharacterConverter() {
    }

    public CharacterConverter(Object defaultValue) {
        super(defaultValue);
    }

    @Override
    protected Class<?> getDefaultType() {
        return Character.class;
    }

    @Override
    protected String convertToString(Object value) {
        String strValue = value.toString();
        return strValue.length() == 0 ? "" : strValue.substring(0, 1);
    }

    @Override
    protected <T> T convertToType(Class<T> type, Object value) throws Exception {
        if (Character.class.equals(type) || Character.TYPE.equals(type)) {
            return type.cast(new Character(value.toString().charAt(0)));
        }
        throw this.conversionException(type, value);
    }
}

