/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.build.model.conversions;

import aQute.bnd.build.model.conversions.Converter;
import java.util.Map;

public class PropertiesEntryFormatter
implements Converter<String, Map.Entry<String, String>> {
    @Override
    public String convert(Map.Entry<String, String> entry) {
        StringBuilder buffer = new StringBuilder();
        String name = entry.getKey();
        buffer.append(name).append('=');
        String value = entry.getValue();
        if (value != null && value.length() > 0) {
            int quotableIndex = value.indexOf(44);
            if (quotableIndex == -1) {
                quotableIndex = value.indexOf(61);
            }
            if (quotableIndex >= 0) {
                buffer.append('\'').append(value).append('\'');
            } else {
                buffer.append(value);
            }
        }
        return buffer.toString();
    }

    @Override
    public String error(String msg) {
        return msg;
    }
}

