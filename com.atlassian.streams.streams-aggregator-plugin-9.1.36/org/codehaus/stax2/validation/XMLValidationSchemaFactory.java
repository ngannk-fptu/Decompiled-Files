/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.validation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.XMLValidationSchema;

public abstract class XMLValidationSchemaFactory {
    public static final String INTERNAL_ID_SCHEMA_DTD = "dtd";
    public static final String INTERNAL_ID_SCHEMA_RELAXNG = "relaxng";
    public static final String INTERNAL_ID_SCHEMA_W3C = "w3c";
    public static final String INTERNAL_ID_SCHEMA_TREX = "trex";
    static final HashMap sSchemaIds = new HashMap();
    static final String JAXP_PROP_FILENAME = "jaxp.properties";
    public static final String SYSTEM_PROPERTY_FOR_IMPL = "org.codehaus.stax2.validation.XMLValidationSchemaFactory.";
    public static final String SERVICE_DEFINITION_PATH = "META-INF/services/org.codehaus.stax2.validation.XMLValidationSchemaFactory.";
    public static final String P_IS_NAMESPACE_AWARE = "org.codehaus2.stax2.validation.isNamespaceAware";
    public static final String P_ENABLE_CACHING = "org.codehaus2.stax2.validation.enableCaching";
    protected final String mSchemaType;

    protected XMLValidationSchemaFactory(String string) {
        this.mSchemaType = string;
    }

    public static XMLValidationSchemaFactory newInstance(String string) throws FactoryConfigurationError {
        return XMLValidationSchemaFactory.newInstance(string, Thread.currentThread().getContextClassLoader());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static XMLValidationSchemaFactory newInstance(String string, ClassLoader classLoader) throws FactoryConfigurationError {
        Enumeration<URL> enumeration;
        String string2;
        SecurityException securityException;
        String string3;
        block19: {
            Object object;
            Serializable serializable;
            String string4;
            block18: {
                string4 = (String)sSchemaIds.get(string);
                if (string4 == null) {
                    throw new FactoryConfigurationError("Unrecognized schema type (id '" + string + "')");
                }
                string3 = SYSTEM_PROPERTY_FOR_IMPL + string4;
                securityException = null;
                try {
                    string2 = System.getProperty(string3);
                    if (string2 != null && string2.length() > 0) {
                        return XMLValidationSchemaFactory.createNewInstance(classLoader, string2);
                    }
                }
                catch (SecurityException securityException2) {
                    securityException = securityException2;
                }
                try {
                    string2 = System.getProperty("java.home");
                    enumeration = new File(string2);
                    enumeration = new File((File)((Object)enumeration), "lib");
                    enumeration = new File((File)((Object)enumeration), JAXP_PROP_FILENAME);
                    if (!((File)((Object)enumeration)).exists()) break block18;
                    try {
                        serializable = new Properties();
                        ((Properties)serializable).load(new FileInputStream((File)((Object)enumeration)));
                        object = ((Properties)serializable).getProperty(string3);
                        if (object != null && ((String)object).length() > 0) {
                            return XMLValidationSchemaFactory.createNewInstance(classLoader, (String)object);
                        }
                    }
                    catch (IOException iOException) {}
                }
                catch (SecurityException securityException3) {
                    securityException = securityException3;
                }
            }
            string2 = SERVICE_DEFINITION_PATH + string4;
            try {
                enumeration = classLoader == null ? ClassLoader.getSystemResources(string2) : classLoader.getResources(string2);
                if (enumeration == null) break block19;
                while (enumeration.hasMoreElements()) {
                    serializable = enumeration.nextElement();
                    object = ((URL)serializable).openStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((InputStream)object, "ISO-8859-1"));
                    String string5 = null;
                    try {
                        String string6;
                        while ((string6 = bufferedReader.readLine()) != null) {
                            if ((string6 = string6.trim()).length() <= 0 || string6.charAt(0) == '#') continue;
                            string5 = string6;
                            break;
                        }
                    }
                    finally {
                        bufferedReader.close();
                    }
                    if (string5 == null || string5.length() <= 0) continue;
                    return XMLValidationSchemaFactory.createNewInstance(classLoader, string5);
                }
            }
            catch (SecurityException securityException4) {
                securityException = securityException4;
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        enumeration = "No XMLValidationSchemaFactory implementation class specified or accessible (via system property '" + string3 + "', or service definition under '" + string2 + "')";
        if (securityException != null) {
            throw new FactoryConfigurationError((String)((Object)enumeration) + " (possibly caused by: " + securityException + ")", securityException);
        }
        throw new FactoryConfigurationError((String)((Object)enumeration));
    }

    public XMLValidationSchema createSchema(InputStream inputStream) throws XMLStreamException {
        return this.createSchema(inputStream, null);
    }

    public XMLValidationSchema createSchema(InputStream inputStream, String string) throws XMLStreamException {
        return this.createSchema(inputStream, string, null, null);
    }

    public abstract XMLValidationSchema createSchema(InputStream var1, String var2, String var3, String var4) throws XMLStreamException;

    public XMLValidationSchema createSchema(Reader reader) throws XMLStreamException {
        return this.createSchema(reader, null, null);
    }

    public abstract XMLValidationSchema createSchema(Reader var1, String var2, String var3) throws XMLStreamException;

    public abstract XMLValidationSchema createSchema(URL var1) throws XMLStreamException;

    public abstract XMLValidationSchema createSchema(File var1) throws XMLStreamException;

    public abstract boolean isPropertySupported(String var1);

    public abstract boolean setProperty(String var1, Object var2);

    public abstract Object getProperty(String var1);

    public final String getSchemaType() {
        return this.mSchemaType;
    }

    private static XMLValidationSchemaFactory createNewInstance(ClassLoader classLoader, String string) throws FactoryConfigurationError {
        try {
            Class<?> clazz = classLoader == null ? Class.forName(string) : classLoader.loadClass(string);
            return (XMLValidationSchemaFactory)clazz.newInstance();
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new FactoryConfigurationError("XMLValidationSchemaFactory implementation '" + string + "' not found (missing jar in classpath?)", classNotFoundException);
        }
        catch (Exception exception) {
            throw new FactoryConfigurationError("XMLValidationSchemaFactory implementation '" + string + "' could not be instantiated: " + exception, exception);
        }
    }

    static {
        sSchemaIds.put("http://www.w3.org/XML/1998/namespace", INTERNAL_ID_SCHEMA_DTD);
        sSchemaIds.put("http://relaxng.org/ns/structure/0.9", INTERNAL_ID_SCHEMA_RELAXNG);
        sSchemaIds.put("http://www.w3.org/2001/XMLSchema", INTERNAL_ID_SCHEMA_W3C);
        sSchemaIds.put("http://www.thaiopensource.com/trex", INTERNAL_ID_SCHEMA_TREX);
    }
}

