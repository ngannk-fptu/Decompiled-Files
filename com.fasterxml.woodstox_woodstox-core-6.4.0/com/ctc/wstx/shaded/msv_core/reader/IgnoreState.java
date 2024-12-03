/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader;

import com.ctc.wstx.shaded.msv_core.reader.State;
import org.xml.sax.Attributes;

public class IgnoreState
extends State {
    private int depth = 1;

    public final void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
        ++this.depth;
    }

    public final void endElement(String namespaceURI, String localName, String qName) {
        --this.depth;
        if (this.depth == 0) {
            this.reader.popState();
        }
    }

    public final void endDocument() {
        this.reader.popState();
    }

    public void characters(char[] buffer, int from, int len) {
    }
}

