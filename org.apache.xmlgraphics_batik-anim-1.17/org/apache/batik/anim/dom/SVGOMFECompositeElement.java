/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.util.DoublyIndexedTable
 *  org.w3c.dom.svg.SVGAnimatedEnumeration
 *  org.w3c.dom.svg.SVGAnimatedNumber
 *  org.w3c.dom.svg.SVGAnimatedString
 *  org.w3c.dom.svg.SVGFECompositeElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGOMAnimatedEnumeration;
import org.apache.batik.anim.dom.SVGOMAnimatedNumber;
import org.apache.batik.anim.dom.SVGOMAnimatedString;
import org.apache.batik.anim.dom.SVGOMFilterPrimitiveStandardAttributes;
import org.apache.batik.anim.dom.TraitInformation;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGFECompositeElement;

public class SVGOMFECompositeElement
extends SVGOMFilterPrimitiveStandardAttributes
implements SVGFECompositeElement {
    protected static DoublyIndexedTable xmlTraitInformation;
    protected static final String[] OPERATOR_VALUES;
    protected SVGOMAnimatedString in;
    protected SVGOMAnimatedString in2;
    protected SVGOMAnimatedEnumeration operator;
    protected SVGOMAnimatedNumber k1;
    protected SVGOMAnimatedNumber k2;
    protected SVGOMAnimatedNumber k3;
    protected SVGOMAnimatedNumber k4;

    protected SVGOMFECompositeElement() {
    }

    public SVGOMFECompositeElement(String prefix, AbstractDocument owner) {
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
        this.in2 = this.createLiveAnimatedString(null, "in2");
        this.operator = this.createLiveAnimatedEnumeration(null, "operator", OPERATOR_VALUES, (short)1);
        this.k1 = this.createLiveAnimatedNumber(null, "k1", 0.0f);
        this.k2 = this.createLiveAnimatedNumber(null, "k2", 0.0f);
        this.k3 = this.createLiveAnimatedNumber(null, "k3", 0.0f);
        this.k4 = this.createLiveAnimatedNumber(null, "k4", 0.0f);
    }

    public String getLocalName() {
        return "feComposite";
    }

    public SVGAnimatedString getIn1() {
        return this.in;
    }

    public SVGAnimatedString getIn2() {
        return this.in2;
    }

    public SVGAnimatedEnumeration getOperator() {
        return this.operator;
    }

    public SVGAnimatedNumber getK1() {
        return this.k1;
    }

    public SVGAnimatedNumber getK2() {
        return this.k2;
    }

    public SVGAnimatedNumber getK3() {
        return this.k3;
    }

    public SVGAnimatedNumber getK4() {
        return this.k4;
    }

    protected Node newNode() {
        return new SVGOMFECompositeElement();
    }

    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return xmlTraitInformation;
    }

    static {
        DoublyIndexedTable t = new DoublyIndexedTable(SVGOMFilterPrimitiveStandardAttributes.xmlTraitInformation);
        t.put(null, (Object)"in", (Object)new TraitInformation(true, 16));
        t.put(null, (Object)"in2", (Object)new TraitInformation(true, 16));
        t.put(null, (Object)"operator", (Object)new TraitInformation(true, 15));
        t.put(null, (Object)"k1", (Object)new TraitInformation(true, 2));
        t.put(null, (Object)"k2", (Object)new TraitInformation(true, 2));
        t.put(null, (Object)"k3", (Object)new TraitInformation(true, 2));
        t.put(null, (Object)"k4", (Object)new TraitInformation(true, 2));
        xmlTraitInformation = t;
        OPERATOR_VALUES = new String[]{"", "over", "in", "out", "atop", "xor", "arithmetic"};
    }
}

