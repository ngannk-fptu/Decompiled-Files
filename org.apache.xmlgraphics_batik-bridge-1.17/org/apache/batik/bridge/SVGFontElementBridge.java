/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.gvt.font.GVTFontFace
 *  org.apache.batik.gvt.text.ArabicTextHandler
 */
package org.apache.batik.bridge;

import org.apache.batik.bridge.AbstractSVGBridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.SVGGVTFont;
import org.apache.batik.gvt.font.GVTFontFace;
import org.apache.batik.gvt.text.ArabicTextHandler;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class SVGFontElementBridge
extends AbstractSVGBridge {
    @Override
    public String getLocalName() {
        return "font";
    }

    public SVGGVTFont createFont(BridgeContext ctx, Element fontElement, Element textElement, float size, GVTFontFace fontFace) {
        NodeList glyphElements = fontElement.getElementsByTagNameNS("http://www.w3.org/2000/svg", "glyph");
        int numGlyphs = glyphElements.getLength();
        String[] glyphCodes = new String[numGlyphs];
        String[] glyphNames = new String[numGlyphs];
        String[] glyphLangs = new String[numGlyphs];
        String[] glyphOrientations = new String[numGlyphs];
        String[] glyphForms = new String[numGlyphs];
        Element[] glyphElementArray = new Element[numGlyphs];
        for (int i = 0; i < numGlyphs; ++i) {
            Element glyphElement = (Element)glyphElements.item(i);
            glyphCodes[i] = glyphElement.getAttributeNS(null, "unicode");
            if (glyphCodes[i].length() > 1 && ArabicTextHandler.arabicChar((char)glyphCodes[i].charAt(0))) {
                glyphCodes[i] = new StringBuffer(glyphCodes[i]).reverse().toString();
            }
            glyphNames[i] = glyphElement.getAttributeNS(null, "glyph-name");
            glyphLangs[i] = glyphElement.getAttributeNS(null, "lang");
            glyphOrientations[i] = glyphElement.getAttributeNS(null, "orientation");
            glyphForms[i] = glyphElement.getAttributeNS(null, "arabic-form");
            glyphElementArray[i] = glyphElement;
        }
        NodeList missingGlyphElements = fontElement.getElementsByTagNameNS("http://www.w3.org/2000/svg", "missing-glyph");
        Element missingGlyphElement = null;
        if (missingGlyphElements.getLength() > 0) {
            missingGlyphElement = (Element)missingGlyphElements.item(0);
        }
        NodeList hkernElements = fontElement.getElementsByTagNameNS("http://www.w3.org/2000/svg", "hkern");
        Element[] hkernElementArray = new Element[hkernElements.getLength()];
        for (int i = 0; i < hkernElementArray.length; ++i) {
            Element hkernElement;
            hkernElementArray[i] = hkernElement = (Element)hkernElements.item(i);
        }
        NodeList vkernElements = fontElement.getElementsByTagNameNS("http://www.w3.org/2000/svg", "vkern");
        Element[] vkernElementArray = new Element[vkernElements.getLength()];
        for (int i = 0; i < vkernElementArray.length; ++i) {
            Element vkernElement;
            vkernElementArray[i] = vkernElement = (Element)vkernElements.item(i);
        }
        return new SVGGVTFont(size, fontFace, glyphCodes, glyphNames, glyphLangs, glyphOrientations, glyphForms, ctx, glyphElementArray, missingGlyphElement, hkernElementArray, vkernElementArray, textElement);
    }
}

