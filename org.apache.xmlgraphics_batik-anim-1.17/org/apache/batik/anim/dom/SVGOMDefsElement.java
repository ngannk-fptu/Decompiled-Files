/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.w3c.dom.svg.SVGDefsElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGGraphicsElement;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGDefsElement;

public class SVGOMDefsElement
extends SVGGraphicsElement
implements SVGDefsElement {
    protected SVGOMDefsElement() {
    }

    public SVGOMDefsElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    public String getLocalName() {
        return "defs";
    }

    protected Node newNode() {
        return new SVGOMDefsElement();
    }
}

