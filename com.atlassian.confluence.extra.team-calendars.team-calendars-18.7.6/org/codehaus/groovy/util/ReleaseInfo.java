/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;
import org.codehaus.groovy.runtime.DefaultGroovyMethodsSupport;

public class ReleaseInfo {
    private static final Properties RELEASE_INFO = new Properties();
    private static final String RELEASE_INFO_FILE = "META-INF/groovy-release-info.properties";
    private static final String KEY_IMPLEMENTATION_VERSION = "ImplementationVersion";

    public static String getVersion() {
        return ReleaseInfo.get(KEY_IMPLEMENTATION_VERSION);
    }

    public static Properties getAllProperties() {
        return RELEASE_INFO;
    }

    private static String get(String propName) {
        String propValue = RELEASE_INFO.getProperty(propName);
        return propValue == null ? "" : propValue;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static {
        URL url = null;
        ClassLoader cl = ReleaseInfo.class.getClassLoader();
        if (cl == null) {
            cl = ClassLoader.getSystemClassLoader();
        }
        if ((url = cl instanceof URLClassLoader ? ((URLClassLoader)cl).findResource(RELEASE_INFO_FILE) : cl.getResource(RELEASE_INFO_FILE)) != null) {
            InputStream is = null;
            try {
                is = url.openStream();
                if (is != null) {
                    RELEASE_INFO.load(is);
                }
            }
            catch (IOException iOException) {
            }
            finally {
                DefaultGroovyMethodsSupport.closeQuietly(is);
            }
        }
    }
}

