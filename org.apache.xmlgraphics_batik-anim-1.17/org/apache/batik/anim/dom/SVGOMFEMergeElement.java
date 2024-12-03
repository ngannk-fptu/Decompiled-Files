/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.w3c.dom.svg.SVGFEMergeElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGOMFilterPrimitiveStandardAttributes;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGFEMergeElement;

public class SVGOMFEMergeElement
extends SVGOMFilterPrimitiveStandardAttributes
implements SVGFEMergeElement {
    protected SVGOMFEMergeElement() {
    }

    public SVGOMFEMergeElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    public String getLocalName() {
        return "feMerge";
    }

    protected Node newNode() {
        return new SVGOMFEMergeElement();
    }
}

