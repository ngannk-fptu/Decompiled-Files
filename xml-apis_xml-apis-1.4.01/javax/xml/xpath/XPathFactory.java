/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.xpath;

import javax.xml.xpath.SecuritySupport;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactoryConfigurationException;
import javax.xml.xpath.XPathFactoryFinder;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathVariableResolver;

public abstract class XPathFactory {
    public static final String DEFAULT_PROPERTY_NAME = "javax.xml.xpath.XPathFactory";
    public static final String DEFAULT_OBJECT_MODEL_URI = "http://java.sun.com/jaxp/xpath/dom";
    static /* synthetic */ Class class$javax$xml$xpath$XPathFactory;

    protected XPathFactory() {
    }

    public static final XPathFactory newInstance() {
        try {
            return XPathFactory.newInstance(DEFAULT_OBJECT_MODEL_URI);
        }
        catch (XPathFactoryConfigurationException xPathFactoryConfigurationException) {
            throw new RuntimeException("XPathFactory#newInstance() failed to create an XPathFactory for the default object model: http://java.sun.com/jaxp/xpath/dom with the XPathFactoryConfigurationException: " + xPathFactoryConfigurationException.toString());
        }
    }

    public static final XPathFactory newInstance(String string) throws XPathFactoryConfigurationException {
        XPathFactory xPathFactory;
        if (string == null) {
            throw new NullPointerException("XPathFactory#newInstance(String uri) cannot be called with uri == null");
        }
        if (string.length() == 0) {
            throw new IllegalArgumentException("XPathFactory#newInstance(String uri) cannot be called with uri == \"\"");
        }
        ClassLoader classLoader = SecuritySupport.getContextClassLoader();
        if (classLoader == null) {
            classLoader = (class$javax$xml$xpath$XPathFactory == null ? (class$javax$xml$xpath$XPathFactory = XPathFactory.class$(DEFAULT_PROPERTY_NAME)) : class$javax$xml$xpath$XPathFactory).getClassLoader();
        }
        if ((xPathFactory = new XPathFactoryFinder(classLoader).newFactory(string)) == null) {
            throw new XPathFactoryConfigurationException("No XPathFctory implementation found for the object model: " + string);
        }
        return xPathFactory;
    }

    public static XPathFactory newInstance(String string, String string2, ClassLoader classLoader) throws XPathFactoryConfigurationException {
        XPathFactory xPathFactory;
        if (string == null) {
            throw new NullPointerException("XPathFactory#newInstance(String uri) cannot be called with uri == null");
        }
        if (string.length() == 0) {
            throw new IllegalArgumentException("XPathFactory#newInstance(String uri) cannot be called with uri == \"\"");
        }
        if (string2 == null) {
            throw new XPathFactoryConfigurationException("factoryClassName cannot be null.");
        }
        if (classLoader == null) {
            classLoader = SecuritySupport.getContextClassLoader();
        }
        if ((xPathFactory = new XPathFactoryFinder(classLoader).createInstance(string2)) == null || !xPathFactory.isObjectModelSupported(string)) {
            throw new XPathFactoryConfigurationException("No XPathFctory implementation found for the object model: " + string);
        }
        return xPathFactory;
    }

    public abstract boolean isObjectModelSupported(String var1);

    public abstract void setFeature(String var1, boolean var2) throws XPathFactoryConfigurationException;

    public abstract boolean getFeature(String var1) throws XPathFactoryConfigurationException;

    public abstract void setXPathVariableResolver(XPathVariableResolver var1);

    public abstract void setXPathFunctionResolver(XPathFunctionResolver var1);

    public abstract XPath newXPath();

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }
}

