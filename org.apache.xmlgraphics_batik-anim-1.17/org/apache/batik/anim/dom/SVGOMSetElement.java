/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.w3c.dom.svg.SVGSetElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGOMAnimationElement;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGSetElement;

public class SVGOMSetElement
extends SVGOMAnimationElement
implements SVGSetElement {
    protected SVGOMSetElement() {
    }

    public SVGOMSetElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    public String getLocalName() {
        return "set";
    }

    protected Node newNode() {
        return new SVGOMSetElement();
    }
}

