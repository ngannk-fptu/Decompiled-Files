/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import org.apache.xml.utils.SecuritySupport;

final class ObjectFactory {
    private static final String DEFAULT_PROPERTIES_FILENAME = "xalan.properties";
    private static final String SERVICES_PATH = "META-INF/services/";
    private static final boolean DEBUG = false;
    private static Properties fXalanProperties = null;
    private static long fLastModified = -1L;

    ObjectFactory() {
    }

    static Object createObject(String factoryId, String fallbackClassName) throws ConfigurationError {
        return ObjectFactory.createObject(factoryId, null, fallbackClassName);
    }

    static Object createObject(String factoryId, String propertiesFilename, String fallbackClassName) throws ConfigurationError {
        Class factoryClass = ObjectFactory.lookUpFactoryClass(factoryId, propertiesFilename, fallbackClassName);
        if (factoryClass == null) {
            throw new ConfigurationError("Provider for " + factoryId + " cannot be found", null);
        }
        try {
            Object instance = factoryClass.newInstance();
            ObjectFactory.debugPrintln("created new instance of factory " + factoryId);
            return instance;
        }
        catch (Exception x) {
            throw new ConfigurationError("Provider for factory " + factoryId + " could not be instantiated: " + x, x);
        }
    }

    static Class lookUpFactoryClass(String factoryId) throws ConfigurationError {
        return ObjectFactory.lookUpFactoryClass(factoryId, null, null);
    }

