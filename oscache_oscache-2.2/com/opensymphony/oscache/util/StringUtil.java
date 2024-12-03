/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.oscache.util;

import java.util.ArrayList;
import java.util.List;

public class StringUtil {
    public static List split(String str, char delimiter) {
        int currentIndex;
        if (str == null || "".equals(str)) {
            return new ArrayList();
        }
        ArrayList<String> parts = new ArrayList<String>();
        int previousIndex = 0;
        while ((currentIndex = str.indexOf(delimiter, previousIndex)) > 0) {
            String part = str.substring(previousIndex, currentIndex).trim();
            parts.add(part);
            previousIndex = currentIndex + 1;
        }
        parts.add(str.substring(previousIndex, str.length()).trim());
        return parts;
    }
}

