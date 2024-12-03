/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.util.DoublyIndexedTable
 *  org.w3c.dom.svg.SVGAnimatedEnumeration
 *  org.w3c.dom.svg.SVGAnimatedString
 *  org.w3c.dom.svg.SVGFEBlendElement
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
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGFEBlendElement;

public class SVGOMFEBlendElement
extends SVGOMFilterPrimitiveStandardAttributes
implements SVGFEBlendElement {
    protected static DoublyIndexedTable xmlTraitInformation;
    protected static final String[] MODE_VALUES;
    protected SVGOMAnimatedString in;
    protected SVGOMAnimatedString in2;
    protected SVGOMAnimatedEnumeration mode;

    protected SVGOMFEBlendElement() {
    }

    public SVGOMFEBlendElement(String prefix, AbstractDocument owner) {
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
        this.mode = this.createLiveAnimatedEnumeration(null, "mode", MODE_VALUES, (short)1);
    }

    public String getLocalName() {
        return "feBlend";
    }

    public SVGAnimatedString getIn1() {
        return this.in;
    }

    public SVGAnimatedString getIn2() {
        return this.in2;
    }

    public SVGAnimatedEnumeration getMode() {
        return this.mode;
    }

    protected Node newNode() {
        return new SVGOMFEBlendElement();
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
        MODE_VALUES = new String[]{"", "normal", "multiply", "screen", "darken", "lighten"};
    }
}

