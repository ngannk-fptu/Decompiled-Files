/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.soap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import javax.xml.soap.SOAPException;

class FactoryFinder {
    static /* synthetic */ Class class$javax$xml$soap$FactoryFinder;

    FactoryFinder() {
    }

    private static Object newInstance(String factoryClassName) throws SOAPException {
        ClassLoader classloader = null;
        try {
            classloader = Thread.currentThread().getContextClassLoader();
        }
        catch (Exception exception) {
            throw new SOAPException(exception.toString(), exception);
        }
        try {
            Class<?> factory = null;
            if (classloader == null) {
                factory = Class.forName(factoryClassName);
            } else {
                try {
                    factory = classloader.loadClass(factoryClassName);
                }
                catch (ClassNotFoundException classNotFoundException) {
                    // empty catch block
                }
            }
            if (factory == null) {
                classloader = (class$javax$xml$soap$FactoryFinder == null ? (class$javax$xml$soap$FactoryFinder = FactoryFinder.class$("javax.xml.soap.FactoryFinder")) : class$javax$xml$soap$FactoryFinder).getClassLoader();
                factory = classloader.loadClass(factoryClassName);
            }
            return factory.newInstance();
        }
        catch (ClassNotFoundException classnotfoundexception) {
            throw new SOAPException("Provider " + factoryClassName + " not found", classnotfoundexception);
        }
        catch (Exception exception) {
            throw new SOAPException("Provider " + factoryClassName + " could not be instantiated: " + exception, exception);
        }
    }

    static Object find(String factoryPropertyName, String defaultFactoryClassName) throws SOAPException {
        try {
            String factoryClassName = System.getProperty(factoryPropertyName);
            if (factoryClassName != null) {
                return FactoryFinder.newInstance(factoryClassName);
            }
        }
        catch (SecurityException securityexception) {
            // empty catch block
        }
        try {
            String propertiesFileName = System.getProperty("java.home") + File.separator + "lib" + File.separator + "jaxm.properties";
            File file = new File(propertiesFileName);
            if (file.exists()) {
                FileInputStream fileInput = new FileInputStream(file);
                Properties properties = new Properties();
                properties.load(fileInput);
                fileInput.close();
                String factoryClassName = properties.getProperty(factoryPropertyName);
                return FactoryFinder.newInstance(factoryClassName);
            }
        }
        catch (Exception exception1) {
            // empty catch block
        }
        String factoryResource = "META-INF/services/" + factoryPropertyName;
        try {
            InputStream inputstream = FactoryFinder.getResource(factoryResource);
            if (inputstream != null) {
                BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputstream, "UTF-8"));
                String factoryClassName = bufferedreader.readLine();
                bufferedreader.close();
                if (factoryClassName != null && !"".equals(factoryClassName)) {
                    return FactoryFinder.newInstance(factoryClassName);
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (defaultFactoryClassName == null) {
            throw new SOAPException("Provider for " + factoryPropertyName + " cannot be found", null);
        }
        return FactoryFinder.newInstance(defaultFactoryClassName);
    }

    private static InputStream getResource(String factoryResource) {
        ClassLoader classloader = null;
        try {
            classloader = Thread.currentThread().getContextClassLoader();
        }
        catch (SecurityException securityexception) {
            // empty catch block
        }
        InputStream inputstream = classloader == null ? ClassLoader.getSystemResourceAsStream(factoryResource) : classloader.getResourceAsStream(factoryResource);
        if (inputstream == null) {
            inputstream = (class$javax$xml$soap$FactoryFinder == null ? (class$javax$xml$soap$FactoryFinder = FactoryFinder.class$("javax.xml.soap.FactoryFinder")) : class$javax$xml$soap$FactoryFinder).getClassLoader().getResourceAsStream(factoryResource);
        }
        return inputstream;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

