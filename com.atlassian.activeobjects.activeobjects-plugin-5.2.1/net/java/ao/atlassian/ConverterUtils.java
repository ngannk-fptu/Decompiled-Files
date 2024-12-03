/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.atlassian;

import net.java.ao.ActiveObjectsException;

final class ConverterUtils {
    static final int MAX_LENGTH = 30;

    ConverterUtils() {
    }

    static String checkLength(String name, String errorMsg) {
        if (ConverterUtils.enforceLength() && name != null && name.length() > 30) {
            throw new ActiveObjectsException(errorMsg);
        }
        return name;
    }

    private static boolean enforceLength() {
        return Boolean.valueOf(System.getProperty("ao.atlassian.enforce.length", "true"));
    }
}

