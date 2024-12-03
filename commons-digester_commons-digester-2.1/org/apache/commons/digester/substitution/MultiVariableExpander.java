/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester.substitution;

import java.util.ArrayList;
import java.util.Map;
import org.apache.commons.digester.substitution.VariableExpander;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MultiVariableExpander
implements VariableExpander {
    private int nEntries = 0;
    private ArrayList<String> markers = new ArrayList(2);
    private ArrayList<Map<String, Object>> sources = new ArrayList(2);

    public void addSource(String marker, Map<String, Object> source) {
        ++this.nEntries;
        this.markers.add(marker);
        this.sources.add(source);
    }

    @Override
    public String expand(String param) {
        for (int i = 0; i < this.nEntries; ++i) {
            param = this.expand(param, this.markers.get(i), this.sources.get(i));
        }
        return param;
    }

    public String expand(String str, String marker, Map<String, Object> source) {
        String startMark = marker + "{";
        int markLen = startMark.length();
        int index = 0;
        while ((index = str.indexOf(startMark, index)) != -1) {
            int startIndex = index + markLen;
            if (startIndex > str.length()) {
                throw new IllegalArgumentException("var expression starts at end of string");
            }
            int endIndex = str.indexOf("}", index + markLen);
            if (endIndex == -1) {
                throw new IllegalArgumentException("var expression starts but does not end");
            }
            String key = str.substring(index + markLen, endIndex);
            Object value = source.get(key);
            if (value == null) {
                throw new IllegalArgumentException("parameter [" + key + "] is not defined.");
            }
            String varValue = value.toString();
            str = str.substring(0, index) + varValue + str.substring(endIndex + 1);
            index += varValue.length();
        }
        return str;
    }
}

