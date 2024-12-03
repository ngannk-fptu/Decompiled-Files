/*
 * Decompiled with CFR 0.152.
 */
package org.cyberneko.html;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import org.cyberneko.html.SecuritySupport;

class ObjectFactory {
    private static final String DEFAULT_PROPERTIES_FILENAME = "xerces.properties";
    private static final boolean DEBUG = false;
    private static final int DEFAULT_LINE_LENGTH = 80;
    private static Properties fXercesProperties = null;
    private static long fLastModified = -1L;

    ObjectFactory() {
    }

    static Object createObject(String factoryId, String fallbackClassName) throws ConfigurationError {
        return ObjectFactory.createObject(factoryId, null, fallbackClassName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static Object createObject(String factoryId, String propertiesFilename, String fallbackClassName) throws ConfigurationError {
        SecuritySupport ss = SecuritySupport.getInstance();
        ClassLoader cl = ObjectFactory.findClassLoader();
        try {
            String systemProp = ss.getSystemProperty(factoryId);
            if (systemProp != null) {
                return ObjectFactory.newInstance(systemProp, cl, true);
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
                String javah = ss.getSystemProperty("java.home");
                propertiesFilename = javah + File.separator + "lib" + File.separator + DEFAULT_PROPERTIES_FILENAME;
                propertiesFile = new File(propertiesFilename);
                propertiesFileExists = ss.getFileExists(propertiesFile);
            }
            catch (SecurityException e) {
                fLastModified = -1L;
                fXercesProperties = null;
            }
            Class<ObjectFactory> clazz = ObjectFactory.class;
            synchronized (ObjectFactory.class) {
                boolean loadProperties = false;
                try {
                    if (fLastModified >= 0L) {
                        if (propertiesFileExists && fLastModified < (fLastModified = ss.getLastModified(propertiesFile))) {
                            loadProperties = true;
                        } else if (!propertiesFileExists) {
                            fLastModified = -1L;
                            fXercesProperties = null;
                        }
                    } else if (propertiesFileExists) {
                        loadProperties = true;
                        fLastModified = ss.getLastModified(propertiesFile);
                    }
                    if (loadProperties) {
                        fXercesProperties = new Properties();
                        FileInputStream fis = ss.getFileInputStream(propertiesFile);
                        fXercesProperties.load(fis);
                        fis.close();
                    }
                }
                catch (Exception x) {
                    fXercesProperties = null;
                    fLastModified = -1L;
                }
                if (fXercesProperties != null) {
                    factoryClassName = fXercesProperties.getProperty(factoryId);
                }
            }
        } else {
            try {
                FileInputStream fis = ss.getFileInputStream(new File(propertiesFilename));
                Properties props = new Properties();
                props.load(fis);
                fis.close();
                factoryClassName = props.getProperty(factoryId);
            }
            catch (Exception fis) {
                // empty catch block
            }
        }
        {
            if (factoryClassName != null) {
                return ObjectFactory.newInstance(factoryClassName, cl, true);
            }
            Object provider = ObjectFactory.findJarServiceProvider(factoryId);
            if (provider != null) {
                return provider;
            }
            if (fallbackClassName == null) {
                throw new ConfigurationError("Provider for " + factoryId + " cannot be found", null);
            }
            return ObjectFactory.newInstance(fallbackClassName, cl, true);
        }
    }

    private static void debugPrintln(String msg) {
    }

    static ClassLoader findClassLoader() throws ConfigurationError {
        ClassLoader system;
        SecuritySupport ss = SecuritySupport.getInstance();
        ClassLoader context = ss.getContextClassLoader();
        ClassLoader chain = system = ss.getSystemClassLoader();
        while (true) {
            if (context == chain) {
                ClassLoader current = ObjectFactory.class.getClassLoader();
                chain = system;
                while (true) {
                    if (current == chain) {
                        return system;
                    }
                    if (chain == null) break;
                    chain = ss.getParentClassLoader(chain);
                }
                return current;
            }
            if (chain == null) break;
            chain = ss.getParentClassLoader(chain);
        }
        return context;
    }

    static Object newInstance(String className, ClassLoader cl, boolean doFallback) throws ConfigurationError {
        try {
            Class providerClass = ObjectFactory.findProviderClass(className, cl, doFallback);
            Object instance = providerClass.newInstance();
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
            int lastDot = className.lastIndexOf(".");
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

    private static Object findJarServiceProvider(String factoryId) throws ConfigurationError {
        BufferedReader rd;
        ClassLoader current;
        SecuritySupport ss = SecuritySupport.getInstance();
        String serviceId = "META-INF/services/" + factoryId;
        InputStream is = null;
        ClassLoader cl = ObjectFactory.findClassLoader();
        is = ss.getResourceAsStream(cl, serviceId);
        if (is == null && cl != (current = ObjectFactory.class.getClassLoader())) {
            cl = current;
            is = ss.getResourceAsStream(cl, serviceId);
        }
        if (is == null) {
            return null;
        }
        try {
            rd = new BufferedReader(new InputStreamReader(is, "UTF-8"), 80);
        }
        catch (UnsupportedEncodingException e) {
            rd = new BufferedReader(new InputStreamReader(is), 80);
        }
        String factoryClassName = null;
        try {
            factoryClassName = rd.readLine();
            rd.close();
        }
        catch (IOException x) {
            return null;
        }
        if (factoryClassName != null && !"".equals(factoryClassName)) {
            return ObjectFactory.newInstance(factoryClassName, cl, false);
        }
        return null;
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

