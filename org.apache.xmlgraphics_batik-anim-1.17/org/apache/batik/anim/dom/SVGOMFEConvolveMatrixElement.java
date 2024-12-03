/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.util.DoublyIndexedTable
 *  org.w3c.dom.svg.SVGAnimatedBoolean
 *  org.w3c.dom.svg.SVGAnimatedEnumeration
 *  org.w3c.dom.svg.SVGAnimatedInteger
 *  org.w3c.dom.svg.SVGAnimatedNumber
 *  org.w3c.dom.svg.SVGAnimatedNumberList
 *  org.w3c.dom.svg.SVGAnimatedString
 *  org.w3c.dom.svg.SVGFEConvolveMatrixElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGOMAnimatedBoolean;
import org.apache.batik.anim.dom.SVGOMAnimatedEnumeration;
import org.apache.batik.anim.dom.SVGOMAnimatedNumber;
import org.apache.batik.anim.dom.SVGOMAnimatedString;
import org.apache.batik.anim.dom.SVGOMFilterPrimitiveStandardAttributes;
import org.apache.batik.anim.dom.TraitInformation;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedInteger;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedNumberList;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGFEConvolveMatrixElement;

public class SVGOMFEConvolveMatrixElement
extends SVGOMFilterPrimitiveStandardAttributes
implements SVGFEConvolveMatrixElement {
    protected static DoublyIndexedTable xmlTraitInformation;
    protected static final String[] EDGE_MODE_VALUES;
    protected SVGOMAnimatedString in;
    protected SVGOMAnimatedEnumeration edgeMode;
    protected SVGOMAnimatedNumber bias;
    protected SVGOMAnimatedBoolean preserveAlpha;

    protected SVGOMFEConvolveMatrixElement() {
    }

    public SVGOMFEConvolveMatrixElement(String prefix, AbstractDocument owner) {
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
        this.edgeMode = this.createLiveAnimatedEnumeration(null, "edgeMode", EDGE_MODE_VALUES, (short)1);
        this.bias = this.createLiveAnimatedNumber(null, "bias", 0.0f);
        this.preserveAlpha = this.createLiveAnimatedBoolean(null, "preserveAlpha", false);
    }

    public String getLocalName() {
        return "feConvolveMatrix";
    }

    public SVGAnimatedString getIn1() {
        return this.in;
    }

    public SVGAnimatedEnumeration getEdgeMode() {
        return this.edgeMode;
    }

    public SVGAnimatedNumberList getKernelMatrix() {
        throw new UnsupportedOperationException("SVGFEConvolveMatrixElement.getKernelMatrix is not implemented");
    }

    public SVGAnimatedInteger getOrderX() {
        throw new UnsupportedOperationException("SVGFEConvolveMatrixElement.getOrderX is not implemented");
    }

    public SVGAnimatedInteger getOrderY() {
        throw new UnsupportedOperationException("SVGFEConvolveMatrixElement.getOrderY is not implemented");
    }

    public SVGAnimatedInteger getTargetX() {
        throw new UnsupportedOperationException("SVGFEConvolveMatrixElement.getTargetX is not implemented");
    }

    public SVGAnimatedInteger getTargetY() {
        throw new UnsupportedOperationException("SVGFEConvolveMatrixElement.getTargetY is not implemented");
    }

    public SVGAnimatedNumber getDivisor() {
        throw new UnsupportedOperationException("SVGFEConvolveMatrixElement.getDivisor is not implemented");
    }

    public SVGAnimatedNumber getBias() {
        return this.bias;
    }

    public SVGAnimatedNumber getKernelUnitLengthX() {
        throw new UnsupportedOperationException("SVGFEConvolveMatrixElement.getKernelUnitLengthX is not implemented");
    }

    public SVGAnimatedNumber getKernelUnitLengthY() {
        throw new UnsupportedOperationException("SVGFEConvolveMatrixElement.getKernelUnitLengthY is not implemented");
    }

    public SVGAnimatedBoolean getPreserveAlpha() {
        return this.preserveAlpha;
    }

    protected Node newNode() {
        return new SVGOMFEConvolveMatrixElement();
    }

    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return xmlTraitInformation;
    }

    static {
        DoublyIndexedTable t = new DoublyIndexedTable(SVGOMFilterPrimitiveStandardAttributes.xmlTraitInformation);
        t.put(null, (Object)"in", (Object)new TraitInformation(true, 16));
        t.put(null, (Object)"order", (Object)new TraitInformation(true, 4));
        t.put(null, (Object)"kernelUnitLength", (Object)new TraitInformation(true, 4));
        t.put(null, (Object)"kernelMatrix", (Object)new TraitInformation(true, 13));
        t.put(null, (Object)"divisor", (Object)new TraitInformation(true, 2));
        t.put(null, (Object)"bias", (Object)new TraitInformation(true, 2));
        t.put(null, (Object)"targetX", (Object)new TraitInformation(true, 1));
        t.put(null, (Object)"targetY", (Object)new TraitInformation(true, 1));
        t.put(null, (Object)"edgeMode", (Object)new TraitInformation(true, 15));
        t.put(null, (Object)"preserveAlpha", (Object)new TraitInformation(true, 49));
        xmlTraitInformation = t;
        EDGE_MODE_VALUES = new String[]{"", "duplicate", "wrap", "none"};
    }
}

