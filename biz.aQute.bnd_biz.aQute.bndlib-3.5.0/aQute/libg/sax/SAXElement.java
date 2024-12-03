/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.sax;

import org.xml.sax.Attributes;

public class SAXElement {
    private final String uri;
    private final String localName;
    private final String qName;
    private final Attributes atts;

    public SAXElement(String uri, String localName, String qName, Attributes atts) {
        this.uri = uri;
        this.localName = localName;
        this.qName = qName;
        this.atts = atts;
    }

    public String getUri() {
        return this.uri;
    }

    public String getLocalName() {
        return this.localName;
    }

    public String getqName() {
        return this.qName;
    }

    public Attributes getAtts() {
        return this.atts;
    }
}

