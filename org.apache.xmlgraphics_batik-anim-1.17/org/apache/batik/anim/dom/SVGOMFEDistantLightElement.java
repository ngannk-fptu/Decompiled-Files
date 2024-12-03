/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.util.DoublyIndexedTable
 *  org.w3c.dom.svg.SVGAnimatedNumber
 *  org.w3c.dom.svg.SVGFEDistantLightElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGOMAnimatedNumber;
import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.anim.dom.TraitInformation;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGFEDistantLightElement;

public class SVGOMFEDistantLightElement
extends SVGOMElement
implements SVGFEDistantLightElement {
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedNumber azimuth;
    protected SVGOMAnimatedNumber elevation;

    protected SVGOMFEDistantLightElement() {
    }

    public SVGOMFEDistantLightElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }

    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }

    private void initializeLiveAttributes() {
        this.azimuth = this.createLiveAnimatedNumber(null, "azimuth", 0.0f);
        this.elevation = this.createLiveAnimatedNumber(null, "elevation", 0.0f);
    }

    public String getLocalName() {
        return "feDistantLight";
    }

    public SVGAnimatedNumber getAzimuth() {
        return this.azimuth;
    }

    public SVGAnimatedNumber getElevation() {
        return this.elevation;
    }

    protected Node newNode() {
        return new SVGOMFEDistantLightElement();
    }

    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return xmlTraitInformation;
    }

    static {
        DoublyIndexedTable t = new DoublyIndexedTable(SVGOMElement.xmlTraitInformation);
        t.put(null, (Object)"azimuth", (Object)new TraitInformation(true, 2));
        t.put(null, (Object)"elevation", (Object)new TraitInformation(true, 2));
        xmlTraitInformation = t;
    }
}

