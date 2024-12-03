/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import org.apache.xerces.parsers.SecuritySupport;

final class ObjectFactory {
    private static final String DEFAULT_PROPERTIES_FILENAME = "xerces.properties";
    private static final boolean DEBUG = ObjectFactory.isDebugEnabled();
    private static final int DEFAULT_LINE_LENGTH = 80;
    private static Properties fXercesProperties = null;
    private static long fLastModified = -1L;

    ObjectFactory() {
    }

    static Object createObject(String string, String string2) throws ConfigurationError {
        return ObjectFactory.createObject(string, null, string2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static Object createObject(String string, String string2, String string3) throws ConfigurationError {
        Object object;
        String string4;
        if (DEBUG) {
            ObjectFactory.debugPrintln("debug is on");
        }
        ClassLoader classLoader = ObjectFactory.findClassLoader();
        try {
            string4 = SecuritySupport.getSystemProperty(string);
            if (string4 != null && string4.length() > 0) {
                if (DEBUG) {
                    ObjectFactory.debugPrintln("found system property, value=" + string4);
                }
                return ObjectFactory.newInstance(string4, classLoader, true);
            }
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        string4 = null;
        if (string2 == null) {
            Object object2;
            object = null;
            boolean bl = false;
            try {
                object2 = SecuritySupport.getSystemProperty("java.home");
                string2 = (String)object2 + File.separator + "lib" + File.separator + DEFAULT_PROPERTIES_FILENAME;
                object = new File(string2);
                bl = SecuritySupport.getFileExists((File)object);
            }
            catch (SecurityException securityException) {
                fLastModified = -1L;
                fXercesProperties = null;
            }
            object2 = ObjectFactory.class;
            synchronized (ObjectFactory.class) {
                boolean bl2 = false;
                FileInputStream fileInputStream = null;
                try {
                    if (fLastModified >= 0L) {
                        if (bl && fLastModified < (fLastModified = SecuritySupport.getLastModified((File)object))) {
                            bl2 = true;
                        } else if (!bl) {
                            fLastModified = -1L;
                            fXercesProperties = null;
                        }
                    } else if (bl) {
                        bl2 = true;
                        fLastModified = SecuritySupport.getLastModified((File)object);
                    }
                    if (bl2) {
                        fXercesProperties = new Properties();
                        fileInputStream = SecuritySupport.getFileInputStream((File)object);
                        fXercesProperties.load(fileInputStream);
                    }
                }
                catch (Exception exception) {
                    fXercesProperties = null;
                    fLastModified = -1L;
                }
                finally {
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        }
                        catch (IOException iOException) {}
                    }
                }
                // ** MonitorExit[var7_12] (shouldn't be in output)
                if (fXercesProperties != null) {
                    string4 = fXercesProperties.getProperty(string);
                }
            }
        } else {
            object = null;
            try {
                object = SecuritySupport.getFileInputStream(new File(string2));
                Properties properties = new Properties();
                properties.load((InputStream)object);
                string4 = properties.getProperty(string);
            }
            catch (Exception exception) {
            }
            finally {
                if (object != null) {
                    try {
                        ((FileInputStream)object).close();
                    }
                    catch (IOException iOException) {}
                }
            }
        }
        {
            if (string4 != null) {
                if (DEBUG) {
                    ObjectFactory.debugPrintln("found in " + string2 + ", value=" + string4);
                }
                return ObjectFactory.newInstance(string4, classLoader, true);
            }
            object = ObjectFactory.findJarServiceProvider(string);
            if (object != null) {
                return object;
            }
            if (string3 == null) {
                throw new ConfigurationError("Provider for " + string + " cannot be found", null);
            }
            if (DEBUG) {
                ObjectFactory.debugPrintln("using fallback, value=" + string3);
            }
            return ObjectFactory.newInstance(string3, classLoader, true);
        }
    }

    private static boolean isDebugEnabled() {
        try {
            String string = SecuritySupport.getSystemProperty("xerces.debug");
            return string != null && !"false".equals(string);
        }
        catch (SecurityException securityException) {
            return false;
        }
    }

    private static void debugPrintln(String string) {
        if (DEBUG) {
            System.err.println("XERCES: " + string);
        }
    }

    static ClassLoader findClassLoader() throws ConfigurationError {
        ClassLoader classLoader;
        ClassLoader classLoader2 = SecuritySupport.getContextClassLoader();
        ClassLoader classLoader3 = classLoader = SecuritySupport.getSystemClassLoader();
        while (true) {
            if (classLoader2 == classLoader3) {
                ClassLoader classLoader4 = ObjectFactory.class.getClassLoader();
                classLoader3 = classLoader;
                while (true) {
                    if (classLoader4 == classLoader3) {
                        return classLoader;
                    }
                    if (classLoader3 == null) break;
                    classLoader3 = SecuritySupport.getParentClassLoader(classLoader3);
                }
                return classLoader4;
            }
            if (classLoader3 == null) break;
            classLoader3 = SecuritySupport.getParentClassLoader(classLoader3);
        }
        return classLoader2;
    }

    static Object newInstance(String string, ClassLoader classLoader, boolean bl) throws ConfigurationError {
        try {
            Class clazz = ObjectFactory.findProviderClass(string, classLoader, bl);
            Object t = clazz.newInstance();
            if (DEBUG) {
                ObjectFactory.debugPrintln("created new instance of " + clazz + " using ClassLoader: " + classLoader);
            }
            return t;
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new ConfigurationError("Provider " + string + " not found", classNotFoundException);
        }
        catch (Exception exception) {
            throw new ConfigurationError("Provider " + string + " could not be instantiated: " + exception, exception);
        }
    }

    static Class findProviderClass(String string, ClassLoader classLoader, boolean bl) throws ClassNotFoundException, ConfigurationError {
        Class<?> clazz;
        SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            int n = string.lastIndexOf(46);
            String string2 = string;
            if (n != -1) {
                string2 = string.substring(0, n);
            }
            securityManager.checkPackageAccess(string2);
        }
        if (classLoader == null) {
            clazz = Class.forName(string);
        } else {
            try {
                clazz = classLoader.loadClass(string);
            }
            catch (ClassNotFoundException classNotFoundException) {
                if (bl) {
                    ClassLoader classLoader2 = ObjectFactory.class.getClassLoader();
                    if (classLoader2 == null) {
                        clazz = Class.forName(string);
                    }
                    if (classLoader != classLoader2) {
                        classLoader = classLoader2;
                        clazz = classLoader.loadClass(string);
                    }
                    throw classNotFoundException;
                }
                throw classNotFoundException;
            }
        }
        return clazz;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static Object findJarServiceProvider(String string) throws ConfigurationError {
        Object object;
        String string2 = "META-INF/services/" + string;
        InputStream inputStream = null;
        ClassLoader classLoader = ObjectFactory.findClassLoader();
        inputStream = SecuritySupport.getResourceAsStream(classLoader, string2);
        if (inputStream == null && classLoader != (object = ObjectFactory.class.getClassLoader())) {
            classLoader = object;
            inputStream = SecuritySupport.getResourceAsStream(classLoader, string2);
        }
        if (inputStream == null) {
            return null;
        }
        if (DEBUG) {
            ObjectFactory.debugPrintln("found jar resource=" + string2 + " using ClassLoader: " + classLoader);
        }
        try {
            object = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 80);
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            object = new BufferedReader(new InputStreamReader(inputStream), 80);
        }
        String string3 = null;
        try {
            string3 = ((BufferedReader)object).readLine();
        }
        catch (IOException iOException) {
            Object var7_9 = null;
            return var7_9;
        }
        finally {
            try {
                ((BufferedReader)object).close();
            }
            catch (IOException iOException) {}
        }
        if (string3 != null && !"".equals(string3)) {
            if (DEBUG) {
                ObjectFactory.debugPrintln("found in resource, value=" + string3);
            }
            return ObjectFactory.newInstance(string3, classLoader, false);
        }
        return null;
    }

    static final class ConfigurationError
    extends Error {
        static final long serialVersionUID = -7285495612271660427L;
        private Exception exception;

        ConfigurationError(String string, Exception exception) {
            super(string);
            this.exception = exception;
        }

        Exception getException() {
            return this.exception;
        }
    }
}

