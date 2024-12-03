/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.Node;

public class SVGOMHandlerElement
extends SVGOMElement {
    protected SVGOMHandlerElement() {
    }

    public SVGOMHandlerElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    public String getLocalName() {
        return "handler";
    }

    protected Node newNode() {
        return new SVGOMHandlerElement();
    }
}

