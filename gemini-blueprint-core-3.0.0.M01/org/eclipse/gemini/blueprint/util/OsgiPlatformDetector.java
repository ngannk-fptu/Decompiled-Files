/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package org.eclipse.gemini.blueprint.util;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public abstract class OsgiPlatformDetector {
    private static final String[] EQUINOX_LABELS = new String[]{"Eclipse", "eclipse", "Equinox", "equinox"};
    private static final String[] KF_LABELS = new String[]{"Knopflerfish", "knopflerfish"};
    private static final String[] FELIX_LABELS = new String[]{"Apache Software Foundation", "Felix", "felix"};
    private static final boolean isR41;
    private static final boolean isR42;

    public static boolean isEquinox(BundleContext bundleContext) {
        return OsgiPlatformDetector.determinePlatform(bundleContext, EQUINOX_LABELS);
    }

    public static boolean isKnopflerfish(BundleContext bundleContext) {
        return OsgiPlatformDetector.determinePlatform(bundleContext, KF_LABELS);
    }

    public static boolean isFelix(BundleContext bundleContext) {
        return OsgiPlatformDetector.determinePlatform(bundleContext, FELIX_LABELS);
    }

    private static boolean determinePlatform(BundleContext context, String[] labels) {
        Assert.notNull((Object)context);
        Assert.notNull((Object)labels);
        String vendorProperty = context.getProperty("org.osgi.framework.vendor");
        if (vendorProperty == null) {
            return false;
        }
        return OsgiPlatformDetector.containsAnyOf(vendorProperty, labels);
    }

    private static boolean containsAnyOf(String source, String[] searchTerms) {
        for (int i = 0; i < searchTerms.length; ++i) {
            if (source.indexOf(searchTerms[i]) == -1) continue;
            return true;
        }
        return false;
    }

    public static String getVersion(BundleContext bundleContext) {
        if (bundleContext == null) {
            return "";
        }
        Bundle sysBundle = bundleContext.getBundle(0L);
        return "" + (String)sysBundle.getHeaders().get("Bundle-Version");
    }

    public static boolean isR41() {
        return isR41;
    }

    public static boolean isR42() {
        return isR42;
    }

    static {
        boolean methodAvailable = false;
        ClassLoader loader = Bundle.class.getClassLoader();
        try {
            methodAvailable = Bundle.class.getMethod("start", Integer.TYPE) != null;
        }
        catch (Exception exception) {
            // empty catch block
        }
        isR41 = methodAvailable;
        isR42 = ClassUtils.isPresent((String)"org.osgi.framework.BundleReference", (ClassLoader)loader);
    }
}

