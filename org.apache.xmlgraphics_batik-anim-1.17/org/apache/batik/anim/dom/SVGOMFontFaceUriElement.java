/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.w3c.dom.svg.SVGFontFaceUriElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGFontFaceUriElement;

public class SVGOMFontFaceUriElement
extends SVGOMElement
implements SVGFontFaceUriElement {
    protected SVGOMFontFaceUriElement() {
    }

    public SVGOMFontFaceUriElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    public String getLocalName() {
        return "font-face-uri";
    }

    protected Node newNode() {
        return new SVGOMFontFaceUriElement();
    }
}

