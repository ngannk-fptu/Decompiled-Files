/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.util;

import com.ctc.wstx.util.SymbolTable;

public final class DefaultXmlSymbolTable {
    static final SymbolTable sInstance = new SymbolTable(true, 128);
    static final String mNsPrefixXml = sInstance.findSymbol("xml");
    static final String mNsPrefixXmlns = sInstance.findSymbol("xmlns");

    public static SymbolTable getInstance() {
        return sInstance.makeChild();
    }

    public static String getXmlSymbol() {
        return mNsPrefixXml;
    }

    public static String getXmlnsSymbol() {
        return mNsPrefixXmlns;
    }

    static {
        sInstance.findSymbol("id");
        sInstance.findSymbol("name");
        sInstance.findSymbol("xsd");
        sInstance.findSymbol("xsi");
        sInstance.findSymbol("type");
        sInstance.findSymbol("soap");
        sInstance.findSymbol("SOAP-ENC");
        sInstance.findSymbol("SOAP-ENV");
        sInstance.findSymbol("Body");
        sInstance.findSymbol("Envelope");
    }
}

