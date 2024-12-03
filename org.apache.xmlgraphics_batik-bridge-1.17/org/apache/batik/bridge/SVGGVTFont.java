/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.css.engine.value.Value
 *  org.apache.batik.dom.util.XMLSupport
 *  org.apache.batik.gvt.font.GVTFont
 *  org.apache.batik.gvt.font.GVTFontFace
 *  org.apache.batik.gvt.font.GVTGlyphVector
 *  org.apache.batik.gvt.font.GVTLineMetrics
 *  org.apache.batik.gvt.font.Glyph
 *  org.apache.batik.gvt.font.Kern
 *  org.apache.batik.gvt.font.KerningTable
 *  org.apache.batik.gvt.font.SVGGVTGlyphVector
 *  org.apache.batik.gvt.text.GVTAttributedCharacterIterator$TextAttribute
 *  org.apache.batik.gvt.text.TextPaintInfo
 *  org.apache.batik.util.SVGConstants
 */
package org.apache.batik.bridge;

import java.awt.font.FontRenderContext;
import java.text.AttributedCharacterIterator;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.CSSUtilities;
import org.apache.batik.bridge.SVGGlyphElementBridge;
import org.apache.batik.bridge.SVGHKernElementBridge;
import org.apache.batik.bridge.SVGVKernElementBridge;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.dom.util.XMLSupport;
import org.apache.batik.gvt.font.GVTFont;
import org.apache.batik.gvt.font.GVTFontFace;
import org.apache.batik.gvt.font.GVTGlyphVector;
import org.apache.batik.gvt.font.GVTLineMetrics;
import org.apache.batik.gvt.font.Glyph;
import org.apache.batik.gvt.font.Kern;
import org.apache.batik.gvt.font.KerningTable;
import org.apache.batik.gvt.font.SVGGVTGlyphVector;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;
import org.apache.batik.gvt.text.TextPaintInfo;
import org.apache.batik.util.SVGConstants;
import org.w3c.dom.Element;

