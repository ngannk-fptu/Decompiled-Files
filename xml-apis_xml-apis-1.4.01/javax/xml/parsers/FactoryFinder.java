/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.xml.parsers.SecuritySupport;

final class FactoryFinder {
    private static boolean debug = false;
    private static Properties cacheProps = new Properties();
    private static boolean firstTime = true;
    private static final int DEFAULT_LINE_LENGTH = 80;
    static /* synthetic */ Class class$javax$xml$parsers$FactoryFinder;

    private FactoryFinder() {
    }

    private static void dPrint(String string) {
        if (debug) {
            System.err.println("JAXP: " + string);
        }
    }

    static Object newInstance(String string, ClassLoader classLoader, boolean bl) throws ConfigurationError {
        try {
            Class<?> clazz;
            if (classLoader == null) {
                clazz = Class.forName(string);
            } else {
                try {
                    clazz = classLoader.loadClass(string);
                }
                catch (ClassNotFoundException classNotFoundException) {
                    if (bl) {
                        classLoader = (class$javax$xml$parsers$FactoryFinder == null ? (class$javax$xml$parsers$FactoryFinder = FactoryFinder.class$("javax.xml.parsers.FactoryFinder")) : class$javax$xml$parsers$FactoryFinder).getClassLoader();
                        clazz = classLoader != null ? classLoader.loadClass(string) : Class.forName(string);
                    }
                    throw classNotFoundException;
                }
            }
            Object obj = clazz.newInstance();
            if (debug) {
                FactoryFinder.dPrint("created new instance of " + clazz + " using ClassLoader: " + classLoader);
            }
            return obj;
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new ConfigurationError("Provider " + string + " not found", classNotFoundException);
        }
        catch (Exception exception) {
            throw new ConfigurationError("Provider " + string + " could not be instantiated: " + exception, exception);
        }
    }

    static Object find(String string, String string2) throws ConfigurationError {
        Object object;
        ClassLoader classLoader;
        block20: {
            classLoader = SecuritySupport.getContextClassLoader();
            if (classLoader == null) {
                classLoader = (class$javax$xml$parsers$FactoryFinder == null ? (class$javax$xml$parsers$FactoryFinder = FactoryFinder.class$("javax.xml.parsers.FactoryFinder")) : class$javax$xml$parsers$FactoryFinder).getClassLoader();
            }
            if (debug) {
                FactoryFinder.dPrint("find factoryId =" + string);
            }
            try {
                object = SecuritySupport.getSystemProperty(string);
                if (object != null && ((String)object).length() > 0) {
                    if (debug) {
                        FactoryFinder.dPrint("found system property, value=" + (String)object);
                    }
                    return FactoryFinder.newInstance((String)object, classLoader, true);
                }
            }
            catch (SecurityException securityException) {
                // empty catch block
            }
            try {
                object = SecuritySupport.getSystemProperty("java.home");
                String string3 = (String)object + File.separator + "lib" + File.separator + "jaxp.properties";
                String string4 = null;
                if (firstTime) {
                    Properties properties = cacheProps;
                    synchronized (properties) {
                        if (firstTime) {
                            File file = new File(string3);
                            firstTime = false;
                            if (SecuritySupport.doesFileExist(file)) {
                                if (debug) {
                                    FactoryFinder.dPrint("Read properties file " + file);
                                }
                                cacheProps.load(SecuritySupport.getFileInputStream(file));
                            }
                        }
                    }
                }
                if ((string4 = cacheProps.getProperty(string)) != null) {
                    if (debug) {
                        FactoryFinder.dPrint("found in $java.home/jaxp.properties, value=" + string4);
                    }
                    return FactoryFinder.newInstance(string4, classLoader, true);
                }
            }
            catch (Exception exception) {
                if (!debug) break block20;
                exception.printStackTrace();
            }
        }
        if ((object = FactoryFinder.findJarServiceProvider(string)) != null) {
            return object;
        }
        if (string2 == null) {
            throw new ConfigurationError("Provider for " + string + " cannot be found", null);
        }
        if (debug) {
            FactoryFinder.dPrint("loaded from fallback value: " + string2);
        }
        return FactoryFinder.newInstance(string2, classLoader, true);
    }

    /*
     * Loose catch block
     */
    private static Object findJarServiceProvider(String string) throws ConfigurationError {
        String string2;
        ClassLoader classLoader;
        block18: {
            BufferedReader bufferedReader;
            String string3 = "META-INF/services/" + string;
            InputStream inputStream = null;
            classLoader = SecuritySupport.getContextClassLoader();
            if (classLoader != null) {
                inputStream = SecuritySupport.getResourceAsStream(classLoader, string3);
                if (inputStream == null) {
                    classLoader = (class$javax$xml$parsers$FactoryFinder == null ? (class$javax$xml$parsers$FactoryFinder = FactoryFinder.class$("javax.xml.parsers.FactoryFinder")) : class$javax$xml$parsers$FactoryFinder).getClassLoader();
                    inputStream = SecuritySupport.getResourceAsStream(classLoader, string3);
                }
            } else {
                classLoader = (class$javax$xml$parsers$FactoryFinder == null ? (class$javax$xml$parsers$FactoryFinder = FactoryFinder.class$("javax.xml.parsers.FactoryFinder")) : class$javax$xml$parsers$FactoryFinder).getClassLoader();
                inputStream = SecuritySupport.getResourceAsStream(classLoader, string3);
            }
            if (inputStream == null) {
                return null;
            }
            if (debug) {
                FactoryFinder.dPrint("found jar resource=" + string3 + " using ClassLoader: " + classLoader);
            }
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 80);
            }
            catch (UnsupportedEncodingException unsupportedEncodingException) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 80);
            }
            string2 = null;
            string2 = bufferedReader.readLine();
            Object var9_7 = null;
            try {
                bufferedReader.close();
            }
            catch (IOException iOException) {}
            break block18;
            {
                catch (IOException iOException) {
                    Object var7_14 = null;
                    Object var9_8 = null;
                    try {
                        bufferedReader.close();
                    }
                    catch (IOException iOException2) {
                        // empty catch block
                    }
                    return var7_14;
                }
            }
            catch (Throwable throwable) {
                Object var9_9 = null;
                try {
                    bufferedReader.close();
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                throw throwable;
            }
        }
        if (string2 != null && !"".equals(string2)) {
            if (debug) {
                FactoryFinder.dPrint("found in resource, value=" + string2);
            }
            return FactoryFinder.newInstance(string2, classLoader, false);
        }
        return null;
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    static {
        try {
            String string = SecuritySupport.getSystemProperty("jaxp.debug");
            debug = string != null && !"false".equals(string);
        }
        catch (SecurityException securityException) {
            debug = false;
        }
    }

    static class ConfigurationError
    extends Error {
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

