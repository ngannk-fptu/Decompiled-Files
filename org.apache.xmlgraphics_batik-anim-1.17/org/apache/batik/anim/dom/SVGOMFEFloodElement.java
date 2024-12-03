/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.util.DoublyIndexedTable
 *  org.w3c.dom.svg.SVGAnimatedString
 *  org.w3c.dom.svg.SVGFEFloodElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGOMAnimatedString;
import org.apache.batik.anim.dom.SVGOMFilterPrimitiveStandardAttributes;
import org.apache.batik.anim.dom.TraitInformation;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGFEFloodElement;

public class SVGOMFEFloodElement
extends SVGOMFilterPrimitiveStandardAttributes
implements SVGFEFloodElement {
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedString in;

    protected SVGOMFEFloodElement() {
    }

    public SVGOMFEFloodElement(String prefix, AbstractDocument owner) {
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
    }

    public String getLocalName() {
        return "feFlood";
    }

    public SVGAnimatedString getIn1() {
        return this.in;
    }

    protected Node newNode() {
        return new SVGOMFEFloodElement();
    }

    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return xmlTraitInformation;
    }

    static {
        DoublyIndexedTable t = new DoublyIndexedTable(SVGOMFilterPrimitiveStandardAttributes.xmlTraitInformation);
        t.put(null, (Object)"in", (Object)new TraitInformation(true, 16));
        xmlTraitInformation = t;
    }
}

