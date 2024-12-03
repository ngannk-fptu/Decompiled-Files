/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.ws.spi;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.spi.ServiceLoaderUtil;

class FactoryFinder {
    private static final Logger logger = Logger.getLogger("javax.xml.ws");
    private static final ServiceLoaderUtil.ExceptionHandler<WebServiceException> EXCEPTION_HANDLER = new ServiceLoaderUtil.ExceptionHandler<WebServiceException>(){

        @Override
        public WebServiceException createException(Throwable throwable, String message) {
            return new WebServiceException(message, throwable);
        }
    };
    private static final String OSGI_SERVICE_LOADER_CLASS_NAME = "org.glassfish.hk2.osgiresourcelocator.ServiceLoader";

    FactoryFinder() {
    }

    static <T> T find(Class<T> factoryClass, String fallbackClassName) {
        ClassLoader classLoader = ServiceLoaderUtil.contextClassLoader(EXCEPTION_HANDLER);
        Object provider = ServiceLoaderUtil.firstByServiceLoader(factoryClass, logger, EXCEPTION_HANDLER);
        if (provider != null) {
            return provider;
        }
        String factoryId = factoryClass.getName();
        provider = FactoryFinder.fromJDKProperties(factoryId, fallbackClassName, classLoader);
        if (provider != null) {
            return provider;
        }
        provider = FactoryFinder.fromSystemProperty(factoryId, fallbackClassName, classLoader);
        if (provider != null) {
            return provider;
        }
        if (FactoryFinder.isOsgi()) {
            return (T)FactoryFinder.lookupUsingOSGiServiceLoader(factoryId);
        }
        if (fallbackClassName == null) {
            throw new WebServiceException("Provider for " + factoryId + " cannot be found", null);
        }
        return (T)ServiceLoaderUtil.newInstance(fallbackClassName, fallbackClassName, classLoader, EXCEPTION_HANDLER);
    }

    private static Object fromSystemProperty(String factoryId, String fallbackClassName, ClassLoader classLoader) {
        try {
            String systemProp = System.getProperty(factoryId);
            if (systemProp != null) {
                return ServiceLoaderUtil.newInstance(systemProp, fallbackClassName, classLoader, EXCEPTION_HANDLER);
            }
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        return null;
    }

    private static Object fromJDKProperties(String factoryId, String fallbackClassName, ClassLoader classLoader) {
        block9: {
            Path path = null;
            try {
                String JAVA_HOME = System.getProperty("java.home");
                path = Paths.get(JAVA_HOME, "conf", "jaxws.properties");
                if (!Files.exists(path, new LinkOption[0])) {
                    path = Paths.get(JAVA_HOME, "lib", "jaxws.properties");
                }
                if (!Files.exists(path, new LinkOption[0])) break block9;
                Properties props = new Properties();
                try (InputStream inStream = Files.newInputStream(path, new OpenOption[0]);){
                    props.load(inStream);
                }
                String factoryClassName = props.getProperty(factoryId);
                return ServiceLoaderUtil.newInstance(factoryClassName, fallbackClassName, classLoader, EXCEPTION_HANDLER);
            }
            catch (Exception ignored) {
                logger.log(Level.SEVERE, "Error reading JAX-WS configuration from [" + path + "] file. Check it is accessible and has correct format.", ignored);
            }
        }
        return null;
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

