/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.util.DoublyIndexedTable
 *  org.w3c.dom.svg.SVGAnimatedEnumeration
 *  org.w3c.dom.svg.SVGAnimatedNumber
 *  org.w3c.dom.svg.SVGAnimatedString
 *  org.w3c.dom.svg.SVGFEMorphologyElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGOMAnimatedEnumeration;
import org.apache.batik.anim.dom.SVGOMAnimatedString;
import org.apache.batik.anim.dom.SVGOMFilterPrimitiveStandardAttributes;
import org.apache.batik.anim.dom.TraitInformation;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGFEMorphologyElement;

public class SVGOMFEMorphologyElement
extends SVGOMFilterPrimitiveStandardAttributes
implements SVGFEMorphologyElement {
    protected static DoublyIndexedTable xmlTraitInformation;
    protected static final String[] OPERATOR_VALUES;
    protected SVGOMAnimatedString in;
    protected SVGOMAnimatedEnumeration operator;

    protected SVGOMFEMorphologyElement() {
    }

    public SVGOMFEMorphologyElement(String prefix, AbstractDocument owner) {
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
        this.operator = this.createLiveAnimatedEnumeration(null, "operator", OPERATOR_VALUES, (short)1);
    }

    public String getLocalName() {
        return "feMorphology";
    }

    public SVGAnimatedString getIn1() {
        return this.in;
    }

    public SVGAnimatedEnumeration getOperator() {
        return this.operator;
    }

    public SVGAnimatedNumber getRadiusX() {
        throw new UnsupportedOperationException("SVGFEMorphologyElement.getRadiusX is not implemented");
    }

    public SVGAnimatedNumber getRadiusY() {
        throw new UnsupportedOperationException("SVGFEMorphologyElement.getRadiusY is not implemented");
    }

    protected Node newNode() {
        return new SVGOMFEMorphologyElement();
    }

    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return xmlTraitInformation;
    }

    static {
        DoublyIndexedTable t = new DoublyIndexedTable(SVGOMFilterPrimitiveStandardAttributes.xmlTraitInformation);
        t.put(null, (Object)"in", (Object)new TraitInformation(true, 16));
        t.put(null, (Object)"operator", (Object)new TraitInformation(true, 15));
        t.put(null, (Object)"radius", (Object)new TraitInformation(true, 4));
        xmlTraitInformation = t;
        OPERATOR_VALUES = new String[]{"", "erode", "dilate"};
    }
}

