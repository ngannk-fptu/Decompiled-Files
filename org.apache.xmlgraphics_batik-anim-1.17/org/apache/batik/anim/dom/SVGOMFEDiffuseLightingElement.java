/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.util.DoublyIndexedTable
 *  org.w3c.dom.svg.SVGAnimatedNumber
 *  org.w3c.dom.svg.SVGAnimatedString
 *  org.w3c.dom.svg.SVGFEDiffuseLightingElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGOMAnimatedNumber;
import org.apache.batik.anim.dom.SVGOMAnimatedString;
import org.apache.batik.anim.dom.SVGOMFilterPrimitiveStandardAttributes;
import org.apache.batik.anim.dom.TraitInformation;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGFEDiffuseLightingElement;

public class SVGOMFEDiffuseLightingElement
extends SVGOMFilterPrimitiveStandardAttributes
implements SVGFEDiffuseLightingElement {
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedString in;
    protected SVGOMAnimatedNumber surfaceScale;
    protected SVGOMAnimatedNumber diffuseConstant;

    protected SVGOMFEDiffuseLightingElement() {
    }

    public SVGOMFEDiffuseLightingElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }

    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }

    private void initializeLiveAttributes() {
        this.in = this.createLiveAnimatedString(null, "in");
        this.surfaceScale = this.createLiveAnimatedNumber(null, "surfaceScale", 1.0f);
        this.diffuseConstant = this.createLiveAnimatedNumber(null, "diffuseConstant", 1.0f);
    }

    public String getLocalName() {
        return "feDiffuseLighting";
    }

    public SVGAnimatedString getIn1() {
        return this.in;
    }

    public SVGAnimatedNumber getSurfaceScale() {
        return this.surfaceScale;
    }

    public SVGAnimatedNumber getDiffuseConstant() {
        return this.diffuseConstant;
    }

    public SVGAnimatedNumber getKernelUnitLengthX() {
        throw new UnsupportedOperationException("SVGFEDiffuseLightingElement.getKernelUnitLengthX is not implemented");
    }

    public SVGAnimatedNumber getKernelUnitLengthY() {
        throw new UnsupportedOperationException("SVGFEDiffuseLightingElement.getKernelUnitLengthY is not implemented");
    }

    protected Node newNode() {
        return new SVGOMFEDiffuseLightingElement();
    }

    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return xmlTraitInformation;
    }

    static {
        DoublyIndexedTable t = new DoublyIndexedTable(SVGOMFilterPrimitiveStandardAttributes.xmlTraitInformation);
        t.put(null, (Object)"in", (Object)new TraitInformation(true, 16));
        t.put(null, (Object)"surfaceScale", (Object)new TraitInformation(true, 2));
        t.put(null, (Object)"diffuseConstant", (Object)new TraitInformation(true, 2));
        t.put(null, (Object)"kernelUnitLength", (Object)new TraitInformation(true, 4));
        xmlTraitInformation = t;
    }
}

