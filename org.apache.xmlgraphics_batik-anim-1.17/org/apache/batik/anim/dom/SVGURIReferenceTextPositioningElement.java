/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.util.DoublyIndexedTable
 *  org.w3c.dom.svg.SVGAnimatedString
 *  org.w3c.dom.svg.SVGURIReference
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGOMAnimatedString;
import org.apache.batik.anim.dom.SVGOMTextPositioningElement;
import org.apache.batik.anim.dom.TraitInformation;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGURIReference;

public abstract class SVGURIReferenceTextPositioningElement
extends SVGOMTextPositioningElement
implements SVGURIReference {
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedString href;

    protected SVGURIReferenceTextPositioningElement() {
    }

    protected SVGURIReferenceTextPositioningElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }

    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }

    private void initializeLiveAttributes() {
        this.href = this.createLiveAnimatedString("http://www.w3.org/1999/xlink", "href");
    }

    public SVGAnimatedString getHref() {
        return this.href;
    }

    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return xmlTraitInformation;
    }

    static {
        DoublyIndexedTable t = new DoublyIndexedTable(SVGOMTextPositioningElement.xmlTraitInformation);
        t.put((Object)"http://www.w3.org/1999/xlink", (Object)"href", (Object)new TraitInformation(true, 10));
        xmlTraitInformation = t;
    }
}

