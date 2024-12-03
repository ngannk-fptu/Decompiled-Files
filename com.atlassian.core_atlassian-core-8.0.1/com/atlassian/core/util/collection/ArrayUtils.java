/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.core.util.collection;

import org.apache.commons.lang3.StringUtils;

public class ArrayUtils {
    @Deprecated
    public static String[] add(String[] array, String obj) {
        if (array != null) {
            String[] newArray = new String[array.length + 1];
            System.arraycopy(array, 0, newArray, 0, array.length);
            newArray[array.length] = obj;
            return newArray;
        }
        if (obj != null) {
            return new String[]{obj};
        }
        return null;
    }

    public static boolean isContainsOneBlank(String[] array) {
        return array != null && array.length == 1 && StringUtils.isBlank((CharSequence)array[0]);
    }
}

