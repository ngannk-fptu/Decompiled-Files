/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.datatype;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Properties;
import javax.xml.datatype.SecuritySupport;

final class FactoryFinder {
    private static final String CLASS_NAME = "javax.xml.datatype.FactoryFinder";
    private static boolean debug = false;
    private static Properties cacheProps = new Properties();
    private static boolean firstTime = true;
    private static final int DEFAULT_LINE_LENGTH = 80;
    static /* synthetic */ Class class$javax$xml$datatype$FactoryFinder;

    private FactoryFinder() {
    }

    private static void debugPrintln(String string) {
        if (debug) {
            System.err.println("javax.xml.datatype.FactoryFinder:" + string);
        }
    }

    private static ClassLoader findClassLoader() throws ConfigurationError {
        ClassLoader classLoader = SecuritySupport.getContextClassLoader();
        if (debug) {
            FactoryFinder.debugPrintln("Using context class loader: " + classLoader);
        }
        if (classLoader == null) {
            classLoader = (class$javax$xml$datatype$FactoryFinder == null ? (class$javax$xml$datatype$FactoryFinder = FactoryFinder.class$(CLASS_NAME)) : class$javax$xml$datatype$FactoryFinder).getClassLoader();
            if (debug) {
                FactoryFinder.debugPrintln("Using the class loader of FactoryFinder: " + classLoader);
            }
        }
        return classLoader;
    }

    static Object newInstance(String string, ClassLoader classLoader) throws ConfigurationError {
        try {
            Class<?> clazz = classLoader == null ? Class.forName(string) : classLoader.loadClass(string);
            if (debug) {
                FactoryFinder.debugPrintln("Loaded " + string + " from " + FactoryFinder.which(clazz));
            }
            return clazz.newInstance();
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
        block18: {
            classLoader = FactoryFinder.findClassLoader();
            try {
                object = SecuritySupport.getSystemProperty(string);
                if (object != null && ((String)object).length() > 0) {
                    if (debug) {
                        FactoryFinder.debugPrintln("found " + (String)object + " in the system property " + string);
                    }
                    return FactoryFinder.newInstance((String)object, classLoader);
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
                                    FactoryFinder.debugPrintln("Read properties file " + file);
                                }
                                cacheProps.load(SecuritySupport.getFileInputStream(file));
                            }
                        }
                    }
                }
                string4 = cacheProps.getProperty(string);
                if (debug) {
                    FactoryFinder.debugPrintln("found " + string4 + " in $java.home/jaxp.properties");
                }
                if (string4 != null) {
                    return FactoryFinder.newInstance(string4, classLoader);
                }
            }
            catch (Exception exception) {
                if (!debug) break block18;
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
            FactoryFinder.debugPrintln("loaded from fallback value: " + string2);
        }
        return FactoryFinder.newInstance(string2, classLoader);
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
                    classLoader = (class$javax$xml$datatype$FactoryFinder == null ? (class$javax$xml$datatype$FactoryFinder = FactoryFinder.class$(CLASS_NAME)) : class$javax$xml$datatype$FactoryFinder).getClassLoader();
                    inputStream = SecuritySupport.getResourceAsStream(classLoader, string3);
                }
            } else {
                classLoader = (class$javax$xml$datatype$FactoryFinder == null ? (class$javax$xml$datatype$FactoryFinder = FactoryFinder.class$(CLASS_NAME)) : class$javax$xml$datatype$FactoryFinder).getClassLoader();
                inputStream = SecuritySupport.getResourceAsStream(classLoader, string3);
            }
            if (inputStream == null) {
                return null;
            }
            if (debug) {
                FactoryFinder.debugPrintln("found jar resource=" + string3 + " using ClassLoader: " + classLoader);
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
                FactoryFinder.debugPrintln("found in resource, value=" + string2);
            }
            return FactoryFinder.newInstance(string2, classLoader);
        }
        return null;
    }

    private static String which(Class clazz) {
        block5: {
            try {
                String string = clazz.getName().replace('.', '/') + ".class";
                ClassLoader classLoader = clazz.getClassLoader();
                URL uRL = classLoader != null ? classLoader.getResource(string) : ClassLoader.getSystemResource(string);
                if (uRL != null) {
                    return uRL.toString();
                }
            }
            catch (VirtualMachineError virtualMachineError) {
                throw virtualMachineError;
            }
            catch (ThreadDeath threadDeath) {
                throw threadDeath;
            }
            catch (Throwable throwable) {
                if (!debug) break block5;
                throwable.printStackTrace();
            }
        }
        return "unknown location";
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
        catch (Exception exception) {
            debug = false;
        }
    }

    static class ConfigurationError
    extends Error {
        private static final long serialVersionUID = -3644413026244211347L;
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

