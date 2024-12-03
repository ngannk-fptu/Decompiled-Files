/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.w3c.dom.svg.SVGAnimatedString
 *  org.w3c.dom.svg.SVGGlyphRefElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.AttributeInitializer;
import org.apache.batik.anim.dom.SVGOMAnimatedString;
import org.apache.batik.anim.dom.SVGStylableElement;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAnimatedString;
import org.w3c.dom.svg.SVGGlyphRefElement;

public class SVGOMGlyphRefElement
extends SVGStylableElement
implements SVGGlyphRefElement {
    protected static final AttributeInitializer attributeInitializer = new AttributeInitializer(4);
    protected SVGOMAnimatedString href;

    protected SVGOMGlyphRefElement() {
    }

    public SVGOMGlyphRefElement(String prefix, AbstractDocument owner) {
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

    public String getLocalName() {
        return "glyphRef";
    }

    public SVGAnimatedString getHref() {
        return this.href;
    }

    public String getGlyphRef() {
        return this.getAttributeNS(null, "glyphRef");
    }

    public void setGlyphRef(String glyphRef) throws DOMException {
        this.setAttributeNS(null, "glyphRef", glyphRef);
    }

    public String getFormat() {
        return this.getAttributeNS(null, "format");
    }

    public void setFormat(String format) throws DOMException {
        this.setAttributeNS(null, "format", format);
    }

    public float getX() {
        return Float.parseFloat(this.getAttributeNS(null, "x"));
    }

    public void setX(float x) throws DOMException {
        this.setAttributeNS(null, "x", String.valueOf(x));
    }

    public float getY() {
        return Float.parseFloat(this.getAttributeNS(null, "y"));
    }

    public void setY(float y) throws DOMException {
        this.setAttributeNS(null, "y", String.valueOf(y));
    }

    public float getDx() {
        return Float.parseFloat(this.getAttributeNS(null, "dx"));
    }

    public void setDx(float dx) throws DOMException {
        this.setAttributeNS(null, "dx", String.valueOf(dx));
    }

    public float getDy() {
        return Float.parseFloat(this.getAttributeNS(null, "dy"));
    }

    public void setDy(float dy) throws DOMException {
        this.setAttributeNS(null, "dy", String.valueOf(dy));
    }

    @Override
    protected AttributeInitializer getAttributeInitializer() {
        return attributeInitializer;
    }

    protected Node newNode() {
        return new SVGOMGlyphRefElement();
    }

    static {
        attributeInitializer.addAttribute("http://www.w3.org/2000/xmlns/", null, "xmlns:xlink", "http://www.w3.org/1999/xlink");
        attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "type", "simple");
        attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "show", "other");
        attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "actuate", "onLoad");
    }
}

