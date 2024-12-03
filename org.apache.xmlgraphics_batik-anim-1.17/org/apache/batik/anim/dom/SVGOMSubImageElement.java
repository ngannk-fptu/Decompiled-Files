/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGStylableElement;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.Node;

public class SVGOMSubImageElement
extends SVGStylableElement {
    protected SVGOMSubImageElement() {
    }

    public SVGOMSubImageElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    public String getLocalName() {
        return "subImage";
    }

    protected Node newNode() {
        return new SVGOMSubImageElement();
    }
}

