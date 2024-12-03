/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGOMTextPositioningElement;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.Node;

public class SVGOMFlowSpanElement
extends SVGOMTextPositioningElement {
    protected SVGOMFlowSpanElement() {
    }

    public SVGOMFlowSpanElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    public String getLocalName() {
        return "flowSpan";
    }

    protected Node newNode() {
        return new SVGOMFlowSpanElement();
    }
}

