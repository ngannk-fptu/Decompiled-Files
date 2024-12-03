/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.core.util;

import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;

public class FilterUtils {
    public static String verifyString(String s) {
        if (!StringUtils.isBlank((CharSequence)s)) {
            return s;
        }
        return null;
    }

    public static String[] verifyStringArray(String[] sa) {
        ArrayList<String> result = new ArrayList<String>();
        for (String value : sa) {
            String s = FilterUtils.verifyString(value);
            if (s == null) continue;
            result.add(s);
        }
        if (result.size() == 0) {
            return null;
        }
        return result.toArray(new String[0]);
    }

    public static Long verifyLong(Long id) {
        if (id != null && id > 0L) {
            return id;
        }
        return null;
    }
}

