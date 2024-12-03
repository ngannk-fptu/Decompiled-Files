/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.w3c.dom.svg.SVGPolygonElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGPointShapeElement;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGPolygonElement;

public class SVGOMPolygonElement
extends SVGPointShapeElement
implements SVGPolygonElement {
    protected SVGOMPolygonElement() {
    }

    public SVGOMPolygonElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    public String getLocalName() {
        return "polygon";
    }

    protected Node newNode() {
        return new SVGOMPolygonElement();
    }
}

