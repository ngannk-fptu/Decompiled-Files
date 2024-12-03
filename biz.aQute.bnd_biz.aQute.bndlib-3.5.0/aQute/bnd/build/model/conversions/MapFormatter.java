/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.build.model.conversions;

import aQute.bnd.build.model.conversions.CollectionFormatter;
import aQute.bnd.build.model.conversions.Converter;
import java.util.Map;

public class MapFormatter
implements Converter<String, Map<String, String>> {
    private CollectionFormatter<Map.Entry<String, String>> entrySetFormatter;

    public MapFormatter(String listSeparator, Converter<String, ? super Map.Entry<String, String>> entryFormatter, String emptyOutput) {
        this.entrySetFormatter = new CollectionFormatter<Map.Entry<String, String>>(listSeparator, entryFormatter, emptyOutput);
    }

    @Override
    public String convert(Map<String, String> input) throws IllegalArgumentException {
        return this.entrySetFormatter.convert(input.entrySet());
    }

    @Override
    public String error(String msg) {
        return msg;
    }
}

