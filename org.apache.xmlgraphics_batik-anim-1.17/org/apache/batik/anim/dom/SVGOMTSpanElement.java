/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.w3c.dom.svg.SVGTSpanElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGOMTextPositioningElement;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGTSpanElement;

public class SVGOMTSpanElement
extends SVGOMTextPositioningElement
implements SVGTSpanElement {
    protected SVGOMTSpanElement() {
    }

    public SVGOMTSpanElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    public String getLocalName() {
        return "tspan";
    }

    protected Node newNode() {
        return new SVGOMTSpanElement();
    }
}

