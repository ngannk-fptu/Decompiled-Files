/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.verifier.identity;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv_core.verifier.identity.IDConstraintChecker;
import com.ctc.wstx.shaded.msv_core.verifier.identity.Matcher;
import org.xml.sax.SAXException;

class MatcherBundle
extends Matcher {
    protected Matcher[] children;
    private int depth = 0;

    protected final int getDepth() {
        return this.depth;
    }

    protected MatcherBundle(IDConstraintChecker owner) {
        super(owner);
    }

    protected void startElement(String namespaceURI, String localName) throws SAXException {
        ++this.depth;
        for (int i = 0; i < this.children.length; ++i) {
            this.children[i].startElement(namespaceURI, localName);
        }
    }

    protected void onAttribute(String namespaceURI, String localName, String value, Datatype type) throws SAXException {
        for (int i = 0; i < this.children.length; ++i) {
            this.children[i].onAttribute(namespaceURI, localName, value, type);
        }
    }

    protected void endElement(Datatype type) throws SAXException {
        for (int i = 0; i < this.children.length; ++i) {
            this.children[i].endElement(type);
        }
        if (this.depth-- == 0) {
            this.owner.remove(this);
            this.onRemoved();
        }
    }

    protected void characters(char[] buf, int start, int len) throws SAXException {
        for (int i = 0; i < this.children.length; ++i) {
            this.children[i].characters(buf, start, len);
        }
    }

    protected void onRemoved() throws SAXException {
    }
}

