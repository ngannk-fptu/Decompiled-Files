/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.util.DoublyIndexedTable
 *  org.w3c.dom.svg.SVGAnimatedPoints
 *  org.w3c.dom.svg.SVGPointList
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGGraphicsElement;
import org.apache.batik.anim.dom.SVGOMAnimatedPoints;
import org.apache.batik.anim.dom.TraitInformation;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGAnimatedPoints;
import org.w3c.dom.svg.SVGPointList;

public abstract class SVGPointShapeElement
extends SVGGraphicsElement
implements SVGAnimatedPoints {
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedPoints points;

    protected SVGPointShapeElement() {
    }

    public SVGPointShapeElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }

    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }

    private void initializeLiveAttributes() {
        this.points = this.createLiveAnimatedPoints(null, "points", "");
    }

    public SVGOMAnimatedPoints getSVGOMAnimatedPoints() {
        return this.points;
    }

    public SVGPointList getPoints() {
        return this.points.getPoints();
    }

    public SVGPointList getAnimatedPoints() {
        return this.points.getAnimatedPoints();
    }

    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return xmlTraitInformation;
    }

    static {
        DoublyIndexedTable t = new DoublyIndexedTable(SVGGraphicsElement.xmlTraitInformation);
        t.put(null, (Object)"points", (Object)new TraitInformation(true, 31));
        xmlTraitInformation = t;
    }
}

