/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.w3c.dom.svg.SVGGElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGGraphicsElement;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGGElement;

public class SVGOMGElement
extends SVGGraphicsElement
implements SVGGElement {
    protected SVGOMGElement() {
    }

    public SVGOMGElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    public String getLocalName() {
        return "g";
    }

    protected Node newNode() {
        return new SVGOMGElement();
    }
}