public final class SVGGVTFont
implements GVTFont,
SVGConstants {
    public static final AttributedCharacterIterator.Attribute PAINT_INFO = GVTAttributedCharacterIterator.TextAttribute.PAINT_INFO;
    private float fontSize;
    private GVTFontFace fontFace;
    private String[] glyphUnicodes;
    private String[] glyphNames;
    private String[] glyphLangs;
    private String[] glyphOrientations;
    private String[] glyphForms;
    private Element[] glyphElements;
    private Element[] hkernElements;
    private Element[] vkernElements;
    private BridgeContext ctx;
    private Element textElement;
    private Element missingGlyphElement;
    private KerningTable hKerningTable;
    private KerningTable vKerningTable;
    private String language;
    private String orientation;
    private float scale;
    private GVTLineMetrics lineMetrics = null;

    public SVGGVTFont(float fontSize, GVTFontFace fontFace, String[] glyphUnicodes, String[] glyphNames, String[] glyphLangs, String[] glyphOrientations, String[] glyphForms, BridgeContext ctx, Element[] glyphElements, Element missingGlyphElement, Element[] hkernElements, Element[] vkernElements, Element textElement) {
        this.fontFace = fontFace;
        this.fontSize = fontSize;
        this.glyphUnicodes = glyphUnicodes;
        this.glyphNames = glyphNames;
        this.glyphLangs = glyphLangs;
        this.glyphOrientations = glyphOrientations;
        this.glyphForms = glyphForms;
        this.ctx = ctx;
        this.glyphElements = glyphElements;
        this.missingGlyphElement = missingGlyphElement;
        this.hkernElements = hkernElements;
        this.vkernElements = vkernElements;
        this.scale = fontSize / fontFace.getUnitsPerEm();
        this.textElement = textElement;
        this.language = XMLSupport.getXMLLang((Element)textElement);
        Value v = CSSUtilities.getComputedStyle(textElement, 59);
        this.orientation = v.getStringValue().startsWith("tb") ? "v" : "h";
        this.createKerningTables();
    }

    private void createKerningTables() {
        Kern[] hEntries = new Kern[this.hkernElements.length];
        for (int i = 0; i < this.hkernElements.length; ++i) {
            Kern hkern;
            Element hkernElement = this.hkernElements[i];
            SVGHKernElementBridge hkernBridge = (SVGHKernElementBridge)this.ctx.getBridge(hkernElement);
            hEntries[i] = hkern = hkernBridge.createKern(this.ctx, hkernElement, this);
        }
        this.hKerningTable = new KerningTable(hEntries);
        Kern[] vEntries = new Kern[this.vkernElements.length];
        for (int i = 0; i < this.vkernElements.length; ++i) {
            Kern vkern;
            Element vkernElement = this.vkernElements[i];
            SVGVKernElementBridge vkernBridge = (SVGVKernElementBridge)this.ctx.getBridge(vkernElement);
            vEntries[i] = vkern = vkernBridge.createKern(this.ctx, vkernElement, this);
        }
        this.vKerningTable = new KerningTable(vEntries);
    }

    public float getHKern(int glyphCode1, int glyphCode2) {
        if (glyphCode1 < 0 || glyphCode1 >= this.glyphUnicodes.length || glyphCode2 < 0 || glyphCode2 >= this.glyphUnicodes.length) {
            return 0.0f;
        }
        float ret = this.hKerningTable.getKerningValue(glyphCode1, glyphCode2, this.glyphUnicodes[glyphCode1], this.glyphUnicodes[glyphCode2]);
        return ret * this.scale;
    }

    public float getVKern(int glyphCode1, int glyphCode2) {
        if (glyphCode1 < 0 || glyphCode1 >= this.glyphUnicodes.length || glyphCode2 < 0 || glyphCode2 >= this.glyphUnicodes.length) {
            return 0.0f;
        }
        float ret = this.vKerningTable.getKerningValue(glyphCode1, glyphCode2, this.glyphUnicodes[glyphCode1], this.glyphUnicodes[glyphCode2]);
        return ret * this.scale;
    }

    public int[] getGlyphCodesForName(String name) {
        ArrayList<Integer> glyphCodes = new ArrayList<Integer>();
        for (int i = 0; i < this.glyphNames.length; ++i) {
            if (this.glyphNames[i] == null || !this.glyphNames[i].equals(name)) continue;
            glyphCodes.add(i);
        }
        int[] glyphCodeArray = new int[glyphCodes.size()];
        for (int i = 0; i < glyphCodes.size(); ++i) {
            glyphCodeArray[i] = (Integer)glyphCodes.get(i);
        }
        return glyphCodeArray;
    }

    public int[] getGlyphCodesForUnicode(String unicode) {
        ArrayList<Integer> glyphCodes = new ArrayList<Integer>();
        for (int i = 0; i < this.glyphUnicodes.length; ++i) {
            if (this.glyphUnicodes[i] == null || !this.glyphUnicodes[i].equals(unicode)) continue;
            glyphCodes.add(i);
        }
        int[] glyphCodeArray = new int[glyphCodes.size()];
        for (int i = 0; i < glyphCodes.size(); ++i) {
            glyphCodeArray[i] = (Integer)glyphCodes.get(i);
        }
        return glyphCodeArray;
    }

    private boolean languageMatches(String glyphLang) {
        if (glyphLang == null || glyphLang.length() == 0) {
            return true;
        }
        StringTokenizer st = new StringTokenizer(glyphLang, ",");
        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            if (!s.equals(this.language) && (!s.startsWith(this.language) || s.length() <= this.language.length() || s.charAt(this.language.length()) != '-')) continue;
            return true;
        }
        return false;
    }

    private boolean orientationMatches(String glyphOrientation) {
        if (glyphOrientation == null || glyphOrientation.length() == 0) {
            return true;
        }
        return glyphOrientation.equals(this.orientation);
    }

    private boolean formMatches(String glyphUnicode, String glyphForm, AttributedCharacterIterator aci, int currentIndex) {
        if (aci == null || glyphForm == null || glyphForm.length() == 0) {
            return true;
        }
        char c = aci.setIndex(currentIndex);
        Integer form = (Integer)aci.getAttribute((AttributedCharacterIterator.Attribute)GVTAttributedCharacterIterator.TextAttribute.ARABIC_FORM);
        if (form == null || form.equals(GVTAttributedCharacterIterator.TextAttribute.ARABIC_NONE)) {
            return false;
        }
        if (glyphUnicode.length() > 1) {
            boolean matched = true;
            for (int j = 1; j < glyphUnicode.length(); ++j) {
                c = aci.next();
                if (glyphUnicode.charAt(j) == c) continue;
                matched = false;
                break;
            }
            aci.setIndex(currentIndex);
            if (matched) {
                aci.setIndex(currentIndex + glyphUnicode.length() - 1);
                Integer lastForm = (Integer)aci.getAttribute((AttributedCharacterIterator.Attribute)GVTAttributedCharacterIterator.TextAttribute.ARABIC_FORM);
                aci.setIndex(currentIndex);
                if (form != null && lastForm != null) {
                    if (form.equals(GVTAttributedCharacterIterator.TextAttribute.ARABIC_TERMINAL) && lastForm.equals(GVTAttributedCharacterIterator.TextAttribute.ARABIC_INITIAL)) {
                        return glyphForm.equals("isolated");
                    }
                    if (form.equals(GVTAttributedCharacterIterator.TextAttribute.ARABIC_TERMINAL)) {
                        return glyphForm.equals("terminal");
                    }
                    if (form.equals(GVTAttributedCharacterIterator.TextAttribute.ARABIC_MEDIAL) && lastForm.equals(GVTAttributedCharacterIterator.TextAttribute.ARABIC_MEDIAL)) {
                        return glyphForm.equals("medial");
                    }
                }
            }
        }
        if (form.equals(GVTAttributedCharacterIterator.TextAttribute.ARABIC_ISOLATED)) {
            return glyphForm.equals("isolated");
        }
        if (form.equals(GVTAttributedCharacterIterator.TextAttribute.ARABIC_TERMINAL)) {
            return glyphForm.equals("terminal");
        }
        if (form.equals(GVTAttributedCharacterIterator.TextAttribute.ARABIC_INITIAL)) {
            return glyphForm.equals("initial");
        }
        if (form.equals(GVTAttributedCharacterIterator.TextAttribute.ARABIC_MEDIAL)) {
            return glyphForm.equals("medial");
        }
        return false;
    }

    public boolean canDisplayGivenName(String name) {
        for (int i = 0; i < this.glyphNames.length; ++i) {
            if (this.glyphNames[i] == null || !this.glyphNames[i].equals(name) || !this.languageMatches(this.glyphLangs[i]) || !this.orientationMatches(this.glyphOrientations[i])) continue;
            return true;
        }
        return false;
    }

    public boolean canDisplay(char c) {
        for (int i = 0; i < this.glyphUnicodes.length; ++i) {
            if (this.glyphUnicodes[i].indexOf(c) == -1 || !this.languageMatches(this.glyphLangs[i]) || !this.orientationMatches(this.glyphOrientations[i])) continue;
            return true;
        }
        return false;
    }

    public int canDisplayUpTo(char[] text, int start, int limit) {
        StringCharacterIterator sci = new StringCharacterIterator(new String(text));
        return this.canDisplayUpTo(sci, start, limit);
    }

    public int canDisplayUpTo(CharacterIterator iter, int start, int limit) {
        AttributedCharacterIterator aci = null;
        if (iter instanceof AttributedCharacterIterator) {
            aci = (AttributedCharacterIterator)iter;
        }
        char c = iter.setIndex(start);
        int currentIndex = start;
        while (c != '\uffff' && currentIndex < limit) {
            boolean foundMatchingGlyph = false;
            for (int i = 0; i < this.glyphUnicodes.length; ++i) {
                if (this.glyphUnicodes[i].indexOf(c) != 0 || !this.languageMatches(this.glyphLangs[i]) || !this.orientationMatches(this.glyphOrientations[i]) || !this.formMatches(this.glyphUnicodes[i], this.glyphForms[i], aci, currentIndex)) continue;
                if (this.glyphUnicodes[i].length() == 1) {
                    foundMatchingGlyph = true;
                    break;
                }
                boolean matched = true;
                for (int j = 1; j < this.glyphUnicodes[i].length(); ++j) {
                    c = iter.next();
                    if (this.glyphUnicodes[i].charAt(j) == c) continue;
                    matched = false;
                    break;
                }
                if (matched) {
                    foundMatchingGlyph = true;
                    break;
                }
                c = iter.setIndex(currentIndex);
            }
            if (!foundMatchingGlyph) {
                return currentIndex;
            }
            c = iter.next();
            currentIndex = iter.getIndex();
        }
        return -1;
    }

    public int canDisplayUpTo(String str) {
        StringCharacterIterator sci = new StringCharacterIterator(str);
        return this.canDisplayUpTo(sci, 0, str.length());
    }

    public GVTGlyphVector createGlyphVector(FontRenderContext frc, char[] chars) {
        StringCharacterIterator sci = new StringCharacterIterator(new String(chars));
        return this.createGlyphVector(frc, sci);
    }

    public GVTGlyphVector createGlyphVector(FontRenderContext frc, CharacterIterator ci) {
        AttributedCharacterIterator aci = null;
        if (ci instanceof AttributedCharacterIterator) {
            aci = (AttributedCharacterIterator)ci;
        }
        ArrayList<Glyph> glyphs = new ArrayList<Glyph>();
        char c = ci.first();
        while (c != '\uffff') {
            boolean foundMatchingGlyph = false;
            for (int i = 0; i < this.glyphUnicodes.length; ++i) {
                if (this.glyphUnicodes[i].indexOf(c) != 0 || !this.languageMatches(this.glyphLangs[i]) || !this.orientationMatches(this.glyphOrientations[i]) || !this.formMatches(this.glyphUnicodes[i], this.glyphForms[i], aci, ci.getIndex())) continue;
                if (this.glyphUnicodes[i].length() == 1) {
                    Element glyphElement = this.glyphElements[i];
                    SVGGlyphElementBridge glyphBridge = (SVGGlyphElementBridge)this.ctx.getBridge(glyphElement);
                    TextPaintInfo tpi = null;
                    if (aci != null) {
                        tpi = (TextPaintInfo)aci.getAttribute(PAINT_INFO);
                    }
                    Glyph glyph = glyphBridge.createGlyph(this.ctx, glyphElement, this.textElement, i, this.fontSize, this.fontFace, tpi);
                    glyphs.add(glyph);
                    foundMatchingGlyph = true;
                    break;
                }
                int current = ci.getIndex();
                boolean matched = true;
                for (int j = 1; j < this.glyphUnicodes[i].length(); ++j) {
                    c = ci.next();
                    if (this.glyphUnicodes[i].charAt(j) == c) continue;
                    matched = false;
                    break;
                }
                if (matched) {
                    Element glyphElement = this.glyphElements[i];
                    SVGGlyphElementBridge glyphBridge = (SVGGlyphElementBridge)this.ctx.getBridge(glyphElement);
                    TextPaintInfo tpi = null;
                    if (aci != null) {
                        aci.setIndex(ci.getIndex());
                        tpi = (TextPaintInfo)aci.getAttribute(PAINT_INFO);
                    }
                    Glyph glyph = glyphBridge.createGlyph(this.ctx, glyphElement, this.textElement, i, this.fontSize, this.fontFace, tpi);
                    glyphs.add(glyph);
                    foundMatchingGlyph = true;
                    break;
                }
                c = ci.setIndex(current);
            }
            if (!foundMatchingGlyph) {
                SVGGlyphElementBridge glyphBridge = (SVGGlyphElementBridge)this.ctx.getBridge(this.missingGlyphElement);
                TextPaintInfo tpi = null;
                if (aci != null) {
                    aci.setIndex(ci.getIndex());
                    tpi = (TextPaintInfo)aci.getAttribute(PAINT_INFO);
                }
                Glyph glyph = glyphBridge.createGlyph(this.ctx, this.missingGlyphElement, this.textElement, -1, this.fontSize, this.fontFace, tpi);
                glyphs.add(glyph);
            }
            c = ci.next();
        }
        int numGlyphs = glyphs.size();
        Glyph[] glyphArray = glyphs.toArray(new Glyph[numGlyphs]);
        return new SVGGVTGlyphVector((GVTFont)this, glyphArray, frc);
    }

    public GVTGlyphVector createGlyphVector(FontRenderContext frc, int[] glyphCodes, CharacterIterator ci) {
        int nGlyphs = glyphCodes.length;
        StringBuffer workBuff = new StringBuffer(nGlyphs);
        for (int glyphCode : glyphCodes) {
            workBuff.append(this.glyphUnicodes[glyphCode]);
        }
        StringCharacterIterator sci = new StringCharacterIterator(workBuff.toString());
        return this.createGlyphVector(frc, sci);
    }

    public GVTGlyphVector createGlyphVector(FontRenderContext frc, String str) {
        StringCharacterIterator sci = new StringCharacterIterator(str);
        return this.createGlyphVector(frc, sci);
    }

    public GVTFont deriveFont(float size) {
        return new SVGGVTFont(size, this.fontFace, this.glyphUnicodes, this.glyphNames, this.glyphLangs, this.glyphOrientations, this.glyphForms, this.ctx, this.glyphElements, this.missingGlyphElement, this.hkernElements, this.vkernElements, this.textElement);
    }

    public String getFamilyName() {
        return this.fontFace.getFamilyName();
    }

    protected GVTLineMetrics getLineMetrics(int beginIndex, int limit) {
        if (this.lineMetrics != null) {
            return this.lineMetrics;
        }
        float fontHeight = this.fontFace.getUnitsPerEm();
        float scale = this.fontSize / fontHeight;
        float ascent = this.fontFace.getAscent() * scale;
        float descent = this.fontFace.getDescent() * scale;
        float[] baselineOffsets = new float[]{0.0f, (ascent + descent) / 2.0f - ascent, -ascent};
        float stOffset = this.fontFace.getStrikethroughPosition() * -scale;
        float stThickness = this.fontFace.getStrikethroughThickness() * scale;
        float ulOffset = this.fontFace.getUnderlinePosition() * scale;
        float ulThickness = this.fontFace.getUnderlineThickness() * scale;
        float olOffset = this.fontFace.getOverlinePosition() * -scale;
        float olThickness = this.fontFace.getOverlineThickness() * scale;
        this.lineMetrics = new GVTLineMetrics(ascent, 0, baselineOffsets, descent, fontHeight, fontHeight, limit - beginIndex, stOffset, stThickness, ulOffset, ulThickness, olOffset, olThickness);
        return this.lineMetrics;
    }

    public GVTLineMetrics getLineMetrics(char[] chars, int beginIndex, int limit, FontRenderContext frc) {
        return this.getLineMetrics(beginIndex, limit);
    }

    public GVTLineMetrics getLineMetrics(CharacterIterator ci, int beginIndex, int limit, FontRenderContext frc) {
        return this.getLineMetrics(beginIndex, limit);
    }

    public GVTLineMetrics getLineMetrics(String str, FontRenderContext frc) {
        StringCharacterIterator sci = new StringCharacterIterator(str);
        return this.getLineMetrics(sci, 0, str.length(), frc);
    }

    public GVTLineMetrics getLineMetrics(String str, int beginIndex, int limit, FontRenderContext frc) {
        StringCharacterIterator sci = new StringCharacterIterator(str);
        return this.getLineMetrics(sci, beginIndex, limit, frc);
    }

    public float getSize() {
        return this.fontSize;
    }

    public String toString() {
        return this.fontFace.getFamilyName() + " " + this.fontFace.getFontWeight() + " " + this.fontFace.getFontStyle();
    }
}

