/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xerces.parsers.DOMParser
 *  org.apache.xerces.xni.Augmentations
 *  org.apache.xerces.xni.XNIException
 *  org.apache.xerces.xni.parser.XMLParserConfiguration
 */
package org.cyberneko.html.parsers;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.cyberneko.html.HTMLConfiguration;
import org.cyberneko.html.xercesbridge.XercesBridge;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class DOMParser
extends org.apache.xerces.parsers.DOMParser {
    public DOMParser() {
        super((XMLParserConfiguration)new HTMLConfiguration());
        try {
            this.setProperty("http://apache.org/xml/properties/dom/document-class-name", "org.apache.html.dom.HTMLDocumentImpl");
        }
        catch (SAXNotRecognizedException e) {
            throw new RuntimeException("http://apache.org/xml/properties/dom/document-class-name property not recognized");
        }
        catch (SAXNotSupportedException e) {
            throw new RuntimeException("http://apache.org/xml/properties/dom/document-class-name property not supported");
        }
    }

    public void doctypeDecl(String root, String pubid, String sysid, Augmentations augs) throws XNIException {
        String VERSION = XercesBridge.getInstance().getVersion();
        boolean okay = true;
        if (VERSION.startsWith("Xerces-J 2.")) {
            okay = DOMParser.getParserSubVersion() > 5;
        } else if (VERSION.startsWith("XML4J")) {
            okay = false;
        }
        if (okay) {
            super.doctypeDecl(root, pubid, sysid, augs);
        }
    }

    private static int getParserSubVersion() {
        try {
            String VERSION = XercesBridge.getInstance().getVersion();
            int index1 = VERSION.indexOf(46) + 1;
            int index2 = VERSION.indexOf(46, index1);
            if (index2 == -1) {
                index2 = VERSION.length();
            }
            return Integer.parseInt(VERSION.substring(index1, index2));
        }
        catch (Exception e) {
            return -1;
        }
    }
}

