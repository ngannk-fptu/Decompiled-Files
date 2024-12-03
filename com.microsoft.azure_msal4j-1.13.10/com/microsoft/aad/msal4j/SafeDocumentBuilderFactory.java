/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.aad.msal4j;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

final class SafeDocumentBuilderFactory {
    SafeDocumentBuilderFactory() {
    }

    public static DocumentBuilderFactory createInstance() throws ParserConfigurationException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        String feature = "http://apache.org/xml/features/disallow-doctype-decl";
        builderFactory.setFeature(feature, true);
        feature = "http://xml.org/sax/features/external-general-entities";
        builderFactory.setFeature(feature, false);
        feature = "http://xml.org/sax/features/external-parameter-entities";
        builderFactory.setFeature(feature, false);
        feature = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
        builderFactory.setFeature(feature, false);
        builderFactory.setXIncludeAware(false);
        builderFactory.setExpandEntityReferences(false);
        builderFactory.setNamespaceAware(true);
        return builderFactory;
    }
}

