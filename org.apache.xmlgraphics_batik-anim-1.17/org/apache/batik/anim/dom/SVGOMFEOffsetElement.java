/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.util.DoublyIndexedTable
 *  org.w3c.dom.svg.SVGAnimatedNumber
 *  org.w3c.dom.svg.SVGAnimatedString
 *  org.w3c.dom.svg.SVGFEOffsetElement
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
import org.w3c.dom.svg.SVGFEOffsetElement;

public class SVGOMFEOffsetElement
extends SVGOMFilterPrimitiveStandardAttributes
implements SVGFEOffsetElement {
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedString in;
    protected SVGOMAnimatedNumber dx;
    protected SVGOMAnimatedNumber dy;

    protected SVGOMFEOffsetElement() {
    }

    public SVGOMFEOffsetElement(String prefix, AbstractDocument owner) {
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
        this.dx = this.createLiveAnimatedNumber(null, "dx", 0.0f);
        this.dy = this.createLiveAnimatedNumber(null, "dy", 0.0f);
    }

    public String getLocalName() {
        return "feOffset";
    }

    public SVGAnimatedString getIn1() {
        return this.in;
    }

    public SVGAnimatedNumber getDx() {
        return this.dx;
    }

    public SVGAnimatedNumber getDy() {
        return this.dy;
    }

    protected Node newNode() {
        return new SVGOMFEOffsetElement();
    }

    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return xmlTraitInformation;
    }

    static {
        DoublyIndexedTable t = new DoublyIndexedTable(SVGOMFilterPrimitiveStandardAttributes.xmlTraitInformation);
        t.put(null, (Object)"in", (Object)new TraitInformation(true, 16));
        t.put(null, (Object)"dx", (Object)new TraitInformation(true, 2));
        t.put(null, (Object)"dy", (Object)new TraitInformation(true, 2));
        xmlTraitInformation = t;
    }
}

