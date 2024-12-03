/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.w3c.dom.svg.SVGDescElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGDescriptiveElement;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGDescElement;

public class SVGOMDescElement
extends SVGDescriptiveElement
implements SVGDescElement {
    protected SVGOMDescElement() {
    }

    public SVGOMDescElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    public String getLocalName() {
        return "desc";
    }

    protected Node newNode() {
        return new SVGOMDescElement();
    }
}

