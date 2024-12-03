/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.g2d.GraphicContext
 */
package org.apache.batik.svggen;

import java.awt.Font;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.util.HashMap;
import java.util.Map;
import org.apache.batik.ext.awt.g2d.GraphicContext;
import org.apache.batik.svggen.AbstractSVGConverter;
import org.apache.batik.svggen.SVGDescriptor;
import org.apache.batik.svggen.SVGFontDescriptor;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGPath;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class SVGFont
extends AbstractSVGConverter {
    public static final float EXTRA_LIGHT = TextAttribute.WEIGHT_EXTRA_LIGHT.floatValue();
    public static final float LIGHT = TextAttribute.WEIGHT_LIGHT.floatValue();
    public static final float DEMILIGHT = TextAttribute.WEIGHT_DEMILIGHT.floatValue();
    public static final float REGULAR = TextAttribute.WEIGHT_REGULAR.floatValue();
    public static final float SEMIBOLD = TextAttribute.WEIGHT_SEMIBOLD.floatValue();
    public static final float MEDIUM = TextAttribute.WEIGHT_MEDIUM.floatValue();
    public static final float DEMIBOLD = TextAttribute.WEIGHT_DEMIBOLD.floatValue();
    public static final float BOLD = TextAttribute.WEIGHT_BOLD.floatValue();
    public static final float HEAVY = TextAttribute.WEIGHT_HEAVY.floatValue();
    public static final float EXTRABOLD = TextAttribute.WEIGHT_EXTRABOLD.floatValue();
    public static final float ULTRABOLD = TextAttribute.WEIGHT_ULTRABOLD.floatValue();
    public static final float POSTURE_REGULAR = TextAttribute.POSTURE_REGULAR.floatValue();
    public static final float POSTURE_OBLIQUE = TextAttribute.POSTURE_OBLIQUE.floatValue();
    static final float[] fontStyles = new float[]{POSTURE_REGULAR + (POSTURE_OBLIQUE - POSTURE_REGULAR) / 2.0f};
    static final String[] svgStyles = new String[]{"normal", "italic"};
    static final float[] fontWeights = new float[]{EXTRA_LIGHT + (LIGHT - EXTRA_LIGHT) / 2.0f, LIGHT + (DEMILIGHT - LIGHT) / 2.0f, DEMILIGHT + (REGULAR - DEMILIGHT) / 2.0f, REGULAR + (SEMIBOLD - REGULAR) / 2.0f, SEMIBOLD + (MEDIUM - SEMIBOLD) / 2.0f, MEDIUM + (DEMIBOLD - MEDIUM) / 2.0f, DEMIBOLD + (BOLD - DEMIBOLD) / 2.0f, BOLD + (HEAVY - BOLD) / 2.0f, HEAVY + (EXTRABOLD - HEAVY) / 2.0f, EXTRABOLD + (ULTRABOLD - EXTRABOLD)};
    static final String[] svgWeights = new String[]{"100", "200", "300", "normal", "500", "500", "600", "bold", "800", "800", "900"};
    static Map logicalFontMap = new HashMap();
    static final int COMMON_FONT_SIZE = 100;
    final Map fontStringMap = new HashMap();

    public SVGFont(SVGGeneratorContext generatorContext) {
        super(generatorContext);
    }

    public void recordFontUsage(String string, Font font) {
        Font commonSizeFont = SVGFont.createCommonSizeFont(font);
        String fontKey = commonSizeFont.getFamily() + commonSizeFont.getStyle();
        CharListHelper chl = (CharListHelper)this.fontStringMap.get(fontKey);
        if (chl == null) {
            chl = new CharListHelper();
        }
        for (int i = 0; i < string.length(); ++i) {
            char ch = string.charAt(i);
            chl.add(ch);
        }
        this.fontStringMap.put(fontKey, chl);
    }

    private static Font createCommonSizeFont(Font font) {
        HashMap<TextAttribute, Float> attributes = new HashMap<TextAttribute, Float>();
        attributes.put(TextAttribute.SIZE, Float.valueOf(100.0f));
        attributes.put(TextAttribute.TRANSFORM, null);
        return font.deriveFont(attributes);
    }

    @Override
    public SVGDescriptor toSVG(GraphicContext gc) {
        return this.toSVG(gc.getFont(), gc.getFontRenderContext());
    }

    public SVGFontDescriptor toSVG(Font font, FontRenderContext frc) {
        Element fontDef;
        FontRenderContext localFRC = new FontRenderContext(new AffineTransform(), frc.isAntiAliased(), frc.usesFractionalMetrics());
        String fontSize = this.doubleString(font.getSize2D()) + "px";
        String fontWeight = SVGFont.weightToSVG(font);
        String fontStyle = SVGFont.styleToSVG(font);
        String fontFamilyStr = SVGFont.familyToSVG(font);
        Font commonSizeFont = SVGFont.createCommonSizeFont(font);
        String fontKey = commonSizeFont.getFamily() + commonSizeFont.getStyle();
        CharListHelper clh = (CharListHelper)this.fontStringMap.get(fontKey);
        if (clh == null) {
            return new SVGFontDescriptor(fontSize, fontWeight, fontStyle, fontFamilyStr, null);
        }
        Document domFactory = this.generatorContext.domFactory;
        SVGFontDescriptor fontDesc = (SVGFontDescriptor)this.descMap.get(fontKey);
        if (fontDesc != null) {
            fontDef = fontDesc.getDef();
        } else {
            fontDef = domFactory.createElementNS("http://www.w3.org/2000/svg", "font");
            Element fontFace = domFactory.createElementNS("http://www.w3.org/2000/svg", "font-face");
            String svgFontFamilyString = fontFamilyStr;
            if (fontFamilyStr.startsWith("'") && fontFamilyStr.endsWith("'")) {
                svgFontFamilyString = fontFamilyStr.substring(1, fontFamilyStr.length() - 1);
            }
            fontFace.setAttributeNS(null, "font-family", svgFontFamilyString);
            fontFace.setAttributeNS(null, "font-weight", fontWeight);
            fontFace.setAttributeNS(null, "font-style", fontStyle);
            fontFace.setAttributeNS(null, "units-per-em", "100");
            fontDef.appendChild(fontFace);
            Element missingGlyphElement = domFactory.createElementNS("http://www.w3.org/2000/svg", "missing-glyph");
            int[] missingGlyphCode = new int[]{commonSizeFont.getMissingGlyphCode()};
            GlyphVector gv = commonSizeFont.createGlyphVector(localFRC, missingGlyphCode);
            Shape missingGlyphShape = gv.getGlyphOutline(0);
            GlyphMetrics gm = gv.getGlyphMetrics(0);
            AffineTransform at = AffineTransform.getScaleInstance(1.0, -1.0);
            missingGlyphShape = at.createTransformedShape(missingGlyphShape);
            missingGlyphElement.setAttributeNS(null, "d", SVGPath.toSVGPathData(missingGlyphShape, this.generatorContext));
            missingGlyphElement.setAttributeNS(null, "horiz-adv-x", String.valueOf(gm.getAdvance()));
            fontDef.appendChild(missingGlyphElement);
            fontDef.setAttributeNS(null, "horiz-adv-x", String.valueOf(gm.getAdvance()));
            LineMetrics lm = commonSizeFont.getLineMetrics("By", localFRC);
            fontFace.setAttributeNS(null, "ascent", String.valueOf(lm.getAscent()));
            fontFace.setAttributeNS(null, "descent", String.valueOf(lm.getDescent()));
            fontDef.setAttributeNS(null, "id", this.generatorContext.idGenerator.generateID("font"));
        }
        String textUsingFont = clh.getNewChars();
        clh.clearNewChars();
        for (int i = textUsingFont.length() - 1; i >= 0; --i) {
            char c = textUsingFont.charAt(i);
            String searchStr = String.valueOf(c);
            boolean foundGlyph = false;
            NodeList fontChildren = fontDef.getChildNodes();
            for (int j = 0; j < fontChildren.getLength(); ++j) {
                Element childElement;
                if (!(fontChildren.item(j) instanceof Element) || !(childElement = (Element)fontChildren.item(j)).getAttributeNS(null, "unicode").equals(searchStr)) continue;
                foundGlyph = true;
                break;
            }
            if (foundGlyph) break;
            Element glyphElement = domFactory.createElementNS("http://www.w3.org/2000/svg", "glyph");
            GlyphVector gv = commonSizeFont.createGlyphVector(localFRC, "" + c);
            Shape glyphShape = gv.getGlyphOutline(0);
            GlyphMetrics gm = gv.getGlyphMetrics(0);
            AffineTransform at = AffineTransform.getScaleInstance(1.0, -1.0);
            glyphShape = at.createTransformedShape(glyphShape);
            glyphElement.setAttributeNS(null, "d", SVGPath.toSVGPathData(glyphShape, this.generatorContext));
            glyphElement.setAttributeNS(null, "horiz-adv-x", String.valueOf(gm.getAdvance()));
            glyphElement.setAttributeNS(null, "unicode", String.valueOf(c));
            fontDef.appendChild(glyphElement);
        }
        SVGFontDescriptor newFontDesc = new SVGFontDescriptor(fontSize, fontWeight, fontStyle, fontFamilyStr, fontDef);
        if (fontDesc == null) {
            this.descMap.put(fontKey, newFontDesc);
            this.defSet.add(fontDef);
        }
        return newFontDesc;
    }

    public static String familyToSVG(Font font) {
        String fontFamilyStr = font.getFamily();
        String logicalFontFamily = (String)logicalFontMap.get(font.getName().toLowerCase());
        if (logicalFontFamily != null) {
            fontFamilyStr = logicalFontFamily;
        } else {
            int QUOTE = 39;
            fontFamilyStr = '\'' + fontFamilyStr + '\'';
        }
        return fontFamilyStr;
    }

    public static String styleToSVG(Font font) {
        Map<TextAttribute, ?> attrMap = font.getAttributes();
        Float styleValue = (Float)attrMap.get(TextAttribute.POSTURE);
        if (styleValue == null) {
            styleValue = font.isItalic() ? TextAttribute.POSTURE_OBLIQUE : TextAttribute.POSTURE_REGULAR;
        }
        float style = styleValue.floatValue();
        int i = 0;
        for (i = 0; i < fontStyles.length && !(style <= fontStyles[i]); ++i) {
        }
        return svgStyles[i];
    }

    public static String weightToSVG(Font font) {
        Map<TextAttribute, ?> attrMap = font.getAttributes();
        Float weightValue = (Float)attrMap.get(TextAttribute.WEIGHT);
        if (weightValue == null) {
            weightValue = font.isBold() ? TextAttribute.WEIGHT_BOLD : TextAttribute.WEIGHT_REGULAR;
        }
        float weight = weightValue.floatValue();
        int i = 0;
        for (i = 0; i < fontWeights.length && !(weight <= fontWeights[i]); ++i) {
        }
        return svgWeights[i];
    }

    static {
        logicalFontMap.put("dialog", "sans-serif");
        logicalFontMap.put("dialoginput", "monospace");
        logicalFontMap.put("monospaced", "monospace");
        logicalFontMap.put("serif", "serif");
        logicalFontMap.put("sansserif", "sans-serif");
        logicalFontMap.put("symbol", "'WingDings'");
    }

    private static class CharListHelper {
        private int nUsed = 0;
        private int[] charList = new int[40];
        private StringBuffer freshChars = new StringBuffer(40);

        CharListHelper() {
        }

        String getNewChars() {
            return this.freshChars.toString();
        }

        void clearNewChars() {
            this.freshChars = new StringBuffer(40);
        }

        boolean add(int c) {
            int pos = CharListHelper.binSearch(this.charList, this.nUsed, c);
            if (pos >= 0) {
                return false;
            }
            if (this.nUsed == this.charList.length) {
                int[] t = new int[this.nUsed + 20];
                System.arraycopy(this.charList, 0, t, 0, this.nUsed);
                this.charList = t;
            }
            pos = -pos - 1;
            System.arraycopy(this.charList, pos, this.charList, pos + 1, this.nUsed - pos);
            this.charList[pos] = c;
            this.freshChars.append((char)c);
            ++this.nUsed;
            return true;
        }

        static int binSearch(int[] list, int nUsed, int chr) {
            int low = 0;
            int high = nUsed - 1;
            while (low <= high) {
                int mid = low + high >>> 1;
                int midVal = list[mid];
                if (midVal < chr) {
                    low = mid + 1;
                    continue;
                }
                if (midVal > chr) {
                    high = mid - 1;
                    continue;
                }
                return mid;
            }
            return -(low + 1);
        }
    }
}

