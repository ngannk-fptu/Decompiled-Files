/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.rpc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

class FactoryFinder {
    private static final boolean debug = false;
    static /* synthetic */ Class class$java$lang$Thread;
    static /* synthetic */ Class class$javax$xml$rpc$FactoryFinder;

    FactoryFinder() {
    }

    private static void debugPrintln(String msg) {
    }

    private static ClassLoader findClassLoader() throws ConfigurationError {
        Method m = null;
        try {
            m = (class$java$lang$Thread == null ? (class$java$lang$Thread = FactoryFinder.class$("java.lang.Thread")) : class$java$lang$Thread).getMethod("getContextClassLoader", null);
        }
        catch (NoSuchMethodException e) {
            FactoryFinder.debugPrintln("assuming JDK 1.1");
            return (class$javax$xml$rpc$FactoryFinder == null ? (class$javax$xml$rpc$FactoryFinder = FactoryFinder.class$("javax.xml.rpc.FactoryFinder")) : class$javax$xml$rpc$FactoryFinder).getClassLoader();
        }
        try {
            return (ClassLoader)m.invoke((Object)Thread.currentThread(), null);
        }
        catch (IllegalAccessException e) {
            throw new ConfigurationError("Unexpected IllegalAccessException", e);
        }
        catch (InvocationTargetException e) {
            throw new ConfigurationError("Unexpected InvocationTargetException", e);
        }
    }

    private static Object newInstance(String className, ClassLoader classLoader) throws ConfigurationError {
        try {
            if (classLoader != null) {
                try {
                    return classLoader.loadClass(className).newInstance();
                }
                catch (ClassNotFoundException x) {
                    // empty catch block
                }
            }
            return Class.forName(className).newInstance();
        }
        catch (ClassNotFoundException x) {
            throw new ConfigurationError("Provider " + className + " not found", x);
        }
        catch (Exception x) {
            throw new ConfigurationError("Provider " + className + " could not be instantiated: " + x, x);
        }
    }

    static Object find(String factoryId, String fallbackClassName) throws ConfigurationError {
        ClassLoader classLoader;
        block12: {
            FactoryFinder.debugPrintln("debug is on");
            classLoader = FactoryFinder.findClassLoader();
            try {
                String systemProp = System.getProperty(factoryId);
                if (systemProp != null) {
                    FactoryFinder.debugPrintln("found system property " + systemProp);
                    return FactoryFinder.newInstance(systemProp, classLoader);
                }
            }
            catch (SecurityException se) {
                // empty catch block
            }
            try {
                String javah = System.getProperty("java.home");
                String configFile = javah + File.separator + "lib" + File.separator + "jaxrpc.properties";
                File f = new File(configFile);
                if (f.exists()) {
                    Properties props = new Properties();
                    props.load(new FileInputStream(f));
                    String factoryClassName = props.getProperty(factoryId);
                    FactoryFinder.debugPrintln("found java.home property " + factoryClassName);
                    return FactoryFinder.newInstance(factoryClassName, classLoader);
                }
            }
            catch (Exception ex) {
                // empty catch block
            }
            String serviceId = "META-INF/services/" + factoryId;
            try {
                BufferedReader rd;
                InputStream is = null;
                is = classLoader == null ? ClassLoader.getSystemResourceAsStream(serviceId) : classLoader.getResourceAsStream(serviceId);
                if (is == null) break block12;
                FactoryFinder.debugPrintln("found " + serviceId);
                try {
                    rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                }
                catch (UnsupportedEncodingException e) {
                    rd = new BufferedReader(new InputStreamReader(is));
                }
                String factoryClassName = rd.readLine();
                rd.close();
                if (factoryClassName != null && !"".equals(factoryClassName)) {
                    FactoryFinder.debugPrintln("loaded from services: " + factoryClassName);
                    return FactoryFinder.newInstance(factoryClassName, classLoader);
                }
            }
            catch (Exception ex) {
                // empty catch block
            }
        }
        if (fallbackClassName == null) {
            throw new ConfigurationError("Provider for " + factoryId + " cannot be found", null);
        }
        FactoryFinder.debugPrintln("loaded from fallback value: " + fallbackClassName);
        return FactoryFinder.newInstance(fallbackClassName, classLoader);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static class ConfigurationError
    extends Error {
        private Exception exception;

        ConfigurationError(String msg, Exception x) {
            super(msg);
            this.exception = x;
        }

        Exception getException() {
            return this.exception;
        }
    }
}

