/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.SpringVersion
 */
package org.springframework.security.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.SpringVersion;
import org.springframework.security.core.ComparableVersion;

public final class SpringSecurityCoreVersion {
    private static final String DISABLE_CHECKS = SpringSecurityCoreVersion.class.getName().concat(".DISABLE_CHECKS");
    private static final Log logger = LogFactory.getLog(SpringSecurityCoreVersion.class);
    public static final long SERIAL_VERSION_UID = 580L;
    static final String MIN_SPRING_VERSION = SpringSecurityCoreVersion.getSpringVersion();

    private SpringSecurityCoreVersion() {
    }

    private static void performVersionChecks() {
        SpringSecurityCoreVersion.performVersionChecks(MIN_SPRING_VERSION);
    }

    private static void performVersionChecks(String minSpringVersion) {
        String version;
        if (minSpringVersion == null) {
            return;
        }
        String springVersion = SpringVersion.getVersion();
        if (SpringSecurityCoreVersion.disableChecks(springVersion, version = SpringSecurityCoreVersion.getVersion())) {
            return;
        }
        logger.info((Object)("You are running with Spring Security Core " + version));
        if (new ComparableVersion(springVersion).compareTo(new ComparableVersion(minSpringVersion)) < 0) {
            logger.warn((Object)("**** You are advised to use Spring " + minSpringVersion + " or later with this version. You are running: " + springVersion));
        }
    }

    public static String getVersion() {
        Package pkg = SpringSecurityCoreVersion.class.getPackage();
        return pkg != null ? pkg.getImplementationVersion() : null;
    }

    private static boolean disableChecks(String springVersion, String springSecurityVersion) {
        if (springVersion == null || springVersion.equals(springSecurityVersion)) {
            return true;
        }
        return Boolean.getBoolean(DISABLE_CHECKS);
    }

    private static String getSpringVersion() {
        Properties properties = new Properties();
        try (InputStream is = SpringSecurityCoreVersion.class.getClassLoader().getResourceAsStream("META-INF/spring-security.versions");){
            properties.load(is);
        }
        catch (IOException | NullPointerException ex) {
            return null;
        }
        return properties.getProperty("org.springframework:spring-core");
    }

    static {
        SpringSecurityCoreVersion.performVersionChecks();
    }
}

