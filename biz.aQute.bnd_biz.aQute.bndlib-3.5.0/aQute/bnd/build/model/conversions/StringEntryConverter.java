/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.build.model.conversions;

import aQute.bnd.build.model.conversions.Converter;
import java.util.Map;

public class StringEntryConverter
implements Converter<String, Map.Entry<String, ?>> {
    @Override
    public String convert(Map.Entry<String, ?> input) throws IllegalArgumentException {
        if (input == null) {
            return null;
        }
        return input.getKey();
    }

    @Override
    public String error(String msg) {
        return msg;
    }
}

