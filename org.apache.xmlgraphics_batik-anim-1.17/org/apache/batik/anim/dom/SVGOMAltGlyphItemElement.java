/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.w3c.dom.svg.SVGAltGlyphItemElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAltGlyphItemElement;

public class SVGOMAltGlyphItemElement
extends SVGOMElement
implements SVGAltGlyphItemElement {
    protected SVGOMAltGlyphItemElement() {
    }

    public SVGOMAltGlyphItemElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    public String getLocalName() {
        return "altGlyphItem";
    }

    protected Node newNode() {
        return new SVGOMAltGlyphItemElement();
    }
}

