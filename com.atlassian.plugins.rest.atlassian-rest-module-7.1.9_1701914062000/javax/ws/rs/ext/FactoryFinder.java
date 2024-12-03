/*
 * Decompiled with CFR 0.152.
 */
package javax.ws.rs.ext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;

class FactoryFinder {
    FactoryFinder() {
    }

    static ClassLoader getContextClassLoader() {
        return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>(){

            @Override
            public ClassLoader run() {
                ClassLoader cl = null;
                try {
                    cl = Thread.currentThread().getContextClassLoader();
                }
                catch (SecurityException securityException) {
                    // empty catch block
                }
                return cl;
            }
        });
    }

    private static Object newInstance(String className, ClassLoader classLoader) throws ClassNotFoundException {
        try {
            Class<?> spiClass;
            if (classLoader == null) {
                spiClass = Class.forName(className);
            } else {
                try {
                    spiClass = Class.forName(className, false, classLoader);
                }
                catch (ClassNotFoundException ex) {
                    spiClass = Class.forName(className);
                }
            }
            return spiClass.newInstance();
        }
        catch (ClassNotFoundException x) {
            throw x;
        }
        catch (Exception x) {
            throw new ClassNotFoundException("Provider " + className + " could not be instantiated: " + x, x);
        }
    }

    static Object find(String factoryId, String fallbackClassName) throws ClassNotFoundException {
        ClassLoader classLoader = FactoryFinder.getContextClassLoader();
        String serviceId = "META-INF/services/" + factoryId;
        try {
            InputStream is = classLoader == null ? ClassLoader.getSystemResourceAsStream(serviceId) : classLoader.getResourceAsStream(serviceId);
            if (is != null) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String factoryClassName = rd.readLine();
                rd.close();
                if (factoryClassName != null && !"".equals(factoryClassName)) {
                    return FactoryFinder.newInstance(factoryClassName, classLoader);
                }
            }
        }
        catch (Exception ex) {
            // empty catch block
        }
        try {
            String javah = System.getProperty("java.home");
            String configFile = javah + File.separator + "lib" + File.separator + "jaxrs.properties";
            File f = new File(configFile);
            if (f.exists()) {
                Properties props = new Properties();
                props.load(new FileInputStream(f));
                String factoryClassName = props.getProperty(factoryId);
                return FactoryFinder.newInstance(factoryClassName, classLoader);
            }
        }
        catch (Exception ex) {
            // empty catch block
        }
        try {
            String systemProp = System.getProperty(factoryId);
            if (systemProp != null) {
                return FactoryFinder.newInstance(systemProp, classLoader);
            }
        }
        catch (SecurityException se) {
            // empty catch block
        }
        if (fallbackClassName == null) {
            throw new ClassNotFoundException("Provider for " + factoryId + " cannot be found", null);
        }
        return FactoryFinder.newInstance(fallbackClassName, classLoader);
    }
}

