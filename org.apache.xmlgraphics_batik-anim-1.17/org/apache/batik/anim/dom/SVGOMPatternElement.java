/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.dom.svg.SVGTestsSupport
 *  org.apache.batik.dom.util.XMLSupport
 *  org.apache.batik.util.DoublyIndexedTable
 *  org.w3c.dom.svg.SVGAnimatedBoolean
 *  org.w3c.dom.svg.SVGAnimatedEnumeration
 *  org.w3c.dom.svg.SVGAnimatedLength
 *  org.w3c.dom.svg.SVGAnimatedPreserveAspectRatio
 *  org.w3c.dom.svg.SVGAnimatedRect
 *  org.w3c.dom.svg.SVGAnimatedString
 *  org.w3c.dom.svg.SVGAnimatedTransformList
 *  org.w3c.dom.svg.SVGPatternElement
 *  org.w3c.dom.svg.SVGStringList
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.AttributeInitializer;
import org.apache.batik.anim.dom.SVGOMAnimatedBoolean;
import org.apache.batik.anim.dom.SVGOMAnimatedEnumeration;
import org.apache.batik.anim.dom.SVGOMAnimatedLength;
import org.apache.batik.anim.dom.SVGOMAnimatedPreserveAspectRatio;
import org.apache.batik.anim.dom.SVGOMAnimatedString;
import org.apache.batik.anim.dom.SVGStylableElement;
import org.apache.batik.anim.dom.TraitInformation;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.svg.SVGTestsSupport;
import org.apache.batik.dom.util.XMLSupport;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGAnimatedPreserveAspectRatio;
import org.w3c.dom.svg.SVGAnimatedRect;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGAnimatedTransformList;
import org.w3c.dom.svg.SVGPatternElement;
import org.w3c.dom.svg.SVGStringList;

public class SVGOMPatternElement
extends SVGStylableElement
implements SVGPatternElement {
    protected static DoublyIndexedTable xmlTraitInformation;
    protected static final AttributeInitializer attributeInitializer;
    protected static final String[] UNITS_VALUES;
    protected SVGOMAnimatedLength x;
    protected SVGOMAnimatedLength y;
    protected SVGOMAnimatedLength width;
    protected SVGOMAnimatedLength height;
    protected SVGOMAnimatedEnumeration patternUnits;
    protected SVGOMAnimatedEnumeration patternContentUnits;
    protected SVGOMAnimatedString href;
    protected SVGOMAnimatedBoolean externalResourcesRequired;
    protected SVGOMAnimatedPreserveAspectRatio preserveAspectRatio;

    protected SVGOMPatternElement() {
    }

    public SVGOMPatternElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }

    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }

    private void initializeLiveAttributes() {
        this.x = this.createLiveAnimatedLength(null, "x", "0", (short)2, false);
        this.y = this.createLiveAnimatedLength(null, "y", "0", (short)1, false);
        this.width = this.createLiveAnimatedLength(null, "width", "0", (short)2, true);
        this.height = this.createLiveAnimatedLength(null, "height", "0", (short)1, true);
        this.patternUnits = this.createLiveAnimatedEnumeration(null, "patternUnits", UNITS_VALUES, (short)2);
        this.patternContentUnits = this.createLiveAnimatedEnumeration(null, "patternContentUnits", UNITS_VALUES, (short)1);
        this.href = this.createLiveAnimatedString("http://www.w3.org/1999/xlink", "href");
        this.externalResourcesRequired = this.createLiveAnimatedBoolean(null, "externalResourcesRequired", false);
        this.preserveAspectRatio = this.createLiveAnimatedPreserveAspectRatio();
    }

    public String getLocalName() {
        return "pattern";
    }

    public SVGAnimatedTransformList getPatternTransform() {
        throw new UnsupportedOperationException("SVGPatternElement.getPatternTransform is not implemented");
    }

    public SVGAnimatedEnumeration getPatternUnits() {
        return this.patternUnits;
    }

    public SVGAnimatedEnumeration getPatternContentUnits() {
        return this.patternContentUnits;
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

    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return xmlTraitInformation;
    }

    public SVGAnimatedString getHref() {
        return this.href;
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

    public SVGStringList getRequiredFeatures() {
        return SVGTestsSupport.getRequiredFeatures((Element)((Object)this));
    }

    public SVGStringList getRequiredExtensions() {
        return SVGTestsSupport.getRequiredExtensions((Element)((Object)this));
    }

    public SVGStringList getSystemLanguage() {
        return SVGTestsSupport.getSystemLanguage((Element)((Object)this));
    }

    public boolean hasExtension(String extension) {
        return SVGTestsSupport.hasExtension((Element)((Object)this), (String)extension);
    }

    @Override
    protected AttributeInitializer getAttributeInitializer() {
        return attributeInitializer;
    }

    protected Node newNode() {
        return new SVGOMPatternElement();
    }

    static {
        DoublyIndexedTable t = new DoublyIndexedTable(SVGStylableElement.xmlTraitInformation);
        t.put(null, (Object)"x", (Object)new TraitInformation(true, 3, 1));
        t.put(null, (Object)"y", (Object)new TraitInformation(true, 3, 2));
        t.put(null, (Object)"width", (Object)new TraitInformation(true, 3, 1));
        t.put(null, (Object)"height", (Object)new TraitInformation(true, 3, 2));
        t.put(null, (Object)"patternUnits", (Object)new TraitInformation(true, 15));
        t.put(null, (Object)"patternContentUnits", (Object)new TraitInformation(true, 15));
        t.put(null, (Object)"patternTransform", (Object)new TraitInformation(true, 9));
        t.put(null, (Object)"viewBox", (Object)new TraitInformation(true, 13));
        t.put(null, (Object)"preserveAspectRatio", (Object)new TraitInformation(true, 32));
        t.put(null, (Object)"externalResourcesRequired", (Object)new TraitInformation(true, 49));
        xmlTraitInformation = t;
        attributeInitializer = new AttributeInitializer(5);
        attributeInitializer.addAttribute(null, null, "preserveAspectRatio", "xMidYMid meet");
        attributeInitializer.addAttribute("http://www.w3.org/2000/xmlns/", null, "xmlns:xlink", "http://www.w3.org/1999/xlink");
        attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "type", "simple");
        attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "show", "other");
        attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "actuate", "onLoad");
        UNITS_VALUES = new String[]{"", "userSpaceOnUse", "objectBoundingBox"};
    }
}

