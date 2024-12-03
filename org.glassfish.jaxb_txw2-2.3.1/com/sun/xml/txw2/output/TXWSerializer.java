/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.txw2.output;

import com.sun.xml.txw2.TypedXmlWriter;
import com.sun.xml.txw2.output.XmlSerializer;

public final class TXWSerializer
implements XmlSerializer {
    public final TypedXmlWriter txw;

    public TXWSerializer(TypedXmlWriter txw) {
        this.txw = txw;
    }

    @Override
    public void startDocument() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void endDocument() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void beginStartTag(String uri, String localName, String prefix) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeAttribute(String uri, String localName, String prefix, StringBuilder value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeXmlns(String prefix, String uri) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void endStartTag(String uri, String localName, String prefix) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void endTag() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void text(StringBuilder text) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void cdata(StringBuilder text) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void comment(StringBuilder comment) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void flush() {
        throw new UnsupportedOperationException();
    }
}