    static Class lookUpFactoryClass(String factoryId, String propertiesFilename, String fallbackClassName) throws ConfigurationError {
        String factoryClassName = ObjectFactory.lookUpFactoryClassName(factoryId, propertiesFilename, fallbackClassName);
        ClassLoader cl = ObjectFactory.findClassLoader();
        if (factoryClassName == null) {
            factoryClassName = fallbackClassName;
        }
        try {
            Class providerClass = ObjectFactory.findProviderClass(factoryClassName, cl, true);
            ObjectFactory.debugPrintln("created new instance of " + providerClass + " using ClassLoader: " + cl);
            return providerClass;
        }
        catch (ClassNotFoundException x) {
            throw new ConfigurationError("Provider " + factoryClassName + " not found", x);
        }
        catch (Exception x) {
            throw new ConfigurationError("Provider " + factoryClassName + " could not be instantiated: " + x, x);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static String lookUpFactoryClassName(String factoryId, String propertiesFilename, String fallbackClassName) {
        try {
            String systemProp = SecuritySupport.getSystemProperty(factoryId);
            if (systemProp != null) {
                ObjectFactory.debugPrintln("found system property, value=" + systemProp);
                return systemProp;
            }
        }
        catch (SecurityException systemProp) {
            // empty catch block
        }
        String factoryClassName = null;
        if (propertiesFilename == null) {
            File propertiesFile = null;
            boolean propertiesFileExists = false;
            try {
                String javah = SecuritySupport.getSystemProperty("java.home");
                propertiesFilename = javah + File.separator + "lib" + File.separator + DEFAULT_PROPERTIES_FILENAME;
                propertiesFile = new File(propertiesFilename);
                propertiesFileExists = SecuritySupport.getFileExists(propertiesFile);
            }
            catch (SecurityException e) {
                fLastModified = -1L;
                fXalanProperties = null;
            }
            Class<ObjectFactory> clazz = ObjectFactory.class;
            synchronized (ObjectFactory.class) {
                boolean loadProperties = false;
                FileInputStream fis = null;
                try {
                    if (fLastModified >= 0L) {
                        if (propertiesFileExists && fLastModified < (fLastModified = SecuritySupport.getLastModified(propertiesFile))) {
                            loadProperties = true;
                        } else if (!propertiesFileExists) {
                            fLastModified = -1L;
                            fXalanProperties = null;
                        }
                    } else if (propertiesFileExists) {
                        loadProperties = true;
                        fLastModified = SecuritySupport.getLastModified(propertiesFile);
                    }
                    if (loadProperties) {
                        fXalanProperties = new Properties();
                        fis = SecuritySupport.getFileInputStream(propertiesFile);
                        fXalanProperties.load(fis);
                    }
                }
                catch (Exception x) {
                    fXalanProperties = null;
                    fLastModified = -1L;
                }
                finally {
                    if (fis != null) {
                        try {
                            fis.close();
                        }
                        catch (IOException iOException) {}
                    }
                }
                // ** MonitorExit[var6_12] (shouldn't be in output)
                if (fXalanProperties != null) {
                    factoryClassName = fXalanProperties.getProperty(factoryId);
                }
            }
        } else {
            FileInputStream fis = null;
            try {
                fis = SecuritySupport.getFileInputStream(new File(propertiesFilename));
                Properties props = new Properties();
                props.load(fis);
                factoryClassName = props.getProperty(factoryId);
            }
            catch (Exception exception) {
            }
            finally {
                if (fis != null) {
                    try {
                        fis.close();
                    }
                    catch (IOException iOException) {}
                }
            }
        }
        {
            if (factoryClassName != null) {
                ObjectFactory.debugPrintln("found in " + propertiesFilename + ", value=" + factoryClassName);
                return factoryClassName;
            }
            return ObjectFactory.findJarServiceProviderName(factoryId);
        }
    }

    private static void debugPrintln(String msg) {
    }

    static ClassLoader findClassLoader() throws ConfigurationError {
        ClassLoader system;
        ClassLoader context = SecuritySupport.getContextClassLoader();
        ClassLoader chain = system = SecuritySupport.getSystemClassLoader();
        while (true) {
            if (context == chain) {
                ClassLoader current = ObjectFactory.class.getClassLoader();
                chain = system;
                while (true) {
                    if (current == chain) {
                        return system;
                    }
                    if (chain == null) break;
                    chain = SecuritySupport.getParentClassLoader(chain);
                }
                return current;
            }
            if (chain == null) break;
            chain = SecuritySupport.getParentClassLoader(chain);
        }
        return context;
    }

    static Object newInstance(String className, ClassLoader cl, boolean doFallback) throws ConfigurationError {
        try {
            Class providerClass = ObjectFactory.findProviderClass(className, cl, doFallback);
            Object instance = providerClass.newInstance();
            ObjectFactory.debugPrintln("created new instance of " + providerClass + " using ClassLoader: " + cl);
            return instance;
        }
        catch (ClassNotFoundException x) {
            throw new ConfigurationError("Provider " + className + " not found", x);
        }
        catch (Exception x) {
            throw new ConfigurationError("Provider " + className + " could not be instantiated: " + x, x);
        }
    }

    static Class findProviderClass(String className, ClassLoader cl, boolean doFallback) throws ClassNotFoundException, ConfigurationError {
        Class<?> providerClass;
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            int lastDot = className.lastIndexOf(46);
            String packageName = className;
            if (lastDot != -1) {
                packageName = className.substring(0, lastDot);
            }
            security.checkPackageAccess(packageName);
        }
        if (cl == null) {
            providerClass = Class.forName(className);
        } else {
            try {
                providerClass = cl.loadClass(className);
            }
            catch (ClassNotFoundException x) {
                if (doFallback) {
                    ClassLoader current = ObjectFactory.class.getClassLoader();
                    if (current == null) {
                        providerClass = Class.forName(className);
                    }
                    if (cl != current) {
                        cl = current;
                        providerClass = cl.loadClass(className);
                    }
                    throw x;
                }
                throw x;
            }
        }
        return providerClass;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static String findJarServiceProviderName(String factoryId) {
        BufferedReader rd;
        ClassLoader current;
        String serviceId = SERVICES_PATH + factoryId;
        InputStream is = null;
        ClassLoader cl = ObjectFactory.findClassLoader();
        is = SecuritySupport.getResourceAsStream(cl, serviceId);
        if (is == null && cl != (current = ObjectFactory.class.getClassLoader())) {
            cl = current;
            is = SecuritySupport.getResourceAsStream(cl, serviceId);
        }
        if (is == null) {
            return null;
        }
        ObjectFactory.debugPrintln("found jar resource=" + serviceId + " using ClassLoader: " + cl);
        try {
            rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        }
        catch (UnsupportedEncodingException e) {
            rd = new BufferedReader(new InputStreamReader(is));
        }
        String factoryClassName = null;
        try {
            factoryClassName = rd.readLine();
        }
        catch (IOException x) {
            String string = null;
            return string;
        }
        finally {
            try {
                rd.close();
            }
            catch (IOException iOException) {}
        }
        if (factoryClassName != null && !"".equals(factoryClassName)) {
            ObjectFactory.debugPrintln("found in resource, value=" + factoryClassName);
            return factoryClassName;
        }
        return null;
    }

    static class ConfigurationError
    extends Error {
        static final long serialVersionUID = 2036619216663421552L;
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

