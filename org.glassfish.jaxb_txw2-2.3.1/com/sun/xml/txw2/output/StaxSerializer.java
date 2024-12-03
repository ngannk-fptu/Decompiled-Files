/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.txw2.output;

import com.sun.xml.txw2.TxwException;
import com.sun.xml.txw2.output.IndentingXMLStreamWriter;
import com.sun.xml.txw2.output.XmlSerializer;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class StaxSerializer
implements XmlSerializer {
    private final XMLStreamWriter out;

    public StaxSerializer(XMLStreamWriter writer) {
        this(writer, true);
    }

    public StaxSerializer(XMLStreamWriter writer, boolean indenting) {
        if (indenting) {
            writer = new IndentingXMLStreamWriter(writer);
        }
        this.out = writer;
    }

    @Override
    public void startDocument() {
        try {
            this.out.writeStartDocument();
        }
        catch (XMLStreamException e) {
            throw new TxwException(e);
        }
    }

    @Override
    public void beginStartTag(String uri, String localName, String prefix) {
        try {
            this.out.writeStartElement(prefix, localName, uri);
        }
        catch (XMLStreamException e) {
            throw new TxwException(e);
        }
    }

    @Override
    public void writeAttribute(String uri, String localName, String prefix, StringBuilder value) {
        try {
            this.out.writeAttribute(prefix, uri, localName, value.toString());
        }
        catch (XMLStreamException e) {
            throw new TxwException(e);
        }
    }

    @Override
    public void writeXmlns(String prefix, String uri) {
        try {
            if (prefix.length() == 0) {
                this.out.setDefaultNamespace(uri);
            } else {
                this.out.setPrefix(prefix, uri);
            }
            this.out.writeNamespace(prefix, uri);
        }
        catch (XMLStreamException e) {
            throw new TxwException(e);
        }
    }

    @Override
    public void endStartTag(String uri, String localName, String prefix) {
    }

    @Override
    public void endTag() {
        try {
            this.out.writeEndElement();
        }
        catch (XMLStreamException e) {
            throw new TxwException(e);
        }
    }

    @Override
    public void text(StringBuilder text) {
        try {
            this.out.writeCharacters(text.toString());
        }
        catch (XMLStreamException e) {
            throw new TxwException(e);
        }
    }

    @Override
    public void cdata(StringBuilder text) {
        try {
            this.out.writeCData(text.toString());
        }
        catch (XMLStreamException e) {
            throw new TxwException(e);
        }
    }

    @Override
    public void comment(StringBuilder comment) {
        try {
            this.out.writeComment(comment.toString());
        }
        catch (XMLStreamException e) {
            throw new TxwException(e);
        }
    }

    @Override
    public void endDocument() {
        try {
            this.out.writeEndDocument();
            this.out.flush();
        }
        catch (XMLStreamException e) {
            throw new TxwException(e);
        }
    }

    @Override
    public void flush() {
        try {
            this.out.flush();
        }
        catch (XMLStreamException e) {
            throw new TxwException(e);
        }
    }
}

