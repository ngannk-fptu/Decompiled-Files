/*
 * Decompiled with CFR 0.152.
 */
package javax.wsdl.factory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;
import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.extensions.ExtensionRegistry;
import javax.wsdl.xml.WSDLReader;
import javax.wsdl.xml.WSDLWriter;

public abstract class WSDLFactory {
    private static final String PROPERTY_NAME = "javax.wsdl.factory.WSDLFactory";
    private static final String PROPERTY_FILE_NAME = "wsdl.properties";
    private static final String META_INF_SERVICES_PROPERTY_FILE_NAME = "javax.wsdl.factory.WSDLFactory";
    private static final String DEFAULT_FACTORY_IMPL_NAME = "com.ibm.wsdl.factory.WSDLFactoryImpl";
    private static String fullPropertyFileName = null;
    private static String metaInfServicesFullPropertyFileName = null;

    public static WSDLFactory newInstance() throws WSDLException {
        String factoryImplName = WSDLFactory.findFactoryImplName();
        return WSDLFactory.newInstance(factoryImplName);
    }

    public static WSDLFactory newInstance(String factoryImplName) throws WSDLException {
        if (factoryImplName != null) {
            try {
                Class<?> cl = Class.forName(factoryImplName);
                return (WSDLFactory)cl.newInstance();
            }
            catch (Exception e) {
                throw new WSDLException("CONFIGURATION_ERROR", "Problem instantiating factory implementation.", e);
            }
        }
        throw new WSDLException("CONFIGURATION_ERROR", "Unable to find name of factory implementation.");
    }

    public static WSDLFactory newInstance(String factoryImplName, ClassLoader classLoader) throws WSDLException {
        if (factoryImplName != null) {
            try {
                Class<?> cl = classLoader.loadClass(factoryImplName);
                return (WSDLFactory)cl.newInstance();
            }
            catch (Exception e) {
                throw new WSDLException("CONFIGURATION_ERROR", "Problem instantiating factory implementation.", e);
            }
        }
        throw new WSDLException("CONFIGURATION_ERROR", "Unable to find name of factory implementation.");
    }

    public abstract Definition newDefinition();

    public abstract WSDLReader newWSDLReader();

    public abstract WSDLWriter newWSDLWriter();

    public abstract ExtensionRegistry newPopulatedExtensionRegistry();

    private static String findFactoryImplName() {
        String factoryImplName = null;
        final String metaInfServicesPropFileName = WSDLFactory.getMetaInfFullPropertyFileName();
        if (metaInfServicesPropFileName != null) {
            try {
                InputStream is = (InputStream)AccessController.doPrivileged(new PrivilegedAction(){

                    public Object run() {
                        return WSDLFactory.class.getResourceAsStream(metaInfServicesPropFileName);
                    }
                });
                if (is != null) {
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    factoryImplName = br.readLine();
                    br.close();
                    isr.close();
                    is.close();
                }
                if (factoryImplName != null) {
                    return factoryImplName;
                }
            }
            catch (IOException e) {
                // empty catch block
            }
        }
        try {
            factoryImplName = System.getProperty("javax.wsdl.factory.WSDLFactory");
            if (factoryImplName != null) {
                return factoryImplName;
            }
        }
        catch (SecurityException e) {
            // empty catch block
        }
        String propFileName = WSDLFactory.getFullPropertyFileName();
        if (propFileName != null) {
            try {
                Properties properties = new Properties();
                File propFile = new File(propFileName);
                FileInputStream fis = new FileInputStream(propFile);
                properties.load(fis);
                fis.close();
                factoryImplName = properties.getProperty("javax.wsdl.factory.WSDLFactory");
                if (factoryImplName != null) {
                    return factoryImplName;
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        return DEFAULT_FACTORY_IMPL_NAME;
    }

    private static String getFullPropertyFileName() {
        if (fullPropertyFileName == null) {
            try {
                String javaHome = System.getProperty("java.home");
                fullPropertyFileName = javaHome + File.separator + "lib" + File.separator + PROPERTY_FILE_NAME;
            }
            catch (SecurityException securityException) {
                // empty catch block
            }
        }
        return fullPropertyFileName;
    }

    private static String getMetaInfFullPropertyFileName() {
        if (metaInfServicesFullPropertyFileName == null) {
            String metaInfServices = "/META-INF/services/";
            metaInfServicesFullPropertyFileName = metaInfServices + "javax.wsdl.factory.WSDLFactory";
        }
        return metaInfServicesFullPropertyFileName;
    }
}

