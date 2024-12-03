/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.util.DoublyIndexedTable
 *  org.w3c.dom.svg.SVGAnimatedLengthList
 *  org.w3c.dom.svg.SVGAnimatedNumberList
 *  org.w3c.dom.svg.SVGTextPositioningElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGOMAnimatedLengthList;
import org.apache.batik.anim.dom.SVGOMAnimatedNumberList;
import org.apache.batik.anim.dom.SVGOMTextContentElement;
import org.apache.batik.anim.dom.TraitInformation;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGAnimatedLengthList;
import org.w3c.dom.svg.SVGAnimatedNumberList;
import org.w3c.dom.svg.SVGTextPositioningElement;

public abstract class SVGOMTextPositioningElement
extends SVGOMTextContentElement
implements SVGTextPositioningElement {
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedLengthList x;
    protected SVGOMAnimatedLengthList y;
    protected SVGOMAnimatedLengthList dx;
    protected SVGOMAnimatedLengthList dy;
    protected SVGOMAnimatedNumberList rotate;

    protected SVGOMTextPositioningElement() {
    }

    protected SVGOMTextPositioningElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }

    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }

    private void initializeLiveAttributes() {
        this.x = this.createLiveAnimatedLengthList(null, "x", this.getDefaultXValue(), true, (short)2);
        this.y = this.createLiveAnimatedLengthList(null, "y", this.getDefaultYValue(), true, (short)1);
        this.dx = this.createLiveAnimatedLengthList(null, "dx", "", true, (short)2);
        this.dy = this.createLiveAnimatedLengthList(null, "dy", "", true, (short)1);
        this.rotate = this.createLiveAnimatedNumberList(null, "rotate", "", true);
    }

    public SVGAnimatedLengthList getX() {
        return this.x;
    }

    public SVGAnimatedLengthList getY() {
        return this.y;
    }

    public SVGAnimatedLengthList getDx() {
        return this.dx;
    }

    public SVGAnimatedLengthList getDy() {
        return this.dy;
    }

    public SVGAnimatedNumberList getRotate() {
        return this.rotate;
    }

    protected String getDefaultXValue() {
        return "";
    }

    protected String getDefaultYValue() {
        return "";
    }

    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return xmlTraitInformation;
    }

    static {
        DoublyIndexedTable t = new DoublyIndexedTable(SVGOMTextContentElement.xmlTraitInformation);
        t.put(null, (Object)"x", (Object)new TraitInformation(true, 14, 1));
        t.put(null, (Object)"y", (Object)new TraitInformation(true, 14, 2));
        t.put(null, (Object)"dx", (Object)new TraitInformation(true, 14, 1));
        t.put(null, (Object)"dy", (Object)new TraitInformation(true, 14, 2));
        t.put(null, (Object)"rotate", (Object)new TraitInformation(true, 13));
        xmlTraitInformation = t;
    }
}

