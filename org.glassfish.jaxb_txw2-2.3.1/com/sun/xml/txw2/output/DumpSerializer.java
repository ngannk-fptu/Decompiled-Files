/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.txw2.output;

import com.sun.xml.txw2.output.XmlSerializer;
import java.io.PrintStream;

public class DumpSerializer
implements XmlSerializer {
    private final PrintStream out;

    public DumpSerializer(PrintStream out) {
        this.out = out;
    }

    @Override
    public void beginStartTag(String uri, String localName, String prefix) {
        this.out.println('<' + prefix + ':' + localName);
    }

    @Override
    public void writeAttribute(String uri, String localName, String prefix, StringBuilder value) {
        this.out.println('@' + prefix + ':' + localName + '=' + value);
    }

    @Override
    public void writeXmlns(String prefix, String uri) {
        this.out.println("xmlns:" + prefix + '=' + uri);
    }

    @Override
    public void endStartTag(String uri, String localName, String prefix) {
        this.out.println('>');
    }

    @Override
    public void endTag() {
        this.out.println("</  >");
    }

    @Override
    public void text(StringBuilder text) {
        this.out.println(text);
    }

    @Override
    public void cdata(StringBuilder text) {
        this.out.println("<![CDATA[");
        this.out.println(text);
        this.out.println("]]>");
    }

    @Override
    public void comment(StringBuilder comment) {
        this.out.println("<!--");
        this.out.println(comment);
        this.out.println("-->");
    }

    @Override
    public void startDocument() {
        this.out.println("<?xml?>");
    }

    @Override
    public void endDocument() {
        this.out.println("done");
    }

    @Override
    public void flush() {
        this.out.println("flush");
    }
}

