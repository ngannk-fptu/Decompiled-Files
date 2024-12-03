/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.w3c.dom.svg.SVGAnimateElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGOMAnimationElement;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimateElement;

public class SVGOMAnimateElement
extends SVGOMAnimationElement
implements SVGAnimateElement {
    protected SVGOMAnimateElement() {
    }

    public SVGOMAnimateElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    public String getLocalName() {
        return "animate";
    }

    protected Node newNode() {
        return new SVGOMAnimateElement();
    }
}

