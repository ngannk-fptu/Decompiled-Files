/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.build.model.conversions;

import aQute.bnd.build.model.conversions.Converter;

public class DefaultFormatter
implements Converter<String, Object> {
    @Override
    public String convert(Object input) throws IllegalArgumentException {
        return input == null ? null : input.toString();
    }

    @Override
    public String error(String msg) {
        return msg;
    }
}

