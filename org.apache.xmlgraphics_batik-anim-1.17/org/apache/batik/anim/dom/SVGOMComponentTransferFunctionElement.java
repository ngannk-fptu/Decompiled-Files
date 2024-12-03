/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.util.DoublyIndexedTable
 *  org.w3c.dom.svg.SVGAnimatedEnumeration
 *  org.w3c.dom.svg.SVGAnimatedNumber
 *  org.w3c.dom.svg.SVGAnimatedNumberList
 *  org.w3c.dom.svg.SVGComponentTransferFunctionElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGOMAnimatedEnumeration;
import org.apache.batik.anim.dom.SVGOMAnimatedNumber;
import org.apache.batik.anim.dom.SVGOMAnimatedNumberList;
import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.anim.dom.TraitInformation;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedNumberList;
import org.w3c.dom.svg.SVGComponentTransferFunctionElement;

public abstract class SVGOMComponentTransferFunctionElement
extends SVGOMElement
implements SVGComponentTransferFunctionElement {
    protected static DoublyIndexedTable xmlTraitInformation;
    protected static final String[] TYPE_VALUES;
    protected SVGOMAnimatedEnumeration type;
    protected SVGOMAnimatedNumberList tableValues;
    protected SVGOMAnimatedNumber slope;
    protected SVGOMAnimatedNumber intercept;
    protected SVGOMAnimatedNumber amplitude;
    protected SVGOMAnimatedNumber exponent;
    protected SVGOMAnimatedNumber offset;

    protected SVGOMComponentTransferFunctionElement() {
    }

    protected SVGOMComponentTransferFunctionElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }

    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }

    private void initializeLiveAttributes() {
        this.type = this.createLiveAnimatedEnumeration(null, "type", TYPE_VALUES, (short)1);
        this.tableValues = this.createLiveAnimatedNumberList(null, "tableValues", "", false);
        this.slope = this.createLiveAnimatedNumber(null, "slope", 1.0f);
        this.intercept = this.createLiveAnimatedNumber(null, "intercept", 0.0f);
        this.amplitude = this.createLiveAnimatedNumber(null, "amplitude", 1.0f);
        this.exponent = this.createLiveAnimatedNumber(null, "exponent", 1.0f);
        this.offset = this.createLiveAnimatedNumber(null, "exponent", 0.0f);
    }

    public SVGAnimatedEnumeration getType() {
        return this.type;
    }

    public SVGAnimatedNumberList getTableValues() {
        throw new UnsupportedOperationException("SVGComponentTransferFunctionElement.getTableValues is not implemented");
    }

    public SVGAnimatedNumber getSlope() {
        return this.slope;
    }

    public SVGAnimatedNumber getIntercept() {
        return this.intercept;
    }

    public SVGAnimatedNumber getAmplitude() {
        return this.amplitude;
    }

    public SVGAnimatedNumber getExponent() {
        return this.exponent;
    }

    public SVGAnimatedNumber getOffset() {
        return this.offset;
    }

    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return xmlTraitInformation;
    }

    static {
        DoublyIndexedTable t = new DoublyIndexedTable(SVGOMElement.xmlTraitInformation);
        t.put(null, (Object)"type", (Object)new TraitInformation(true, 15));
        t.put(null, (Object)"tableValues", (Object)new TraitInformation(true, 13));
        t.put(null, (Object)"slope", (Object)new TraitInformation(true, 2));
        t.put(null, (Object)"intercept", (Object)new TraitInformation(true, 2));
        t.put(null, (Object)"amplitude", (Object)new TraitInformation(true, 2));
        t.put(null, (Object)"exponent", (Object)new TraitInformation(true, 2));
        t.put(null, (Object)"offset", (Object)new TraitInformation(true, 2));
        xmlTraitInformation = t;
        TYPE_VALUES = new String[]{"", "identity", "table", "discrete", "linear", "gamma"};
    }
}

