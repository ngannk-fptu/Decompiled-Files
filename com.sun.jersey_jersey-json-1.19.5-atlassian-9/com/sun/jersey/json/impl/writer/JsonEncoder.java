/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.json.impl.writer;

import java.util.Formatter;

public class JsonEncoder {
    public static String encode(String text) {
        if (null == text || text.length() == 0) {
            return text;
        }
        Formatter formatter = new Formatter();
        StringBuffer result = new StringBuffer();
        block9: for (int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            switch (c) {
                case '\"': {
                    result.append("\\\"");
                    continue block9;
                }
                case '\\': {
                    result.append("\\\\");
                    continue block9;
                }
                case '\b': {
                    result.append("\\b");
                    continue block9;
                }
                case '\f': {
                    result.append("\\f");
                    continue block9;
                }
                case '\n': {
                    result.append("\\n");
                    continue block9;
                }
                case '\r': {
                    result.append("\\r");
                    continue block9;
                }
                case '\t': {
                    result.append("\\t");
                    continue block9;
                }
                default: {
                    if (c < ' ') {
                        result.append(formatter.format("\\u%04X", c));
                        continue block9;
                    }
                    result.append(c);
                }
            }
        }
        return result.toString();
    }
}

