/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.dom.svg.SVGZoomAndPanSupport
 *  org.apache.batik.dom.util.XMLSupport
 *  org.apache.batik.util.DoublyIndexedTable
 *  org.w3c.dom.svg.SVGAnimatedBoolean
 *  org.w3c.dom.svg.SVGAnimatedPreserveAspectRatio
 *  org.w3c.dom.svg.SVGAnimatedRect
 *  org.w3c.dom.svg.SVGSymbolElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.AttributeInitializer;
import org.apache.batik.anim.dom.SVGOMAnimatedBoolean;
import org.apache.batik.anim.dom.SVGOMAnimatedPreserveAspectRatio;
import org.apache.batik.anim.dom.SVGStylableElement;
import org.apache.batik.anim.dom.TraitInformation;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.svg.SVGZoomAndPanSupport;
import org.apache.batik.dom.util.XMLSupport;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedPreserveAspectRatio;
import org.w3c.dom.svg.SVGAnimatedRect;
import org.w3c.dom.svg.SVGSymbolElement;

public class SVGOMSymbolElement
extends SVGStylableElement
implements SVGSymbolElement {
    protected static DoublyIndexedTable xmlTraitInformation;
    protected SVGOMAnimatedPreserveAspectRatio preserveAspectRatio;
    protected static final AttributeInitializer attributeInitializer;
    protected SVGOMAnimatedBoolean externalResourcesRequired;

    protected SVGOMSymbolElement() {
    }

    public SVGOMSymbolElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }

    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }

    private void initializeLiveAttributes() {
        this.preserveAspectRatio = this.createLiveAnimatedPreserveAspectRatio();
    }

    public String getLocalName() {
        return "symbol";
    }

    public String getXMLlang() {
        return XMLSupport.getXMLLang((Element)((Object)this));
    }

    public void setXMLlang(String lang) {
        this.setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:lang", lang);
    }

    public String getXMLspace() {
        return XMLSupport.getXMLSpace((Element)((Object)this));
    }

    public void setXMLspace(String space) {
        this.setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:space", space);
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
        return new SVGOMSymbolElement();
    }

    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return xmlTraitInformation;
    }

    static {
        DoublyIndexedTable t = new DoublyIndexedTable(SVGStylableElement.xmlTraitInformation);
        t.put(null, (Object)"externalResourcesRequired", (Object)new TraitInformation(true, 49));
        t.put(null, (Object)"preserveAspectRatio", (Object)new TraitInformation(true, 32));
        t.put(null, (Object)"viewBox", (Object)new TraitInformation(true, 13));
        xmlTraitInformation = t;
        attributeInitializer = new AttributeInitializer(1);
        attributeInitializer.addAttribute(null, null, "preserveAspectRatio", "xMidYMid meet");
    }
}

