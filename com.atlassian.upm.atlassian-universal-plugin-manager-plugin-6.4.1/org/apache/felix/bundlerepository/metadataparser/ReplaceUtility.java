/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.bundlerepository.metadataparser;

import java.util.Map;

public class ReplaceUtility {
    public static String replace(String str, Map values) {
        int len = str.length();
        StringBuffer sb = new StringBuffer(len);
        int prev = 0;
        int start = str.indexOf("${");
        int end = str.indexOf("}", start);
        while (start != -1 && end != -1) {
            String key = str.substring(start + 2, end);
            Object value = values.get(key);
            if (value != null) {
                sb.append(str.substring(prev, start));
                sb.append(value);
            } else {
                sb.append(str.substring(prev, end + 1));
            }
            prev = end + 1;
            if (prev >= str.length()) break;
            start = str.indexOf("${", prev);
            if (start == -1) continue;
            end = str.indexOf("}", start);
        }
        sb.append(str.substring(prev));
        return sb.toString();
    }
}

