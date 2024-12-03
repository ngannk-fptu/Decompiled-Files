/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.header;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FieldValueParser {
    public static List<String> tokenizeList(String list) {
        String[] split = list.split(",");
        if (split.length == 1) {
            return Collections.singletonList(split[0].trim());
        }
        ArrayList<String> result = new ArrayList<String>();
        String inCodedUrl = null;
        for (String t : split) {
            String trimmed = t.trim();
            if (trimmed.startsWith("<") && !trimmed.endsWith(">")) {
                inCodedUrl = trimmed + ",";
                continue;
            }
            if (inCodedUrl != null && trimmed.endsWith(">")) {
                inCodedUrl = inCodedUrl + trimmed;
                result.add(inCodedUrl);
                inCodedUrl = null;
                continue;
            }
            if (trimmed.length() == 0) continue;
            result.add(trimmed);
        }
        return Collections.unmodifiableList(result);
    }
}

