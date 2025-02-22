/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.gvt.font.GVTFont
 *  org.apache.batik.gvt.font.GVTFontFace
 *  org.apache.batik.gvt.font.GVTFontFamily
 *  org.apache.batik.gvt.text.GVTAttributedCharacterIterator$TextAttribute
 */
package org.apache.batik.bridge;

import java.lang.ref.SoftReference;
import java.text.AttributedCharacterIterator;
import java.util.Map;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.GraphicsNodeBridge;
import org.apache.batik.bridge.SVGFontElementBridge;
import org.apache.batik.gvt.font.GVTFont;
import org.apache.batik.gvt.font.GVTFontFace;
import org.apache.batik.gvt.font.GVTFontFamily;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SVGFontFamily
implements GVTFontFamily {
    public static final AttributedCharacterIterator.Attribute TEXT_COMPOUND_ID = GVTAttributedCharacterIterator.TextAttribute.TEXT_COMPOUND_ID;
    protected GVTFontFace fontFace;
    protected Element fontElement;
    protected BridgeContext ctx;
    protected Boolean complex = null;

    public SVGFontFamily(GVTFontFace fontFace, Element fontElement, BridgeContext ctx) {
        this.fontFace = fontFace;
        this.fontElement = fontElement;
        this.ctx = ctx;
    }

    public String getFamilyName() {
        return this.fontFace.getFamilyName();
    }

    public GVTFontFace getFontFace() {
        return this.fontFace;
    }

    public GVTFont deriveFont(float size, AttributedCharacterIterator aci) {
        return this.deriveFont(size, aci.getAttributes());
    }

    public GVTFont deriveFont(float size, Map attrs) {
        SVGFontElementBridge fontBridge = (SVGFontElementBridge)this.ctx.getBridge(this.fontElement);
        SoftReference sr = (SoftReference)attrs.get(TEXT_COMPOUND_ID);
        Element textElement = (Element)sr.get();
        return fontBridge.createFont(this.ctx, this.fontElement, textElement, size, this.fontFace);
    }

    public boolean isComplex() {
        if (this.complex != null) {
            return this.complex;
        }
        boolean ret = SVGFontFamily.isComplex(this.fontElement, this.ctx);
        this.complex = ret ? Boolean.TRUE : Boolean.FALSE;
        return ret;
    }

    public static boolean isComplex(Element fontElement, BridgeContext ctx) {
        NodeList glyphElements = fontElement.getElementsByTagNameNS("http://www.w3.org/2000/svg", "glyph");
        int numGlyphs = glyphElements.getLength();
        for (int i = 0; i < numGlyphs; ++i) {
            Element glyph = (Element)glyphElements.item(i);
            for (Node child = glyph.getFirstChild(); child != null; child = child.getNextSibling()) {
                Element e;
                Bridge b;
                if (child.getNodeType() != 1 || (b = ctx.getBridge(e = (Element)child)) == null || !(b instanceof GraphicsNodeBridge)) continue;
                return true;
            }
        }
        return false;
    }
}

