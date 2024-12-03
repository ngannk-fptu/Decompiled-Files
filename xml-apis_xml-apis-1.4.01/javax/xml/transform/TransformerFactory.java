/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.transform;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.FactoryFinder;
import javax.xml.transform.SecuritySupport;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.URIResolver;

public abstract class TransformerFactory {
    protected TransformerFactory() {
    }

    public static TransformerFactory newInstance() throws TransformerFactoryConfigurationError {
        try {
            return (TransformerFactory)FactoryFinder.find("javax.xml.transform.TransformerFactory", "org.apache.xalan.processor.TransformerFactoryImpl");
        }
        catch (FactoryFinder.ConfigurationError configurationError) {
            throw new TransformerFactoryConfigurationError(configurationError.getException(), configurationError.getMessage());
        }
    }

    public static TransformerFactory newInstance(String string, ClassLoader classLoader) throws TransformerFactoryConfigurationError {
        if (string == null) {
            throw new TransformerFactoryConfigurationError("factoryClassName cannot be null.");
        }
        if (classLoader == null) {
            classLoader = SecuritySupport.getContextClassLoader();
        }
        try {
            return (TransformerFactory)FactoryFinder.newInstance(string, classLoader, false);
        }
        catch (FactoryFinder.ConfigurationError configurationError) {
            throw new TransformerFactoryConfigurationError(configurationError.getException(), configurationError.getMessage());
        }
    }

    public abstract Transformer newTransformer(Source var1) throws TransformerConfigurationException;

    public abstract Transformer newTransformer() throws TransformerConfigurationException;

    public abstract Templates newTemplates(Source var1) throws TransformerConfigurationException;

    public abstract Source getAssociatedStylesheet(Source var1, String var2, String var3, String var4) throws TransformerConfigurationException;

    public abstract void setURIResolver(URIResolver var1);

    public abstract URIResolver getURIResolver();

    public abstract void setFeature(String var1, boolean var2) throws TransformerConfigurationException;

    public abstract boolean getFeature(String var1);

    public abstract void setAttribute(String var1, Object var2);

    public abstract Object getAttribute(String var1);

    public abstract void setErrorListener(ErrorListener var1);

    public abstract ErrorListener getErrorListener();
}

