/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.util.DoublyIndexedTable
 *  org.w3c.dom.svg.SVGAnimatedLength
 *  org.w3c.dom.svg.SVGLinearGradientElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGOMAnimatedLength;
import org.apache.batik.anim.dom.SVGOMGradientElement;
import org.apache.batik.anim.dom.TraitInformation;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGLinearGradientElement;

public class SVGOMLinearGradientElement
extends SVGOMGradientElement
implements SVGLinearGradientElement {
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedLength x1;
    protected SVGOMAnimatedLength y1;
    protected SVGOMAnimatedLength x2;
    protected SVGOMAnimatedLength y2;

    protected SVGOMLinearGradientElement() {
    }

    public SVGOMLinearGradientElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }

    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }

    private void initializeLiveAttributes() {
        this.x1 = this.createLiveAnimatedLength(null, "x1", "0%", (short)2, false);
        this.y1 = this.createLiveAnimatedLength(null, "y1", "0%", (short)1, false);
        this.x2 = this.createLiveAnimatedLength(null, "x2", "100%", (short)2, false);
        this.y2 = this.createLiveAnimatedLength(null, "y2", "0%", (short)1, false);
    }

    public String getLocalName() {
        return "linearGradient";
    }

    public SVGAnimatedLength getX1() {
        return this.x1;
    }

    public SVGAnimatedLength getY1() {
        return this.y1;
    }

    public SVGAnimatedLength getX2() {
        return this.x2;
    }

    public SVGAnimatedLength getY2() {
        return this.y2;
    }

    @Override
    protected Node newNode() {
        return new SVGOMLinearGradientElement();
    }

    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return xmlTraitInformation;
    }

    static {
        DoublyIndexedTable t = new DoublyIndexedTable(SVGOMGradientElement.xmlTraitInformation);
        t.put(null, (Object)"x", (Object)new TraitInformation(true, 3, 1));
        t.put(null, (Object)"y", (Object)new TraitInformation(true, 3, 2));
        t.put(null, (Object)"width", (Object)new TraitInformation(true, 3, 1));
        t.put(null, (Object)"height", (Object)new TraitInformation(true, 3, 2));
        xmlTraitInformation = t;
    }
}

