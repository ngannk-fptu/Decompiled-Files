/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.util.DoublyIndexedTable
 *  org.w3c.dom.svg.SVGAnimatedNumber
 *  org.w3c.dom.svg.SVGFEPointLightElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGOMAnimatedNumber;
import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.anim.dom.TraitInformation;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGFEPointLightElement;

public class SVGOMFEPointLightElement
extends SVGOMElement
implements SVGFEPointLightElement {
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedNumber x;
    protected SVGOMAnimatedNumber y;
    protected SVGOMAnimatedNumber z;

    protected SVGOMFEPointLightElement() {
    }

    public SVGOMFEPointLightElement(String prefix, AbstractDocument owner) {
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
    }

    public String getLocalName() {
        return "fePointLight";
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

    protected Node newNode() {
        return new SVGOMFEPointLightElement();
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
        xmlTraitInformation = t;
    }
}

