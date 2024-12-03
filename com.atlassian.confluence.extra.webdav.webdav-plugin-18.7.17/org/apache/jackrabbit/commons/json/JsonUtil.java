/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.json;

public class JsonUtil {
    public static String getJsonString(String str) {
        if (str == null || str.length() == 0) {
            return "\"\"";
        }
        int len = str.length();
        StringBuffer sb = new StringBuffer(len + 2);
        sb.append('\"');
        block8: for (int i = 0; i < len; ++i) {
            char c = str.charAt(i);
            switch (c) {
                case '\"': 
                case '\\': {
                    sb.append('\\').append(c);
                    continue block8;
                }
                case '\b': {
                    sb.append("\\b");
                    continue block8;
                }
                case '\f': {
                    sb.append("\\f");
                    continue block8;
                }
                case '\n': {
                    sb.append("\\n");
                    continue block8;
                }
                case '\r': {
                    sb.append("\\r");
                    continue block8;
                }
                case '\t': {
                    sb.append("\\t");
                    continue block8;
                }
                default: {
                    if (c < ' ') {
                        String uc = Integer.toHexString(c);
                        sb.append("\\u");
                        int uLen = uc.length();
                        while (uLen++ < 4) {
                            sb.append('0');
                        }
                        sb.append(uc);
                        continue block8;
                    }
                    sb.append(c);
                }
            }
        }
        sb.append('\"');
        return sb.toString();
    }
}

