/*
 * Decompiled with CFR 0.152.
 */
package org.jaxen.saxpath.helpers;

import org.jaxen.saxpath.SAXPathException;
import org.jaxen.saxpath.XPathReader;

public class XPathReaderFactory {
    public static final String DRIVER_PROPERTY = "org.saxpath.driver";
    protected static final String DEFAULT_DRIVER = "org.jaxen.saxpath.base.XPathReader";
    static /* synthetic */ Class class$org$jaxen$saxpath$helpers$XPathReaderFactory;
    static /* synthetic */ Class class$org$jaxen$saxpath$XPathReader;

    private XPathReaderFactory() {
    }

    public static XPathReader createReader() throws SAXPathException {
        String className = null;
        try {
            className = System.getProperty(DRIVER_PROPERTY);
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        if (className == null || className.length() == 0) {
            className = DEFAULT_DRIVER;
        }
        return XPathReaderFactory.createReader(className);
    }

    public static XPathReader createReader(String className) throws SAXPathException {
        Class<?> readerClass = null;
        XPathReader reader = null;
        try {
            readerClass = Class.forName(className, true, (class$org$jaxen$saxpath$helpers$XPathReaderFactory == null ? (class$org$jaxen$saxpath$helpers$XPathReaderFactory = XPathReaderFactory.class$("org.jaxen.saxpath.helpers.XPathReaderFactory")) : class$org$jaxen$saxpath$helpers$XPathReaderFactory).getClassLoader());
            if (!(class$org$jaxen$saxpath$XPathReader == null ? (class$org$jaxen$saxpath$XPathReader = XPathReaderFactory.class$("org.jaxen.saxpath.XPathReader")) : class$org$jaxen$saxpath$XPathReader).isAssignableFrom(readerClass)) {
                throw new SAXPathException("Class [" + className + "] does not implement the org.jaxen.saxpath.XPathReader interface.");
            }
        }
        catch (ClassNotFoundException e) {
            throw new SAXPathException(e);
        }
        try {
            reader = (XPathReader)readerClass.newInstance();
        }
        catch (IllegalAccessException e) {
            throw new SAXPathException(e);
        }
        catch (InstantiationException e) {
            throw new SAXPathException(e);
        }
        return reader;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

