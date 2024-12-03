/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.util.DoublyIndexedTable
 *  org.w3c.dom.svg.SVGAnimatedNumber
 *  org.w3c.dom.svg.SVGStopElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGOMAnimatedNumber;
import org.apache.batik.anim.dom.SVGStylableElement;
import org.apache.batik.anim.dom.TraitInformation;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedNumber;
import org.w3c.dom.svg.SVGStopElement;

public class SVGOMStopElement
extends SVGStylableElement
implements SVGStopElement {
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedNumber offset;

    protected SVGOMStopElement() {
    }

    public SVGOMStopElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }

    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }

    private void initializeLiveAttributes() {
        this.offset = this.createLiveAnimatedNumber(null, "offset", 0.0f, true);
    }

    public String getLocalName() {
        return "stop";
    }

    public SVGAnimatedNumber getOffset() {
        return this.offset;
    }

    protected Node newNode() {
        return new SVGOMStopElement();
    }

    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return xmlTraitInformation;
    }

    static {
        DoublyIndexedTable t = new DoublyIndexedTable(SVGStylableElement.xmlTraitInformation);
        t.put(null, (Object)"offset", (Object)new TraitInformation(true, 47));
        xmlTraitInformation = t;
    }
}

