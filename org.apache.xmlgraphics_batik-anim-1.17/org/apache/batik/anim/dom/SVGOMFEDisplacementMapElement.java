/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.util.DoublyIndexedTable
 *  org.w3c.dom.svg.SVGAnimatedEnumeration
 *  org.w3c.dom.svg.SVGAnimatedNumber
 *  org.w3c.dom.svg.SVGAnimatedString
 *  org.w3c.dom.svg.SVGFEDisplacementMapElement
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
import org.w3c.dom.svg.SVGFEDisplacementMapElement;

public class SVGOMFEDisplacementMapElement
extends SVGOMFilterPrimitiveStandardAttributes
implements SVGFEDisplacementMapElement {
    protected static DoublyIndexedTable xmlTraitInformation;
    protected static final String[] CHANNEL_SELECTOR_VALUES;
    protected SVGOMAnimatedString in;
    protected SVGOMAnimatedString in2;
    protected SVGOMAnimatedNumber scale;
    protected SVGOMAnimatedEnumeration xChannelSelector;
    protected SVGOMAnimatedEnumeration yChannelSelector;

    protected SVGOMFEDisplacementMapElement() {
    }

    public SVGOMFEDisplacementMapElement(String prefix, AbstractDocument owner) {
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
        this.scale = this.createLiveAnimatedNumber(null, "scale", 0.0f);
        this.xChannelSelector = this.createLiveAnimatedEnumeration(null, "xChannelSelector", CHANNEL_SELECTOR_VALUES, (short)4);
        this.yChannelSelector = this.createLiveAnimatedEnumeration(null, "yChannelSelector", CHANNEL_SELECTOR_VALUES, (short)4);
    }

    public String getLocalName() {
        return "feDisplacementMap";
    }

    public SVGAnimatedString getIn1() {
        return this.in;
    }

    public SVGAnimatedString getIn2() {
        return this.in2;
    }

    public SVGAnimatedNumber getScale() {
        return this.scale;
    }

    public SVGAnimatedEnumeration getXChannelSelector() {
        return this.xChannelSelector;
    }

    public SVGAnimatedEnumeration getYChannelSelector() {
        return this.yChannelSelector;
    }

    protected Node newNode() {
        return new SVGOMFEDisplacementMapElement();
    }

    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return xmlTraitInformation;
    }

    static {
        DoublyIndexedTable t = new DoublyIndexedTable(SVGOMFilterPrimitiveStandardAttributes.xmlTraitInformation);
        t.put(null, (Object)"in", (Object)new TraitInformation(true, 16));
        t.put(null, (Object)"in2", (Object)new TraitInformation(true, 16));
        t.put(null, (Object)"scale", (Object)new TraitInformation(true, 2));
        t.put(null, (Object)"xChannelSelector", (Object)new TraitInformation(true, 15));
        t.put(null, (Object)"yChannelSelector", (Object)new TraitInformation(true, 15));
        xmlTraitInformation = t;
        CHANNEL_SELECTOR_VALUES = new String[]{"", "R", "G", "B", "A"};
    }
}

