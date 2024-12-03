/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.stream;

import java.io.OutputStream;
import java.io.Writer;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.FactoryFinder;
import javax.xml.stream.SecuritySupport;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;

public abstract class XMLOutputFactory {
    public static final String IS_REPAIRING_NAMESPACES = "javax.xml.stream.isRepairingNamespaces";
    private static final String PROPERTY_NAME = "javax.xml.stream.XMLOutputFactory";
    private static final String DEFAULT_FACTORY = "com.ctc.wstx.stax.WstxOutputFactory";

    protected XMLOutputFactory() {
    }

    public static XMLOutputFactory newInstance() throws FactoryConfigurationError {
        try {
            return (XMLOutputFactory)FactoryFinder.find(PROPERTY_NAME, DEFAULT_FACTORY);
        }
        catch (FactoryFinder.ConfigurationError configurationError) {
            throw new FactoryConfigurationError(configurationError.getException(), configurationError.getMessage());
        }
    }

    public static XMLInputFactory newInstance(String string, ClassLoader classLoader) throws FactoryConfigurationError {
        if (classLoader == null) {
            classLoader = SecuritySupport.getContextClassLoader();
        }
        try {
            return (XMLInputFactory)FactoryFinder.find(string, classLoader, "com.ctc.wstx.stax.WstxInputFactory");
        }
        catch (FactoryFinder.ConfigurationError configurationError) {
            throw new FactoryConfigurationError(configurationError.getException(), configurationError.getMessage());
        }
    }

    public abstract XMLStreamWriter createXMLStreamWriter(Writer var1) throws XMLStreamException;

    public abstract XMLStreamWriter createXMLStreamWriter(OutputStream var1) throws XMLStreamException;

    public abstract XMLStreamWriter createXMLStreamWriter(OutputStream var1, String var2) throws XMLStreamException;

    public abstract XMLStreamWriter createXMLStreamWriter(Result var1) throws XMLStreamException;

    public abstract XMLEventWriter createXMLEventWriter(Result var1) throws XMLStreamException;

    public abstract XMLEventWriter createXMLEventWriter(OutputStream var1) throws XMLStreamException;

    public abstract XMLEventWriter createXMLEventWriter(OutputStream var1, String var2) throws XMLStreamException;

    public abstract XMLEventWriter createXMLEventWriter(Writer var1) throws XMLStreamException;

    public abstract void setProperty(String var1, Object var2) throws IllegalArgumentException;

    public abstract Object getProperty(String var1) throws IllegalArgumentException;

    public abstract boolean isPropertySupported(String var1);
}

