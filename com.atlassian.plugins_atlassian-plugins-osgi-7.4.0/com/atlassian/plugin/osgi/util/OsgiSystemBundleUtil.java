/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 */
package com.atlassian.plugin.osgi.util;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class OsgiSystemBundleUtil {
    public static final int SYSTEM_BUNDLE_ID = 0;

    public static Bundle getSystemBundle(BundleContext currentBundleContext) {
        return currentBundleContext.getBundle(0L);
    }

    public static BundleContext getSystemBundleContext(Bundle currentBundle) {
        switch (currentBundle.getState()) {
            case 8: 
            case 16: 
            case 32: {
                return OsgiSystemBundleUtil.getSystemBundleContext(currentBundle.getBundleContext());
            }
        }
        throw new IllegalStateException("Cannot get system bundle context when bundle is not in the STARTING, ACTIVE or STOPPING states.");
    }

    public static BundleContext getSystemBundleContext(BundleContext currentBundleContext) {
        return OsgiSystemBundleUtil.getSystemBundle(currentBundleContext).getBundleContext();
    }
}

