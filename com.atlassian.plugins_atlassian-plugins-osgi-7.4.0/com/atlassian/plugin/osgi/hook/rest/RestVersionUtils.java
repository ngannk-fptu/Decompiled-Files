/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Version
 *  org.osgi.framework.wiring.BundleCapability
 */
package com.atlassian.plugin.osgi.hook.rest;

import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleCapability;

public final class RestVersionUtils {
    private RestVersionUtils() {
    }

    public static boolean isJaxRsPackage(BundleCapability bundleCapability) {
        Object wiringPackage = bundleCapability.getAttributes().get("osgi.wiring.package");
        if (wiringPackage instanceof String) {
            return ((String)wiringPackage).startsWith("javax.ws.rs");
        }
        return false;
    }

    public static boolean isCapabilityWithMajorVersion(BundleCapability bundleCapability, int majorVersion) {
        Object version = bundleCapability.getAttributes().get("version");
        if (version instanceof Version) {
            return ((Version)version).getMajor() == majorVersion;
        }
        return false;
    }
}

