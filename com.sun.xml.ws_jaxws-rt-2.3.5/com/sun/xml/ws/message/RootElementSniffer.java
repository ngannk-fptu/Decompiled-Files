/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.message;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

public final class RootElementSniffer
extends DefaultHandler {
    private String nsUri = "##error";
    private String localName = "##error";
    private Attributes atts;
    private final boolean parseAttributes;
    private static final SAXException aSAXException = new SAXException();
    private static final Attributes EMPTY_ATTRIBUTES = new AttributesImpl();

    public RootElementSniffer(boolean parseAttributes) {
        this.parseAttributes = parseAttributes;
    }

    public RootElementSniffer() {
        this(true);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes a) throws SAXException {
        this.nsUri = uri;
        this.localName = localName;
        if (this.parseAttributes) {
            this.atts = a.getLength() == 0 ? EMPTY_ATTRIBUTES : new AttributesImpl(a);
        }
        throw aSAXException;
    }

    public String getNsUri() {
        return this.nsUri;
    }

    public String getLocalName() {
        return this.localName;
    }

    public Attributes getAttributes() {
        return this.atts;
    }
}

