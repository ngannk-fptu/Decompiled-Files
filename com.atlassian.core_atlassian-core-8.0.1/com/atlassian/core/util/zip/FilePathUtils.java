/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.core.util.zip;

import com.atlassian.annotations.VisibleForTesting;
import org.apache.commons.lang3.StringUtils;

class FilePathUtils {
    FilePathUtils() {
    }

    @VisibleForTesting
    static String stripSlashes(String path) {
        String result = path;
        result = result.replaceAll("\\\\", "/");
        result = result.replaceAll("(/)+", "/");
        result = result.replaceAll("(\\.){2,}+/", "");
        if (StringUtils.startsWith((CharSequence)(result = result.replaceAll("(\\./)", "")), (CharSequence)"/")) {
            result = StringUtils.substring((String)result, (int)1);
        }
        if (StringUtils.endsWith((CharSequence)result, (CharSequence)"/")) {
            result = StringUtils.substring((String)result, (int)0, (int)(result.length() - 1));
        }
        return result;
    }
}

