/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.sl.draw;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.poi.common.usermodel.fonts.FontCharset;
import org.apache.poi.common.usermodel.fonts.FontInfo;
import org.apache.poi.sl.draw.DrawFontInfo;
import org.apache.poi.sl.draw.DrawFontManager;
import org.apache.poi.sl.draw.Drawable;
import org.apache.poi.util.StringUtil;

public class DrawFontManagerDefault
implements DrawFontManager {
    protected final Set<String> knownSymbolFonts = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);

    public DrawFontManagerDefault() {
        this.knownSymbolFonts.add("Wingdings");
        this.knownSymbolFonts.add("Symbol");
    }

    @Override
    public FontInfo getMappedFont(Graphics2D graphics, FontInfo fontInfo) {
        return this.getFontWithFallback(graphics, Drawable.FONT_MAP, fontInfo);
    }

    @Override
    public FontInfo getFallbackFont(Graphics2D graphics, FontInfo fontInfo) {
        FontInfo fi = this.getFontWithFallback(graphics, Drawable.FONT_FALLBACK, fontInfo);
        if (fi == null) {
            fi = new DrawFontInfo("SansSerif");
        }
        return fi;
    }

    @Override
    public String mapFontCharset(Graphics2D graphics, FontInfo fontInfo, String text) {
        if (fontInfo == null || text == null || text.isEmpty()) {
            return text;
        }
        String typeface = fontInfo.getTypeface();
        if (fontInfo.getCharset() == FontCharset.SYMBOL || this.knownSymbolFonts.contains(typeface)) {
            int[] cps = text.codePoints().map(DrawFontManagerDefault::mapSymbolChar).toArray();
            String ret = new String(cps, 0, cps.length);
            String[] allFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
            boolean hasFont = Arrays.asList(allFonts).contains(typeface);
            return hasFont ? ret : StringUtil.mapMsCodepointString(ret);
        }
        return text;
    }

    @Override
    public Font createAWTFont(Graphics2D graphics, FontInfo fontInfo, double fontSize, boolean bold, boolean italic) {
        int style = (bold ? 1 : 0) | (italic ? 2 : 0);
        Font font = new Font(fontInfo.getTypeface(), style, 12);
        if ("Dialog".equals(font.getFamily())) {
            font = new Font("SansSerif", style, 12);
        }
        return font.deriveFont((float)fontSize);
    }

    private FontInfo getFontWithFallback(Graphics2D graphics, Drawable.DrawableHint hint, FontInfo fontInfo) {
        Map fontMap = (Map)graphics.getRenderingHint(hint);
        if (fontMap == null) {
            return fontInfo;
        }
        String f = fontInfo != null ? fontInfo.getTypeface() : null;
        String mappedTypeface = null;
        if (fontMap.containsKey(f)) {
            mappedTypeface = (String)fontMap.get(f);
        } else if (fontMap.containsKey("*")) {
            mappedTypeface = (String)fontMap.get("*");
        }
        return mappedTypeface != null ? new DrawFontInfo(mappedTypeface) : fontInfo;
    }

    private static int mapSymbolChar(int cp) {
        return 32 <= cp && cp <= 127 || 160 <= cp && cp <= 255 ? cp | 0xF000 : cp;
    }
}

