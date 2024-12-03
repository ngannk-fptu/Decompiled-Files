/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.w3c.dom.svg.SVGPolylineElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGPointShapeElement;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGPolylineElement;

public class SVGOMPolylineElement
extends SVGPointShapeElement
implements SVGPolylineElement {
    protected SVGOMPolylineElement() {
    }

    public SVGOMPolylineElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    public String getLocalName() {
        return "polyline";
    }

    protected Node newNode() {
        return new SVGOMPolylineElement();
    }
}

