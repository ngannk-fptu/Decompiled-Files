/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.util.DoublyIndexedTable
 *  org.w3c.dom.svg.SVGAnimatedEnumeration
 *  org.w3c.dom.svg.SVGAnimatedLength
 *  org.w3c.dom.svg.SVGAnimatedString
 *  org.w3c.dom.svg.SVGTextPathElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.AttributeInitializer;
import org.apache.batik.anim.dom.SVGOMAnimatedEnumeration;
import org.apache.batik.anim.dom.SVGOMAnimatedLength;
import org.apache.batik.anim.dom.SVGOMAnimatedString;
import org.apache.batik.anim.dom.SVGOMTextContentElement;
import org.apache.batik.anim.dom.TraitInformation;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.util.DoublyIndexedTable;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedEnumeration;
import org.w3c.dom.svg.SVGAnimatedLength;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGTextPathElement;

public class SVGOMTextPathElement
extends SVGOMTextContentElement
implements SVGTextPathElement {
    protected static DoublyIndexedTable xmlTraitInformation;
    protected static final AttributeInitializer attributeInitializer;
    protected static final String[] METHOD_VALUES;
    protected static final String[] SPACING_VALUES;
    protected SVGOMAnimatedEnumeration method;
    protected SVGOMAnimatedEnumeration spacing;
    protected SVGOMAnimatedLength startOffset;
    protected SVGOMAnimatedString href;

    protected SVGOMTextPathElement() {
    }

    public SVGOMTextPathElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
        this.initializeLiveAttributes();
    }

    @Override
    protected void initializeAllLiveAttributes() {
        super.initializeAllLiveAttributes();
        this.initializeLiveAttributes();
    }

    private void initializeLiveAttributes() {
        this.method = this.createLiveAnimatedEnumeration(null, "method", METHOD_VALUES, (short)1);
        this.spacing = this.createLiveAnimatedEnumeration(null, "spacing", SPACING_VALUES, (short)2);
        this.startOffset = this.createLiveAnimatedLength(null, "startOffset", "0", (short)0, false);
        this.href = this.createLiveAnimatedString("http://www.w3.org/1999/xlink", "href");
    }

    public String getLocalName() {
        return "textPath";
    }

    public SVGAnimatedLength getStartOffset() {
        return this.startOffset;
    }

    public SVGAnimatedEnumeration getMethod() {
        return this.method;
    }

    public SVGAnimatedEnumeration getSpacing() {
        return this.spacing;
    }

    public SVGAnimatedString getHref() {
        return this.href;
    }

    @Override
    protected AttributeInitializer getAttributeInitializer() {
        return attributeInitializer;
    }

    protected Node newNode() {
        return new SVGOMTextPathElement();
    }

    @Override
    protected DoublyIndexedTable getTraitInformationTable() {
        return xmlTraitInformation;
    }

    static {
        DoublyIndexedTable t = new DoublyIndexedTable(SVGOMTextContentElement.xmlTraitInformation);
        t.put(null, (Object)"method", (Object)new TraitInformation(true, 15));
        t.put(null, (Object)"spacing", (Object)new TraitInformation(true, 15));
        t.put(null, (Object)"startOffset", (Object)new TraitInformation(true, 3));
        t.put((Object)"http://www.w3.org/1999/xlink", (Object)"href", (Object)new TraitInformation(true, 10));
        xmlTraitInformation = t;
        attributeInitializer = new AttributeInitializer(4);
        attributeInitializer.addAttribute("http://www.w3.org/2000/xmlns/", null, "xmlns:xlink", "http://www.w3.org/1999/xlink");
        attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "type", "simple");
        attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "show", "other");
        attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "actuate", "onLoad");
        METHOD_VALUES = new String[]{"", "align", "stretch"};
        SPACING_VALUES = new String[]{"", "auto", "exact"};
    }
}

