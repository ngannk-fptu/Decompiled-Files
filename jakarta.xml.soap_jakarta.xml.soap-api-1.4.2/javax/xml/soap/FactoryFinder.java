/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.soap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.soap.SOAPException;
import javax.xml.soap.ServiceLoaderUtil;

class FactoryFinder {
    private static final Logger logger = Logger.getLogger("javax.xml.soap");
    private static final ServiceLoaderUtil.ExceptionHandler<SOAPException> EXCEPTION_HANDLER = new ServiceLoaderUtil.ExceptionHandler<SOAPException>(){

        @Override
        public SOAPException createException(Throwable throwable, String message) {
            return new SOAPException(message, throwable);
        }
    };
    private static final String OSGI_SERVICE_LOADER_CLASS_NAME = "org.glassfish.hk2.osgiresourcelocator.ServiceLoader";

    FactoryFinder() {
    }

    static <T> T find(Class<T> factoryClass, String defaultClassName, boolean tryFallback, String deprecatedFactoryId) throws SOAPException {
        Object result;
        ClassLoader tccl = ServiceLoaderUtil.contextClassLoader(EXCEPTION_HANDLER);
        String factoryId = factoryClass.getName();
        String className = FactoryFinder.fromSystemProperty(factoryId, deprecatedFactoryId);
        if (className != null && (result = FactoryFinder.newInstance(className, defaultClassName, tccl)) != null) {
            return (T)result;
        }
        className = FactoryFinder.fromJDKProperties(factoryId, deprecatedFactoryId);
        if (className != null && (result = FactoryFinder.newInstance(className, defaultClassName, tccl)) != null) {
            return (T)result;
        }
        T factory = ServiceLoaderUtil.firstByServiceLoader(factoryClass, logger, EXCEPTION_HANDLER);
        if (factory != null) {
            return factory;
        }
        className = FactoryFinder.fromMetaInfServices(deprecatedFactoryId, tccl);
        if (className != null) {
            logger.log(Level.WARNING, "Using deprecated META-INF/services mechanism with non-standard property: {0}. Property {1} should be used instead.", new Object[]{deprecatedFactoryId, factoryId});
            Object result2 = FactoryFinder.newInstance(className, defaultClassName, tccl);
            if (result2 != null) {
                return (T)result2;
            }
        }
        if (FactoryFinder.isOsgi()) {
            return (T)FactoryFinder.lookupUsingOSGiServiceLoader(factoryId);
        }
        if (!tryFallback) {
            return null;
        }
        if (defaultClassName == null) {
            throw new SOAPException("Provider for " + factoryId + " cannot be found", null);
        }
        return (T)FactoryFinder.newInstance(defaultClassName, defaultClassName, tccl);
    }

    static <T> T find(Class<T> factoryClass, String defaultClassName, boolean tryFallback) throws SOAPException {
        return FactoryFinder.find(factoryClass, defaultClassName, tryFallback, null);
    }

    private static Object newInstance(String className, String defaultClassName, ClassLoader tccl) throws SOAPException {
        return ServiceLoaderUtil.newInstance(className, defaultClassName, tccl, EXCEPTION_HANDLER);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static String fromMetaInfServices(String deprecatedFactoryId, ClassLoader tccl) {
        String serviceId = "META-INF/services/" + deprecatedFactoryId;
        logger.log(Level.FINE, "Checking deprecated {0} resource", serviceId);
        try (InputStream is = tccl == null ? ClassLoader.getSystemResourceAsStream(serviceId) : tccl.getResourceAsStream(serviceId);){
            String factoryClassName;
            if (is == null) return null;
            try (InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
                 BufferedReader rd = new BufferedReader(isr);){
                factoryClassName = rd.readLine();
            }
            FactoryFinder.logFound(factoryClassName);
            if (factoryClassName == null) return null;
            if ("".equals(factoryClassName)) return null;
            String string = factoryClassName;
            return string;
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return null;
    }

    private static String fromJDKProperties(String factoryId, String deprecatedFactoryId) {
        block12: {
            Path path = null;
            try {
                String JAVA_HOME = FactoryFinder.getSystemProperty("java.home");
                path = Paths.get(JAVA_HOME, "conf", "jaxm.properties");
                logger.log(Level.FINE, "Checking configuration in {0}", path);
                if (!Files.exists(path, new LinkOption[0])) {
                    path = Paths.get(JAVA_HOME, "lib", "jaxm.properties");
                }
                logger.log(Level.FINE, "Checking configuration in {0}", path);
                if (!Files.exists(path, new LinkOption[0])) break block12;
                Properties props = new Properties();
                try (InputStream inputStream = Files.newInputStream(path, new OpenOption[0]);){
                    props.load(inputStream);
                }
                logger.log(Level.FINE, "Checking property {0}", factoryId);
                String factoryClassName = props.getProperty(factoryId);
                FactoryFinder.logFound(factoryClassName);
                if (factoryClassName != null) {
                    return factoryClassName;
                }
                if (deprecatedFactoryId != null) {
                    logger.log(Level.FINE, "Checking deprecated property {0}", deprecatedFactoryId);
                    factoryClassName = props.getProperty(deprecatedFactoryId);
                    FactoryFinder.logFound(factoryClassName);
                    if (factoryClassName != null) {
                        logger.log(Level.WARNING, "Using non-standard property: {0}. Property {1} should be used instead.", new Object[]{deprecatedFactoryId, factoryId});
                        return factoryClassName;
                    }
                }
            }
            catch (Exception ignored) {
                logger.log(Level.SEVERE, "Error reading SAAJ configuration from [" + path + "] file. Check it is accessible and has correct format.", ignored);
            }
        }
        return null;
    }

    private static String fromSystemProperty(String factoryId, String deprecatedFactoryId) {
        String systemProp = FactoryFinder.getSystemProperty(factoryId);
        if (systemProp != null) {
            return systemProp;
        }
        if (deprecatedFactoryId != null && (systemProp = FactoryFinder.getSystemProperty(deprecatedFactoryId)) != null) {
            logger.log(Level.WARNING, "Using non-standard property: {0}. Property {1} should be used instead.", new Object[]{deprecatedFactoryId, factoryId});
            return systemProp;
        }
        return null;
    }

    private static String getSystemProperty(final String property) {
        logger.log(Level.FINE, "Checking system property {0}", property);
        String value = AccessController.doPrivileged(new PrivilegedAction<String>(){

            @Override
            public String run() {
                return System.getProperty(property);
            }
        });
        FactoryFinder.logFound(value);
        return value;
    }

    private static void logFound(String value) {
        if (value != null) {
            logger.log(Level.FINE, "  found {0}", value);
        } else {
            logger.log(Level.FINE, "  not found");
        }
    }

    private static boolean isOsgi() {
        try {
            Class.forName(OSGI_SERVICE_LOADER_CLASS_NAME);
            return true;
        }
        catch (ClassNotFoundException classNotFoundException) {
            return false;
        }
    }

    private static Object lookupUsingOSGiServiceLoader(String factoryId) {
        try {
            Class<?> serviceClass = Class.forName(factoryId);
            Class[] args = new Class[]{serviceClass};
            Class<?> target = Class.forName(OSGI_SERVICE_LOADER_CLASS_NAME);
            Method m = target.getMethod("lookupProviderInstances", Class.class);
            Iterator iter = ((Iterable)m.invoke(null, (Object[])args)).iterator();
            return iter.hasNext() ? iter.next() : null;
        }
        catch (Exception ignored) {
            return null;
        }
    }
}

