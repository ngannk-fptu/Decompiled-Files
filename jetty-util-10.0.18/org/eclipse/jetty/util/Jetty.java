/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.util;

import java.io.InputStream;
import java.time.Instant;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Jetty {
    private static final Logger LOG = LoggerFactory.getLogger(Jetty.class);
    public static final String VERSION;
    public static final String POWERED_BY;
    public static final boolean STABLE;
    public static final String GIT_HASH;
    public static final String BUILD_TIMESTAMP;
    private static final Properties __buildProperties;

    private Jetty() {
    }

    private static String formatTimestamp(String timestamp) {
        try {
            long epochMillis = Long.parseLong(timestamp);
            return Instant.ofEpochMilli(epochMillis).toString();
        }
        catch (NumberFormatException e) {
            LOG.trace("IGNORED", (Throwable)e);
            return "unknown";
        }
    }

    static {
        __buildProperties = new Properties();
        try (InputStream inputStream = Jetty.class.getResourceAsStream("/org/eclipse/jetty/version/build.properties");){
            __buildProperties.load(inputStream);
        }
        catch (Exception e) {
            LOG.trace("IGNORED", (Throwable)e);
        }
        String gitHash = __buildProperties.getProperty("buildNumber", "unknown");
        if (gitHash.startsWith("${")) {
            gitHash = "unknown";
        }
        GIT_HASH = gitHash;
        System.setProperty("jetty.git.hash", GIT_HASH);
        BUILD_TIMESTAMP = Jetty.formatTimestamp(__buildProperties.getProperty("timestamp", "unknown"));
        Package pkg = Jetty.class.getPackage();
        VERSION = pkg != null && "Eclipse Jetty Project".equals(pkg.getImplementationVendor()) && pkg.getImplementationVersion() != null ? pkg.getImplementationVersion() : System.getProperty("jetty.version", __buildProperties.getProperty("version", "10.0.z-SNAPSHOT"));
        POWERED_BY = "<a href=\"https://eclipse.org/jetty\">Powered by Jetty:// " + VERSION + "</a>";
        STABLE = !VERSION.matches("^.*\\.(RC|M)[0-9]+$");
    }
}

