/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.w3c.dom.svg.SVGHKernElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGHKernElement;

public class SVGOMHKernElement
extends SVGOMElement
implements SVGHKernElement {
    protected SVGOMHKernElement() {
    }

    public SVGOMHKernElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    public String getLocalName() {
        return "hkern";
    }

    protected Node newNode() {
        return new SVGOMHKernElement();
    }
}

