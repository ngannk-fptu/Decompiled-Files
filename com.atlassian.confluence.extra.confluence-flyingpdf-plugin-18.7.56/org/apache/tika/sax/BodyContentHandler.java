/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax;

import java.io.OutputStream;
import java.io.Writer;
import org.apache.tika.sax.ContentHandlerDecorator;
import org.apache.tika.sax.WriteOutContentHandler;
import org.apache.tika.sax.xpath.Matcher;
import org.apache.tika.sax.xpath.MatchingContentHandler;
import org.apache.tika.sax.xpath.XPathParser;
import org.xml.sax.ContentHandler;

public class BodyContentHandler
extends ContentHandlerDecorator {
    private static final XPathParser PARSER = new XPathParser("xhtml", "http://www.w3.org/1999/xhtml");
    private static final Matcher MATCHER = PARSER.parse("/xhtml:html/xhtml:body/descendant::node()");

    public BodyContentHandler(ContentHandler handler) {
        super(new MatchingContentHandler(handler, MATCHER));
    }

    public BodyContentHandler(Writer writer) {
        this(new WriteOutContentHandler(writer));
    }

    public BodyContentHandler(OutputStream stream) {
        this(new WriteOutContentHandler(stream));
    }

    public BodyContentHandler(int writeLimit) {
        this(new WriteOutContentHandler(writeLimit));
    }

    public BodyContentHandler() {
        this(new WriteOutContentHandler());
    }
}

