/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools;

import java.util.LinkedHashMap;
import java.util.Map;

public class GrapeUtil {
    public static Map<String, Object> getIvyParts(String allstr) {
        String[] parts;
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        String ext = "";
        if (allstr.contains("@")) {
            parts = allstr.split("@");
            if (parts.length > 2) {
                return result;
            }
            allstr = parts[0];
            ext = parts[1];
        }
        if ((parts = allstr.split(":")).length > 4) {
            return result;
        }
        if (parts.length > 3) {
            result.put("classifier", parts[3]);
        }
        if (parts.length > 2) {
            result.put("version", parts[2]);
        } else {
            result.put("version", "*");
        }
        if (ext.length() > 0) {
            result.put("ext", ext);
        }
        result.put("module", parts[1]);
        result.put("group", parts[0]);
        return result;
    }
}

