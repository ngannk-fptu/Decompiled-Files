/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.jdk.utilities;

import com.atlassian.jdk.utilities.exception.InvalidVersionException;

@Deprecated
public class JvmProperties {
    public static float getJvmVersion() throws InvalidVersionException {
        String property = System.getProperty("java.specification.version");
        try {
            return Float.valueOf(property).floatValue();
        }
        catch (Exception e) {
            throw new InvalidVersionException("Invalid JVM version: '" + property + "'. " + e.getMessage());
        }
    }

    public static boolean isJvmVersion(float versionNumber) throws InvalidVersionException {
        return JvmProperties.getJvmVersion() >= versionNumber;
    }
}

