/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.tools.ant.types.EnumeratedAttribute;

public class CharSet
extends EnumeratedAttribute {
    private static final List<String> VALUES = new ArrayList<String>();

    public CharSet() {
    }

    public CharSet(String value) {
        this.setValue(value);
    }

    public static CharSet getDefault() {
        return new CharSet(Charset.defaultCharset().name());
    }

    public static CharSet getAscii() {
        return new CharSet(StandardCharsets.US_ASCII.name());
    }

    public static CharSet getUtf8() {
        return new CharSet(StandardCharsets.UTF_8.name());
    }

    public boolean equivalent(CharSet cs) {
        return this.getCharset().name().equals(cs.getCharset().name());
    }

    public Charset getCharset() {
        return Charset.forName(this.getValue());
    }

    @Override
    public String[] getValues() {
        return VALUES.toArray(new String[0]);
    }

    @Override
    public final void setValue(String value) {
        String realValue = value;
        if (value == null || value.isEmpty()) {
            realValue = Charset.defaultCharset().name();
        } else {
            for (String v : Arrays.asList(value, value.toLowerCase(), value.toUpperCase())) {
                if (!VALUES.contains(v)) continue;
                realValue = v;
                break;
            }
        }
        super.setValue(realValue);
    }

    static {
        for (Map.Entry<String, Charset> entry : Charset.availableCharsets().entrySet()) {
            VALUES.add(entry.getKey());
            VALUES.addAll(entry.getValue().aliases());
        }
    }
}

