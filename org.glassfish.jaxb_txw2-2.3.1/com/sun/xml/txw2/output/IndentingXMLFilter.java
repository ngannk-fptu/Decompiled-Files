/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.txw2.output;

import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLFilterImpl;

public class IndentingXMLFilter
extends XMLFilterImpl
implements LexicalHandler {
    private LexicalHandler lexical;
    private static final char[] NEWLINE = new char[]{'\n'};
    private static final Object SEEN_NOTHING = new Object();
    private static final Object SEEN_ELEMENT = new Object();
    private static final Object SEEN_DATA = new Object();
    private Object state = SEEN_NOTHING;
    private Stack<Object> stateStack = new Stack();
    private String indentStep = "";
    private int depth = 0;

    public IndentingXMLFilter() {
    }

    public IndentingXMLFilter(ContentHandler handler) {
        this.setContentHandler(handler);
    }

    public IndentingXMLFilter(ContentHandler handler, LexicalHandler lexical) {
        this.setContentHandler(handler);
        this.setLexicalHandler(lexical);
    }

    public LexicalHandler getLexicalHandler() {
        return this.lexical;
    }

    public void setLexicalHandler(LexicalHandler lexical) {
        this.lexical = lexical;
    }

    public int getIndentStep() {
        return this.indentStep.length();
    }

    public void setIndentStep(int indentStep) {
        StringBuilder s = new StringBuilder();
        while (indentStep > 0) {
            s.append(' ');
            --indentStep;
        }
        this.setIndentStep(s.toString());
    }

    public void setIndentStep(String s) {
        this.indentStep = s;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        this.stateStack.push(SEEN_ELEMENT);
        this.state = SEEN_NOTHING;
        if (this.depth > 0) {
            this.writeNewLine();
        }
        this.doIndent();
        super.startElement(uri, localName, qName, atts);
        ++this.depth;
    }

    private void writeNewLine() throws SAXException {
        super.characters(NEWLINE, 0, NEWLINE.length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        --this.depth;
        if (this.state == SEEN_ELEMENT) {
            this.writeNewLine();
            this.doIndent();
        }
        super.endElement(uri, localName, qName);
        this.state = this.stateStack.pop();
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        this.state = SEEN_DATA;
        super.characters(ch, start, length);
    }

    @Override
    public void comment(char[] ch, int start, int length) throws SAXException {
        if (this.depth > 0) {
            this.writeNewLine();
        }
        this.doIndent();
        if (this.lexical != null) {
            this.lexical.comment(ch, start, length);
        }
    }

    @Override
    public void startDTD(String name, String publicId, String systemId) throws SAXException {
        if (this.lexical != null) {
            this.lexical.startDTD(name, publicId, systemId);
        }
    }

    @Override
    public void endDTD() throws SAXException {
        if (this.lexical != null) {
            this.lexical.endDTD();
        }
    }

    @Override
    public void startEntity(String name) throws SAXException {
        if (this.lexical != null) {
            this.lexical.startEntity(name);
        }
    }

    @Override
    public void endEntity(String name) throws SAXException {
        if (this.lexical != null) {
            this.lexical.endEntity(name);
        }
    }

    @Override
    public void startCDATA() throws SAXException {
        if (this.lexical != null) {
            this.lexical.startCDATA();
        }
    }

    @Override
    public void endCDATA() throws SAXException {
        if (this.lexical != null) {
            this.lexical.endCDATA();
        }
    }

    private void doIndent() throws SAXException {
        if (this.depth > 0) {
            char[] ch = this.indentStep.toCharArray();
            for (int i = 0; i < this.depth; ++i) {
                this.characters(ch, 0, ch.length);
            }
        }
    }
}

