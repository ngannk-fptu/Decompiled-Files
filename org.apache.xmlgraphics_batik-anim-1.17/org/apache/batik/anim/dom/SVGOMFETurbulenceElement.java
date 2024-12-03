/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.util.DoublyIndexedTable
 *  org.w3c.dom.svg.SVGAnimatedEnumeration
 *  org.w3c.dom.svg.SVGAnimatedInteger
 *  org.w3c.dom.svg.SVGAnimatedNumber
 *  org.w3c.dom.svg.SVGFETurbulenceElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGOMAnimatedEnumeration;
import org.apache.batik.anim.dom.SVGOMAnimatedInteger;
import org.apache.batik.anim.dom.SVGOMAnimatedNumber;
import org.apache.batik.anim.dom.SVGOMFilterPrimitiveStandardAttributes;
import org.apache.batik.anim.dom.TraitInformation;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedInteger;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGFETurbulenceElement;

public class SVGOMFETurbulenceElement
extends SVGOMFilterPrimitiveStandardAttributes
implements SVGFETurbulenceElement {
    protected static DoublyIndexedTable xmlTraitInformation;
    protected static final String[] STITCH_TILES_VALUES;
    protected static final String[] TYPE_VALUES;
    protected SVGOMAnimatedInteger numOctaves;
    protected SVGOMAnimatedNumber seed;
    protected SVGOMAnimatedEnumeration stitchTiles;
    protected SVGOMAnimatedEnumeration type;

    protected SVGOMFETurbulenceElement() {
    }

    public SVGOMFETurbulenceElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }

    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }

    private void initializeLiveAttributes() {
        this.numOctaves = this.createLiveAnimatedInteger(null, "numOctaves", 1);
        this.seed = this.createLiveAnimatedNumber(null, "seed", 0.0f);
        this.stitchTiles = this.createLiveAnimatedEnumeration(null, "stitchTiles", STITCH_TILES_VALUES, (short)2);
        this.type = this.createLiveAnimatedEnumeration(null, "type", TYPE_VALUES, (short)2);
    }

    public String getLocalName() {
        return "feTurbulence";
    }

    public SVGAnimatedNumber getBaseFrequencyX() {
        throw new UnsupportedOperationException("SVGFETurbulenceElement.getBaseFrequencyX is not implemented");
    }

    public SVGAnimatedNumber getBaseFrequencyY() {
        throw new UnsupportedOperationException("SVGFETurbulenceElement.getBaseFrequencyY is not implemented");
    }

    public SVGAnimatedInteger getNumOctaves() {
        return this.numOctaves;
    }

    public SVGAnimatedNumber getSeed() {
        return this.seed;
    }

    public SVGAnimatedEnumeration getStitchTiles() {
        return this.stitchTiles;
    }

    public SVGAnimatedEnumeration getType() {
        return this.type;
    }

    protected Node newNode() {
        return new SVGOMFETurbulenceElement();
    }

    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return xmlTraitInformation;
    }

    static {
        DoublyIndexedTable t = new DoublyIndexedTable(SVGOMFilterPrimitiveStandardAttributes.xmlTraitInformation);
        t.put(null, (Object)"baseFrequency", (Object)new TraitInformation(true, 4));
        t.put(null, (Object)"numOctaves", (Object)new TraitInformation(true, 1));
        t.put(null, (Object)"seed", (Object)new TraitInformation(true, 2));
        t.put(null, (Object)"stitchTiles", (Object)new TraitInformation(true, 15));
        t.put(null, (Object)"type", (Object)new TraitInformation(true, 15));
        xmlTraitInformation = t;
        STITCH_TILES_VALUES = new String[]{"", "stitch", "noStitch"};
        TYPE_VALUES = new String[]{"", "fractalNoise", "turbulence"};
    }
}

