/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.util;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;

public final class SaxHelper {
    private SaxHelper() {
    }

    public static boolean isXdkParserFactory(SAXParserFactory parserFactory) {
        return SaxHelper.isXdkFactory(parserFactory, "oracle.xml.jaxp.JXSAXParserFactory");
    }

    public static boolean isXdkDocumentBuilderFactory(DocumentBuilderFactory builderFactory) {
        return SaxHelper.isXdkFactory(builderFactory, "oracle.xml.jaxp.JXDocumentBuilderFactory");
    }

    private static boolean isXdkFactory(Object factory, String className) {
        try {
            Class<?> xdkFactoryClass = Class.forName(className);
            return xdkFactoryClass.isAssignableFrom(factory.getClass());
        }
        catch (ClassNotFoundException classNotFoundException) {
            return false;
        }
    }
}

