/*
 * Decompiled with CFR 0.152.
 */
package org.unbescape;

import java.util.Properties;
import org.unbescape.ClassLoaderUtils;

public final class Unbescape {
    public static final String VERSION;
    public static final String BUILD_TIMESTAMP;
    public static final int VERSION_MAJOR;
    public static final int VERSION_MINOR;
    public static final int VERSION_BUILD;
    public static final String VERSION_TYPE;

    public static boolean isVersionStableRelease() {
        return "RELEASE".equals(VERSION_TYPE);
    }

    private Unbescape() {
    }

    static {
        String version = null;
        String buildTimestamp = null;
        try {
            Properties properties = new Properties();
            properties.load(ClassLoaderUtils.loadResourceAsStream("org/unbescape/unbescape.properties"));
            version = properties.getProperty("version");
            buildTimestamp = properties.getProperty("build.date");
        }
        catch (Exception properties) {
            // empty catch block
        }
        VERSION = version;
        BUILD_TIMESTAMP = buildTimestamp;
        if (VERSION == null || VERSION.trim().length() == 0) {
            VERSION_MAJOR = 0;
            VERSION_MINOR = 0;
            VERSION_BUILD = 0;
            VERSION_TYPE = "UNKNOWN";
        } else {
            try {
                String versionRemainder = VERSION;
                int separatorIdx = versionRemainder.indexOf(46);
                VERSION_MAJOR = Integer.parseInt(versionRemainder.substring(0, separatorIdx));
                versionRemainder = versionRemainder.substring(separatorIdx + 1);
                separatorIdx = versionRemainder.indexOf(46);
                VERSION_MINOR = Integer.parseInt(versionRemainder.substring(0, separatorIdx));
                versionRemainder = versionRemainder.substring(separatorIdx + 1);
                separatorIdx = versionRemainder.indexOf(46);
                if (separatorIdx < 0) {
                    separatorIdx = versionRemainder.indexOf(45);
                }
                VERSION_BUILD = Integer.parseInt(versionRemainder.substring(0, separatorIdx));
                VERSION_TYPE = versionRemainder.substring(separatorIdx + 1);
            }
            catch (Exception e) {
                throw new ExceptionInInitializerError("Exception during initialization of Unbescape versioning utilities. Identified Unbescape version is '" + VERSION + "', which does not follow the {major}.{minor}.{build}[.|-]{type} scheme");
            }
        }
    }
}

