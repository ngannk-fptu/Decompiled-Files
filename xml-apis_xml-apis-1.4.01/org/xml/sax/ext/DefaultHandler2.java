/*
 * Decompiled with CFR 0.152.
 */
package org.xml.sax.ext;

import java.io.IOException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.EntityResolver2;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class DefaultHandler2
extends DefaultHandler
implements LexicalHandler,
DeclHandler,
EntityResolver2 {
    public void startCDATA() throws SAXException {
    }

    public void endCDATA() throws SAXException {
    }

    public void startDTD(String string, String string2, String string3) throws SAXException {
    }

    public void endDTD() throws SAXException {
    }

    public void startEntity(String string) throws SAXException {
    }

    public void endEntity(String string) throws SAXException {
    }

    public void comment(char[] cArray, int n, int n2) throws SAXException {
    }

    public void attributeDecl(String string, String string2, String string3, String string4, String string5) throws SAXException {
    }

    public void elementDecl(String string, String string2) throws SAXException {
    }

    public void externalEntityDecl(String string, String string2, String string3) throws SAXException {
    }

    public void internalEntityDecl(String string, String string2) throws SAXException {
    }

    public InputSource getExternalSubset(String string, String string2) throws SAXException, IOException {
        return null;
    }

    public InputSource resolveEntity(String string, String string2, String string3, String string4) throws SAXException, IOException {
        return null;
    }

    public InputSource resolveEntity(String string, String string2) throws SAXException, IOException {
        return this.resolveEntity(null, string, null, string2);
    }
}

