/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.javanet.staxutils;

import com.atlassian.javanet.staxutils.Indentation;
import com.atlassian.javanet.staxutils.helpers.StreamWriterDelegate;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class IndentingXMLStreamWriter
extends StreamWriterDelegate
implements Indentation {
    private int depth = 0;
    private int[] stack = new int[]{0, 0, 0, 0};
    private static final int WROTE_MARKUP = 1;
    private static final int WROTE_DATA = 2;
    private String indent = "  ";
    private String newLine = "\n";
    private char[] linePrefix = null;

    public IndentingXMLStreamWriter(XMLStreamWriter out) {
        super(out);
    }

    public void setIndent(String indent) {
        if (!indent.equals(this.indent)) {
            this.indent = indent;
            this.linePrefix = null;
        }
    }

    public String getIndent() {
        return this.indent;
    }

    public void setNewLine(String newLine) {
        if (!newLine.equals(this.newLine)) {
            this.newLine = newLine;
            this.linePrefix = null;
        }
    }

    public static String getLineSeparator() {
        try {
            return System.getProperty("line.separator");
        }
        catch (SecurityException securityException) {
            return "\n";
        }
    }

    public String getNewLine() {
        return this.newLine;
    }

    public void writeStartDocument() throws XMLStreamException {
        this.beforeMarkup();
        this.out.writeStartDocument();
        this.afterMarkup();
    }

    public void writeStartDocument(String version) throws XMLStreamException {
        this.beforeMarkup();
        this.out.writeStartDocument(version);
        this.afterMarkup();
    }

    public void writeStartDocument(String encoding, String version) throws XMLStreamException {
        this.beforeMarkup();
        this.out.writeStartDocument(encoding, version);
        this.afterMarkup();
    }

    public void writeDTD(String dtd) throws XMLStreamException {
        this.beforeMarkup();
        this.out.writeDTD(dtd);
        this.afterMarkup();
    }

    public void writeProcessingInstruction(String target) throws XMLStreamException {
        this.beforeMarkup();
        this.out.writeProcessingInstruction(target);
        this.afterMarkup();
    }

    public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
        this.beforeMarkup();
        this.out.writeProcessingInstruction(target, data);
        this.afterMarkup();
    }

    public void writeComment(String data) throws XMLStreamException {
        this.beforeMarkup();
        this.out.writeComment(data);
        this.afterMarkup();
    }

    public void writeEmptyElement(String localName) throws XMLStreamException {
        this.beforeMarkup();
        this.out.writeEmptyElement(localName);
        this.afterMarkup();
    }

    public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
        this.beforeMarkup();
        this.out.writeEmptyElement(namespaceURI, localName);
        this.afterMarkup();
    }

    public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        this.beforeMarkup();
        this.out.writeEmptyElement(prefix, localName, namespaceURI);
        this.afterMarkup();
    }

    public void writeStartElement(String localName) throws XMLStreamException {
        this.beforeStartElement();
        this.out.writeStartElement(localName);
        this.afterStartElement();
    }

    public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
        this.beforeStartElement();
        this.out.writeStartElement(namespaceURI, localName);
        this.afterStartElement();
    }

    public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        this.beforeStartElement();
        this.out.writeStartElement(prefix, localName, namespaceURI);
        this.afterStartElement();
    }

    public void writeCharacters(String text) throws XMLStreamException {
        this.out.writeCharacters(text);
        this.afterData();
    }

    public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
        this.out.writeCharacters(text, start, len);
        this.afterData();
    }

    public void writeCData(String data) throws XMLStreamException {
        this.out.writeCData(data);
        this.afterData();
    }

    public void writeEntityRef(String name) throws XMLStreamException {
        this.out.writeEntityRef(name);
        this.afterData();
    }

    public void writeEndElement() throws XMLStreamException {
        this.beforeEndElement();
        this.out.writeEndElement();
        this.afterEndElement();
    }

    public void writeEndDocument() throws XMLStreamException {
        try {
            while (this.depth > 0) {
                this.writeEndElement();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.out.writeEndDocument();
        this.afterEndDocument();
    }

    protected void beforeMarkup() {
        int soFar = this.stack[this.depth];
        if ((soFar & 2) == 0 && (this.depth > 0 || soFar != 0)) {
            try {
                this.writeNewLine(this.depth);
                if (this.depth > 0 && this.getIndent().length() > 0) {
                    this.afterMarkup();
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    protected void afterMarkup() {
        int n = this.depth;
        this.stack[n] = this.stack[n] | 1;
    }

    protected void afterData() {
        int n = this.depth;
        this.stack[n] = this.stack[n] | 2;
    }

    protected void beforeStartElement() {
        this.beforeMarkup();
        if (this.stack.length <= this.depth + 1) {
            int[] newStack = new int[this.stack.length * 2];
            System.arraycopy(this.stack, 0, newStack, 0, this.stack.length);
            this.stack = newStack;
        }
        this.stack[this.depth + 1] = 0;
    }

    protected void afterStartElement() {
        this.afterMarkup();
        ++this.depth;
    }

    protected void beforeEndElement() {
        if (this.depth > 0 && this.stack[this.depth] == 1) {
            try {
                this.writeNewLine(this.depth - 1);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    protected void afterEndElement() {
        if (this.depth > 0) {
            --this.depth;
        }
    }

    protected void afterEndDocument() {
        this.depth = 0;
        if (this.stack[0] == 1) {
            try {
                this.writeNewLine(0);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        this.stack[this.depth] = 0;
    }

    protected void writeNewLine(int indentation) throws XMLStreamException {
        int newLineLength = this.getNewLine().length();
        int prefixLength = newLineLength + this.getIndent().length() * indentation;
        if (prefixLength > 0) {
            if (this.linePrefix == null) {
                this.linePrefix = (this.getNewLine() + this.getIndent()).toCharArray();
            }
            while (prefixLength > this.linePrefix.length) {
                char[] newPrefix = new char[newLineLength + (this.linePrefix.length - newLineLength) * 2];
                System.arraycopy(this.linePrefix, 0, newPrefix, 0, this.linePrefix.length);
                System.arraycopy(this.linePrefix, newLineLength, newPrefix, this.linePrefix.length, this.linePrefix.length - newLineLength);
                this.linePrefix = newPrefix;
            }
            this.out.writeCharacters(this.linePrefix, 0, prefixLength);
        }
    }
}

