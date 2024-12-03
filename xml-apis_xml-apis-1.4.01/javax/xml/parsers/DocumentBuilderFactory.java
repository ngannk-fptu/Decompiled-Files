/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.parsers;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.FactoryFinder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SecuritySupport;
import javax.xml.validation.Schema;

public abstract class DocumentBuilderFactory {
    private boolean validating = false;
    private boolean namespaceAware = false;
    private boolean whitespace = false;
    private boolean expandEntityRef = true;
    private boolean ignoreComments = false;
    private boolean coalescing = false;

    protected DocumentBuilderFactory() {
    }

    public static DocumentBuilderFactory newInstance() {
        try {
            return (DocumentBuilderFactory)FactoryFinder.find("javax.xml.parsers.DocumentBuilderFactory", "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
        }
        catch (FactoryFinder.ConfigurationError configurationError) {
            throw new FactoryConfigurationError(configurationError.getException(), configurationError.getMessage());
        }
    }

    public static DocumentBuilderFactory newInstance(String string, ClassLoader classLoader) {
        if (string == null) {
            throw new FactoryConfigurationError("factoryClassName cannot be null.");
        }
        if (classLoader == null) {
            classLoader = SecuritySupport.getContextClassLoader();
        }
        try {
            return (DocumentBuilderFactory)FactoryFinder.newInstance(string, classLoader, false);
        }
        catch (FactoryFinder.ConfigurationError configurationError) {
            throw new FactoryConfigurationError(configurationError.getException(), configurationError.getMessage());
        }
    }

    public abstract DocumentBuilder newDocumentBuilder() throws ParserConfigurationException;

    public void setNamespaceAware(boolean bl) {
        this.namespaceAware = bl;
    }

    public void setValidating(boolean bl) {
        this.validating = bl;
    }

    public void setIgnoringElementContentWhitespace(boolean bl) {
        this.whitespace = bl;
    }

    public void setExpandEntityReferences(boolean bl) {
        this.expandEntityRef = bl;
    }

    public void setIgnoringComments(boolean bl) {
        this.ignoreComments = bl;
    }

    public void setCoalescing(boolean bl) {
        this.coalescing = bl;
    }

    public boolean isNamespaceAware() {
        return this.namespaceAware;
    }

    public boolean isValidating() {
        return this.validating;
    }

    public boolean isIgnoringElementContentWhitespace() {
        return this.whitespace;
    }

    public boolean isExpandEntityReferences() {
        return this.expandEntityRef;
    }

    public boolean isIgnoringComments() {
        return this.ignoreComments;
    }

    public boolean isCoalescing() {
        return this.coalescing;
    }

    public abstract void setAttribute(String var1, Object var2) throws IllegalArgumentException;

    public abstract Object getAttribute(String var1) throws IllegalArgumentException;

    public abstract void setFeature(String var1, boolean var2) throws ParserConfigurationException;

    public abstract boolean getFeature(String var1) throws ParserConfigurationException;

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

