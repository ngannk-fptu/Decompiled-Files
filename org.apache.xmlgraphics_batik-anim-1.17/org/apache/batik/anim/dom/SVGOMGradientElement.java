/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.util.DoublyIndexedTable
 *  org.w3c.dom.svg.SVGAnimatedBoolean
 *  org.w3c.dom.svg.SVGAnimatedEnumeration
 *  org.w3c.dom.svg.SVGAnimatedString
 *  org.w3c.dom.svg.SVGAnimatedTransformList
 *  org.w3c.dom.svg.SVGGradientElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.AttributeInitializer;
import org.apache.batik.anim.dom.SVGOMAElement;
import org.apache.batik.anim.dom.SVGOMAnimatedBoolean;
import org.apache.batik.anim.dom.SVGOMAnimatedEnumeration;
import org.apache.batik.anim.dom.SVGOMAnimatedString;
import org.apache.batik.anim.dom.SVGStylableElement;
import org.apache.batik.anim.dom.TraitInformation;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedBoolean;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGAnimatedTransformList;
import org.w3c.dom.svg.SVGGradientElement;

public abstract class SVGOMGradientElement
extends SVGStylableElement
implements SVGGradientElement {
    protected static DoublyIndexedTable xmlTraitInformation;
    protected static final AttributeInitializer attributeInitializer;
    protected static final String[] UNITS_VALUES;
    protected static final String[] SPREAD_METHOD_VALUES;
    protected SVGOMAnimatedEnumeration gradientUnits;
    protected SVGOMAnimatedEnumeration spreadMethod;
    protected SVGOMAnimatedString href;
    protected SVGOMAnimatedBoolean externalResourcesRequired;

    protected SVGOMGradientElement() {
    }

    protected SVGOMGradientElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }

    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }

    private void initializeLiveAttributes() {
        this.gradientUnits = this.createLiveAnimatedEnumeration(null, "gradientUnits", UNITS_VALUES, (short)2);
        this.spreadMethod = this.createLiveAnimatedEnumeration(null, "spreadMethod", SPREAD_METHOD_VALUES, (short)1);
        this.href = this.createLiveAnimatedString("http://www.w3.org/1999/xlink", "href");
        this.externalResourcesRequired = this.createLiveAnimatedBoolean(null, "externalResourcesRequired", false);
    }

    public SVGAnimatedTransformList getGradientTransform() {
        throw new UnsupportedOperationException("SVGGradientElement.getGradientTransform is not implemented");
    }

    public SVGAnimatedEnumeration getGradientUnits() {
        return this.gradientUnits;
    }

    public SVGAnimatedEnumeration getSpreadMethod() {
        return this.spreadMethod;
    }

    public SVGAnimatedString getHref() {
        return this.href;
    }

    public SVGAnimatedBoolean getExternalResourcesRequired() {
        return this.externalResourcesRequired;
    }

    @Override
    protected AttributeInitializer getAttributeInitializer() {
        return attributeInitializer;
    }

    protected Node newNode() {
        return new SVGOMAElement();
    }

    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return xmlTraitInformation;
    }

    static {
        DoublyIndexedTable t = new DoublyIndexedTable(SVGStylableElement.xmlTraitInformation);
        t.put(null, (Object)"gradientUnits", (Object)new TraitInformation(true, 15));
        t.put(null, (Object)"spreadMethod", (Object)new TraitInformation(true, 15));
        t.put(null, (Object)"gradientTransform", (Object)new TraitInformation(true, 9));
        t.put(null, (Object)"externalResourcesRequired", (Object)new TraitInformation(true, 49));
        t.put((Object)"http://www.w3.org/1999/xlink", (Object)"href", (Object)new TraitInformation(true, 10));
        xmlTraitInformation = t;
        attributeInitializer = new AttributeInitializer(4);
        attributeInitializer.addAttribute("http://www.w3.org/2000/xmlns/", null, "xmlns:xlink", "http://www.w3.org/1999/xlink");
        attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "type", "simple");
        attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "show", "other");
        attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "actuate", "onLoad");
        UNITS_VALUES = new String[]{"", "userSpaceOnUse", "objectBoundingBox"};
        SPREAD_METHOD_VALUES = new String[]{"", "pad", "reflect", "repeat"};
    }
}

