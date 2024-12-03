/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.w3c.dom.svg.SVGTRefElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGURIReferenceTextPositioningElement;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGTRefElement;

public class SVGOMTRefElement
extends SVGURIReferenceTextPositioningElement
implements SVGTRefElement {
    protected SVGOMTRefElement() {
    }

    public SVGOMTRefElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    public String getLocalName() {
        return "tref";
    }

    protected Node newNode() {
        return new SVGOMTRefElement();
    }
}

