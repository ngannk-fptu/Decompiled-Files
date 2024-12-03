/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.sax;

import java.util.HashMap;

public final class SAXProperty {
    public static final String STD_PROPERTY_PREFIX = "http://xml.org/sax/properties/";
    static final HashMap sInstances = new HashMap();
    public static final SAXProperty DECLARATION_HANDLER = new SAXProperty("declaration-handler");
    public static final SAXProperty DOCUMENT_XML_VERSION = new SAXProperty("document-xml-version");
    public static final SAXProperty DOM_NODE = new SAXProperty("dom-node");
    public static final SAXProperty LEXICAL_HANDLER = new SAXProperty("lexical-handler");
    static final SAXProperty XML_STRING = new SAXProperty("xml-string");
    private final String mSuffix;

    private SAXProperty(String suffix) {
        this.mSuffix = suffix;
        sInstances.put(suffix, this);
    }

    public static SAXProperty findByUri(String uri) {
        if (uri.startsWith(STD_PROPERTY_PREFIX)) {
            return SAXProperty.findBySuffix(uri.substring(STD_PROPERTY_PREFIX.length()));
        }
        return null;
    }

    public static SAXProperty findBySuffix(String suffix) {
        return (SAXProperty)sInstances.get(suffix);
    }

    public String getSuffix() {
        return this.mSuffix;
    }

    public String toString() {
        return STD_PROPERTY_PREFIX + this.mSuffix;
    }
}

