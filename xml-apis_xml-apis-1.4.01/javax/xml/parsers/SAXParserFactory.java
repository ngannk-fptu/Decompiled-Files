/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.parsers;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.FactoryFinder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SecuritySupport;
import javax.xml.validation.Schema;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public abstract class SAXParserFactory {
    private boolean validating = false;
    private boolean namespaceAware = false;

    protected SAXParserFactory() {
    }

    public static SAXParserFactory newInstance() {
        try {
            return (SAXParserFactory)FactoryFinder.find("javax.xml.parsers.SAXParserFactory", "org.apache.xerces.jaxp.SAXParserFactoryImpl");
        }
        catch (FactoryFinder.ConfigurationError configurationError) {
            throw new FactoryConfigurationError(configurationError.getException(), configurationError.getMessage());
        }
    }

    public static SAXParserFactory newInstance(String string, ClassLoader classLoader) {
        if (string == null) {
            throw new FactoryConfigurationError("factoryClassName cannot be null.");
        }
        if (classLoader == null) {
            classLoader = SecuritySupport.getContextClassLoader();
        }
        try {
            return (SAXParserFactory)FactoryFinder.newInstance(string, classLoader, false);
        }
        catch (FactoryFinder.ConfigurationError configurationError) {
            throw new FactoryConfigurationError(configurationError.getException(), configurationError.getMessage());
        }
    }

    public abstract SAXParser newSAXParser() throws ParserConfigurationException, SAXException;

    public void setNamespaceAware(boolean bl) {
        this.namespaceAware = bl;
    }

    public void setValidating(boolean bl) {
        this.validating = bl;
    }

    public boolean isNamespaceAware() {
        return this.namespaceAware;
    }

    public boolean isValidating() {
        return this.validating;
    }

    public abstract void setFeature(String var1, boolean var2) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException;

    public abstract boolean getFeature(String var1) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException;

    public Schema getSchema() {
        throw new UnsupportedOperationException("This parser does not support specification \"" + this.getClass().getPackage().getSpecificationTitle() + "\" version \"" + this.getClass().getPackage().getSpecificationVersion() + "\"");
    }

    public void setSchema(Schema schema) {
        throw new UnsupportedOperationException("This parser does not support specification \"" + this.getClass().getPackage().getSpecificationTitle() + "\" version \"" + this.getClass().getPackage().getSpecificationVersion() + "\"");
    }

    public void setXIncludeAware(boolean bl) {
        throw new UnsupportedOperationException("This parser does not support specification \"" + this.getClass().getPackage().getSpecificationTitle() + "\" version \"" + this.getClass().getPackage().getSpecificationVersion() + "\"");
    }

    public boolean isXIncludeAware() {
        throw new UnsupportedOperationException("This parser does not support specification \"" + this.getClass().getPackage().getSpecificationTitle() + "\" version \"" + this.getClass().getPackage().getSpecificationVersion() + "\"");
    }
}

