/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.dom.svg.SVGZoomAndPanSupport
 *  org.apache.batik.util.DoublyIndexedTable
 *  org.w3c.dom.svg.SVGAnimatedBoolean
 *  org.w3c.dom.svg.SVGAnimatedPreserveAspectRatio
 *  org.w3c.dom.svg.SVGAnimatedRect
 *  org.w3c.dom.svg.SVGStringList
 *  org.w3c.dom.svg.SVGViewElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.AttributeInitializer;
import org.apache.batik.anim.dom.SVGOMAnimatedBoolean;
import org.apache.batik.anim.dom.SVGOMAnimatedPreserveAspectRatio;
import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.anim.dom.TraitInformation;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.svg.SVGZoomAndPanSupport;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedPreserveAspectRatio;
import org.w3c.dom.svg.SVGAnimatedRect;
import org.w3c.dom.svg.SVGStringList;
import org.w3c.dom.svg.SVGViewElement;

public class SVGOMViewElement
extends SVGOMElement
implements SVGViewElement {
    protected static DoublyIndexedTable xmlTraitInformation;
    protected static final AttributeInitializer attributeInitializer;
    protected SVGOMAnimatedBoolean externalResourcesRequired;
    protected SVGOMAnimatedPreserveAspectRatio preserveAspectRatio;

    protected SVGOMViewElement() {
    }

    public SVGOMViewElement(String prefix, AbstractDocument owner) {
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
        this.preserveAspectRatio = this.createLiveAnimatedPreserveAspectRatio();
    }

    public String getLocalName() {
        return "view";
    }

    public SVGStringList getViewTarget() {
        throw new UnsupportedOperationException("SVGViewElement.getViewTarget is not implemented");
    }

    public short getZoomAndPan() {
        return SVGZoomAndPanSupport.getZoomAndPan((Element)((Object)this));
    }

    public void setZoomAndPan(short val) {
        SVGZoomAndPanSupport.setZoomAndPan((Element)((Object)this), (short)val);
    }

    public SVGAnimatedRect getViewBox() {
        throw new UnsupportedOperationException("SVGFitToViewBox.getViewBox is not implemented");
    }

    public SVGAnimatedPreserveAspectRatio getPreserveAspectRatio() {
        return this.preserveAspectRatio;
    }

    public SVGAnimatedBoolean getExternalResourcesRequired() {
        return this.externalResourcesRequired;
    }

    @Override
    protected AttributeInitializer getAttributeInitializer() {
        return attributeInitializer;
    }

    protected Node newNode() {
        return new SVGOMViewElement();
    }

    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return xmlTraitInformation;
    }

    static {
        DoublyIndexedTable t = new DoublyIndexedTable(SVGOMElement.xmlTraitInformation);
        t.put(null, (Object)"preserveAspectRatio", (Object)new TraitInformation(true, 32));
        t.put(null, (Object)"viewBox", (Object)new TraitInformation(true, 13));
        t.put(null, (Object)"externalResourcesRequired", (Object)new TraitInformation(true, 49));
        xmlTraitInformation = t;
        attributeInitializer = new AttributeInitializer(2);
        attributeInitializer.addAttribute(null, null, "preserveAspectRatio", "xMidYMid meet");
        attributeInitializer.addAttribute(null, null, "zoomAndPan", "magnify");
    }
}

