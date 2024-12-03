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

class FactoryLocator {
    FactoryLocator() {
    }

    static Object locate(String factoryId) throws FactoryConfigurationError {
        return FactoryLocator.locate(factoryId, null);
    }

    static Object locate(String factoryId, String altClassName) throws FactoryConfigurationError {
        return FactoryLocator.locate(factoryId, altClassName, Thread.currentThread().getContextClassLoader());
    }

    static Object locate(String factoryId, String altClassName, ClassLoader classLoader) throws FactoryConfigurationError {
        try {
            String prop = System.getProperty(factoryId);
            if (prop != null) {
                return FactoryLocator.loadFactory(prop, classLoader);
            }
        }
        catch (Exception e) {
            // empty catch block
        }
        try {
            String configFile = System.getProperty("java.home") + File.separator + "lib" + File.separator + "stax.properties";
            File f = new File(configFile);
            if (f.exists()) {
                Properties props = new Properties();
                props.load(new FileInputStream(f));
                String factoryClassName = props.getProperty(factoryId);
                return FactoryLocator.loadFactory(factoryClassName, classLoader);
            }
        }
        catch (Exception e) {
            // empty catch block
        }
        String serviceId = "META-INF/services/" + factoryId;
        try {
            InputStream is = null;
            is = classLoader == null ? ClassLoader.getSystemResourceAsStream(serviceId) : classLoader.getResourceAsStream(serviceId);
            if (is != null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String factoryClassName = br.readLine();
                br.close();
                if (factoryClassName != null && !"".equals(factoryClassName)) {
                    return FactoryLocator.loadFactory(factoryClassName, classLoader);
                }
            }
        }
        catch (Exception ex) {
            // empty catch block
        }
        if (altClassName == null) {
            throw new FactoryConfigurationError("Unable to locate factory for " + factoryId + ".", null);
        }
        return FactoryLocator.loadFactory(altClassName, classLoader);
    }

    private static Object loadFactory(String className, ClassLoader classLoader) throws FactoryConfigurationError {
        try {
            Class<?> factoryClass = classLoader == null ? Class.forName(className) : classLoader.loadClass(className);
            return factoryClass.newInstance();
        }
        catch (ClassNotFoundException x) {
            throw new FactoryConfigurationError("Requested factory " + className + " cannot be located.  Classloader =" + classLoader.toString(), x);
        }
        catch (Exception x) {
            throw new FactoryConfigurationError("Requested factory " + className + " could not be instantiated: " + x, x);
        }
    }
}

