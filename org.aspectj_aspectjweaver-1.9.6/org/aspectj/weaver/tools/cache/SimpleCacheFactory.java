/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.tools.cache;

import java.io.File;
import org.aspectj.weaver.Dump;
import org.aspectj.weaver.tools.cache.SimpleCache;

public class SimpleCacheFactory {
    public static final String CACHE_ENABLED_PROPERTY = "aj.weaving.cache.enabled";
    public static final String CACHE_DIR = "aj.weaving.cache.dir";
    public static final String CACHE_IMPL = "aj.weaving.cache.impl";
    public static final String PATH_DEFAULT = "/tmp/";
    public static final boolean BYDEFAULT = false;
    public static String path = "/tmp/";
    public static Boolean enabled = false;
    private static boolean determinedIfEnabled = false;
    private static SimpleCache lacache = null;

    public static synchronized SimpleCache createSimpleCache() {
        if (lacache == null) {
            if (!determinedIfEnabled) {
                SimpleCacheFactory.determineIfEnabled();
            }
            if (!enabled.booleanValue()) {
                return null;
            }
            try {
                path = System.getProperty(CACHE_DIR);
                if (path == null) {
                    path = PATH_DEFAULT;
                }
            }
            catch (Throwable t) {
                path = PATH_DEFAULT;
                t.printStackTrace();
                Dump.dumpWithException(t);
            }
            File f = new File(path);
            if (!f.exists()) {
                f.mkdir();
            }
            lacache = new SimpleCache(path, enabled);
        }
        return lacache;
    }

    private static void determineIfEnabled() {
        try {
            String impl;
            String property = System.getProperty(CACHE_ENABLED_PROPERTY);
            enabled = property == null ? Boolean.valueOf(false) : (property.equalsIgnoreCase("true") ? ("shared".equals(impl = System.getProperty(CACHE_IMPL)) ? Boolean.valueOf(true) : Boolean.valueOf(false)) : Boolean.valueOf(false));
        }
        catch (Throwable t) {
            enabled = false;
            System.err.println("Error creating cache");
            t.printStackTrace();
            Dump.dumpWithException(t);
        }
        determinedIfEnabled = true;
    }

    public static boolean isEnabled() {
        if (!determinedIfEnabled) {
            SimpleCacheFactory.determineIfEnabled();
        }
        return enabled;
    }
}

