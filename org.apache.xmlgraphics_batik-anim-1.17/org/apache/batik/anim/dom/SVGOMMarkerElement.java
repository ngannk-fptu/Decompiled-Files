/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.dom.util.XMLSupport
 *  org.apache.batik.util.DoublyIndexedTable
 *  org.w3c.dom.svg.SVGAngle
 *  org.w3c.dom.svg.SVGAnimatedAngle
 *  org.w3c.dom.svg.SVGAnimatedBoolean
 *  org.w3c.dom.svg.SVGAnimatedEnumeration
 *  org.w3c.dom.svg.SVGAnimatedLength
 *  org.w3c.dom.svg.SVGAnimatedPreserveAspectRatio
 *  org.w3c.dom.svg.SVGAnimatedRect
 *  org.w3c.dom.svg.SVGMarkerElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.AttributeInitializer;
import org.apache.batik.anim.dom.SVGOMAnimatedBoolean;
import org.apache.batik.anim.dom.SVGOMAnimatedEnumeration;
import org.apache.batik.anim.dom.SVGOMAnimatedLength;
import org.apache.batik.anim.dom.SVGOMAnimatedMarkerOrientValue;
import org.apache.batik.anim.dom.SVGOMAnimatedPreserveAspectRatio;
import org.apache.batik.anim.dom.SVGOMAnimatedRect;
import org.apache.batik.anim.dom.SVGStylableElement;
import org.apache.batik.anim.dom.TraitInformation;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.util.XMLSupport;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAngle;
import org.w3c.dom.svg.SVGAnimatedAngle;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGAnimatedPreserveAspectRatio;
import org.w3c.dom.svg.SVGAnimatedRect;
import org.w3c.dom.svg.SVGMarkerElement;

public class SVGOMMarkerElement
extends SVGStylableElement
implements SVGMarkerElement {
    protected static DoublyIndexedTable xmlTraitInformation;
    protected static final AttributeInitializer attributeInitializer;
    protected static final String[] UNITS_VALUES;
    protected static final String[] ORIENT_TYPE_VALUES;
    protected SVGOMAnimatedLength refX;
    protected SVGOMAnimatedLength refY;
    protected SVGOMAnimatedLength markerWidth;
    protected SVGOMAnimatedLength markerHeight;
    protected SVGOMAnimatedMarkerOrientValue orient;
    protected SVGOMAnimatedEnumeration markerUnits;
    protected SVGOMAnimatedPreserveAspectRatio preserveAspectRatio;
    protected SVGOMAnimatedRect viewBox;
    protected SVGOMAnimatedBoolean externalResourcesRequired;

    protected SVGOMMarkerElement() {
    }

    public SVGOMMarkerElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }

    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }

    private void initializeLiveAttributes() {
        this.refX = this.createLiveAnimatedLength(null, "refX", "0", (short)2, false);
        this.refY = this.createLiveAnimatedLength(null, "refY", "0", (short)1, false);
        this.markerWidth = this.createLiveAnimatedLength(null, "markerWidth", "3", (short)2, true);
        this.markerHeight = this.createLiveAnimatedLength(null, "markerHeight", "3", (short)1, true);
        this.orient = this.createLiveAnimatedMarkerOrientValue(null, "orient");
        this.markerUnits = this.createLiveAnimatedEnumeration(null, "markerUnits", UNITS_VALUES, (short)2);
        this.preserveAspectRatio = this.createLiveAnimatedPreserveAspectRatio();
        this.viewBox = this.createLiveAnimatedRect(null, "viewBox", null);
        this.externalResourcesRequired = this.createLiveAnimatedBoolean(null, "externalResourcesRequired", false);
    }

    public String getLocalName() {
        return "marker";
    }

    public SVGAnimatedLength getRefX() {
        return this.refX;
    }

    public SVGAnimatedLength getRefY() {
        return this.refY;
    }

    public SVGAnimatedEnumeration getMarkerUnits() {
        return this.markerUnits;
    }

    public SVGAnimatedLength getMarkerWidth() {
        return this.markerWidth;
    }

    public SVGAnimatedLength getMarkerHeight() {
        return this.markerHeight;
    }

    public SVGAnimatedEnumeration getOrientType() {
        return this.orient.getAnimatedEnumeration();
    }

    public SVGAnimatedAngle getOrientAngle() {
        return this.orient.getAnimatedAngle();
    }

    public void setOrientToAuto() {
        this.setAttributeNS(null, "orient", "auto");
    }

    public void setOrientToAngle(SVGAngle angle) {
        this.setAttributeNS(null, "orient", angle.getValueAsString());
    }

    public SVGAnimatedRect getViewBox() {
        return this.viewBox;
    }

    public SVGAnimatedPreserveAspectRatio getPreserveAspectRatio() {
        return this.preserveAspectRatio;
    }

    public SVGAnimatedBoolean getExternalResourcesRequired() {
        return this.externalResourcesRequired;
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

    @Override
    protected AttributeInitializer getAttributeInitializer() {
        return attributeInitializer;
    }

    protected Node newNode() {
        return new SVGOMMarkerElement();
    }

    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return xmlTraitInformation;
    }

    static {
        DoublyIndexedTable t = new DoublyIndexedTable(SVGStylableElement.xmlTraitInformation);
        t.put(null, (Object)"refX", (Object)new TraitInformation(true, 3, 1));
        t.put(null, (Object)"refY", (Object)new TraitInformation(true, 3, 2));
        t.put(null, (Object)"markerWidth", (Object)new TraitInformation(true, 3, 1));
        t.put(null, (Object)"markerHeight", (Object)new TraitInformation(true, 3, 2));
        t.put(null, (Object)"markerUnits", (Object)new TraitInformation(true, 15));
        t.put(null, (Object)"orient", (Object)new TraitInformation(true, 15));
        t.put(null, (Object)"preserveAspectRatio", (Object)new TraitInformation(true, 32));
        t.put(null, (Object)"externalResourcesRequired", (Object)new TraitInformation(true, 49));
        xmlTraitInformation = t;
        attributeInitializer = new AttributeInitializer(1);
        attributeInitializer.addAttribute(null, null, "preserveAspectRatio", "xMidYMid meet");
        UNITS_VALUES = new String[]{"", "userSpaceOnUse", "stroke-width"};
        ORIENT_TYPE_VALUES = new String[]{"", "auto", ""};
    }
}

