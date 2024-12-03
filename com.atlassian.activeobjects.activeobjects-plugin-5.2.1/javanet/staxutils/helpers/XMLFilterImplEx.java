/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils.helpers;

import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLFilterImpl;

public class XMLFilterImplEx
extends XMLFilterImpl
implements LexicalHandler {
    protected LexicalHandler lexicalHandler;
    protected boolean namespacePrefixes;

    public void setNamespacePrefixes(boolean v) {
        this.namespacePrefixes = v;
    }

    public boolean getNamespacePrefixes() {
        return this.namespacePrefixes;
    }

    public void setLexicalHandler(LexicalHandler handler) {
        this.lexicalHandler = handler;
    }

    public LexicalHandler getLexicalHandler() {
        return this.lexicalHandler;
    }

    public void startDTD(String name, String publicId, String systemId) throws SAXException {
        if (this.lexicalHandler != null) {
            this.lexicalHandler.startDTD(name, publicId, systemId);
        }
    }

    public void endDTD() throws SAXException {
        if (this.lexicalHandler != null) {
            this.lexicalHandler.endDTD();
        }
    }

    public void startEntity(String name) throws SAXException {
        if (this.lexicalHandler != null) {
            this.lexicalHandler.startEntity(name);
        }
    }

    public void endEntity(String name) throws SAXException {
        if (this.lexicalHandler != null) {
            this.lexicalHandler.endEntity(name);
        }
    }

    public void startCDATA() throws SAXException {
        if (this.lexicalHandler != null) {
            this.lexicalHandler.startCDATA();
        }
    }

    public void endCDATA() throws SAXException {
        if (this.lexicalHandler != null) {
            this.lexicalHandler.endCDATA();
        }
    }

    public void comment(char[] ch, int start, int length) throws SAXException {
        if (this.lexicalHandler != null) {
            this.lexicalHandler.comment(ch, start, length);
        }
    }
}

