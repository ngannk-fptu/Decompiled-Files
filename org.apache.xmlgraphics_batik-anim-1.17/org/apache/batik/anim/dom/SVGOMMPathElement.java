/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.util.DoublyIndexedTable
 *  org.w3c.dom.svg.SVGAnimatedBoolean
 *  org.w3c.dom.svg.SVGMPathElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.AttributeInitializer;
import org.apache.batik.anim.dom.SVGOMAnimatedBoolean;
import org.apache.batik.anim.dom.SVGOMURIReferenceElement;
import org.apache.batik.anim.dom.TraitInformation;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGMPathElement;

public class SVGOMMPathElement
extends SVGOMURIReferenceElement
implements SVGMPathElement {
    protected static DoublyIndexedTable xmlTraitInformation;
    protected static final AttributeInitializer attributeInitializer;
    protected SVGOMAnimatedBoolean externalResourcesRequired;

    protected SVGOMMPathElement() {
    }

    public SVGOMMPathElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }

    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }

    private void initializeLiveAttributes() {
        this.externalResourcesRequired = this.createLiveAnimatedBoolean(null, "externalResourcesRequired", false);
    }

    public String getLocalName() {
        return "mpath";
    }

    public SVGAnimatedBoolean getExternalResourcesRequired() {
        return this.externalResourcesRequired;
    }

    @Override
    protected AttributeInitializer getAttributeInitializer() {
        return attributeInitializer;
    }

    protected Node newNode() {
        return new SVGOMMPathElement();
    }

    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return xmlTraitInformation;
    }

    static {
        DoublyIndexedTable t = new DoublyIndexedTable(SVGOMURIReferenceElement.xmlTraitInformation);
        t.put(null, (Object)"externalResourcesRequired", (Object)new TraitInformation(true, 49));
        xmlTraitInformation = t;
        attributeInitializer = new AttributeInitializer(4);
        attributeInitializer.addAttribute("http://www.w3.org/2000/xmlns/", null, "xmlns:xlink", "http://www.w3.org/1999/xlink");
        attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "type", "simple");
        attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "show", "other");
        attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "actuate", "onLoad");
    }
}

