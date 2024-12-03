/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleActivator
 *  org.osgi.framework.BundleContext
 */
package org.xerial.snappy;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.xerial.snappy.SnappyLoader;
import org.xerial.snappy.SnappyNative;

public class SnappyBundleActivator
implements BundleActivator {
    public static final String LIBRARY_NAME = "snappyjava";

    public void start(BundleContext bundleContext) throws Exception {
        String string = System.mapLibraryName(LIBRARY_NAME);
        String string2 = System.getProperty("os.arch");
        if (string.toLowerCase().endsWith(".dylib") && "x86".equals(string2)) {
            string = string.replace(".dylib", ".jnilib");
        }
        System.loadLibrary(string);
        SnappyLoader.setSnappyApi(new SnappyNative());
    }

    public void stop(BundleContext bundleContext) throws Exception {
        SnappyLoader.setSnappyApi(null);
        SnappyLoader.cleanUpExtractedNativeLib();
    }
}

