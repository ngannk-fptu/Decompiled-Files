/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.soap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import org.apache.xmlbeans.SystemProperties;
import org.apache.xmlbeans.impl.soap.SOAPException;

class FactoryFinder {
    FactoryFinder() {
    }

    private static Object newInstance(String factoryClassName) throws SOAPException {
        ClassLoader classloader;
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
                classloader = FactoryFinder.class.getClassLoader();
                factory = classloader.loadClass(factoryClassName);
            }
            return factory.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (ClassNotFoundException classnotfoundexception) {
            throw new SOAPException("Provider " + factoryClassName + " not found", classnotfoundexception);
        }
        catch (Exception exception) {
            throw new SOAPException("Provider " + factoryClassName + " could not be instantiated: " + exception, exception);
        }
    }

    static Object find(String factoryPropertyName, String defaultFactoryClassName) throws SOAPException {
        block35: {
            block33: {
                try {
                    String factoryClassName = SystemProperties.getProperty(factoryPropertyName);
                    if (factoryClassName != null) {
                        return FactoryFinder.newInstance(factoryClassName);
                    }
                }
                catch (SecurityException factoryClassName) {
                    // empty catch block
                }
                try {
                    String propertiesFileName = SystemProperties.getProperty("java.home") + File.separator + "lib" + File.separator + "jaxm.properties";
                    File file = new File(propertiesFileName);
                    if (!file.exists()) break block33;
                    Properties properties = new Properties();
                    try (FileInputStream fileInput = new FileInputStream(file);){
                        properties.load(fileInput);
                    }
                    String factoryClassName = properties.getProperty(factoryPropertyName);
                    return FactoryFinder.newInstance(factoryClassName);
                }
                catch (Exception propertiesFileName) {
                    // empty catch block
                }
            }
            String factoryResource = "META-INF/services/" + factoryPropertyName;
            try {
                String factoryClassName;
                InputStream inputstream = FactoryFinder.getResource(factoryResource);
                if (inputstream == null) break block35;
                try (BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));){
                    factoryClassName = bufferedreader.readLine();
                }
                if (factoryClassName != null && !"".equals(factoryClassName)) {
                    return FactoryFinder.newInstance(factoryClassName);
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
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
        catch (SecurityException securityException) {
            // empty catch block
        }
        InputStream inputstream = classloader == null ? ClassLoader.getSystemResourceAsStream(factoryResource) : classloader.getResourceAsStream(factoryResource);
        if (inputstream == null) {
            inputstream = FactoryFinder.class.getResourceAsStream(factoryResource);
        }
        if (inputstream == null && FactoryFinder.class.getClassLoader() != null) {
            inputstream = FactoryFinder.class.getClassLoader().getResourceAsStream(factoryResource);
        }
        return inputstream;
    }
}

