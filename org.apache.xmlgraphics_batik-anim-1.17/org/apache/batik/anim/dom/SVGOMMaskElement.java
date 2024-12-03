/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.util.DoublyIndexedTable
 *  org.w3c.dom.svg.SVGAnimatedEnumeration
 *  org.w3c.dom.svg.SVGAnimatedLength
 *  org.w3c.dom.svg.SVGMaskElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGGraphicsElement;
import org.apache.batik.anim.dom.SVGOMAnimatedEnumeration;
import org.apache.batik.anim.dom.SVGOMAnimatedLength;
import org.apache.batik.anim.dom.TraitInformation;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGMaskElement;

public class SVGOMMaskElement
extends SVGGraphicsElement
implements SVGMaskElement {
    protected static DoublyIndexedTable xmlTraitInformation;
    protected static final String[] UNITS_VALUES;
    protected SVGOMAnimatedLength x;
    protected SVGOMAnimatedLength y;
    protected SVGOMAnimatedLength width;
    protected SVGOMAnimatedLength height;
    protected SVGOMAnimatedEnumeration maskUnits;
    protected SVGOMAnimatedEnumeration maskContentUnits;

    protected SVGOMMaskElement() {
    }

    public SVGOMMaskElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }

    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }

    private void initializeLiveAttributes() {
        this.x = this.createLiveAnimatedLength(null, "x", "-10%", (short)2, false);
        this.y = this.createLiveAnimatedLength(null, "y", "-10%", (short)1, false);
        this.width = this.createLiveAnimatedLength(null, "width", "120%", (short)2, true);
        this.height = this.createLiveAnimatedLength(null, "height", "120%", (short)1, true);
        this.maskUnits = this.createLiveAnimatedEnumeration(null, "maskUnits", UNITS_VALUES, (short)2);
        this.maskContentUnits = this.createLiveAnimatedEnumeration(null, "maskContentUnits", UNITS_VALUES, (short)1);
    }

    public String getLocalName() {
        return "mask";
    }

    public SVGAnimatedEnumeration getMaskUnits() {
        return this.maskUnits;
    }

    public SVGAnimatedEnumeration getMaskContentUnits() {
        return this.maskContentUnits;
    }

    public SVGAnimatedLength getX() {
        return this.x;
    }

    public SVGAnimatedLength getY() {
        return this.y;
    }

    public SVGAnimatedLength getWidth() {
        return this.width;
    }

    public SVGAnimatedLength getHeight() {
        return this.height;
    }

    protected Node newNode() {
        return new SVGOMMaskElement();
    }

    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return xmlTraitInformation;
    }

    static {
        DoublyIndexedTable t = new DoublyIndexedTable(SVGGraphicsElement.xmlTraitInformation);
        t.put(null, (Object)"x", (Object)new TraitInformation(true, 3, 1));
        t.put(null, (Object)"y", (Object)new TraitInformation(true, 3, 2));
        t.put(null, (Object)"width", (Object)new TraitInformation(true, 3, 1));
        t.put(null, (Object)"height", (Object)new TraitInformation(true, 3, 2));
        t.put(null, (Object)"maskUnits", (Object)new TraitInformation(true, 15));
        t.put(null, (Object)"maskContentUnits", (Object)new TraitInformation(true, 15));
        xmlTraitInformation = t;
        UNITS_VALUES = new String[]{"", "userSpaceOnUse", "objectBoundingBox"};
    }
}

