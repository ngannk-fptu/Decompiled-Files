/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.search;

import org.apache.commons.lang3.StringUtils;

public class LexicographicUtils {
    public static String intAsString(int i, int strSize) {
        return StringUtils.leftPad((String)String.valueOf(i), (int)strSize, (char)'0');
    }
}

