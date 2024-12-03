/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.w3c.dom.svg.SVGAltGlyphElement
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.AttributeInitializer;
import org.apache.batik.anim.dom.SVGURIReferenceTextPositioningElement;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.svg.SVGAltGlyphElement;

public class SVGOMAltGlyphElement
extends SVGURIReferenceTextPositioningElement
implements SVGAltGlyphElement {
    protected static final AttributeInitializer attributeInitializer = new AttributeInitializer(4);

    protected SVGOMAltGlyphElement() {
    }

    public SVGOMAltGlyphElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    public String getLocalName() {
        return "altGlyph";
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

    @Override
    protected AttributeInitializer getAttributeInitializer() {
        return attributeInitializer;
    }

    protected Node newNode() {
        return new SVGOMAltGlyphElement();
    }

    static {
        attributeInitializer.addAttribute("http://www.w3.org/2000/xmlns/", null, "xmlns:xlink", "http://www.w3.org/1999/xlink");
        attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "type", "simple");
        attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "show", "other");
        attributeInitializer.addAttribute("http://www.w3.org/1999/xlink", "xlink", "actuate", "onLoad");
    }
}

