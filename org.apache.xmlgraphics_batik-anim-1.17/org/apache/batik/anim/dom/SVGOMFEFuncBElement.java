/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.w3c.dom.svg.SVGFEFuncBElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGOMComponentTransferFunctionElement;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGFEFuncBElement;

public class SVGOMFEFuncBElement
extends SVGOMComponentTransferFunctionElement
implements SVGFEFuncBElement {
    protected SVGOMFEFuncBElement() {
    }

    public SVGOMFEFuncBElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    public String getLocalName() {
        return "feFuncB";
    }

    protected Node newNode() {
        return new SVGOMFEFuncBElement();
    }
}

