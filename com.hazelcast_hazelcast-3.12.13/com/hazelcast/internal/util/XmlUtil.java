/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.hazelcast.internal.util;

import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.nio.IOUtil;
import java.io.StringReader;
import java.io.StringWriter;
import javax.annotation.Nullable;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.SAXException;

public final class XmlUtil {
    public static final String SYSTEM_PROPERTY_IGNORE_XXE_PROTECTION_FAILURES = "hazelcast.ignoreXxeProtectionFailures";
    private static final ILogger LOGGER = Logger.getLogger(XmlUtil.class);

    private XmlUtil() {
    }

    public static TransformerFactory getTransformerFactory() {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        XmlUtil.setAttribute(transformerFactory, "http://javax.xml.XMLConstants/property/accessExternalDTD");
        XmlUtil.setAttribute(transformerFactory, "http://javax.xml.XMLConstants/property/accessExternalStylesheet");
        return transformerFactory;
    }

    public static SchemaFactory getSchemaFactory() throws SAXException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        XmlUtil.setProperty(schemaFactory, "http://javax.xml.XMLConstants/property/accessExternalSchema");
        XmlUtil.setProperty(schemaFactory, "http://javax.xml.XMLConstants/property/accessExternalDTD");
        return schemaFactory;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String format(@Nullable String input, int indent) throws IllegalArgumentException {
        if (input == null || indent < 0) {
            return input;
        }
        if (indent == 0) {
            throw new IllegalArgumentException("Indentation must not be 0.");
        }
        StreamResult xmlOutput = null;
        try {
            Transformer transformer;
            StreamSource xmlInput;
            block13: {
                TransformerFactory transformerFactory;
                block12: {
                    xmlInput = new StreamSource(new StringReader(input));
                    xmlOutput = new StreamResult(new StringWriter());
                    transformerFactory = XmlUtil.getTransformerFactory();
                    try {
                        transformerFactory.setAttribute("indent-number", indent);
                    }
                    catch (IllegalArgumentException e) {
                        if (!LOGGER.isFinestEnabled()) break block12;
                        LOGGER.finest("Failed to set indent-number attribute; cause: " + e.getMessage());
                    }
                }
                transformer = transformerFactory.newTransformer();
                transformer.setErrorListener(ThrowingErrorListener.INSTANCE);
                transformer.setOutputProperty("omit-xml-declaration", "yes");
                transformer.setOutputProperty("encoding", "UTF-8");
                transformer.setOutputProperty("indent", "yes");
                try {
                    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", Integer.toString(indent));
                }
                catch (IllegalArgumentException e) {
                    if (!LOGGER.isFinestEnabled()) break block13;
                    LOGGER.finest("Failed to set indent-amount property; cause: " + e.getMessage());
                }
            }
            transformer.transform(xmlInput, xmlOutput);
            String string = xmlOutput.getWriter().toString();
            return string;
        }
        catch (Exception e) {
            LOGGER.warning(e);
            String string = input;
            return string;
        }
        finally {
            if (xmlOutput != null) {
                IOUtil.closeResource(xmlOutput.getWriter());
            }
        }
    }

    public ErrorListener getErrorListener() {
        return ThrowingErrorListener.INSTANCE;
    }

    static void setAttribute(TransformerFactory transformerFactory, String attributeName) {
        try {
            transformerFactory.setAttribute(attributeName, "");
        }
        catch (IllegalArgumentException iae) {
            if (Boolean.getBoolean(SYSTEM_PROPERTY_IGNORE_XXE_PROTECTION_FAILURES)) {
                LOGGER.warning("Enabling XXE protection failed. The attribute " + attributeName + " is not supported by the TransformerFactory. The " + SYSTEM_PROPERTY_IGNORE_XXE_PROTECTION_FAILURES + " system property is used so the XML processing continues in the UNSECURE mode with XXE protection disabled!!!");
            }
            LOGGER.severe("Enabling XXE protection failed. The attribute " + attributeName + " is not supported by the TransformerFactory. This usually mean an outdated XML processor is present on the classpath (e.g. Xerces, Xalan). If you are not able to resolve the issue by fixing the classpath, the " + SYSTEM_PROPERTY_IGNORE_XXE_PROTECTION_FAILURES + " system property can be used to disable XML External Entity protections. We don't recommend disabling the XXE as such the XML processor configuration is unsecure!!!", iae);
            throw iae;
        }
    }

    static void setProperty(SchemaFactory schemaFactory, String propertyName) throws SAXException {
        try {
            schemaFactory.setProperty(propertyName, "");
        }
        catch (SAXException e) {
            if (Boolean.getBoolean(SYSTEM_PROPERTY_IGNORE_XXE_PROTECTION_FAILURES)) {
                LOGGER.warning("Enabling XXE protection failed. The property " + propertyName + " is not supported by the SchemaFactory. The " + SYSTEM_PROPERTY_IGNORE_XXE_PROTECTION_FAILURES + " system property is used so the XML processing continues in the UNSECURE mode with XXE protection disabled!!!");
            }
            LOGGER.severe("Enabling XXE protection failed. The property " + propertyName + " is not supported by the SchemaFactory. This usually mean an outdated XML processor is present on the classpath (e.g. Xerces, Xalan). If you are not able to resolve the issue by fixing the classpath, the " + SYSTEM_PROPERTY_IGNORE_XXE_PROTECTION_FAILURES + " system property can be used to disable XML External Entity protections. We don't recommend disabling the XXE as such the XML processor configuration is unsecure!!!", e);
            throw e;
        }
    }

    static final class ThrowingErrorListener
    implements ErrorListener {
        public static final ThrowingErrorListener INSTANCE = new ThrowingErrorListener();

        private ThrowingErrorListener() {
        }

        @Override
        public void warning(TransformerException exception) throws TransformerException {
            throw exception;
        }

        @Override
        public void fatalError(TransformerException exception) throws TransformerException {
            throw exception;
        }

        @Override
        public void error(TransformerException exception) throws TransformerException {
            throw exception;
        }
    }
}

