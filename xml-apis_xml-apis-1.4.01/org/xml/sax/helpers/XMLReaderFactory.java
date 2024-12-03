/*
 * Decompiled with CFR 0.152.
 */
package org.xml.sax.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.NewInstance;
import org.xml.sax.helpers.ParserAdapter;
import org.xml.sax.helpers.ParserFactory;
import org.xml.sax.helpers.SecuritySupport;

public final class XMLReaderFactory {
    private static final String property = "org.xml.sax.driver";
    private static final int DEFAULT_LINE_LENGTH = 80;
    static /* synthetic */ Class class$org$xml$sax$helpers$XMLReaderFactory;

    private XMLReaderFactory() {
    }

    /*
     * Loose catch block
     */
    public static XMLReader createXMLReader() throws SAXException {
        ClassLoader classLoader;
        String string;
        block21: {
            string = null;
            classLoader = NewInstance.getClassLoader();
            try {
                string = SecuritySupport.getSystemProperty(property);
            }
            catch (Exception exception) {
                // empty catch block
            }
            if (string == null || string.length() == 0) {
                String string2 = "META-INF/services/org.xml.sax.driver";
                InputStream inputStream = null;
                string = null;
                ClassLoader classLoader2 = SecuritySupport.getContextClassLoader();
                if (classLoader2 != null) {
                    inputStream = SecuritySupport.getResourceAsStream(classLoader2, string2);
                    if (inputStream == null) {
                        classLoader2 = (class$org$xml$sax$helpers$XMLReaderFactory == null ? (class$org$xml$sax$helpers$XMLReaderFactory = XMLReaderFactory.class$("org.xml.sax.helpers.XMLReaderFactory")) : class$org$xml$sax$helpers$XMLReaderFactory).getClassLoader();
                        inputStream = SecuritySupport.getResourceAsStream(classLoader2, string2);
                    }
                } else {
                    classLoader2 = (class$org$xml$sax$helpers$XMLReaderFactory == null ? (class$org$xml$sax$helpers$XMLReaderFactory = XMLReaderFactory.class$("org.xml.sax.helpers.XMLReaderFactory")) : class$org$xml$sax$helpers$XMLReaderFactory).getClassLoader();
                    inputStream = SecuritySupport.getResourceAsStream(classLoader2, string2);
                }
                if (inputStream != null) {
                    BufferedReader bufferedReader;
                    try {
                        bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 80);
                    }
                    catch (UnsupportedEncodingException unsupportedEncodingException) {
                        bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 80);
                    }
                    string = bufferedReader.readLine();
                    Object var8_10 = null;
                    try {
                        bufferedReader.close();
                    }
                    catch (IOException iOException) {}
                    break block21;
                    {
                        catch (Exception exception) {
                            Object var8_11 = null;
                            try {
                                bufferedReader.close();
                            }
                            catch (IOException iOException) {}
                        }
                    }
                    catch (Throwable throwable) {
                        Object var8_12 = null;
                        try {
                            bufferedReader.close();
                        }
                        catch (IOException iOException) {
                            // empty catch block
                        }
                        throw throwable;
                    }
                }
            }
        }
        if (string == null) {
            string = "org.apache.xerces.parsers.SAXParser";
        }
        if (string != null) {
            return XMLReaderFactory.loadClass(classLoader, string);
        }
        try {
            return new ParserAdapter(ParserFactory.makeParser());
        }
        catch (Exception exception) {
            throw new SAXException("Can't create default XMLReader; is system property org.xml.sax.driver set?");
        }
    }

    public static XMLReader createXMLReader(String string) throws SAXException {
        return XMLReaderFactory.loadClass(NewInstance.getClassLoader(), string);
    }

    private static XMLReader loadClass(ClassLoader classLoader, String string) throws SAXException {
        try {
            return (XMLReader)NewInstance.newInstance(classLoader, string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new SAXException("SAX2 driver class " + string + " not found", classNotFoundException);
        }
        catch (IllegalAccessException illegalAccessException) {
            throw new SAXException("SAX2 driver class " + string + " found but cannot be loaded", illegalAccessException);
        }
        catch (InstantiationException instantiationException) {
            throw new SAXException("SAX2 driver class " + string + " loaded but cannot be instantiated (no empty public constructor?)", instantiationException);
        }
        catch (ClassCastException classCastException) {
            throw new SAXException("SAX2 driver class " + string + " does not implement XMLReader", classCastException);
        }
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }
}

