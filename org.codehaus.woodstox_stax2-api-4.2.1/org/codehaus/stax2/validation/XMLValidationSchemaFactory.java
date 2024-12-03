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
    static final HashMap<String, String> sSchemaIds = new HashMap();
    static final String JAXP_PROP_FILENAME = "jaxp.properties";
    public static final String SYSTEM_PROPERTY_FOR_IMPL = "org.codehaus.stax2.validation.XMLValidationSchemaFactory.";
    public static final String SERVICE_DEFINITION_PATH = "META-INF/services/org.codehaus.stax2.validation.XMLValidationSchemaFactory.";
    public static final String P_IS_NAMESPACE_AWARE = "org.codehaus2.stax2.validation.isNamespaceAware";
    public static final String P_ENABLE_CACHING = "org.codehaus2.stax2.validation.enableCaching";
    protected final String mSchemaType;

    protected XMLValidationSchemaFactory(String st) {
        this.mSchemaType = st;
    }

    public static XMLValidationSchemaFactory newInstance(String schemaType) throws FactoryConfigurationError {
        return XMLValidationSchemaFactory.newInstance(schemaType, Thread.currentThread().getContextClassLoader());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static XMLValidationSchemaFactory newInstance(String schemaType, ClassLoader classLoader) throws FactoryConfigurationError {
        String path;
        SecurityException secEx;
        String propertyId;
        block19: {
            String internalId;
            block18: {
                internalId = sSchemaIds.get(schemaType);
                if (internalId == null) {
                    throw new FactoryConfigurationError("Unrecognized schema type (id '" + schemaType + "')");
                }
                propertyId = SYSTEM_PROPERTY_FOR_IMPL + internalId;
                secEx = null;
                try {
                    String clsName = System.getProperty(propertyId);
                    if (clsName != null && clsName.length() > 0) {
                        return XMLValidationSchemaFactory.createNewInstance(classLoader, clsName);
                    }
                }
                catch (SecurityException se) {
                    secEx = se;
                }
                try {
                    String home = System.getProperty("java.home");
                    File f = new File(home);
                    f = new File(f, "lib");
                    f = new File(f, JAXP_PROP_FILENAME);
                    if (!f.exists()) break block18;
                    try {
                        Properties props = new Properties();
                        props.load(new FileInputStream(f));
                        String clsName = props.getProperty(propertyId);
                        if (clsName != null && clsName.length() > 0) {
                            return XMLValidationSchemaFactory.createNewInstance(classLoader, clsName);
                        }
                    }
                    catch (IOException props) {}
                }
                catch (SecurityException se) {
                    secEx = se;
                }
            }
            path = SERVICE_DEFINITION_PATH + internalId;
            try {
                Enumeration<URL> en = classLoader == null ? ClassLoader.getSystemResources(path) : classLoader.getResources(path);
                if (en == null) break block19;
                while (en.hasMoreElements()) {
                    URL url = en.nextElement();
                    InputStream is = url.openStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is, "ISO-8859-1"));
                    String clsName = null;
                    try {
                        String line;
                        while ((line = rd.readLine()) != null) {
                            if ((line = line.trim()).length() <= 0 || line.charAt(0) == '#') continue;
                            clsName = line;
                            break;
                        }
                    }
                    finally {
                        rd.close();
                    }
                    if (clsName == null || clsName.length() <= 0) continue;
                    return XMLValidationSchemaFactory.createNewInstance(classLoader, clsName);
                }
            }
            catch (SecurityException se) {
                secEx = se;
            }
            catch (IOException se) {
                // empty catch block
            }
        }
        String msg = "No XMLValidationSchemaFactory implementation class specified or accessible (via system property '" + propertyId + "', or service definition under '" + path + "')";
        if (secEx != null) {
            throw new FactoryConfigurationError(msg + " (possibly caused by: " + secEx + ")", secEx);
        }
        throw new FactoryConfigurationError(msg);
    }

    public XMLValidationSchema createSchema(InputStream in) throws XMLStreamException {
        return this.createSchema(in, null);
    }

    public XMLValidationSchema createSchema(InputStream in, String encoding) throws XMLStreamException {
        return this.createSchema(in, encoding, null, null);
    }

    public abstract XMLValidationSchema createSchema(InputStream var1, String var2, String var3, String var4) throws XMLStreamException;

    public XMLValidationSchema createSchema(Reader r) throws XMLStreamException {
        return this.createSchema(r, null, null);
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

    private static XMLValidationSchemaFactory createNewInstance(ClassLoader cloader, String clsName) throws FactoryConfigurationError {
        try {
            Class<?> factoryClass = cloader == null ? Class.forName(clsName) : cloader.loadClass(clsName);
            return (XMLValidationSchemaFactory)factoryClass.newInstance();
        }
        catch (ClassNotFoundException x) {
            throw new FactoryConfigurationError("XMLValidationSchemaFactory implementation '" + clsName + "' not found (missing jar in classpath?)", x);
        }
        catch (Exception x) {
            throw new FactoryConfigurationError("XMLValidationSchemaFactory implementation '" + clsName + "' could not be instantiated: " + x, x);
        }
    }

    static {
        sSchemaIds.put("http://www.w3.org/XML/1998/namespace", INTERNAL_ID_SCHEMA_DTD);
        sSchemaIds.put("http://relaxng.org/ns/structure/0.9", INTERNAL_ID_SCHEMA_RELAXNG);
        sSchemaIds.put("http://www.w3.org/2001/XMLSchema", INTERNAL_ID_SCHEMA_W3C);
        sSchemaIds.put("http://www.thaiopensource.com/trex", INTERNAL_ID_SCHEMA_TREX);
    }
}

