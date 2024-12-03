/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.embedded.core.util;

import java.util.Arrays;
import java.util.Optional;

public final class EnvironmentUtils {
    private EnvironmentUtils() {
    }

    public static Optional<JRE> getJreVersion() {
        return JRE.forName(EnvironmentUtils.getSystemJavaVersion());
    }

    private static String getSystemJavaVersion() {
        try {
            return System.getProperty("java.version");
        }
        catch (Exception ex) {
            return null;
        }
    }

    public static enum JRE {
        JRE_8("1.8", 8),
        JRE_11("11", 11);

        private final String namePrefix;
        private final int version;

        private JRE(String namePrefix, int version) {
            this.namePrefix = namePrefix;
            this.version = version;
        }

        public String getNamePrefix() {
            return this.namePrefix;
        }

        public int getVersion() {
            return this.version;
        }

        public static Optional<JRE> forName(String version) {
            return version != null && !version.isEmpty() ? Arrays.stream(JRE.values()).filter(jre -> version.startsWith(jre.namePrefix)).findFirst() : Optional.empty();
        }

        public static Optional<JRE> forVersion(int version) {
            return Arrays.stream(JRE.values()).filter(jre -> jre.version == version).findFirst();
        }
    }
}

