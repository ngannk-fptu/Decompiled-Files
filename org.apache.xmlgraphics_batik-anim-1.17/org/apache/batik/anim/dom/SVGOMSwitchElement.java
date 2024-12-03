/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.w3c.dom.svg.SVGSwitchElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGGraphicsElement;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGSwitchElement;

public class SVGOMSwitchElement
extends SVGGraphicsElement
implements SVGSwitchElement {
    protected SVGOMSwitchElement() {
    }

    public SVGOMSwitchElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    public String getLocalName() {
        return "switch";
    }

    protected Node newNode() {
        return new SVGOMSwitchElement();
    }
}

