/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.context.annotation.Condition
 *  org.springframework.context.annotation.ConditionContext
 *  org.springframework.core.type.AnnotatedTypeMetadata
 */
package com.atlassian.troubleshooting.jfr.util;

import com.google.common.annotations.VisibleForTesting;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public final class JfrConditionUtils {
    private static final int MINIMAL_JAVA8_SUPPORTED_SUBVERSION_FOR_JFR = 282;
    private static final int MINIMAL_JAVA11_SUPPORTED_SUBVERSION_FOR_JFR = 5;
    private static final String HOMEBREW_VENDOR_IDENTIFIER = "Homebrew";
    private static final Logger LOG = LoggerFactory.getLogger(JfrConditionUtils.class);

    private JfrConditionUtils() {
    }

    public static boolean isJavaVersionSupported() {
        String javaProductVersion = System.getProperty("java.version");
        try {
            int version = JfrConditionUtils.getVersion(javaProductVersion);
            if (version < 8) {
                return false;
            }
            if (version == 8) {
                return JfrConditionUtils.getJava8Subversion(javaProductVersion) >= 282 && !JfrConditionUtils.isJavaVendorBlackListed();
            }
            if (version == 11) {
                return JfrConditionUtils.getJava9AndAboveSubversion(javaProductVersion) >= 5;
            }
            return true;
        }
        catch (Exception e) {
            LOG.error("Unsupported Java Runtime Environment version: " + javaProductVersion + ". Because of that parsing error JFR won't be available", (Throwable)e);
            return false;
        }
    }

    private static int getJava8Subversion(String version) {
        String simplifiedVersion = version.replaceAll("\\.|8\\.|1.8.0_|8u|u|-|[a-zA-Z]{2,}", "");
        String[] split = simplifiedVersion.split("b");
        if (split.length < 1) {
            return 0;
        }
        return Integer.parseInt(split[0]);
    }

    private static int getJava9AndAboveSubversion(String version) {
        String[] split = version.split("\\.");
        if (split.length < 3) {
            return 0;
        }
        String[] splitSubversion = split[2].split("[+-]");
        if (splitSubversion.length < 1) {
            return 0;
        }
        String subversion = splitSubversion[0];
        return Integer.parseInt(subversion);
    }

    @VisibleForTesting
    static int getVersion(String version) {
        if (version.startsWith("1.")) {
            return Integer.parseInt(version.substring(2, 3));
        }
        if (version.contains(".")) {
            return Integer.parseInt(version.substring(0, version.indexOf(".")));
        }
        if (version.contains("u")) {
            return Integer.parseInt(version.substring(0, version.indexOf("u")));
        }
        if (version.endsWith("-ea")) {
            return Integer.parseInt(version.substring(0, version.indexOf("-ea")));
        }
        return Integer.parseInt(version);
    }

    private static boolean isJavaVendorBlackListed() {
        return HOMEBREW_VENDOR_IDENTIFIER.equalsIgnoreCase(System.getProperty("java.vendor"));
    }

    public static class OnJfrUnsupportedCondition
    implements Condition {
        public boolean matches(@Nonnull ConditionContext context, @Nonnull AnnotatedTypeMetadata metadata) {
            try {
                Class.forName("jdk.jfr.FlightRecorder");
                return !JfrConditionUtils.isJavaVersionSupported();
            }
            catch (ClassNotFoundException e) {
                return true;
            }
        }
    }

    public static class OnJfrSupportedCondition
    implements Condition {
        public boolean matches(@Nonnull ConditionContext context, @Nonnull AnnotatedTypeMetadata metadata) {
            try {
                Class.forName("jdk.jfr.FlightRecorder");
                return JfrConditionUtils.isJavaVersionSupported();
            }
            catch (ClassNotFoundException e) {
                return false;
            }
        }
    }
}

