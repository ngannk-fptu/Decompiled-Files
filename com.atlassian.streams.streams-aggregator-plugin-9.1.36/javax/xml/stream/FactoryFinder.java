/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.stream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import javax.xml.stream.FactoryConfigurationError;

class FactoryFinder {
    private static boolean debug = false;

    FactoryFinder() {
    }

    private static void debugPrintln(String msg) {
        if (debug) {
            System.err.println("STREAM: " + msg);
        }
    }

    private static ClassLoader findClassLoader() throws FactoryConfigurationError {
        ClassLoader classLoader;
        try {
            Class<?> clazz = Class.forName(FactoryFinder.class.getName() + "$ClassLoaderFinderConcrete");
            ClassLoaderFinder clf = (ClassLoaderFinder)clazz.newInstance();
            classLoader = clf.getContextClassLoader();
        }
        catch (LinkageError le) {
            classLoader = FactoryFinder.class.getClassLoader();
        }
        catch (ClassNotFoundException x) {
            classLoader = FactoryFinder.class.getClassLoader();
        }
        catch (Exception x) {
            throw new FactoryConfigurationError(x.toString(), x);
        }
        return classLoader;
    }

    private static Object newInstance(String className, ClassLoader classLoader) throws FactoryConfigurationError {
        try {
            Class<?> spiClass = classLoader == null ? Class.forName(className) : classLoader.loadClass(className);
            return spiClass.newInstance();
        }
        catch (ClassNotFoundException x) {
            throw new FactoryConfigurationError("Provider " + className + " not found", x);
        }
        catch (Exception x) {
            throw new FactoryConfigurationError("Provider " + className + " could not be instantiated: " + x, x);
        }
    }

    static Object find(String factoryId) throws FactoryConfigurationError {
        return FactoryFinder.find(factoryId, null);
    }

    static Object find(String factoryId, String fallbackClassName) throws FactoryConfigurationError {
        ClassLoader classLoader = FactoryFinder.findClassLoader();
        return FactoryFinder.find(factoryId, fallbackClassName, classLoader);
    }

    static Object find(String factoryId, String fallbackClassName, ClassLoader classLoader) throws FactoryConfigurationError {
        block12: {
            block11: {
                try {
                    String systemProp = System.getProperty(factoryId);
                    if (systemProp != null) {
                        FactoryFinder.debugPrintln("found system property" + systemProp);
                        return FactoryFinder.newInstance(systemProp, classLoader);
                    }
                }
                catch (SecurityException se) {
                    // empty catch block
                }
                try {
                    String javah = System.getProperty("java.home");
                    String configFile = javah + File.separator + "lib" + File.separator + "jaxp.properties";
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
                    if (!debug) break block11;
                    ex.printStackTrace();
                }
            }
            String serviceId = "META-INF/services/" + factoryId;
            try {
                InputStream is = null;
                is = classLoader == null ? ClassLoader.getSystemResourceAsStream(serviceId) : classLoader.getResourceAsStream(serviceId);
                if (is != null) {
                    FactoryFinder.debugPrintln("found " + serviceId);
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    String factoryClassName = rd.readLine();
                    rd.close();
                    if (factoryClassName != null && !"".equals(factoryClassName)) {
                        FactoryFinder.debugPrintln("loaded from services: " + factoryClassName);
                        return FactoryFinder.newInstance(factoryClassName, classLoader);
                    }
                }
            }
            catch (Exception ex) {
                if (!debug) break block12;
                ex.printStackTrace();
            }
        }
        if (fallbackClassName == null) {
            throw new FactoryConfigurationError("Provider for " + factoryId + " cannot be found", null);
        }
        FactoryFinder.debugPrintln("loaded from fallback value: " + fallbackClassName);
        return FactoryFinder.newInstance(fallbackClassName, classLoader);
    }

    static {
        try {
            debug = System.getProperty("xml.stream.debug") != null;
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    static class ClassLoaderFinderConcrete
    extends ClassLoaderFinder {
        ClassLoaderFinderConcrete() {
        }

        ClassLoader getContextClassLoader() {
            return Thread.currentThread().getContextClassLoader();
        }
    }

    private static abstract class ClassLoaderFinder {
        private ClassLoaderFinder() {
        }

        abstract ClassLoader getContextClassLoader();
    }
}

