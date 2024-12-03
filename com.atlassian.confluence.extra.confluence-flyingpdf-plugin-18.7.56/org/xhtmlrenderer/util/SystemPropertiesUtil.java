/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.util;

public class SystemPropertiesUtil {
    public static String getPropertyOrDefaultSandbox(String propertyName, String defaultVal) {
        String val = defaultVal;
        try {
            val = System.getProperty(propertyName);
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        return val;
    }
}

