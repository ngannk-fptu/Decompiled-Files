/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.gvt.font.AWTFontFamily
 *  org.apache.batik.gvt.font.AWTGVTFont
 *  org.apache.batik.gvt.font.GVTFontFace
 *  org.apache.batik.gvt.font.GVTFontFamily
 */
package org.apache.batik.bridge;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.batik.bridge.CSSFontFace;
import org.apache.batik.bridge.FontFace;
import org.apache.batik.bridge.FontFamilyResolver;
import org.apache.batik.gvt.font.AWTFontFamily;
import org.apache.batik.gvt.font.AWTGVTFont;
import org.apache.batik.gvt.font.GVTFontFace;
import org.apache.batik.gvt.font.GVTFontFamily;

public final class DefaultFontFamilyResolver
implements FontFamilyResolver {
    public static final DefaultFontFamilyResolver SINGLETON = new DefaultFontFamilyResolver();
    private static final AWTFontFamily DEFAULT_FONT_FAMILY = new AWTFontFamily("SansSerif");
    protected static final Map fonts = new HashMap();
    protected static final List awtFontFamilies = new ArrayList();
    protected static final List awtFonts = new ArrayList();
    protected static final Map resolvedFontFamilies;

    private DefaultFontFamilyResolver() {
    }

    public AWTFontFamily resolve(String familyName, FontFace fontFace) {
        String fontName = (String)fonts.get(fontFace.getFamilyName().toLowerCase());
        if (fontName == null) {
            return null;
        }
        CSSFontFace face = FontFace.createFontFace(fontName, fontFace);
        return new AWTFontFamily((GVTFontFace)fontFace);
    }

    @Override
    public GVTFontFamily loadFont(InputStream in, FontFace ff) throws Exception {
        Font font = Font.createFont(0, in);
        return new AWTFontFamily((GVTFontFace)ff, font);
    }

    @Override
    public GVTFontFamily resolve(String familyName) {
        GVTFontFamily resolvedFF = (GVTFontFamily)resolvedFontFamilies.get(familyName = familyName.toLowerCase());
        if (resolvedFF == null) {
            String awtFamilyName = (String)fonts.get(familyName);
            if (awtFamilyName != null) {
                resolvedFF = new AWTFontFamily(awtFamilyName);
            }
            resolvedFontFamilies.put(familyName, resolvedFF);
        }
        return resolvedFF;
    }

    @Override
    public GVTFontFamily getFamilyThatCanDisplay(char c) {
        for (int i = 0; i < awtFontFamilies.size(); ++i) {
            AWTFontFamily fontFamily = (AWTFontFamily)awtFontFamilies.get(i);
            AWTGVTFont font = (AWTGVTFont)awtFonts.get(i);
            if (!font.canDisplay(c) || fontFamily.getFamilyName().indexOf("Song") != -1) continue;
            return fontFamily;
        }
        return null;
    }

    @Override
    public GVTFontFamily getDefault() {
        return DEFAULT_FONT_FAMILY;
    }

    static {
        fonts.put("sans-serif", "SansSerif");
        fonts.put("serif", "Serif");
        fonts.put("times", "Serif");
        fonts.put("times new roman", "Serif");
        fonts.put("cursive", "Dialog");
        fonts.put("fantasy", "Symbol");
        fonts.put("monospace", "Monospaced");
        fonts.put("monospaced", "Monospaced");
        fonts.put("courier", "Monospaced");
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontNames = env.getAvailableFontFamilyNames();
        int nFonts = fontNames != null ? fontNames.length : 0;
        for (int i = 0; i < nFonts; ++i) {
            fonts.put(fontNames[i].toLowerCase(), fontNames[i]);
            StringTokenizer st = new StringTokenizer(fontNames[i]);
            String fontNameWithoutSpaces = "";
            while (st.hasMoreTokens()) {
                fontNameWithoutSpaces = fontNameWithoutSpaces + st.nextToken();
            }
            fonts.put(fontNameWithoutSpaces.toLowerCase(), fontNames[i]);
            String fontNameWithDashes = fontNames[i].replace(' ', '-');
            if (fontNameWithDashes.equals(fontNames[i])) continue;
            fonts.put(fontNameWithDashes.toLowerCase(), fontNames[i]);
        }
        Font[] allFonts = env.getAllFonts();
        for (Font f : allFonts) {
            fonts.put(f.getFontName().toLowerCase(), f.getFontName());
        }
        awtFontFamilies.add(DEFAULT_FONT_FAMILY);
        awtFonts.add(new AWTGVTFont(DEFAULT_FONT_FAMILY.getFamilyName(), 0, 12));
        Collection fontValues = fonts.values();
        for (Object fontValue : fontValues) {
            String fontFamily = (String)fontValue;
            AWTFontFamily awtFontFamily = new AWTFontFamily(fontFamily);
            awtFontFamilies.add(awtFontFamily);
            AWTGVTFont font = new AWTGVTFont(fontFamily, 0, 12);
            awtFonts.add(font);
        }
        resolvedFontFamilies = new HashMap();
    }
}

