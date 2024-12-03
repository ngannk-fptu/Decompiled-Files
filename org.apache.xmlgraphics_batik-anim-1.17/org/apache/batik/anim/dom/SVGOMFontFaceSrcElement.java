/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.w3c.dom.svg.SVGFontFaceSrcElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGFontFaceSrcElement;

public class SVGOMFontFaceSrcElement
extends SVGOMElement
implements SVGFontFaceSrcElement {
    protected SVGOMFontFaceSrcElement() {
    }

    public SVGOMFontFaceSrcElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    public String getLocalName() {
        return "font-face-src";
    }

    protected Node newNode() {
        return new SVGOMFontFaceSrcElement();
    }
}

