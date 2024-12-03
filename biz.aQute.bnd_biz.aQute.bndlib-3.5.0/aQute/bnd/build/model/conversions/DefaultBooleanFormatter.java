/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.build.model.conversions;

import aQute.bnd.build.model.conversions.Converter;

public class DefaultBooleanFormatter
implements Converter<String, Boolean> {
    private final boolean defaultValue;

    public DefaultBooleanFormatter(boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String convert(Boolean input) throws IllegalArgumentException {
        String result = null;
        if (input != null && input != this.defaultValue) {
            result = input.toString();
        }
        return result;
    }

    @Override
    public String error(String msg) {
        return msg;
    }
}

