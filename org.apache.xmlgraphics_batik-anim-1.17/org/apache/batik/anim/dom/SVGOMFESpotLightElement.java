/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.util.DoublyIndexedTable
 *  org.w3c.dom.svg.SVGAnimatedNumber
 *  org.w3c.dom.svg.SVGFESpotLightElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGOMAnimatedNumber;
import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.anim.dom.TraitInformation;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGFESpotLightElement;

public class SVGOMFESpotLightElement
extends SVGOMElement
implements SVGFESpotLightElement {
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedNumber x;
    protected SVGOMAnimatedNumber y;
    protected SVGOMAnimatedNumber z;
    protected SVGOMAnimatedNumber pointsAtX;
    protected SVGOMAnimatedNumber pointsAtY;
    protected SVGOMAnimatedNumber pointsAtZ;
    protected SVGOMAnimatedNumber specularExponent;
    protected SVGOMAnimatedNumber limitingConeAngle;

    protected SVGOMFESpotLightElement() {
    }

    public SVGOMFESpotLightElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }

    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }

    private void initializeLiveAttributes() {
        this.x = this.createLiveAnimatedNumber(null, "x", 0.0f);
        this.y = this.createLiveAnimatedNumber(null, "y", 0.0f);
        this.z = this.createLiveAnimatedNumber(null, "z", 0.0f);
        this.pointsAtX = this.createLiveAnimatedNumber(null, "pointsAtX", 0.0f);
        this.pointsAtY = this.createLiveAnimatedNumber(null, "pointsAtY", 0.0f);
        this.pointsAtZ = this.createLiveAnimatedNumber(null, "pointsAtZ", 0.0f);
        this.specularExponent = this.createLiveAnimatedNumber(null, "specularExponent", 1.0f);
        this.limitingConeAngle = this.createLiveAnimatedNumber(null, "limitingConeAngle", 0.0f);
    }

    public String getLocalName() {
        return "feSpotLight";
    }

    public SVGAnimatedNumber getX() {
        return this.x;
    }

    public SVGAnimatedNumber getY() {
        return this.y;
    }

    public SVGAnimatedNumber getZ() {
        return this.z;
    }

    public SVGAnimatedNumber getPointsAtX() {
        return this.pointsAtX;
    }

    public SVGAnimatedNumber getPointsAtY() {
        return this.pointsAtY;
    }

    public SVGAnimatedNumber getPointsAtZ() {
        return this.pointsAtZ;
    }

    public SVGAnimatedNumber getSpecularExponent() {
        return this.specularExponent;
    }

    public SVGAnimatedNumber getLimitingConeAngle() {
        return this.limitingConeAngle;
    }

    protected Node newNode() {
        return new SVGOMFESpotLightElement();
    }

    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return xmlTraitInformation;
    }

    static {
        DoublyIndexedTable t = new DoublyIndexedTable(SVGOMElement.xmlTraitInformation);
        t.put(null, (Object)"x", (Object)new TraitInformation(true, 2));
        t.put(null, (Object)"y", (Object)new TraitInformation(true, 2));
        t.put(null, (Object)"z", (Object)new TraitInformation(true, 2));
        t.put(null, (Object)"pointsAtX", (Object)new TraitInformation(true, 2));
        t.put(null, (Object)"pointsAtY", (Object)new TraitInformation(true, 2));
        t.put(null, (Object)"pointsAtZ", (Object)new TraitInformation(true, 2));
        t.put(null, (Object)"specularExponent", (Object)new TraitInformation(true, 2));
        t.put(null, (Object)"limitingConeAngle", (Object)new TraitInformation(true, 2));
        xmlTraitInformation = t;
    }
}

