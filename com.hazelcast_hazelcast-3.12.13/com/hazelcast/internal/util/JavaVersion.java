/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util;

public enum JavaVersion {
    UNKNOWN,
    JAVA_1_6,
    JAVA_1_7,
    JAVA_1_8,
    JAVA_9,
    JAVA_10,
    JAVA_11,
    JAVA_12,
    JAVA_13,
    JAVA_14;

    private static final JavaVersion CURRENT_VERSION;

    public static boolean isAtLeast(JavaVersion version) {
        return JavaVersion.isAtLeast(CURRENT_VERSION, version);
    }

    public static boolean isAtMost(JavaVersion version) {
        return JavaVersion.isAtMost(CURRENT_VERSION, version);
    }

    private static JavaVersion detectCurrentVersion() {
        String version = System.getProperty("java.version");
        return JavaVersion.parseVersion(version);
    }

    static JavaVersion parseVersion(String version) {
        if (version == null) {
            return UNKNOWN;
        }
        JavaVersion result = UNKNOWN;
        if (version.startsWith("1.")) {
            String withoutMajor = version.substring(2, version.length());
            if (withoutMajor.startsWith("6")) {
                result = JAVA_1_6;
            } else if (withoutMajor.startsWith("7")) {
                result = JAVA_1_7;
            } else if (withoutMajor.startsWith("8")) {
                result = JAVA_1_8;
            }
        } else if (version.startsWith("9")) {
            result = JAVA_9;
        } else if (version.startsWith("10")) {
            result = JAVA_10;
        } else if (version.startsWith("11")) {
            result = JAVA_11;
        } else if (version.startsWith("12")) {
            result = JAVA_12;
        } else if (version.startsWith("13")) {
            result = JAVA_13;
        } else if (version.startsWith("14")) {
            result = JAVA_14;
        }
        return result;
    }

    static boolean isAtLeast(JavaVersion currentVersion, JavaVersion minVersion) {
        return currentVersion.ordinal() >= minVersion.ordinal() || currentVersion == UNKNOWN;
    }

    static boolean isAtMost(JavaVersion currentVersion, JavaVersion maxVersion) {
        return currentVersion.ordinal() <= maxVersion.ordinal();
    }

    static {
        CURRENT_VERSION = JavaVersion.detectCurrentVersion();
    }
}

