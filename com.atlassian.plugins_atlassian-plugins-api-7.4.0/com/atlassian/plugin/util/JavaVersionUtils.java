/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.util;

public class JavaVersionUtils {
    private static final Float SPEC_VERSION = Float.valueOf(System.getProperty("java.specification.version"));

    public static boolean satisfiesMinVersion(float versionNumber) {
        return SPEC_VERSION.floatValue() >= versionNumber;
    }

    public static Float resolveVersionFromString(String versionStr) {
        try {
            return Float.valueOf(versionStr);
        }
        catch (Exception e) {
            return null;
        }
    }
}

