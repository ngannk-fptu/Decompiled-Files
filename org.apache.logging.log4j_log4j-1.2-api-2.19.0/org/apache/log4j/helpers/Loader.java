/*
 * Decompiled with CFR 0.152.
 */
package org.apache.log4j.helpers;

import java.net.URL;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;

public class Loader {
    static final String TSTR = "Caught Exception while in Loader.getResource. This may be innocuous.";
    private static boolean ignoreTCL;

    public static URL getResource(String resource) {
        ClassLoader classLoader = null;
        URL url = null;
        try {
            if (!ignoreTCL && (classLoader = Loader.getTCL()) != null) {
                LogLog.debug("Trying to find [" + resource + "] using context classloader " + classLoader + ".");
                url = classLoader.getResource(resource);
                if (url != null) {
                    return url;
                }
            }
            if ((classLoader = Loader.class.getClassLoader()) != null) {
                LogLog.debug("Trying to find [" + resource + "] using " + classLoader + " class loader.");
                url = classLoader.getResource(resource);
                if (url != null) {
                    return url;
                }
            }
        }
        catch (Throwable t) {
            LogLog.warn(TSTR, t);
        }
        LogLog.debug("Trying to find [" + resource + "] using ClassLoader.getSystemResource().");
        return ClassLoader.getSystemResource(resource);
    }

    @Deprecated
    public static URL getResource(String resource, Class clazz) {
        return Loader.getResource(resource);
    }

    private static ClassLoader getTCL() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static boolean isJava1() {
        return false;
    }

    public static Class loadClass(String clazz) throws ClassNotFoundException {
        if (ignoreTCL) {
            return Class.forName(clazz);
        }
        try {
            return Loader.getTCL().loadClass(clazz);
        }
        catch (Throwable throwable) {
            return Class.forName(clazz);
        }
    }

    static {
        String ignoreTCLProp = OptionConverter.getSystemProperty("log4j.ignoreTCL", null);
        if (ignoreTCLProp != null) {
            ignoreTCL = OptionConverter.toBoolean(ignoreTCLProp, true);
        }
    }
}

