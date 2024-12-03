/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.dom.util.XMLSupport
 *  org.apache.batik.util.DoublyIndexedTable
 *  org.w3c.dom.svg.SVGAnimatedBoolean
 *  org.w3c.dom.svg.SVGAnimatedEnumeration
 *  org.w3c.dom.svg.SVGAnimatedInteger
 *  org.w3c.dom.svg.SVGAnimatedLength
 *  org.w3c.dom.svg.SVGAnimatedString
 *  org.w3c.dom.svg.SVGFilterElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.AttributeInitializer;
import org.apache.batik.anim.dom.SVGOMAnimatedBoolean;
import org.apache.batik.anim.dom.SVGOMAnimatedEnumeration;
import org.apache.batik.anim.dom.SVGOMAnimatedLength;
import org.apache.batik.anim.dom.SVGOMAnimatedString;
import org.apache.batik.anim.dom.SVGStylableElement;
import org.apache.batik.anim.dom.TraitInformation;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.util.XMLSupport;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedInteger;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGFilterElement;

public class SVGOMFilterElement
extends SVGStylableElement
implements SVGFilterElement {
    protected static DoublyIndexedTable xmlTraitInformation;
    protected static final AttributeInitializer attributeInitializer;
    protected static final String[] UNITS_VALUES;
    protected SVGOMAnimatedEnumeration filterUnits;
    protected SVGOMAnimatedEnumeration primitiveUnits;
    protected SVGOMAnimatedLength x;
    protected SVGOMAnimatedLength y;
    protected SVGOMAnimatedLength width;
    protected SVGOMAnimatedLength height;
    protected SVGOMAnimatedString href;
    protected SVGOMAnimatedBoolean externalResourcesRequired;

    protected SVGOMFilterElement() {
    }

    public SVGOMFilterElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }

    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }

    private void initializeLiveAttributes() {
        this.filterUnits = this.createLiveAnimatedEnumeration(null, "filterUnits", UNITS_VALUES, (short)2);
        this.primitiveUnits = this.createLiveAnimatedEnumeration(null, "primitiveUnits", UNITS_VALUES, (short)1);
        this.x = this.createLiveAnimatedLength(null, "x", "-10%", (short)2, false);
        this.y = this.createLiveAnimatedLength(null, "y", "-10%", (short)1, false);
        this.width = this.createLiveAnimatedLength(null, "width", "120%", (short)2, true);
        this.height = this.createLiveAnimatedLength(null, "height", "120%", (short)1, true);
        this.href = this.createLiveAnimatedString("http://www.w3.org/1999/xlink", "href");
        this.externalResourcesRequired = this.createLiveAnimatedBoolean(null, "externalResourcesRequired", false);
    }

    public String getLocalName() {
        return "filter";
    }

    public SVGAnimatedEnumeration getFilterUnits() {
        return this.filterUnits;
    }

    public SVGAnimatedEnumeration getPrimitiveUnits() {
        return this.primitiveUnits;
    }

    public SVGAnimatedLength getX() {
        return this.x;
    }

    public SVGAnimatedLength getY() {
        return this.y;
    }

    public SVGAnimatedLength getWidth() {
        return this.width;
    }

    public SVGAnimatedLength getHeight() {
        return this.height;
    }

    public SVGAnimatedInteger getFilterResX() {
        throw new UnsupportedOperationException("SVGFilterElement.getFilterResX is not implemented");
    }

    public SVGAnimatedInteger getFilterResY() {
        throw new UnsupportedOperationException("SVGFilterElement.getFilterResY is not implemented");
    }

    public void setFilterRes(int filterResX, int filterResY) {
        throw new UnsupportedOperationException("SVGFilterElement.setFilterRes is not implemented");
    }

    public SVGAnimatedString getHref() {
        return this.href;
    }

    public SVGAnimatedBoolean getExternalResourcesRequired() {
        return this.externalResourcesRequired;
    }

    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return xmlTraitInformation;
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
        return new SVGOMFilterElement();
    }

    static {
        DoublyIndexedTable t = new DoublyIndexedTable(SVGStylableElement.xmlTraitInformation);
        t.put(null, (Object)"filterUnits", (Object)new TraitInformation(true, 15));
        t.put(null, (Object)"primitiveUnits", (Object)new TraitInformation(true, 15));
        t.put(null, (Object)"x", (Object)new TraitInformation(true, 3, 1));
        t.put(null, (Object)"y", (Object)new TraitInformation(true, 3, 2));
        t.put(null, (Object)"width", (Object)new TraitInformation(true, 3, 1));
        t.put(null, (Object)"height", (Object)new TraitInformation(true, 3, 2));
        t.put(null, (Object)"filterRes", (Object)new TraitInformation(true, 4));
        xmlTraitInformation = t;
        attributeInitializer = new AttributeInitializer(4);
        attributeInitializer.addAttribute("http://www.w3.org/2000/xmlns/", null, "xmlns:xlink", "http://www.w3.org/1999/xlink");
        attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "type", "simple");
        attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "show", "other");
        attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "actuate", "onLoad");
        UNITS_VALUES = new String[]{"", "userSpaceOnUse", "objectBoundingBox"};
    }
}

