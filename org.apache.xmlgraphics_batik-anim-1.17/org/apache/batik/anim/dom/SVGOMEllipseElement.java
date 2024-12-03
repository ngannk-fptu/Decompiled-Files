/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.util.DoublyIndexedTable
 *  org.w3c.dom.svg.SVGAnimatedLength
 *  org.w3c.dom.svg.SVGEllipseElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGGraphicsElement;
import org.apache.batik.anim.dom.SVGOMAnimatedLength;
import org.apache.batik.anim.dom.TraitInformation;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGEllipseElement;

public class SVGOMEllipseElement
extends SVGGraphicsElement
implements SVGEllipseElement {
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedLength cx;
    protected SVGOMAnimatedLength cy;
    protected SVGOMAnimatedLength rx;
    protected SVGOMAnimatedLength ry;

    protected SVGOMEllipseElement() {
    }

    public SVGOMEllipseElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }

    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }

    private void initializeLiveAttributes() {
        this.cx = this.createLiveAnimatedLength(null, "cx", "0", (short)2, false);
        this.cy = this.createLiveAnimatedLength(null, "cy", "0", (short)1, false);
        this.rx = this.createLiveAnimatedLength(null, "rx", null, (short)2, true);
        this.ry = this.createLiveAnimatedLength(null, "ry", null, (short)1, true);
    }

    public String getLocalName() {
        return "ellipse";
    }

    public SVGAnimatedLength getCx() {
        return this.cx;
    }

    public SVGAnimatedLength getCy() {
        return this.cy;
    }

    public SVGAnimatedLength getRx() {
        return this.rx;
    }

    public SVGAnimatedLength getRy() {
        return this.ry;
    }

    protected Node newNode() {
        return new SVGOMEllipseElement();
    }

    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return xmlTraitInformation;
    }

    static {
        DoublyIndexedTable t = new DoublyIndexedTable(SVGGraphicsElement.xmlTraitInformation);
        t.put(null, (Object)"cx", (Object)new TraitInformation(true, 3, 1));
        t.put(null, (Object)"cy", (Object)new TraitInformation(true, 3, 2));
        t.put(null, (Object)"rx", (Object)new TraitInformation(true, 3, 1));
        t.put(null, (Object)"ry", (Object)new TraitInformation(true, 3, 2));
        xmlTraitInformation = t;
    }
}

